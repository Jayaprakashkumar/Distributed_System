package com.concordia.server.ottawaServer;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Endpoint;

import com.concordia.clientServerInterface.ClientInterface;
//import com.concordia.clientInterface.ClientServerInterface;
import com.concordia.common.CustomerData;
import com.concordia.common.EventsData;
import com.concordia.server.MontrealServer.MontrealServerImplementation;


import java.io.IOException;
import java.net.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

@WebService(endpointInterface = "com.concordia.clientServerInterface.ClientInterface")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class OttawaServerImplementation implements ClientInterface, Runnable{
	private static Logger logger = LogManager.getLogger("ottawa");

	private static final long serialVersionUID = 1L;
	public static Map<String, Map<String, EventsData>> ottawaHashMap = new HashMap<String, Map<String, EventsData>>();
	public static List<CustomerData> CustomerDataList = new ArrayList<CustomerData>();
	public static Map<String,Integer> customerBookingAccess = new HashMap<String,Integer>();

	public OttawaServerImplementation()    {
		super();
	}
    
	public static void main(String args[]) {

		try {
			Thread threadSocket = new Thread(new OttawaServerImplementation());
			threadSocket.start();
			
			OttawaServerImplementation ottaServer = new OttawaServerImplementation();
			Endpoint endpoint = Endpoint.publish("http://localhost:1112/Ottawa", ottaServer);
			
			logger.info("In ottawa server");
			logger.info("Ottawa Server is up and running.");
			
		} catch (Exception e) {
			logger.info("Ottawa Server Start up failed: " + e);
			e.printStackTrace();
		}

	}


	public synchronized String AddEvent(String eventId, String eventType, int bookingCapactiy)    {

		String status = "failure";

		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
		EventsData e = new EventsData();
		e.capacityOfBooking = bookingCapactiy;
		e.seatsLeft = bookingCapactiy;
		
		logger.info("Request Type: Add Event");
		logger.info("Parameter: EventID, EventType , Booking Capacity");
		logger.info("ottawa map size :" + ottawaHashMap.size());

		if (!ottawaHashMap.isEmpty()) {

			if (ottawaHashMap.containsKey(eventType)) {
				innerhash = ottawaHashMap.get(eventType);

				if (!innerhash.containsKey(eventId)) {
					innerhash.put(eventId, e);
					ottawaHashMap.put(eventType, innerhash);
					status = "success";
				} else {
					innerhash.put(eventId, e);
					ottawaHashMap.put(eventType, innerhash);
					status = "failure";
				}
			} else {
				innerhash.put(eventId, e);
				ottawaHashMap.put(eventType, innerhash);
				status = "success";
			}

		} else {
			innerhash.put(eventId, e);
			ottawaHashMap.put(eventType, innerhash);
			status = "success";

		}
		
		logger.info("Event Type : " + eventType+"\n" + "Event ID : " + eventId +"\n" + "Booking capacity "
				+ bookingCapactiy);
		logger.info("Ottawa Server: Successfully Added");

		return status;

	}

	public void run() {
		OttawaServerImplementation ottaServer = new OttawaServerImplementation();
		DatagramSocket socket = null;
		try {

			socket = new DatagramSocket(1112);
			

			while (true) {

				byte[] getServerRespnse = new byte[1000];

				String recievedString = "";
				DatagramPacket request = new DatagramPacket(getServerRespnse, getServerRespnse.length);
				socket.receive(request);
				logger.info(" recieved message :"+recievedString);
				recievedString = new String(request.getData());
				logger.info(" recieved message1 :"+recievedString);
				String[] splitRecievedStr = recievedString.split(",");

				String eventChoice = splitRecievedStr[splitRecievedStr.length -1];
				eventChoice = eventChoice.trim();

				logger.info("Request Type :"+eventChoice);
				String outputResponse = "";


				byte[] sendDataByte = new byte[1000];
				
				if(eventChoice.equalsIgnoreCase("bookEvent"))
				{
					if(ottaServer.BookEvent(splitRecievedStr[0], splitRecievedStr[1], splitRecievedStr[2]).equalsIgnoreCase("success"))
					{
						outputResponse = "t";	
					}
					else
					{
						outputResponse = "f";

					}
					
					
					sendDataByte = outputResponse.getBytes();
				}
				else if(eventChoice.equalsIgnoreCase("getBooking"))
				{
					outputResponse  = ottaServer.getCurrentBookingSchedule(splitRecievedStr[0]);

					sendDataByte = outputResponse.getBytes();

				}
				else if(eventChoice.equalsIgnoreCase("cancelEvent"))
				{
					
					boolean result =  ottaServer.CancelEvent(splitRecievedStr[0],splitRecievedStr[1], splitRecievedStr[2]);
					if(result) {
						outputResponse ="t";
					}else {
						outputResponse = "f";
					}
					sendDataByte = outputResponse.getBytes();

				}


				else if(eventChoice.equalsIgnoreCase("listEventAvailability"))
				{
					logger.info("If case inside ottawa server is true");
					outputResponse = ottaServer.currentServerEventAvailability(splitRecievedStr[0]);
					sendDataByte = outputResponse.getBytes();
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
		}

	}
	public synchronized String RemoveEvent(String eventId, String eventType)    {

		String status = "failure";

		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
		
		logger.info("Request Type: Remove Event");
		logger.info("Parameter: EventID, EventType");

		if (ottawaHashMap.get(eventType) != null) {
			innerhash = ottawaHashMap.get(eventType);

			if (innerhash.get(eventId) != null) {
				innerhash = ottawaHashMap.get(eventType);
				innerhash.remove(eventId);
				ottawaHashMap.put(eventType, innerhash);
				status = "success";
				logger.info("EventType :" + eventType +"\n" + " with Event ID : " + eventId +"\n");
				logger.info("Successfully removed from Ottawa server");
				status = "success";
			}
		} else {
			logger.info("EventType : " + eventType + " with Event ID :" + eventId + " does not exist");
			status = "failure";
		}

		return status;

	}


	
	public synchronized String BookEvent(String customerId, String eventId, String eventType)    {

		int count =0;
		String key = customerId + eventId.substring(6);
		if(customerBookingAccess.containsKey(key))
		{
			count = customerBookingAccess.get(key);
		}
		
		logger.info("Request Type: Book Event");
		logger.info("Request Parameter: Customer ID, Event ID, Event Type");

		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();

		String check = "failure";
		

		String city = eventId.substring(0, 3);
		
		String requestMessage = customerId + "," + eventId + "," + eventType + "," + "bookEvent";
		logger.info("Map Size is : " + ottawaHashMap.size()+"\n"+ "city  eventId :" + eventId +"\n"+"city  eventId :" + eventId);

		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;


		byte[] message1 = requestMessage.getBytes();
		byte[] message2 = requestMessage.getBytes();


		if (city.equalsIgnoreCase("otw")) {

			if (ottawaHashMap.get(eventType) != null) {
				innerhash = ottawaHashMap.get(eventType);

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
						ottawaHashMap.put(eventType, innerhash);

						logger.info("Customer:  customer ID  :" + customerId + "\n"+ " event type: "
								+ eventType +"\n"+ "event ID : " + eventId);
						logger.info(innerhash);
						logger.info("Event booked Successfully in Ottawa Location");

						CustomerData cd = new CustomerData();
						cd.setcustID(customerId);
						cd.seteveID(eventId);
						cd.seteveTyp(eventType);
						CustomerDataList.add(cd);

						check = "success";

					} else {
						logger.info("EventType :" + eventType + " with Event ID : " + eventId
								+ "does not have enough seats left");
						check = "failure";
					}

				} else {
					logger.info("EventType :" + eventType + " with Event ID : " + eventId + "does not exist");
					check = "failure";
				}

			} else {
				logger.info("EventType :" + eventType + " does not exist");
				check = "failure";
			}
		} else if (city.equalsIgnoreCase("mtl")) {

			if(count < 3) {
				try {

					logger.info(" Calling Montreal Book Event from Ottawa");

					socket1 = new DatagramSocket();

					InetAddress address = InetAddress.getByName("localhost");

					DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, 1111);
					socket1.send(request1);
					logger.info("Request message sent from the client is : " + new String(request1.getData()));

					byte[] receive1 = new byte[1000];
					DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
					socket1.receive(reply1);
					logger.info("Request message received by the client is : " + new String(reply1.getData()));
					String returnRes = new String(reply1.getData()).trim();
					customerBookingAccess.put(key, count+1);
					logger.info(" Oustide city count is : "+customerBookingAccess.get(key));
					if(returnRes.equalsIgnoreCase("t")) {
						check = "success";
					}else
						check ="failure";

				} catch (SocketException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

			}else {
				logger.info("Increased the Maximum number of booking in outside cities");
				check = "failure";
			}


		}

		else if (city.equalsIgnoreCase("tor")) {

			logger.info(" Calling Tornoto Book Event from Ottawa");

			if (count < 3) {

				try {

					socket2 = new DatagramSocket();
					InetAddress address = InetAddress.getByName("localhost");
					
					DatagramPacket request2 = new DatagramPacket(message1, message1.length, address, 1114);
					socket2.send(request2);
					logger.info("Request message sent from the client is : " + new String(request2.getData()));

					byte[] receive2 = new byte[1000];
					DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);
					socket2.receive(reply2);
					logger.info("Request message received by the client is : " + new String(reply2.getData()));
					String retRes = new String(reply2.getData()).trim();
					customerBookingAccess.put(key, count+1);
					logger.info(" Oustide city count is : "+customerBookingAccess.get(key));
					if(retRes.equalsIgnoreCase("t")) {
						check = "success";
					}else
						check ="failure";
					

				} catch (SocketException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

			}else {
				logger.info("Increased the Maximum number of booking in outside cities");
				check = "failure";
			}

			
			
		}

		return check;
	}

	
	
	public String GetBookingSchedule(String customerId)    {

		logger.info("Request Type: Get booking Schedule");
		logger.info("Request Parameter: Customer ID");
		String returnStr = "";
		List<String> serverResponse = new ArrayList<String>();
		
		serverResponse = otherLocBookingSchedules(customerId);
		
		logger.info("st list size  : "+serverResponse.size());

		for(String i : serverResponse)
		{
			returnStr = returnStr + i.trim();
		}

		returnStr = returnStr + getCurrentBookingSchedule(customerId);

		logger.info(" The List of event for Customer ID : " +customerId+ ": "+returnStr);


		return returnStr;

	}


	private List<String> otherLocBookingSchedules(String customerId) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		Runnable task1 = () -> {
			String s1 = sendToMtlServer(customerId,null,null,"getBooking",1111);
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

	public synchronized String getCurrentBookingSchedule(String customerId) {

		List<CustomerData> lis = new ArrayList<CustomerData>();
		String returnStr = "";

		if (CustomerDataList.size() == 0) {
			logger.info("no registered customers");
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


	
	public synchronized boolean CancelEvent(String cutomerId, String eventId, String TypeOfEvent)    {
		boolean status = false;
		String eventType = "";
		String city = eventId.substring(0, 3);
		
		logger.info("Request Type: Cancel Event");
		logger.info("Request Parameters: Customer ID, Event ID, Event Type");

		String requestMessage = cutomerId + "," + eventId +"," + "cancelEvent";
		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;
		byte[] message1 = requestMessage.getBytes();
		byte[] message2 = requestMessage.getBytes();
		String key =  cutomerId + eventId.substring(6);

		if (city.equalsIgnoreCase("otw")) {

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

			Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
			innerhash = ottawaHashMap.get(eventType);

			if(innerhash!=null) {
			for (Map.Entry<String, EventsData> e : innerhash.entrySet()) {
				logger.info("The event id is                  :" + e.getKey());
				EventsData event = new EventsData();
				event = e.getValue();

				List<String> registerdCustomer = new ArrayList<String>();
				registerdCustomer = event.getregisterdCustomer();

				if (registerdCustomer.contains(cutomerId)) {

					logger.info(
							"Customer with customer ID : " + cutomerId + "cancelled the event with ID : " + eventId);
					registerdCustomer.remove(cutomerId);
					event.seatsFilled = event.seatsFilled - 1;
					event.seatsLeft = event.seatsLeft + 1;

					innerhash.put(eventId, event);
					ottawaHashMap.put(eventType, innerhash);

				}

				logger.info("The event booking capacity is   :" + event.capacityOfBooking);
				logger.info("The event booking seat left is   :" + event.seatsLeft);
				logger.info("The event booking seat booked is :" + event.seatsLeft);

			}
			}

		}

		else if (city.equalsIgnoreCase("mtl"))
		{
			logger.info(" Calling Ottawa Book Event from Montreal");

			int count =0;
			if(customerBookingAccess.containsKey(key)) {
				count = customerBookingAccess.get(key);
			}

			try {

				socket1 = new DatagramSocket();

				InetAddress address = InetAddress.getByName("localhost");

				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, 1111);
				socket1.send(request1);
				logger.info("Request message sent from the client is : " + new String(request1.getData()));

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				logger.info("Request message received by the client is : " + new String(reply1.getData()));
				String ret = new String(reply1.getData()).trim();
				logger.info("ret is :"+ret);
				
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

			logger.info(" Calling Ottawa Book Event from Montreal");

			int count =0;
			if(customerBookingAccess.containsKey(key)) {
				count = customerBookingAccess.get(key);
			}


			try {

				socket2 = new DatagramSocket();

				InetAddress address = InetAddress.getByName("localhost");

				DatagramPacket request2 = new DatagramPacket(message1, message1.length, address, 1114);
				socket2.send(request2);
				logger.info("Request message sent from the client is : " + new String(request2.getData()));

				byte[] receive2 = new byte[1000];
				DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);
				socket2.receive(reply2);
				logger.info("Request message received by the client is : " + new String(reply2.getData()));
				String ret = new String(reply2.getData()).trim();
				logger.info("ret is :"+ret);


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

	
	public String ListEventAvailability(String eventType)    {

		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
		List<String> availableEvents = new ArrayList<String>();
		String str = "";


		logger.info("Request Type: List Event Availability");
		logger.info("Request Parameter: Event Type");
		List<String> serverList = new ArrayList<String>();
		
		serverList = getListEventAvailability(eventType);
		
		for(String i : serverList)
		{
			str = str + i.trim();
		}

		str = str + currentServerEventAvailability(eventType);
		logger.info(" The List of event for Event Type : " +eventType+ ": "+str);
		return str;
	}

	

	private List<String> getListEventAvailability(String eventType) {
		// TODO Auto-generated method stub
		
		List<String> list = new ArrayList<String>();
		
		Runnable task1 = () -> {
			String s1 = sendToMtlServer(null,eventType,null,"listEventAvailability",1111);
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

	public synchronized String currentServerEventAvailability(String eventType)
	{
		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
		List<String> availableEvents = new ArrayList<String>();
		String returnString= "";

		if (ottawaHashMap.get(eventType) != null) {
			innerhash = ottawaHashMap.get(eventType);

			for (Map.Entry<String, EventsData> e : innerhash.entrySet()) {
				logger.info("The event id is                  :" + e.getKey());
				EventsData event = new EventsData();
				event = e.getValue();
				logger.info("The event booking capacity is   :" + event.capacityOfBooking);
				logger.info("The event booking seat left is   :" + event.seatsLeft);
				logger.info("The event booking seat booked is :" + event.seatsLeft);

				if (event.seatsLeft >= 0) {
					returnString = returnString + e.getKey() + " " + event.seatsLeft + ",";
					availableEvents.add(returnString);
				}
			}

		} else {
			logger.info("EventType : " + eventType + "is not avaliable");
		}

		return returnString;
	}


	public synchronized String sendToMtlServer(String customerId,String eventType,String eventId,String requestType,int port)
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
				logger.info("Message is : "+message);

				byte[] message1 = new byte[1000];

				message1 = message.getBytes();
				
				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, port);
				socket1.send(request1);
				logger.info("Request message sent from the client is : " + new String(request1.getData()));

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				logger.info("Request message received by the client is : " + new String(reply1.getData()));
				returnString = returnString +  new String(reply1.getData()).trim();
			}

			else if(requestType.equalsIgnoreCase("getBooking"))
			{

				DatagramSocket socket1 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket1 = new DatagramSocket();

				String message = customerId + "," +requestType;
				logger.info("Message is : "+message);

				byte[] message1 = new byte[1000];

				message1 = message.getBytes();

				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, port);
				socket1.send(request1);
				logger.info("Request message sent from the client is : " + new String(request1.getData()));

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				logger.info("Request message received by the client is : " + new String(reply1.getData()));
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
				logger.info("Message is : "+message);

				byte[] message1 = new byte[1000];
				byte[] message2 = new byte[1000];


				message2 = message.getBytes();


				byte[] receive2 = new byte[1000]; DatagramPacket request2 = new
					DatagramPacket(message2, message2.length, address, port);
				socket2.send(request2);
				DatagramPacket reply2 = new DatagramPacket(receive2,receive2.length);

				logger.info("Request message sent from the client is : " + new String(request2.getData()));
				socket2.receive(reply2);
				logger.info("Request message received by the client is : " + new String(reply2.getData()));
				returnString = returnString +  new String(reply2.getData()).trim();
			}


			else if(requestType.equalsIgnoreCase("getBooking"))
			{


				DatagramSocket socket2 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket2 = new DatagramSocket();

				String message = customerId + "," +requestType;
				logger.info("Message is : "+message);

				byte[] message1 = new byte[1000];
				byte[] message2 = new byte[1000];


				message2 = message.getBytes();


				byte[] receive2 = new byte[1000]; DatagramPacket request2 = new
					DatagramPacket(message2, message2.length, address, port);
				socket2.send(request2);
				DatagramPacket reply2 = new DatagramPacket(receive2,receive2.length);

				logger.info("Request message sent from the client is : " + new String(request2.getData()));
				socket2.receive(reply2);
				logger.info("Request message received by the client is : " + new String(reply2.getData()));
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

	
	public synchronized void ResultHashMap()  {

		logger.info(" Printing Map Details");

		for (Map.Entry<String, Map<String, EventsData>> entry : ottawaHashMap.entrySet()) {

			logger.info("The event type is              :" + entry.getKey());
			Map<String, EventsData> innerHash = new HashMap<String, EventsData>();
			innerHash = entry.getValue();

			if (innerHash != null) {
				for (Map.Entry<String, EventsData> indexVal : innerHash.entrySet()) {
					logger.info("The event id is                  :" + indexVal.getKey());
					EventsData event = new EventsData();
					event = indexVal.getValue();
					logger.info("The event booking capacity is   :" + event.capacityOfBooking);
					logger.info("The event booking seat left is   :" + event.seatsLeft);
					logger.info("The event booking seat booked is :" + event.seatsFilled);
				}

			}
		}
		
		System.out.println();
	}

	@Override
	public  String SwapEvent(String customerID, String newEventID, String newEventType, String oldEventID,
			String oldEventType) {
		OttawaServerImplementation OttServer1 = new OttawaServerImplementation();
		String status = "failure";
		
		if(OttServer1.CancelEvent(customerID, oldEventID, oldEventType)) {
			if(OttServer1.BookEvent(customerID, newEventID, newEventType).equalsIgnoreCase("success")) {
				logger.info("Event is successfully swapped");
				status = "success: Event is successfully swapped with new event id "+newEventID+" and new event type "+newEventType;
			}else {
				logger.info("Couldn't able to book the new event : " +newEventID + "and event type "+ newEventType);
				OttServer1.BookEvent(customerID, oldEventID, oldEventType);
				status = "failure :"+ "Couldn't able to book the new event, Swap event failed : " +newEventID + "and event type "+ newEventType ;
			}
				
		}else {
			logger.info("Event is couldn't able to swap because the event Id "+oldEventID+" and event type "+oldEventType +" doesnot exist");
			status = "failure :"+ "Event is couldn't able to swap because the event Id "+oldEventID+" and event type "+oldEventType +" doesnot exist to cancel";
		}
			
		
		
		return status;
	}


}
