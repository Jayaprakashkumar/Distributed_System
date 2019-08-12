package com.concordia.server.MontrealServer;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import com.concordia.clientServerInterface.ClientInterface;
import com.concordia.common.CustomerData;
import com.concordia.common.EventsData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.IOException;
import java.net.*;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Endpoint;

@WebService(endpointInterface = "com.concordia.clientServerInterface.ClientInterface")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class MontrealServerImplementation  implements ClientInterface, Runnable {

	private static Logger LOGGER = LogManager.getLogger("montreal");

	private static final long serialVersionUID = 1L;
	public static Map<String, Map<String, EventsData>> montrealHashMap = new HashMap<String, Map<String, EventsData>>();
	public static List<CustomerData> CustomerDataList = new ArrayList<CustomerData>();
	public static Map<String,Integer> customerBookingAccess = new HashMap<String,Integer>();

	public MontrealServerImplementation() {
		super();
	}    

	public static void main(String args[]) {
		try {
			
			Thread threadSocket = new Thread(new MontrealServerImplementation());
			threadSocket.start();
			MontrealServerImplementation MtlServer = new MontrealServerImplementation();
			Endpoint endpoint = Endpoint.publish("http://localhost:1111/Montreal", MtlServer);

			LOGGER.info("In montreal server");
			LOGGER.info("Montreal Server is up and running.");

		} catch (Exception e) {
			LOGGER.info("Montreal Server Start up failed: " + e);
			e.printStackTrace();
		}
	}

	public void run() {
		MontrealServerImplementation  MtlServer = new MontrealServerImplementation();
		DatagramSocket socket = null;
		try {

			socket = new DatagramSocket(1111);


			while (true) {

				byte[] getServerResponse = new byte[1000];
				byte[] sendDataByte = new byte[1000];


				String recievedString = "";
				DatagramPacket request = new DatagramPacket(getServerResponse, getServerResponse.length);
				socket.receive(request);
				LOGGER.info(" Recieved message :"+recievedString);		
				
				recievedString = new String(request.getData());

				String[] splitRecievedStr = recievedString.split(",");

				String eventChoice = splitRecievedStr[splitRecievedStr.length -1];
				eventChoice = eventChoice.trim();
				LOGGER.info("Request Type :"+eventChoice);
				
				String outputRes = "";
				if(eventChoice.equalsIgnoreCase("listEventAvailability"))
				{
					LOGGER.info("List event Avaialability: Inter server communication");
					outputRes = MtlServer.getCurrentServerEventAvailability(splitRecievedStr[0]);
					sendDataByte = outputRes.getBytes();
				}
				else if(eventChoice.equalsIgnoreCase("bookEvent"))
				{
					LOGGER.info("Book Event: Inter server communication");
					
					if(MtlServer.BookEvent(splitRecievedStr[0], splitRecievedStr[1], splitRecievedStr[2]).equalsIgnoreCase("success"))
					{
						outputRes = "t";	
					}
					else
					{
						outputRes = "f";

					}
					
					sendDataByte = outputRes.getBytes();
				}
				else if(eventChoice.equalsIgnoreCase("getBooking"))
				{
					LOGGER.info("Get booking Schedule: Inter server communication");
					outputRes  = MtlServer.getCurrentBookingSchedule(splitRecievedStr[0]);
					sendDataByte = outputRes.getBytes();

				}
				else if(eventChoice.equalsIgnoreCase("cancelEvent"))
				{
					LOGGER.info("Cancel Event : Inter server Communication");
					boolean result =  MtlServer.CancelEvent(splitRecievedStr[0],splitRecievedStr[1], splitRecievedStr[2]);
					if(result) {
						outputRes ="t";
					}else {
						outputRes = "f";
					}
						
					sendDataByte = outputRes.getBytes();

				}
				else
				{
					sendDataByte = "".getBytes();
				}


				DatagramPacket reply = new DatagramPacket(sendDataByte, sendDataByte.length, request.getAddress(),
						request.getPort());
				socket.send(reply);
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
	}
	public synchronized String AddEvent(String eventId, String eventType, int bookingCapactiy)   {
		String status = "failure";
		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
		EventsData e = new EventsData();
		e.capacityOfBooking = bookingCapactiy;
		e.seatsLeft = bookingCapactiy;		
		LOGGER.info("Request Type: Add Event");
		LOGGER.info("Parameter: EventID, EventType , Booking Capacity");
		LOGGER.info("Montral Hashmap size :" + montrealHashMap.size());
		
		if (!montrealHashMap.isEmpty()) {

			if (montrealHashMap.containsKey(eventType)) {
				innerhash = montrealHashMap.get(eventType);

				if (!innerhash.containsKey(eventId)) {
					innerhash.put(eventId, e);
					montrealHashMap.put(eventType, innerhash);
					status = "success";
				} else {
					innerhash.put(eventId, e);
					montrealHashMap.put(eventType, innerhash);
					status = "failure";
				}
			} else {
				innerhash.put(eventId, e);
				montrealHashMap.put(eventType, innerhash);
				status = "success";
			}

		} else {
			innerhash.put(eventId, e);
			montrealHashMap.put(eventType, innerhash);
			status = "success";

		}
		LOGGER.info("Event Type : " + eventType+"\n" + "Event ID : " + eventId +"\n" + "Booking capacity "
				+ bookingCapactiy);
		LOGGER.info("Montreal Server: Successfully Added");
		return status;

	}

	
	public synchronized String RemoveEvent(String eventId, String eventType) {

		String status = "failure";

		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
		
		LOGGER.info("Request Type: Remove Event");
		LOGGER.info("Parameter: EventID, EventType");
		
		if (montrealHashMap.get(eventType) != null) {
			innerhash = montrealHashMap.get(eventType);

			if (innerhash.get(eventId) != null) {
				innerhash = montrealHashMap.get(eventType);
				innerhash.remove(eventId);
				montrealHashMap.put(eventType, innerhash);
			
				status = "success";
				LOGGER.info("EventType :" + eventType +"\n" + " with Event ID : " + eventId +"\n");
				LOGGER.info("Removed Successfully from Montreal Server");
				status = "success";

			}
		} else {
			LOGGER.info("EventType : " + eventType + "Event ID :" + eventId + " does not exist");
			
			
			status = "failure";
		}

		return status;

	}

	
	public synchronized void ResultHashMap() {

		LOGGER.info(" Printing Hash Map Details");

		for (Map.Entry<String, Map<String, EventsData>> entry : montrealHashMap.entrySet()) {

			LOGGER.info("The event type              :" + entry.getKey());
			
			Map<String, EventsData> InnerHash = new HashMap<String, EventsData>();
			InnerHash = entry.getValue();

			if (InnerHash != null) {
				for (Map.Entry<String, EventsData> indexVal : InnerHash.entrySet()) {
					LOGGER.info("The event id                  :" + indexVal.getKey());
					EventsData event = new EventsData();

					event = indexVal.getValue();					
					LOGGER.info("Event booking capacity   :" + event.capacityOfBooking);
					LOGGER.info("Event booking seat left   :" + event.seatsLeft);
					LOGGER.info("Event booking seat booked :" + event.seatsFilled);
				}	
			}
		}
		System.out.println();
	}

	public synchronized String BookEvent(String customerId, String eventId, String eventType) {

		int count =0;
		String key = customerId + eventId.substring(6);
		
		if(customerBookingAccess.containsKey(key))
		{
			count = customerBookingAccess.get(key);
		}

		LOGGER.info("Request Type: Book Event");
		LOGGER.info("Request Parameter: Customer ID, Event ID, Event Type");
		
		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();

		String check = "failure";
		String city = eventId.substring(0, 3);
		System.out.println(" City book event is :" + city);
		String requestMessage = customerId + "," + eventId + "," + eventType + "," + "bookEvent";
	
		LOGGER.info("Map Size is : " + montrealHashMap.size()+"\n"+ "city  eventId :" + eventId +"\n"+"city  eventId :" + eventId);
		
		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;
		
		byte[] message1 = requestMessage.getBytes();
		byte[] message2 = requestMessage.getBytes();


		if (city.equalsIgnoreCase("mtl")) {

			if (montrealHashMap.get(eventType) != null) {
				innerhash = montrealHashMap.get(eventType);

				if (innerhash.containsKey(eventId)) {
					EventsData eventDetails = new EventsData();
					eventDetails = innerhash.get(eventId);
					List<String> customerList = new ArrayList<String>();
					customerList = eventDetails.registerdCustomer;

					if (eventDetails.seatsLeft > 0 && eventDetails.seatsFilled <= eventDetails.capacityOfBooking) {
						eventDetails.seatsFilled = eventDetails.seatsFilled + 1;
						eventDetails.seatsLeft = eventDetails.capacityOfBooking - eventDetails.seatsFilled;
						customerList.add(customerId);
						eventDetails.registerdCustomer = customerList;
						innerhash.put(eventId, eventDetails);
						montrealHashMap.put(eventType, innerhash);
						LOGGER.info("Customer:  customer ID  :" + customerId + "\n"+ " event type: "
								+ eventType +"\n"+ "event ID : " + eventId);
						LOGGER.info(innerhash);
						LOGGER.info("Event booked Successfully in Montreal Location");

						CustomerData custData = new CustomerData();
						custData.setcustID(customerId);
						custData.seteveID(eventId);
						custData.seteveTyp(eventType);
						CustomerDataList.add(custData);

						check = "success";

					} else {
						LOGGER.info("EventType :" + eventType + "Event ID : " + eventId
								+ "does not have enough seats left");
					
						check = "failure";
					}

				} else {
					LOGGER.info("EventType :" + eventType + " with Event ID : " + eventId + "does not exist");
					
					check = "failure";
				}

			} else {
				LOGGER.info("EventType :" + eventType + " does not exist");
				check = "failure";
			}
		}

		else if (city.equalsIgnoreCase("otw")) {

			LOGGER.info("Calling Ottawa Book Event from Montreal Location");

			if(count < 3) {
				try {

					socket1 = new DatagramSocket();

					InetAddress address = InetAddress.getByName("localhost");

					DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, 1112);
					socket1.send(request1);
					LOGGER.info("Request message sent from the client is : " + new String(request1.getData()));
					byte[] receive1 = new byte[1000];
					DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
					socket1.receive(reply1);

					String returnRes= new String(reply1.getData()).trim();					
					customerBookingAccess.put(key, count+1);
					LOGGER.info(" Oustide city count is : "+customerBookingAccess.get(key));
					if(returnRes.equalsIgnoreCase("t")) {
						check = "success";
					}else
						check ="failure";
					
					
				} catch (SocketException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

			}else{
				LOGGER.info("Increased the Maximum number of booking in outside cities");
				check = "failure";
			}

			LOGGER.info(check);

		}

		else if (city.equalsIgnoreCase("tor")) {
			LOGGER.info(" Calling Tornoto Book Event from Montreal Server");

			if(count < 3){

			try {

				socket2 = new DatagramSocket();
				InetAddress address = InetAddress.getByName("localhost");

				DatagramPacket request2 = new DatagramPacket(message2, message2.length, address, 1114);
				socket2.send(request2);
				byte[] receive2 = new byte[1000];
				DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);
				socket2.receive(reply2);
				LOGGER.info("Request message received by the client is : " + new String(reply2.getData()));
				String retRes = new String(reply2.getData()).trim();				
				customerBookingAccess.put(key, count+1);
				LOGGER.info(" Oustide city count is : "+customerBookingAccess.get(key));
				if(retRes.equalsIgnoreCase("t"))
					check = "success";
				else
					check = "failure";
				
			} catch (SocketException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}


			}else {
				LOGGER.info("Increased the Maximum number of booking in outside cities");
				check = "failure";
			}
		}
		LOGGER.info("Book event ends");
		return check;

	}
	
	public String GetBookingSchedule(String customerId) {
		String returnStr = "";
		
		
		LOGGER.info("Request Type: Get booking Schedule");
		LOGGER.info("Request Parameter: Customer ID");
		List<String> serverResponse = new ArrayList<String>();
		
		serverResponse = otherLocationBookingSchedule(customerId);
		for(String i : serverResponse)
		{
			returnStr = returnStr + i.trim();
		}

		returnStr = returnStr + getCurrentBookingSchedule(customerId);

		LOGGER.info(" The List of event for Customer ID : " +customerId+ ": "+returnStr);

		return returnStr;

	}



	private List<String> otherLocationBookingSchedule(String customerId) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		Runnable task1 = () -> {
			String s1 = sendToOttServer(customerId,null,null,"getBooking",1112);
			list.add(s1);

		};
		Runnable task2 = () -> {
			String  s2 =  sendToTorServer(customerId,null,null,"getBooking",1114);
			list.add(s2);

		};
		Thread t1 = new Thread(task1);
		Thread t2 = new Thread(task2);
		t1.start();
		t2.start();	

		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		return list;
	}

	public synchronized String getCurrentBookingSchedule(String customerId)  {

		List<CustomerData> lis = new ArrayList<CustomerData>();
		String returnStr = "";

		if (CustomerDataList.size() == 0) {
			LOGGER.info("no registered customers");
		} else {

			for (CustomerData c : CustomerDataList) {
				if (c.getcustID().equals(customerId)) {
					lis.add(c);
					returnStr = returnStr + c.getcustID() + " " + c.geteveTyp() + " " + c.geteveID() + ",";
				}
			}

		}
		return returnStr;
	}
	public synchronized boolean CancelEvent(String cutomerId, String eventId, String TypeOfEvent)   {
		boolean status = false;
		String eventType = "";
		String city = eventId.substring(0, 3);
		String check = "failure";
		
		LOGGER.info("Request Type: Cancel Event");
		LOGGER.info("Request Parameters: Customer ID, Event ID, Event Type");

		String requestMessage = cutomerId + "," + eventId +"," + "cancelEvent";
		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;
		byte[] message1 = requestMessage.getBytes();
		byte[] message2 = requestMessage.getBytes();
		String key =  cutomerId + eventId.substring(6);


		if (city.equalsIgnoreCase("mtl")) {

			List<CustomerData> customerLisClone = new ArrayList<CustomerData>();
			customerLisClone = CustomerDataList;

			for (int i = 0; i < customerLisClone.size(); i++) {
				CustomerData cd = new CustomerData();
				cd = customerLisClone.get(i);

				if (cd.getcustID().equalsIgnoreCase(cutomerId) && cd.geteveID().equalsIgnoreCase(eventId)) {
					eventType = cd.geteveTyp();
					CustomerDataList.remove(i);
					status = true;
				}

			}

			Map<String, EventsData> innerHash = new HashMap<String, EventsData>();
			innerHash = montrealHashMap.get(eventType);
			
			if(innerHash!=null) {
			for (Map.Entry<String, EventsData> e : innerHash.entrySet()) {
				LOGGER.info("The event id                  :" + e.getKey());
				EventsData event = new EventsData();
				event = e.getValue();

				List<String> customerRegistered = new ArrayList<String>();
				customerRegistered = event.getregisterdCustomer();
				if (customerRegistered.contains(cutomerId)) {
					check = "success";
					LOGGER.info(
							"Customer with customer ID : " + cutomerId + "cancelled the event with ID : " + eventId);
					LOGGER.info("Successfully cancelled the event");
					customerRegistered.remove(cutomerId);
					event.seatsFilled = event.seatsFilled - 1;
					event.seatsLeft = event.seatsLeft + 1;

					innerHash.put(eventId, event);
					montrealHashMap.put(eventType, innerHash);

				}				
				LOGGER.info("Event booking capacity   :" + event.capacityOfBooking);
				LOGGER.info("Event booking seat left   :" + event.seatsLeft);
				LOGGER.info("Event booking seat booked :" + event.seatsFilled);

			}
		}else
			LOGGER.info("No data is exist to cancel in the hash map");

		}

		else if (city.equalsIgnoreCase("otw"))
		{
		LOGGER.info(" Calling Ottawa Book Event from Montreal Server");

		int count =0;
		if(customerBookingAccess.containsKey(key)) {
			count = customerBookingAccess.get(key);
		}

			try {

				socket1 = new DatagramSocket();

				InetAddress address = InetAddress.getByName("localhost");

				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, 1112);
				socket1.send(request1);
				LOGGER.info("Request message sent : " + new String(request1.getData()));
				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				String ret = new String(reply1.getData()).trim();
				
				if(count >0)
					customerBookingAccess.put(key, count-1);
				if(ret.equalsIgnoreCase("t")) {
					status = true;
				}else
					status = false;

			} catch (SocketException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}




		}

		else if(city.equalsIgnoreCase("tor"))
		{
			LOGGER.info(" Calling Ottawa Book Event from Montreal Server");
			
			int count =0;
			if(customerBookingAccess.containsKey(key)) {
				count = customerBookingAccess.get(key);
			}

			try {

				socket2 = new DatagramSocket();

				InetAddress address = InetAddress.getByName("localhost");

				DatagramPacket request2 = new DatagramPacket(message1, message1.length, address, 1114);
				socket2.send(request2);
				LOGGER.info("Request message sent : " + new String(request2.getData()));

				byte[] receive2 = new byte[1000];
				DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);
				socket2.receive(reply2);
				String ret = new String(reply2.getData()).trim();
				
				
				if(count>0) 
					customerBookingAccess.put(key, count-1);
					
				if(ret.equalsIgnoreCase("t"))
					status = true;
				else
					status = false;

			} catch (SocketException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		
		return status;
	}

	public  String ListEventAvailability(String eventType)   {

		Map<String, EventsData> innerHash = new HashMap<String, EventsData>();
		List<String> availableEvents = new ArrayList<String>();
		String str = "";


		LOGGER.info("Request Type: List Event Availability");
		LOGGER.info("Request Parameter: Event Type");
		List<String> st = new ArrayList<String>();
		st = getListEventAvailability(eventType);
		
		for(String i : st)
		{
			str = str + i.trim();
		}


		str = str + getCurrentServerEventAvailability(eventType);

		System.out.println("The List of event :"+str);
		LOGGER.info(" The List of event for Event Type : " +eventType+ ": "+str);
		return str;
	}


	private List<String> getListEventAvailability(String eventType) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		Runnable task1 = () -> {
			String s1 = sendToOttServer(null,eventType,null,"listEventAvailability",1112);
			list.add(s1);

		};
		Runnable task2 = () -> {
			String  s2 =  sendToTorServer(null,eventType,null,"listEventAvailability",1114);
			list.add(s2);

		};


		Thread t1 = new Thread(task1);
		Thread t2 = new Thread(task2);

		t1.start();
		t2.start();


		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

	
		
		return list;
	}

	public synchronized String getCurrentServerEventAvailability(String eventType)
	{
		Map<String, EventsData> innerHash = new HashMap<String, EventsData>();
		List<String> availableEvents = new ArrayList<String>();
		String str = "";

		if (montrealHashMap.get(eventType) != null) {
			innerHash = montrealHashMap.get(eventType);

			for (Map.Entry<String, EventsData> e : innerHash.entrySet()) {
				
				EventsData event = new EventsData();
				event = e.getValue();

				LOGGER.info("Event id is                  :" + e.getKey());
				LOGGER.info("Event booking capacity is   :" + event.capacityOfBooking);
				LOGGER.info("Event booking seat left is   :" + event.seatsLeft);
				LOGGER.info("Event booking seat booked is :" + event.seatsLeft);

				
				if (event.seatsLeft >= 0) {
					str = str + e.getKey() + " " + event.seatsLeft + ",";
					availableEvents.add(str);
				}
			}

		} else {
			LOGGER.info("EventType : " + eventType + "is not avaliable");
		}

		return str;

	}



	public synchronized String sendToOttServer(String customerId,String eventType,String eventId,String requestType,int port)
	{
		String result = "";
		String returnString = "";
		try
		{
			if(requestType.equalsIgnoreCase("listEventAvailability"))
			{
				DatagramSocket socket1 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket1 = new DatagramSocket();

				String message = eventType + "," +requestType;
				byte[] message1 = new byte[1000];

				message1 = message.getBytes();
				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, port);
				socket1.send(request1);
				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				returnString = returnString +  new String(reply1.getData()).trim();
			}

			else if(requestType.equalsIgnoreCase("getBooking"))
			{
				DatagramSocket socket1 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket1 = new DatagramSocket();

				String message = customerId + "," +requestType;
				LOGGER.info("Message is : "+message);
				byte[] message1 = new byte[1000];

				message1 = message.getBytes();
				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, port);
				socket1.send(request1);
				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				returnString = returnString +  new String(reply1.getData()).trim();

			}

		}
		catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return returnString;

	}



	public synchronized String sendToTorServer(String customerId,String eventType,String eventId,String requestType,int port)
	{

		String result = "";
		String returnString = "";


		try
		{
			if(requestType.equalsIgnoreCase("listEventAvailability"))
			{
				DatagramSocket socket2 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket2 = new DatagramSocket();

				String message = eventType + "," +requestType;
				LOGGER.info("Message is : "+message);
				byte[] message1 = new byte[1000];
				byte[] message2 = new byte[1000];


				message2 = message.getBytes();


				byte[] receive2 = new byte[1000]; DatagramPacket request2 = new
					DatagramPacket(message2, message2.length, address, port);
				socket2.send(request2);
				DatagramPacket reply2 = new DatagramPacket(receive2,receive2.length);
				
				LOGGER.info("Request message sent from the client is : " + new String(request2.getData()));
				socket2.receive(reply2);
				returnString = returnString +  new String(reply2.getData()).trim();
			}


			else if(requestType.equalsIgnoreCase("getBooking"))
			{

				DatagramSocket socket2 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket2 = new DatagramSocket();

				String message = customerId + "," +requestType;
				byte[] message1 = new byte[1000];
				byte[] message2 = new byte[1000];


				message2 = message.getBytes();


				byte[] receive2 = new byte[1000]; DatagramPacket request2 = new
					DatagramPacket(message2, message2.length, address, port);
				socket2.send(request2);
				DatagramPacket reply2 = new DatagramPacket(receive2,receive2.length);

				LOGGER.info("Request message sent from the client is : " + new String(request2.getData()));
				socket2.receive(reply2);
				returnString = returnString +  new String(reply2.getData()).trim();

			}
		}
		catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return returnString;
	}
	@Override
	public String SwapEvent(String customerID, String newEventID, String newEventType, String oldEventID,
			String oldEventType) {
		
		MontrealServerImplementation MtlServer1 = new MontrealServerImplementation();
		String status = "failure";
		
		if(MtlServer1.CancelEvent(customerID, oldEventID, oldEventType)) {
			if(MtlServer1.BookEvent(customerID, newEventID, newEventType).equalsIgnoreCase("success")) {
				LOGGER.info("Event is successfully swapped");
				status = "success: Event is successfully swapped with new event id "+newEventID+" and new event type "+newEventType;
			}else {
				LOGGER.info("Couldn't able to book the new event : " +newEventID + "and event type "+ newEventType);
				MtlServer1.BookEvent(customerID, oldEventID, oldEventType);
				status = "failure :"+ "Couldn't able to book the new event, , Swap event failed : " +newEventID + "and event type "+ newEventType ;
			}
				
		}else {
			LOGGER.info("Event is couldn't able to swap because the event Id "+oldEventID+" and event type "+oldEventType +" doesnot exist");
			status = "failure :"+ "Event is couldn't able to swap because the event Id "+oldEventID+" and event type "+oldEventType +" doesnot exist to cancel";
		}
			
		
		return status;
	}
	
	
	


}
