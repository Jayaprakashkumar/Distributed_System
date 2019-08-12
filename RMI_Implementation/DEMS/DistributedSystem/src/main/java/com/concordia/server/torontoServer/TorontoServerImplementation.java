package com.concordia.server.torontoServer;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.concordia.clientInterface.ClientServerInterface;
import com.concordia.common.CustomerData;
import com.concordia.common.EventsData;


import java.io.IOException;
import java.net.*;

public class TorontoServerImplementation extends UnicastRemoteObject implements ClientServerInterface {
	private static Logger logger = LogManager.getLogger("tor");
	private static final long serialVersionUID = 1L;
	public static Map<String, Map<String, EventsData>> torHashMap = new HashMap<String, Map<String, EventsData>>();
	public static List<CustomerData> CustomerDataList = new ArrayList<CustomerData>();
	public static Map<String,Integer> customerBookingAccess = new HashMap<String,Integer>();

	public TorontoServerImplementation() throws RemoteException {
		super();
	}

	public static void main(String arg[]) {

		try {
			logger.info("Toronto Server is up and running.");
			
			TorontoServerImplementation torServer = new TorontoServerImplementation();
			Registry register = LocateRegistry.createRegistry(1114);
			String URL = "rmi://localhost:1114";

			register.rebind(URL, torServer);

			

			DatagramSocket socket = null;
			try {

				socket = new DatagramSocket(1114);

				while (true) {

					byte[] getServerResponse = new byte[1000];
					byte[] sendDataByte = new byte[1000];
					String recievedString = "";

					DatagramPacket request = new DatagramPacket(getServerResponse, getServerResponse.length);
					socket.receive(request);

					logger.info(" recieved message :"+recievedString);
					recievedString = new String(request.getData());

					logger.info(" recieved message1 :"+recievedString);
					String[] splitRecievedStr = recievedString.split(",");

					String eventChoice = splitRecievedStr[splitRecievedStr.length -1];
					eventChoice = eventChoice.trim();
					logger.info("Request Type :"+eventChoice);

					String outputResponse = "";
					 if(eventChoice.equalsIgnoreCase("bookEvent"))
					{
					
						outputResponse = torServer.BookEvent(splitRecievedStr[0], splitRecievedStr[1], splitRecievedStr[2]).equalsIgnoreCase("success")? "active" :"inactive";
						sendDataByte = outputResponse.getBytes();
					}else if(eventChoice.equalsIgnoreCase("getBooking"))
					{
						
						outputResponse  = torServer.getCurrentBookingSchedule(splitRecievedStr[0]);

						sendDataByte = outputResponse.getBytes();

					}
					 
					 else if(eventChoice.equalsIgnoreCase("listEventAvailability"))
					{
						outputResponse = torServer.getCurrentServerEventAvailability(splitRecievedStr[0]);
						sendDataByte = outputResponse.getBytes();
					}
				
					
					else if(eventChoice.equalsIgnoreCase("cancelEvent"))
					{
						

						torServer.CancelEvent(splitRecievedStr[0],splitRecievedStr[1], splitRecievedStr[2]);

						outputResponse = "Calling Cancelling Event";
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
			} finally {
				socket.close();
			}

		} catch (Exception e) {
			logger.info("Toronto Server Start up failed: " + e);
			e.printStackTrace();
		}

	}


	public synchronized String AddEvent(String eventId, String eventType, Integer bookingCapactiy) throws RemoteException {

		String status = "failure";
		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
		EventsData e = new EventsData();
		e.capacityOfBooking = bookingCapactiy;
		e.seatsLeft = bookingCapactiy;
		
		logger.info("Request Type: Add Event");
		logger.info("Parameter: EventID, EventType , Booking Capacity");
		logger.info("Toronto map size :" + torHashMap.size());

		if (!torHashMap.isEmpty()) {

			if (torHashMap.containsKey(eventType)) {
				innerhash = torHashMap.get(eventType);

				if (!innerhash.containsKey(eventId)) {
					innerhash.put(eventId, e);
					torHashMap.put(eventType, innerhash);
					status = "success";
				} else {
					innerhash.put(eventId, e);
					torHashMap.put(eventType, innerhash);
					status = "failure";
				}
			} else {
				innerhash.put(eventId, e);
				torHashMap.put(eventType, innerhash);
				status = "success";
			}

		} else {
			innerhash.put(eventId, e);
			torHashMap.put(eventType, innerhash);
			status = "success";

		}
		System.out.println("EventType : " + eventType + " with Event ID : " + eventId + "  added with capacity "
				+ bookingCapactiy);
		logger.info("Event Type : " + eventType+"\n" + "Event ID : " + eventId +"\n" + "Booking capacity "
				+ bookingCapactiy);
		logger.info("Toronto Server: Successfully Added");

		return status;

	}

	
	public synchronized String RemoveEvent(String eventId, String eventType) throws RemoteException {

		String status = "failure";

		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();


		
		logger.info("Request Type: Remove Event");
		logger.info("Parameter: EventID, EventType");
		
		
		if (torHashMap.get(eventType) != null) {
			innerhash = torHashMap.get(eventType);

			if (innerhash.get(eventId) != null) {
				innerhash = torHashMap.get(eventType);
				innerhash.remove(eventId);
				torHashMap.put(eventType, innerhash);
				logger.info("EventType :" + eventType +"\n" + " with Event ID : " + eventId +"\n");
				logger.info("Removed Successfully from Toronto Server");
				status = "success";
			}
		} else {
			logger.info("EventType : " + eventType + " with Event ID :" + eventId + " does not exist");
			status = "failure";
		}

		return status;

	}

	
	public synchronized void ResultHashMap() throws RemoteException {

		logger.info(" Printing Hash Map Details");

		for (Map.Entry<String, Map<String, EventsData>> entry : torHashMap.entrySet()) {

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
	}

	
	public synchronized String BookEvent(String customerId, String eventId, String eventType) throws RemoteException {

		int count =0;
		if(customerBookingAccess.containsKey(customerId))
		{
			count = customerBookingAccess.get(customerId);
		}

		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
		String check = "failure";
		
		logger.info("Request Type: Book Event");
		logger.info("Request Parameter: Customer ID, Event ID, Event Type");
		String city = eventId.substring(0, 3);

		String requestMessage = customerId + "," + eventId + "," + eventType + "," + "bookEvent";

		logger.info("Map Size is : " + torHashMap.size()+"\n"+ "city  eventId :" + eventId +"\n"+"city  eventId :" + eventId);
		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;


		byte[] message1 = requestMessage.getBytes();
		byte[] message2 = requestMessage.getBytes();

		if (city.equalsIgnoreCase("tor")) {

			if (torHashMap.get(eventType) != null) {
				innerhash = torHashMap.get(eventType);

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
						torHashMap.put(eventType, innerhash);

						logger.info("Customer:  customer ID  :" + customerId + "\n"+ " event type: "
								+ eventType +"\n"+ "event ID : " + eventId);
						logger.info(innerhash);
						logger.info("Event booked Successfully in Toronto Location");

						CustomerData custData = new CustomerData();
						custData.setcustID(customerId);
						custData.seteveID(eventId);
						custData.seteveTyp(eventType);
						CustomerDataList.add(custData);

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
					String retRes = new String(reply1.getData()).trim();
					customerBookingAccess.put(customerId,count + 1);
					logger.info(" Oustide city count is : "+customerBookingAccess.get(customerId));

					check = retRes.equalsIgnoreCase("active") ? "success" : "failure";

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



		else if (city.equalsIgnoreCase("otw")) {

			logger.info(" Calling Ottawa Book Event from Montreal");

			if (count < 3) {

				try {

					socket2 = new DatagramSocket();

					InetAddress address = InetAddress.getByName("localhost");

					DatagramPacket request2 = new DatagramPacket(message1, message1.length, address, 1112);
					socket2.send(request2);
					logger.info("Request message sent from the client is : " + new String(request2.getData()));

					byte[] receive1 = new byte[1000];
					DatagramPacket reply2 = new DatagramPacket(receive1, receive1.length);
					socket2.receive(reply2);
					logger.info("Request message received by the client is : " + new String(reply2.getData()));
					String returnRes = new String(reply2.getData()).trim();
					customerBookingAccess.put(customerId,count + 1);
					logger.info(" Oustide city count is : "+customerBookingAccess.get(customerId));


					check = returnRes.equalsIgnoreCase("active") ? "success" : "failure";

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


	
	public String GetBookingSchedule(String customerId) throws RemoteException {
	
		logger.info("Request Type: Get booking Schedule");
		logger.info("Request Parameter: Customer ID");
		String returnStr = "";
		List<String> st = new ArrayList<String>();
		
		st = otherLocationBookingSchedule(customerId);

		for(String i : st)
		{
			
			returnStr = returnStr + i.trim();
			
		}


		returnStr = returnStr + getCurrentBookingSchedule(customerId);

		logger.info(" The List of event for Customer ID : " +customerId+ ": "+returnStr);


		return returnStr;

	}



	private List<String> otherLocationBookingSchedule(String customerId) {
		// TODO Auto-generated method stub
		
		List<String> list = new ArrayList<String>();
		Runnable task1 = () -> {
			String s1 = sendToMtlServer(customerId,null,null,"getBooking",1111);
			list.add(s1);

		};
		Runnable task2 = () -> {
			String  s2 =  sendToOttServer(customerId,null,null,"getBooking",1112);
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






	
	public synchronized void CancelEvent(String cutomerId, String eventId, String TypeOfEvent) throws RemoteException {

		logger.info("Request Type: Cancel Event");
		logger.info("Request Parameters: Customer ID, Event ID, Event Type");
		String eventType = "";
		String city = eventId.substring(0, 3);

		String requestMessage = cutomerId + "," + eventId +"," + "cancelEvent";
		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;
		byte[] message1 = requestMessage.getBytes();
		byte[] message2 = requestMessage.getBytes();

		if (city.equalsIgnoreCase("tor")) {

			List<CustomerData> customerLisClone = new ArrayList<CustomerData>();
			customerLisClone = CustomerDataList;

			for (int i = 0; i < customerLisClone.size(); i++) {
				CustomerData cd = new CustomerData();
				cd = customerLisClone.get(i);

				if (cd.getcustID().equalsIgnoreCase(cutomerId) && cd.geteveID().equalsIgnoreCase(eventId)) {
					eventType = cd.geteveTyp();
					CustomerDataList.remove(i);

				}

			}

			Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
			innerhash = torHashMap.get(eventType);
			
			if(innerhash.entrySet()!=null) {

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
					torHashMap.put(eventType, innerhash);

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




			} catch (SocketException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		else if(city.equalsIgnoreCase("otw"))
		{

			logger.info(" Calling Ottawa Book Event from Montreal");



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




			} catch (SocketException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}


		}

	}

	public synchronized void serverCommunication()
	{
		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;

		byte[] message1 = "Toronoto server is making remote calls to Montreal server".getBytes();
		byte[] message2 = "Toronoto server is making remote calls to Ottawa server".getBytes();

		try {

			socket1 = new DatagramSocket();
			socket2 = new DatagramSocket();
			InetAddress address = InetAddress.getByName("localhost");

			DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, 1111);
			socket1.send(request1);
			logger.info("Request message sent from the client is : " + new String(request1.getData()));

			byte[] receive1 = new byte[1000];
			DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
			socket1.receive(reply1);
			logger.info("Request message received by the client is : " + new String(reply1.getData()));



			byte[] receive2 = new byte[1000]; DatagramPacket request2 = new
					DatagramPacket(message2, message2.length, address, 1112);
			socket2.send(request2);
			DatagramPacket reply2 = new DatagramPacket(receive2,receive2.length);

			logger.info("Request message sent from the client is : " + new String(request2.getData()));
			socket2.receive(reply2);
			logger.info("Request message received by the client is : " + new String(reply2.getData()));

		} catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			socket1.close();
			socket2.close();
		}
	}

	public synchronized String serverCommunication(String customerId,String eventType,String eventId,String requestType)
	{

		String result = "";
		String returnString = "";
		logger.info("Calling server communication inside toronto");

		try {

			if(requestType.equalsIgnoreCase("listEventAvailability"))
			{
				DatagramSocket socket1 = null;
				DatagramSocket socket2 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket1 = new DatagramSocket();
				socket2 = new DatagramSocket();

				String message = eventType + "," +requestType;
				logger.info("Message is : "+message);

				byte[] message1 = new byte[1000];
				byte[] message2 = new byte[1000];

				message1 = message.getBytes();
				message2 = message.getBytes();


				logger.info("Reuqest Type for server to server communication is :"+requestType);
				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, 1112);
				socket1.send(request1);
				logger.info("Request message sent from the client is : " + new String(request1.getData()));

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				logger.info("Request message received by the client is : " + new String(reply1.getData()));
				returnString = returnString +  new String(reply1.getData()).trim();


				byte[] receive2 = new byte[1000]; DatagramPacket request2 = new
					DatagramPacket(message2, message2.length, address, 1111);
				socket2.send(request2);
				DatagramPacket reply2 = new DatagramPacket(receive2,receive2.length);

				logger.info("Request message sent from the client is : " + new String(request2.getData()));
				socket2.receive(reply2);
				logger.info("Request message received by the client is : " + new String(reply2.getData()));
				returnString = returnString +  new String(reply2.getData()).trim();

			}

			else if (requestType.equalsIgnoreCase("getBooking"))
			{

				DatagramSocket socket1 = null;
				DatagramSocket socket2 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket1 = new DatagramSocket();
				socket2 = new DatagramSocket();

				String message = customerId + "," +requestType;
				logger.info("Message is : "+message);

				byte[] message1 = new byte[1000];
				byte[] message2 = new byte[1000];

				message1 = message.getBytes();
				message2 = message.getBytes();


				logger.info("Reuqest Type for server to server communication is :"+requestType);
				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, 1111);
				socket1.send(request1);
				logger.info("Request message sent from the client is : " + new String(request1.getData()));

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				logger.info("Request message received by the client is : " + new String(reply1.getData()));
				returnString = returnString +  new String(reply1.getData()).trim();


				byte[] receive2 = new byte[1000]; DatagramPacket request2 = new
					DatagramPacket(message2, message2.length, address, 1112);
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
		logger.info(" return string from list availiability :"+returnString);
		return returnString;
	}
	
	public String ListEventAvailability(String eventType) throws RemoteException {

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


		str = str + getCurrentServerEventAvailability(eventType);
		System.out.println("The List of event :"+str);
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
			String  s2 =  sendToOttServer(null,eventType,null,"listEventAvailability",1112);
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
		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
		List<String> availableEvents = new ArrayList<String>();
		String s = "";

		if (torHashMap.get(eventType) != null) {
			innerhash = torHashMap.get(eventType);

			for (Map.Entry<String, EventsData> e : innerhash.entrySet()) {
				
				logger.info("The event id is                  :" + e.getKey());
				EventsData event = new EventsData();
				event = e.getValue();
				logger.info("The event booking capacity is   :" + event.capacityOfBooking);
				logger.info("The event booking seat left is   :" + event.seatsLeft);
				logger.info("The event booking seat booked is :" + event.seatsLeft);

				if (event.seatsLeft > 0) {
					s = s + e.getKey() + " " + event.seatsLeft + ",";
					availableEvents.add(s);
				}
			}

		} else {
			logger.info("EventType : " + eventType + "is not avaliable");
		}



		return s;

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


				logger.info("Reuqest Type for server to server communication is :"+requestType);
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


				logger.info("Reuqest Type for server to server communication is :"+requestType);
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



	public synchronized String sendToOttServer(String customerId,String eventType,String eventId,String requestType,int port)
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

}
