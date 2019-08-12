package com.concordia.clientServerInterface;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ClientInterface {
	
	 public String AddEvent( String eventId,  String eventType,  int bookingCapactiy);

	 public String RemoveEvent( String eventId,  String eventType); 
     
	 public String ListEventAvailability( String eventType); 
     
	 public String BookEvent( String customerId,  String eventId,  String eventType); 

	 public String GetBookingSchedule( String customerId);

	 public boolean CancelEvent( String cutomerId,  String eventId,  String eventType); 
     
	 public String SwapEvent( String  customerID,  String newEventID,  String newEventType,  String oldEventID,  String oldEventType);
    
	 public void ResultHashMap();

}
