package servers.rm3.server;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

//import com.concordia.clientInterface.ClientServerInterface;
import servers.rm3.common.CustomerData;
import servers.rm3.common.EventsData;
import utility.Constants;
import frontendImpl.frontendApp;
import frontendImpl.frontendAppHelper;
import frontendImpl.frontendAppPOA;

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

public class OttawaServerImplementation {
	private static Logger logger = LogManager.getLogger("ottawa");

	private static final long serialVersionUID = 1L;
	public static Map<String, Map<String, EventsData>> ottawaHashMap = new HashMap<String, Map<String, EventsData>>();
	public static List<CustomerData> CustomerDataList = new ArrayList<CustomerData>();
	public static Map<String, Integer> customerBookingAccess = new HashMap<String, Integer>();
	private org.omg.CORBA.ORB orbOtw;

	public OttawaServerImplementation() throws Exception {
		super();

		Runnable udp_task = new Runnable() {
			public void run() {
				udp_packet_recv();
			}
		};

		Thread thread = new Thread(udp_task);
		thread.start();
	}
//	
//	public void setORB(org.omg.CORBA.ORB orb_val) {
//		orbOtw = orb_val;
//	}
//	public void shutdown() {
//		orbOtw.shutdown(false);
//	}     

//	public static void main(String args[]) {
//
//		try {
//			
//			OttawaServerImplementation ottaServer = new OttawaServerImplementation();
//			DatagramSocket socket = null;
//			try {
//
//				socket = new DatagramSocket(Constants.RM3_OTW_PORT_NO);
//				
//
//				while (true) {
//
//					byte[] getServerRespnse = new byte[1000];
//
//					String recievedString = "";
//					DatagramPacket request = new DatagramPacket(getServerRespnse, getServerRespnse.length);
//					socket.receive(request);
//					logger.info(" recieved message :"+recievedString);
//					recievedString = new String(request.getData());
//					logger.info(" recieved message1 :"+recievedString);
//					String[] splitRecievedStr = recievedString.split(",");
//
//					String eventChoice = splitRecievedStr[splitRecievedStr.length -1];
//					eventChoice = eventChoice.trim();
//
//					logger.info("Request Type :"+eventChoice);
//					String outputResponse = "";
//
//
//					byte[] sendDataByte = new byte[1000];
//					
//					if(eventChoice.equalsIgnoreCase("bookEvent"))
//					{
//					
//						if(ottaServer.bookEvent(splitRecievedStr[0], splitRecievedStr[1], splitRecievedStr[2]))
//						{
//							outputResponse = "t";	
//						}
//						else
//						{
//							outputResponse = "f";
//
//						}
//						
//						
//						sendDataByte = outputResponse.getBytes();
//					}
//					else if(eventChoice.equalsIgnoreCase("getBooking"))
//					{
//						outputResponse  = ottaServer.getCurrentBookingSchedule(splitRecievedStr[0]);
//
//						sendDataByte = outputResponse.getBytes();
//
//					}
//					else if(eventChoice.equalsIgnoreCase("cancelEvent"))
//					{
//						
//						boolean result =  ottaServer.cancelEvent(splitRecievedStr[0],splitRecievedStr[1], splitRecievedStr[2]);
//						if(result) {
//							outputResponse ="t";
//						}else {
//							outputResponse = "f";
//						}
//						sendDataByte = outputResponse.getBytes();
//
//					}
//
//
//					else if(eventChoice.equalsIgnoreCase("listEventAvailability"))
//					{
//						logger.info("If case inside ottawa server is true");
//						outputResponse = ottaServer.currentServerEventAvailability(splitRecievedStr[0]);
//						sendDataByte = outputResponse.getBytes();
//					}					
//
//					else
//					{
//						sendDataByte = "".getBytes();
//					}
//					DatagramPacket reply = new DatagramPacket(sendDataByte, sendDataByte.length, request.getAddress(),
//							request.getPort());
//					socket.send(reply);
//				}
//
//			} catch (SocketException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

//			Thread threadSocket = new Thread(new OttawaServerImplementation());
//			threadSocket.start();
//			org.omg.CORBA.ORB orb = ORB.init(args, null);
//			
//			// get reference to rootpoa &amp; activate
//			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
//			rootpoa.the_POAManager().activate();
//			OttawaServerImplementation ottaServer = new OttawaServerImplementation();
//			ottaServer.setORB(orb);
//			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(ottaServer);
//			frontendApp href = frontendAppHelper.narrow(ref);
//			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
//			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
//			NameComponent path[] = ncRef.to_name("Ottawa");
//			ncRef.rebind(path, href);
//			
//			logger.info("In ottawa server");

//			int portNo = 1112;
//
//			OttawaServerImplementation ottaServer = new OttawaServerImplementation();
//			Registry register = LocateRegistry.createRegistry(portNo);
//			String URL = "rmi://localhost:1112";
//
//			register.rebind(URL, ottaServer);
//			logger.info("Ottawa Server is up and running.");
//			orb.run();

//		} catch (Exception e) {
//			logger.info("Ottawa Server Start up failed: " + e);
//			e.printStackTrace();
//		}
//
//	}

