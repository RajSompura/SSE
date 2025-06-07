package com.pocs.sse_sb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Notification {
	
	private int id;
	private String msg;
	private String type;
}
