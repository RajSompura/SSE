import { useEffect } from "react";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const EventListner = () =>{
const notify = () => toast.warn("Welcome")
useEffect(() =>{
const eventsource = new EventSource("http://localhost:8080/notifications");
////used if no only object is sent from backend not event based
// eventListner.onmessage = (event) =>{
//     console.log(event.data)
    
//     // console.log(data)
//     // console.log(event.data)
//     // toast.info(data.msg)
// }
const eventList =["Event 1","Event 2","Event 3"];
eventList.forEach((eventName) =>{
eventsource.addEventListener(eventName, (event) => {
    const data = JSON.parse(event.data)
    switch(data.type){
        case "info":
            toast.info(`${eventName} : ${data.msg}`);
            break;
        case "warn":
            toast.warn(`${eventName} : ${data.msg}`);
            break;
        case "error":
            toast.error(`${eventName} : ${data.msg}`);
            break;
        case "success":
            toast.success(`${eventName} : ${data.msg}`);
            break;
        default:
            toast(`${eventName} : ${data.msg}`);
    }
    
});
});
eventsource.onerror = (error)=>{
console.log(error);
eventsource.close();
};

return () => {
    eventsource.close();
}

},[])

return(
    <>
    <button onClick={notify}>Hello</button>
    <ToastContainer 
    hideProgressBar={true}
    autoClose={3000}
    theme="colored"/>
    </>
)
}

export default EventListner;