	public synchronized boolean addEvent(String eventId, String eventType, int bookingCapactiy, String managerID) {

		boolean status = false;

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
					status = true;
				} else {
					innerhash.put(eventId, e);
					ottawaHashMap.put(eventType, innerhash);
					status = true;
				}
			} else {
				innerhash.put(eventId, e);
				ottawaHashMap.put(eventType, innerhash);
				status = true;
			}

		} else {
			innerhash.put(eventId, e);
			ottawaHashMap.put(eventType, innerhash);
			status = true;

		}
		logger.info("Event Type : " + eventType + "\n" + "Event ID : " + eventId + "\n" + "Booking capacity "
				+ bookingCapactiy);
		logger.info("Ottawa Server: Successfully Added");

		return status;

	}

	public void udp_packet_recv() {
//		OttawaServerImplementation ottaServer;
		try {
//			ottaServer = new OttawaServerImplementation();
			DatagramSocket socket = null;
			try {

				socket = new DatagramSocket(Constants.RM3_OTW_PORT_NO);
				logger.info("RM 3: Ottawa UDP Server Started on port: " + Constants.RM3_OTW_PORT_NO);

				while (true) {

					byte[] getServerRespnse = new byte[1000];

					String recievedString = "";
					DatagramPacket request = new DatagramPacket(getServerRespnse, getServerRespnse.length);
					socket.receive(request);
					logger.info(" recieved message :" + recievedString);
					recievedString = new String(request.getData());
					logger.info(" recieved message1 :" + recievedString);
					String[] splitRecievedStr = recievedString.split(",");

					String eventChoice = splitRecievedStr[splitRecievedStr.length - 1];
					eventChoice = eventChoice.trim();

					logger.info("Request Type :" + eventChoice);
					String outputResponse = "";

					byte[] sendDataByte = new byte[1000];

					if (eventChoice.equalsIgnoreCase("bookEvent")) {

						if (bookEvent(splitRecievedStr[0], splitRecievedStr[1], splitRecievedStr[2])) {
							outputResponse = "t";
						} else {
							outputResponse = "f";

						}

						sendDataByte = outputResponse.getBytes();
					} else if (eventChoice.equalsIgnoreCase("getBooking")) {
						outputResponse = getCurrentBookingSchedule(splitRecievedStr[0]);

						sendDataByte = outputResponse.getBytes();

					} else if (eventChoice.equalsIgnoreCase("cancelEvent")) {

						boolean result = cancelEvent(splitRecievedStr[0], splitRecievedStr[1], splitRecievedStr[2]);
						if (result) {
							outputResponse = "t";
						} else {
							outputResponse = "f";
						}
						sendDataByte = outputResponse.getBytes();

					}

					else if (eventChoice.equalsIgnoreCase("listEventAvailability")) {
						logger.info("If case inside ottawa server is true");
						outputResponse = currentServerEventAvailability(splitRecievedStr[0]);
						sendDataByte = outputResponse.getBytes();
					}

					else {
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

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public synchronized boolean removeEvent(String eventId, String eventType, String managerID) {

		boolean status = false;

		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();

		logger.info("Request Type: Remove Event");
		logger.info("Parameter: EventID, EventType");

		List<CustomerData> customerLisClone = new ArrayList<CustomerData>();
		customerLisClone = CustomerDataList;

		for (int i = 0; i < customerLisClone.size(); i++) {
			CustomerData cd = new CustomerData();
			cd = customerLisClone.get(i);

			if (cd.geteveTyp().equalsIgnoreCase(eventType) && cd.geteveID().equalsIgnoreCase(eventId)) {
				eventType = cd.geteveTyp();
				CustomerDataList.remove(i);
				status = true;
				logger.info("Removing " + eventId + " from customer db");
			}
		}
		
		
		if (ottawaHashMap.get(eventType) != null) {
			innerhash = ottawaHashMap.get(eventType);

			if (innerhash.get(eventId) != null) {
				innerhash = ottawaHashMap.get(eventType);
				innerhash.remove(eventId);
				ottawaHashMap.put(eventType, innerhash);
				logger.info("EventType :" + eventType + "\n" + " with Event ID : " + eventId + "\n");
				logger.info("Successfully removed from Ottawa server");
				status = true;
			}
		} else {
			logger.info("EventType : " + eventType + " with Event ID :" + eventId + " does not exist");
			status = false;
		}

		return status;

	}

	public static synchronized boolean bookEvent(String customerId, String eventId, String eventType) {

		int count = 0;
		String key = customerId + eventId.substring(6);
		if (customerBookingAccess.containsKey(key)) {
			count = customerBookingAccess.get(key);
		}

		logger.info("Request Type: Book Event");
		logger.info("Request Parameter: Customer ID, Event ID, Event Type");

		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();

		boolean check = false;

		String city = eventId.substring(0, 3);

		String requestMessage = customerId + "," + eventId + "," + eventType + "," + "bookEvent";
		logger.info("Map Size is : " + ottawaHashMap.size() + "\n" + "city  eventId :" + eventId + "\n"
				+ "city  eventId :" + eventId);

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

						logger.info("Customer:  customer ID  :" + customerId + "\n" + " event type: " + eventType + "\n"
								+ "event ID : " + eventId);
						logger.info(innerhash);
						logger.info("Event booked Successfully in Ottawa Location");

						CustomerData cd = new CustomerData();
						cd.setcustID(customerId);
						cd.seteveID(eventId);
						cd.seteveTyp(eventType);
						CustomerDataList.add(cd);

						check = true;

					} else {
						logger.info("EventType :" + eventType + " with Event ID : " + eventId
								+ "does not have enough seats left");
						check = false;
					}

				} else {
					logger.info("EventType :" + eventType + " with Event ID : " + eventId + "does not exist");
					check = false;
				}

			} else {
				logger.info("EventType :" + eventType + " does not exist");
				check = false;
			}
		} else if (city.equalsIgnoreCase("mtl")) {

			if (count < 3) {
				try {

					logger.info(" Calling Montreal Book Event from Ottawa");

					socket1 = new DatagramSocket();

					InetAddress address = InetAddress.getByName("localhost");

					DatagramPacket request1 = new DatagramPacket(message1, message1.length, address,
							Constants.RM3_MTL_PORT_NO);
					socket1.send(request1);
					logger.info("Request message sent from the client is : " + new String(request1.getData()));

					byte[] receive1 = new byte[1000];
					DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
					socket1.receive(reply1);
					logger.info("Request message received by the client is : " + new String(reply1.getData()));
					String returnRes = new String(reply1.getData()).trim();
					customerBookingAccess.put(key, count + 1);
					logger.info(" Oustide city count is : " + customerBookingAccess.get(key));
					if (returnRes.equalsIgnoreCase("t")) {
						check = true;
					} else
						check = false;

				} catch (SocketException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

			} else {
				logger.info("Increased the Maximum number of booking in outside cities");
				check = false;
			}

		}

		else if (city.equalsIgnoreCase("tor")) {

			logger.info(" Calling Tornoto Book Event from Ottawa");

			if (count < 3) {

				try {

					socket2 = new DatagramSocket();
					InetAddress address = InetAddress.getByName("localhost");

					DatagramPacket request2 = new DatagramPacket(message1, message1.length, address,
							Constants.RM3_TOR_PORT_NO);
					socket2.send(request2);
					logger.info("Request message sent from the client is : " + new String(request2.getData()));

					byte[] receive2 = new byte[1000];
					DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);
					socket2.receive(reply2);
					logger.info("Request message received by the client is : " + new String(reply2.getData()));
					String retRes = new String(reply2.getData()).trim();
					customerBookingAccess.put(key, count + 1);
					logger.info(" Oustide city count is : " + customerBookingAccess.get(key));
					if (retRes.equalsIgnoreCase("t")) {
						check = true;
					} else
						check = false;

				} catch (SocketException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

			} else {
				logger.info("Increased the Maximum number of booking in outside cities");
				check = false;
			}
		}

		return check;
	}

	public String getBookingSchedule(String customerId) {

		logger.info("Request Type: Get booking Schedule");
		logger.info("Request Parameter: Customer ID");
		String returnStr = "";
		List<String> serverResponse = new ArrayList<String>();

		serverResponse = otherLocBookingSchedules(customerId);

		logger.info("st list size  : " + serverResponse.size());

		for (String i : serverResponse) {
			returnStr = returnStr + i.trim();
		}

		returnStr = returnStr + getCurrentBookingSchedule(customerId);

		System.out.println(" The List of event for Customer ID : " + customerId + ": " + returnStr);
		
		if(returnStr.length() ==0)
			returnStr = "No customer with CustomerId " + customerId;

		return returnStr;

	}

	private List<String> otherLocBookingSchedules(String customerId) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		Runnable task1 = () -> {
			String s1 = sendToMtlServer(customerId, null, null, "getBooking", Constants.RM3_MTL_PORT_NO);
			list.add(s1);

		};
		Runnable task2 = () -> {
			String s2 = sendToTorServer(customerId, null, null, "getBooking", Constants.RM3_TOR_PORT_NO);
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

	public static synchronized String getCurrentBookingSchedule(String customerId) {

		List<CustomerData> lis = new ArrayList<CustomerData>();
		String returnStr = "";

		if (CustomerDataList.size() == 0) {
//			logger.info("no registered customers");
//			returnStr = "No customer with CustomerId"+ customerId;
		} else {

			for (CustomerData c : CustomerDataList) {
				if (c.getcustID().equals(customerId)) {
					lis.add(c);
//					returnStr = returnStr + c.getcustID() + " " + c.geteveTyp() + " " + c.geteveID() + ",";
					returnStr += c.geteveID() + " " + c.geteveTyp()+ ",";
				}
			}

		}

		return returnStr;

	}

	public static synchronized boolean cancelEvent(String cutomerId, String eventId, String TypeOfEvent) {
		boolean status = false;
		String eventType = "";
		String city = eventId.substring(0, 3);

		logger.info("Request Type: Cancel Event");
		logger.info("Request Parameters: Customer ID, Event ID, Event Type");

		String requestMessage = cutomerId + "," + eventId + "," + "cancelEvent";
		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;
		byte[] message1 = requestMessage.getBytes();
		byte[] message2 = requestMessage.getBytes();
		String key = cutomerId + eventId.substring(6);

		if (city.equalsIgnoreCase("otw")) {

			List<CustomerData> customerLisClone = new ArrayList<CustomerData>();
			customerLisClone = CustomerDataList;

			for (int i = 0; i < customerLisClone.size(); i++) {
				CustomerData cd = new CustomerData();
				cd = customerLisClone.get(i);

				if (cd.getcustID().equalsIgnoreCase(cutomerId) && cd.geteveID().equalsIgnoreCase(eventId))
				{
					eventType = cd.geteveTyp();
					CustomerDataList.remove(i);
					status = true;
				}
			}

			Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
			innerhash = ottawaHashMap.get(eventType);

			if (innerhash != null) {
				for (Map.Entry<String, EventsData> e : innerhash.entrySet()) {
					logger.info("The event id is                  :" + e.getKey());
					EventsData event = new EventsData();
					event = e.getValue();

					List<String> registerdCustomer = new ArrayList<String>();
					registerdCustomer = event.getregisterdCustomer();

					if (registerdCustomer.contains(cutomerId)) {

						logger.info("Customer with customer ID : " + cutomerId + "cancelled the event with ID : "
								+ eventId);
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

		else if (city.equalsIgnoreCase("mtl")) {
			logger.info(" Calling Ottawa Book Event from Montreal");

			int count = 0;
			if (customerBookingAccess.containsKey(key)) {
				count = customerBookingAccess.get(key);
			}

			try {

				socket1 = new DatagramSocket();

				InetAddress address = InetAddress.getByName("localhost");

				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address,
						Constants.RM3_MTL_PORT_NO);
				socket1.send(request1);
				logger.info("Request message sent from the client is : " + new String(request1.getData()));

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				logger.info("Request message received by the client is : " + new String(reply1.getData()));
				String ret = new String(reply1.getData()).trim();
				logger.info("ret is :" + ret);

				if (count > 0)
					customerBookingAccess.put(key, count - 1);
				if (ret.equalsIgnoreCase("t")) {
					status = true;
				} else
					status = false;

			} catch (SocketException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		else if (city.equalsIgnoreCase("tor")) {

			logger.info(" Calling Ottawa Book Event from Montreal");

			int count = 0;
			if (customerBookingAccess.containsKey(key)) {
				count = customerBookingAccess.get(key);
			}

			try {

				socket2 = new DatagramSocket();

				InetAddress address = InetAddress.getByName("localhost");

				DatagramPacket request2 = new DatagramPacket(message1, message1.length, address,
						Constants.RM3_TOR_PORT_NO);
				socket2.send(request2);
				logger.info("Request message sent from the client is : " + new String(request2.getData()));

				byte[] receive2 = new byte[1000];
				DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);
				socket2.receive(reply2);
				logger.info("Request message received by the client is : " + new String(reply2.getData()));
				String ret = new String(reply2.getData()).trim();
				logger.info("ret is :" + ret);

				if (count > 0)
					customerBookingAccess.put(key, count - 1);
//					System.out.println("ret is :"+ret);

				if (ret.equalsIgnoreCase("t"))
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

	public static String listEventAvailability(String eventType, String managerId) {

		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
		List<String> availableEvents = new ArrayList<String>();
		String str = "";
		String serverName = managerId.substring(0, 3);

		logger.info("Request Type: List Event Availability");
		logger.info("Request Parameter: Event Type");

		List<String> serverList = new ArrayList<String>();
		serverList = getListEventAvailability(eventType);

		for (String i : serverList) {
			str = str + " " + i;
		}

		str = str + currentServerEventAvailability(eventType);

		if (str.length() > 0) {
			str = eventType + " - " + str;
			str = str.substring(0, str.length() - 1);
			logger.info(" The List of event for Event Type : " + eventType + ": " + str);
		} else {
			str = eventType + " - " + "No events found";
		}

		return str;
	}

	private static List<String> getListEventAvailability(String eventType) {
		// TODO Auto-generated method stub

		List<String> list = new ArrayList<String>();

		Runnable task1 = () -> {
			String s1 = sendToMtlServer(null, eventType, null, "listEventAvailability", Constants.RM3_MTL_PORT_NO);
			if(!s1.equalsIgnoreCase(""))
				list.add(s1);

		};
		Runnable task2 = () -> {
			String s2 = sendToTorServer(null, eventType, null, "listEventAvailability", Constants.RM3_TOR_PORT_NO);
			if(!s2.equalsIgnoreCase(""))
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

	public static synchronized String currentServerEventAvailability(String eventType) {
		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
		List<String> availableEvents = new ArrayList<String>();
		String returnString = "";

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
					returnString = returnString + e.getKey() + " " + event.seatsLeft + ", ";
					availableEvents.add(returnString);
				}
			}

		} else {
			logger.info("EventType : " + eventType + "is not avaliable");
		}

		return returnString;

	}

	public synchronized static String sendToMtlServer(String customerId, String eventType, String eventId,
			String requestType, int port) {
		String result = "";
		String returnString = "";

		try {
			if (requestType.equalsIgnoreCase("listEventAvailability")) {
				DatagramSocket socket1 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket1 = new DatagramSocket();

				String message = eventType + "," + requestType;
				logger.info("Message is : " + message);

				byte[] message1 = new byte[1000];

				message1 = message.getBytes();

				logger.info("Reuqest Type for server to server communication is :" + requestType);
				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, port);
				socket1.send(request1);
				logger.info("Request message sent from the client is : " + new String(request1.getData()));

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				logger.info("Request message received by the client is : " + new String(reply1.getData()));
				returnString = returnString + new String(reply1.getData()).trim();
			}

			else if (requestType.equalsIgnoreCase("getBooking")) {

				DatagramSocket socket1 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket1 = new DatagramSocket();

				String message = customerId + "," + requestType;
				logger.info("Message is : " + message);

				byte[] message1 = new byte[1000];

				message1 = message.getBytes();
				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, port);
				socket1.send(request1);
				logger.info("Request message sent from the client is : " + new String(request1.getData()));

				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				logger.info("Request message received by the client is : " + new String(reply1.getData()));
				returnString = returnString + new String(reply1.getData()).trim();

			}

		} catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return returnString;

	}

	public synchronized static String sendToTorServer(String customerId, String eventType, String eventId,
			String requestType, int port) {

		String result = "";
		String returnString = "";

		try {
			if (requestType.equalsIgnoreCase("listEventAvailability")) {
				DatagramSocket socket2 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket2 = new DatagramSocket();

				String message = eventType + "," + requestType;
//				logger.info("Message is : "+message);

				byte[] message1 = new byte[1000];
				byte[] message2 = new byte[1000];

				message2 = message.getBytes();

				byte[] receive2 = new byte[1000];
				DatagramPacket request2 = new DatagramPacket(message2, message2.length, address, port);
				socket2.send(request2);
				DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);

				logger.info("Request message sent from the client is : " + new String(request2.getData()));
				socket2.receive(reply2);
				logger.info("Request message received by the client is : " + new String(reply2.getData()));
				returnString = returnString + new String(reply2.getData()).trim();
			}

			else if (requestType.equalsIgnoreCase("getBooking")) {

				DatagramSocket socket2 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket2 = new DatagramSocket();

				String message = customerId + "," + requestType;
				logger.info("Message is : " + message);

				byte[] message1 = new byte[1000];
				byte[] message2 = new byte[1000];

				message2 = message.getBytes();

				byte[] receive2 = new byte[1000];
				DatagramPacket request2 = new DatagramPacket(message2, message2.length, address, port);
				socket2.send(request2);
				DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);

				logger.info("Request message sent from the client is : " + new String(request2.getData()));
				socket2.receive(reply2);
				logger.info("Request message received by the client is : " + new String(reply2.getData()));
				returnString = returnString + new String(reply2.getData()).trim();

			}

		} catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return returnString;
	}

//	
//	public synchronized void ResultHashMap()  {
//
//		logger.info(" Printing Map Details");
//
//		for (Map.Entry<String, Map<String, EventsData>> entry : ottawaHashMap.entrySet()) {
//
//			logger.info("The event type is              :" + entry.getKey());
//			Map<String, EventsData> innerHash = new HashMap<String, EventsData>();
//			innerHash = entry.getValue();
//
//			if (innerHash != null) {
//				for (Map.Entry<String, EventsData> indexVal : innerHash.entrySet()) {
//					logger.info("The event id is                  :" + indexVal.getKey());
//					EventsData event = new EventsData();
//					event = indexVal.getValue();
//					logger.info("The event booking capacity is   :" + event.capacityOfBooking);
//					logger.info("The event booking seat left is   :" + event.seatsLeft);
//					logger.info("The event booking seat booked is :" + event.seatsFilled);
//				}
//
//			}
//		}
//		
//		System.out.println();
//	}

	public boolean swapEvent(String customerID, String newEventID, String newEventType, String oldEventID,
			String oldEventType) {

		boolean status = false;

		if (cancelEvent(customerID, oldEventID, oldEventType)) {
			if (bookEvent(customerID, newEventID, newEventType)) {
				logger.info("Event is successfully swapped");
				status = true;
			} else {
				logger.info("Couldn't able to book the new event : " + newEventID + "and event type " + newEventType);
				bookEvent(customerID, oldEventID, oldEventType);
				status = false;
			}

		} else {
			logger.info("Event is couldn't able to swap because the event Id " + oldEventID + " and event type "
					+ oldEventType + " does not exist");
			status = false;
		}

		return status;
	}

}
