package com.pocs.sse_sb.controller;


import java.io.IOException;
import java.lang.System.Logger;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pocs.sse_sb.model.Notification;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/notifications")
@Slf4j
public class NotificationController {

	private SseEmitter sseEmitter;
	
	//Purpose is for auto incrementing for id send i notification object
	AtomicInteger id = new AtomicInteger(1);
	
	//Used to store all the emitters
	//Used for broad casting purpose - By using (3rd endpoint)
	List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
	
	
	/**
	 * Api for creating emitter that listens to sse.
	 * @return
	 */
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter sendNotification() {
		sseEmitter = new SseEmitter(Long.MAX_VALUE);
		emitters.add(sseEmitter);
		try {
			sseEmitter.send(SseEmitter.event().name("Welcome").data(new Notification(id.getAndIncrement(), "Welcome to SSE",null)));
		} catch (IOException e) {
			log.error("Failed to send Welcome event");
			emitters.remove(sseEmitter);
			sseEmitter.completeWithError(e);
		}
		
		sseEmitter.onCompletion(() ->{
			log.info("SSE Connection completed");
			emitters.remove(sseEmitter);
			sseEmitter.complete();
		});
		
		sseEmitter.onTimeout(() ->{
			log.info("SSE Connection completed due to timeout");
			emitters.remove(sseEmitter);
			sseEmitter.complete();
		});
		
		sseEmitter.onError((e) ->{
			log.info("SSE Connection completed due to timeout");
			emitters.remove(sseEmitter);
			sseEmitter.completeWithError(e);
		});
		
		return sseEmitter;
	}
	
	/**
	 * Endpoint to trigger notifcation(1st endpoint)
	 * @param msg
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/sendMSG1")
	public ResponseEntity<String> sseTrigger1(@RequestParam("msg") String msg,@RequestParam("type") String type) throws IOException{
		sseEmitter.send(SseEmitter.event().name("Event 1").data(new Notification(id.getAndIncrement(), msg,type)));
		return ResponseEntity.ok("Success");
	}
	

	/**
	 * Endpoint to trigger notifcation(2nd endpoint)
	 * @param msg
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/sendMSG2")
	public ResponseEntity<String> sseTrigger2(@RequestParam("msg") String msg,@RequestParam("type") String type) throws IOException{
		sseEmitter.send(SseEmitter.event().name("Event 2").data(new Notification(id.getAndIncrement(), msg, type)));
		return ResponseEntity.ok("Success");
	}
	
	

	/**
	 * Endpoint to trigger notifcation(3rd endpoint) - to all the emitters (Broadcasting)
	 * @param msg
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/sendMSG3")
	public ResponseEntity<String> sseTrigger3(@RequestParam("msg") String msg,@RequestParam("type") String type){
		for(SseEmitter emitter:emitters) {
		try {
			emitter.send(SseEmitter.event().name("Event 3").data(new Notification(id.getAndIncrement(), msg, type)));
		} catch (IOException e) {
			log.error("Failed to send msg for Event 3");
		}
		}
		return ResponseEntity.ok("Success");
	}
	
	
	
}
