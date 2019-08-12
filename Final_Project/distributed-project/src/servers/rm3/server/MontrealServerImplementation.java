package servers.rm3.server;

import java.util.*;
//import java.util.logging.LogManager;

//import com.concordia.clientInterface.ClientServerInterface;
//import common.CustomerData;
import servers.rm3.common.EventsData;
import utility.Constants;
import servers.rm3.common.CustomerData;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;

public class MontrealServerImplementation {

	private static Logger LOGGER = LogManager.getLogger("montreal");

	private static final long serialVersionUID = 1L;
	public static Map<String, Map<String, EventsData>> montrealHashMap = new HashMap<String, Map<String, EventsData>>();
	public static List<CustomerData> CustomerDataList = new ArrayList<CustomerData>();
	public static Map<String, Integer> customerBookingAccess = new HashMap<String, Integer>();
	private org.omg.CORBA.ORB orbMtl;
	private static int flag = 0;

	public MontrealServerImplementation() throws Exception {
		super();

		Runnable udp_task = new Runnable() {
			public void run() {
				udp_packet_recv();
			}
		};

		Thread thread = new Thread(udp_task);
		thread.start();

	}
	// public void setORB(org.omg.CORBA.ORB orb_val) {
	// orbMtl = orb_val;
	// }
	// public void shutdown() {
	// orbMtl.shutdown(false);
	// }

	// public static void main(String args[]) {
	// try {
	//
	// MontrealServerImplementation MtlServer = new MontrealServerImplementation();
	// DatagramSocket socket = null;
	// try {
	//
	// socket = new DatagramSocket(Constants.RM3_MTL_PORT_NO);
	//
	//
	// while (true) {
	//
	// byte[] getServerResponse = new byte[1000];
	// byte[] sendDataByte = new byte[1000];
	//
	//
	// String recievedString = "";
	// DatagramPacket request = new DatagramPacket(getServerResponse,
	// getServerResponse.length);
	// socket.receive(request);
	// LOGGER.info(" Recieved message :"+recievedString);
	//
	// recievedString = new String(request.getData());
	//
	// String[] splitRecievedStr = recievedString.split(",");
	//
	// String eventChoice = splitRecievedStr[splitRecievedStr.length -1];
	// eventChoice = eventChoice.trim();
	// LOGGER.info("Request Type :"+eventChoice);
	//
	// String outputRes = "";
	// if(eventChoice.equalsIgnoreCase("listEventAvailability"))
	// {
	// LOGGER.info("List event Avaialability: Inter server communication");
	// outputRes = MtlServer.getCurrentServerEventAvailability(splitRecievedStr[0]);
	// sendDataByte = outputRes.getBytes();
	// }
	// else if(eventChoice.equalsIgnoreCase("bookEvent"))
	// {
	// LOGGER.info("Book Event: Inter server communication");
	//
	// if(MtlServer.bookEvent(splitRecievedStr[0], splitRecievedStr[1],
	// splitRecievedStr[2]))
	// {
	// outputRes = "t";
	// }
	// else
	// {
	// outputRes = "f";
	//
	// }
	//
	// sendDataByte = outputRes.getBytes();
	// }
	// else if(eventChoice.equalsIgnoreCase("getBooking"))
	// {
	// LOGGER.info("Get booking Schedule: Inter server communication");
	// outputRes = MtlServer.getCurrentBookingSchedule(splitRecievedStr[0]);
	// sendDataByte = outputRes.getBytes();
	//
	// }
	// else if(eventChoice.equalsIgnoreCase("cancelEvent"))
	// {
	// LOGGER.info("Cancel Event : Inter server Communication");
	// boolean result =
	// MtlServer.cancelEvent(splitRecievedStr[0],splitRecievedStr[1],
	// splitRecievedStr[2]);
	// if(result) {
	// outputRes ="t";
	// }else {
	// outputRes = "f";
	// }
	//
	// sendDataByte = outputRes.getBytes();
	//
	// }
	// else
	// {
	// sendDataByte = "".getBytes();
	// }
	//
	//
	// DatagramPacket reply = new DatagramPacket(sendDataByte, sendDataByte.length,
	// request.getAddress(),
	// request.getPort());
	// socket.send(reply);
	// }
	//
	// } catch (SocketException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// socket.close();
	// }
	//
	// org.omg.CORBA.ORB orb = ORB.init(args, null);
	//
	// // get reference to rootpoa &amp; activate
	// POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
	// rootpoa.the_POAManager().activate();
	// MontrealServerImplementation MtlServer = new MontrealServerImplementation();
	// MtlServer.setORB(orb);
	// org.omg.CORBA.Object ref = rootpoa.servant_to_reference(MtlServer);
	// frontendApp href = frontendAppHelper.narrow(ref);
	// org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
	// NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	// NameComponent path[] = ncRef.to_name("Montreal");
	// ncRef.rebind(path, href);
	//
	// LOGGER.info("In montreal server is up");
	// orb.run();

	// } catch (Exception e) {
	// LOGGER.info("Montreal Server Start up failed: " + e);
	// e.printStackTrace();
	// }
	// }

	public void udp_packet_recv() {
		// MontrealServerImplementation MtlServer;
		try {
			// MtlServer = new MontrealServerImplementation();
			DatagramSocket socket = null;
			try {

				socket = new DatagramSocket(Constants.RM3_MTL_PORT_NO);
				// System.out
				// .println("RM 3: Montreal UDP Server Started on port: " +
				// Constants.RM3_MTL_PORT_NO);

				LOGGER.info("RM 3: Montreal-UDP-Server Started on port: " + Constants.RM3_MTL_PORT_NO);

				while (true) {

					byte[] getServerResponse = new byte[1000];
					byte[] sendDataByte = new byte[1000];

					String recievedString = "";
					DatagramPacket request = new DatagramPacket(getServerResponse, getServerResponse.length);
					socket.receive(request);
					LOGGER.info(" Recieved message :" + recievedString);

					recievedString = new String(request.getData());

					String[] splitRecievedStr = recievedString.split(",");

					String eventChoice = splitRecievedStr[splitRecievedStr.length - 1];
					eventChoice = eventChoice.trim();
					LOGGER.info("Request Type :" + eventChoice);

					String outputRes = "";
					if (eventChoice.equalsIgnoreCase("listEventAvailability")) {
						LOGGER.info("List event Avaialability: Inter server communication");
						outputRes = getCurrentServerEventAvailability(splitRecievedStr[0]);
						sendDataByte = outputRes.getBytes();
					} else if (eventChoice.equalsIgnoreCase("bookEvent")) {
						LOGGER.info("Book Event: Inter server communication");

						if (bookEvent(splitRecievedStr[0], splitRecievedStr[1], splitRecievedStr[2])) {
							outputRes = "t";
						} else {
							outputRes = "f";

						}

						sendDataByte = outputRes.getBytes();
					} else if (eventChoice.equalsIgnoreCase("getBooking")) {
						LOGGER.info("Get booking Schedule: Inter server communication");
						outputRes = getCurrentBookingSchedule(splitRecievedStr[0]);
						sendDataByte = outputRes.getBytes();

					} else if (eventChoice.equalsIgnoreCase("cancelEvent")) {
						LOGGER.info("Cancel Event : Inter server Communication");
						boolean result = cancelEvent(splitRecievedStr[0], splitRecievedStr[1], splitRecievedStr[2]);
						if (result) {
							outputRes = "t";
						} else {
							outputRes = "f";
						}

						sendDataByte = outputRes.getBytes();

					} else {
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
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public synchronized boolean addEvent(String eventId, String eventType, int bookingCapactiy, String managerID) {

		boolean status = false;
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
					status = true;
				} else {
					innerhash.put(eventId, e);
					montrealHashMap.put(eventType, innerhash);
					status = true;
				}
			} else {
				innerhash.put(eventId, e);
				montrealHashMap.put(eventType, innerhash);
				status = true;
			}

		} else {
			innerhash.put(eventId, e);
			montrealHashMap.put(eventType, innerhash);
			status = true;

		}
		LOGGER.info("Event Type : " + eventType + "\n" + "Event ID : " + eventId + "\n" + "Booking capacity "
				+ bookingCapactiy);
		LOGGER.info("Montreal Server: Successfully Added");
		return status;
	}

	public synchronized boolean removeEvent(String eventId, String eventType, String managerID) {

		boolean status = false;

		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();
		LOGGER.info("Request Type: Remove Event");
		LOGGER.info("Parameter: EventID, EventType");

		List<CustomerData> customerLisClone = new ArrayList<CustomerData>();
		customerLisClone = CustomerDataList;

		for (int i = 0; i < customerLisClone.size(); i++) {
			CustomerData cd = new CustomerData();
			cd = customerLisClone.get(i);

			if (cd.geteveTyp().equalsIgnoreCase(eventType) && cd.geteveID().equalsIgnoreCase(eventId)) {
				eventType = cd.geteveTyp();
				CustomerDataList.remove(i);
				status = true;
				LOGGER.info("Removing " + eventId + " from customer db");
			}
		}
		
		
		if (montrealHashMap.get(eventType) != null) {
			innerhash = montrealHashMap.get(eventType);

			if (innerhash.get(eventId) != null) {
				innerhash = montrealHashMap.get(eventType);
				innerhash.remove(eventId);
				montrealHashMap.put(eventType, innerhash);
				LOGGER.info("EventType :" + eventType + "\n" + " with Event ID : " + eventId + "\n");
				LOGGER.info("Removed Successfully from Montreal Server");
				status = true;

			}
		} else {
			LOGGER.info("EventType : " + eventType + "Event ID :" + eventId + " does not exist");

			status = false;
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

	public static synchronized boolean bookEvent(String customerId, String eventId, String eventType) {

		int count = 0;
		String key = customerId + eventId.substring(6);

		if (customerBookingAccess.containsKey(key)) {
			count = customerBookingAccess.get(key);
		}

		LOGGER.info("Request Type: Book Event");
		LOGGER.info("Request Parameter: Customer ID, Event ID, Event Type");

		Map<String, EventsData> innerhash = new HashMap<String, EventsData>();

		boolean check = false;

		String city = eventId.substring(0, 3);
		System.out.println(" City book event is :" + city);
		String requestMessage = customerId + "," + eventId + "," + eventType + "," + "bookEvent";

		LOGGER.info("Map Size is : " + montrealHashMap.size() + "\n" + "city  eventId :" + eventId + "\n"
				+ "city  eventId :" + eventId);

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

						LOGGER.info("Customer:  customer ID  :" + customerId + "\n" + " event type: " + eventType + "\n"
								+ "event ID : " + eventId);
						LOGGER.info(innerhash);
						LOGGER.info("Event booked Successfully in Montreal Location");

						CustomerData custData = new CustomerData();
						custData.setcustID(customerId);
						custData.seteveID(eventId);
						custData.seteveTyp(eventType);
						CustomerDataList.add(custData);

						check = true;

					} else {
						LOGGER.info("EventType :" + eventType + "Event ID : " + eventId
								+ "does not have enough seats left");

						check = false;
					}

				} else {
					LOGGER.info("EventType :" + eventType + " with Event ID : " + eventId + "does not exist");

					check = false;
				}

			} else {
				LOGGER.info("EventType :" + eventType + " does not exist");
				check = false;
			}
		}

		else if (city.equalsIgnoreCase("otw")) {

			LOGGER.info("Calling Ottawa Book Event from Montreal Location");

			if (count < 3) {
				try {

					socket1 = new DatagramSocket();

					InetAddress address = InetAddress.getByName("localhost");

					DatagramPacket request1 = new DatagramPacket(message1, message1.length, address,
							Constants.RM3_OTW_PORT_NO);
					socket1.send(request1);
					LOGGER.info("Request message sent from the client is : " + new String(request1.getData()));
					byte[] receive1 = new byte[1000];
					DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
					socket1.receive(reply1);

					String returnRes = new String(reply1.getData()).trim();
					customerBookingAccess.put(key, count + 1);
					LOGGER.info(" Oustide city count is : " + customerBookingAccess.get(key));
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
				LOGGER.info("Increased the Maximum number of booking in outside cities");
				check = false;
			}

			// LOGGER.info(check);

		}

		else if (city.equalsIgnoreCase("tor")) {

			LOGGER.info(" Calling Tornoto Book Event from Montreal Server");

			if (count < 3) {

				try {

					socket2 = new DatagramSocket();
					InetAddress address = InetAddress.getByName("localhost");

					DatagramPacket request2 = new DatagramPacket(message2, message2.length, address,
							Constants.RM3_TOR_PORT_NO);
					socket2.send(request2);

					byte[] receive2 = new byte[1000];
					DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);
					socket2.receive(reply2);
					LOGGER.info("Request message received by the client is : " + new String(reply2.getData()));
					String retRes = new String(reply2.getData()).trim();
					customerBookingAccess.put(key, count + 1);
					LOGGER.info(" Oustide city count is : " + customerBookingAccess.get(key));
					if (retRes.equalsIgnoreCase("t"))
						check = true;
					else
						check = false;

				} catch (SocketException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

			} else {
				LOGGER.info("Increased the Maximum number of booking in outside cities");
				check = false;
			}
		}

		LOGGER.info("Book event ends");
		return check;

	}

	public String getBookingSchedule(String customerId) {
		String returnStr = "";

		LOGGER.info("Request Type: Get booking Schedule");
		LOGGER.info("Request Parameter: Customer ID");
		List<String> serverResponse = new ArrayList<String>();

		serverResponse = otherLocationBookingSchedule(customerId);
		for (String i : serverResponse) {
			returnStr = returnStr + i.trim();
		}

		returnStr = returnStr + getCurrentBookingSchedule(customerId);

		System.out.println(" The List of event for Customer ID : " + customerId + ": " + returnStr);
		
		if(returnStr.length() ==0)
			returnStr = "No customer with CustomerId " + customerId;

		return returnStr;

	}

	private List<String> otherLocationBookingSchedule(String customerId) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		Runnable task1 = () -> {
			String s1 = sendToOttServer(customerId, null, null, "getBooking", Constants.RM3_OTW_PORT_NO);
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
			LOGGER.info("no registered customers");
//			returnStr = "No customer with CustomerId"+ customerId;
		} else {

			for (CustomerData c : CustomerDataList) {
				if (c.getcustID().equals(customerId)) {
					lis.add(c);
					// returnStr = returnStr + c.getcustID() + " " + c.geteveTyp() + " " +
					// c.geteveID() + ",";
					returnStr += c.geteveID() + " " + c.geteveTyp() + ",";
				}
			}

		}

		return returnStr;

	}

	public static synchronized boolean cancelEvent(String cutomerId, String eventId, String TypeOfEvent) {
		boolean status = false;
		String eventType = "";
		String city = eventId.substring(0, 3);
		String check = "failure";

		LOGGER.info("Request Type: Cancel Event");
		LOGGER.info("Request Parameters: Customer ID, Event ID, Event Type");

		String requestMessage = cutomerId + "," + eventId + "," + "cancelEvent";
		DatagramSocket socket1 = null;
		DatagramSocket socket2 = null;
		byte[] message1 = requestMessage.getBytes();
		byte[] message2 = requestMessage.getBytes();
		String key = cutomerId + eventId.substring(6);

		if (city.equalsIgnoreCase("mtl")) {

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

			Map<String, EventsData> innerHash = new HashMap<String, EventsData>();
			innerHash = montrealHashMap.get(eventType);

			if (innerHash != null) {
				for (Map.Entry<String, EventsData> e : innerHash.entrySet()) {
					LOGGER.info("The event id                  :" + e.getKey());
					EventsData event = new EventsData();
					event = e.getValue();

					List<String> customerRegistered = new ArrayList<String>();
					customerRegistered = event.getregisterdCustomer();
					if (customerRegistered.contains(cutomerId)) {
						check = "success";
						LOGGER.info("Customer with customer ID : " + cutomerId + "cancelled the event with ID : "
								+ eventId);
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
			} else {

			}
			LOGGER.info("No data is exist to cancel in the hash map");

		}

		else if (city.equalsIgnoreCase("otw")) {
			LOGGER.info(" Calling Ottawa Book Event from Montreal Server");

			int count = 0;
			if (customerBookingAccess.containsKey(key)) {
				count = customerBookingAccess.get(key);
			}

			try {

				socket1 = new DatagramSocket();

				InetAddress address = InetAddress.getByName("localhost");

				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address,
						Constants.RM3_OTW_PORT_NO);
				socket1.send(request1);
				LOGGER.info("Request message sent : " + new String(request1.getData()));
				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				String ret = new String(reply1.getData()).trim();

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
			LOGGER.info(" Calling Toronto Book Event from Montreal Server");

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
				// LOGGER.info("Request message sent : " + new String(request2.getData()));

				byte[] receive2 = new byte[1000];
				DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);
				socket2.receive(reply2);
				String ret = new String(reply2.getData()).trim();

				if (count > 0)
					customerBookingAccess.put(key, count - 1);
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

		Map<String, EventsData> innerHash = new HashMap<String, EventsData>();
		List<String> availableEvents = new ArrayList<String>();
		String str = "";
		String serverName = managerId.substring(0, 3);

		LOGGER.info("Request Type: List Event Availability");
		LOGGER.info("Request Parameter: Event Type");

		List<String> st = new ArrayList<String>();
		st = getListEventAvailability(eventType);

		for (String i : st) {
			str = str + " " + i;
		}

		str = str + getCurrentServerEventAvailability(eventType);
		
		if (str.length() > 0) {
			str = eventType + " - " + str;
			str = str.substring(0, str.length() - 1);
			LOGGER.info(" The List of event for Event Type : " + eventType + ": " + str);
		} else {
			str = eventType + " - " + "No events found";
		}
		return str;
	}

	private static List<String> getListEventAvailability(String eventType) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		Runnable task1 = () -> {
			String s1 = sendToOttServer(null, eventType, null, "listEventAvailability", Constants.RM3_OTW_PORT_NO);
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

	public static synchronized String getCurrentServerEventAvailability(String eventType) {
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
					str = str + e.getKey() + " " + event.seatsLeft + ", ";
					availableEvents.add(str);
				}
			}

		} else {
			LOGGER.info("EventType : " + eventType + "is not avaliable");
		}

		return str;

	}

	public static synchronized String sendToOttServer(String customerId, String eventType, String eventId,
			String requestType, int port) {
		String result = "";
		String returnString = "";

		try {
			if (requestType.equalsIgnoreCase("listEventAvailability")) {
				DatagramSocket socket1 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket1 = new DatagramSocket();

				String message = eventType + "," + requestType;
				byte[] message1 = new byte[1000];

				message1 = message.getBytes();
				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, port);
				socket1.send(request1);
				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
				returnString = returnString + new String(reply1.getData()).trim();
			}

			else if (requestType.equalsIgnoreCase("getBooking")) {
				DatagramSocket socket1 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket1 = new DatagramSocket();

				String message = customerId + "," + requestType;
				LOGGER.info("Message is : " + message);
				byte[] message1 = new byte[1000];

				message1 = message.getBytes();
				DatagramPacket request1 = new DatagramPacket(message1, message1.length, address, port);
				socket1.send(request1);
				byte[] receive1 = new byte[1000];
				DatagramPacket reply1 = new DatagramPacket(receive1, receive1.length);
				socket1.receive(reply1);
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
				LOGGER.info("Message is : " + message);
				byte[] message1 = new byte[1000];
				byte[] message2 = new byte[1000];
				message2 = message.getBytes();

				byte[] receive2 = new byte[1000];
				DatagramPacket request2 = new DatagramPacket(message2, message2.length, address, port);
				socket2.send(request2);
				DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);

				LOGGER.info("Request message sent from the client is : " + new String(request2.getData()));
				socket2.receive(reply2);
				returnString = returnString + new String(reply2.getData()).trim();
			}

			else if (requestType.equalsIgnoreCase("getBooking")) {

				DatagramSocket socket2 = null;
				InetAddress address = InetAddress.getByName("localhost");
				socket2 = new DatagramSocket();

				String message = customerId + "," + requestType;
				byte[] message1 = new byte[1000];
				byte[] message2 = new byte[1000];

				message2 = message.getBytes();
				byte[] receive2 = new byte[1000];
				DatagramPacket request2 = new DatagramPacket(message2, message2.length, address, port);
				socket2.send(request2);
				DatagramPacket reply2 = new DatagramPacket(receive2, receive2.length);

				LOGGER.info("Request message sent from the client is : " + new String(request2.getData()));
				socket2.receive(reply2);
				returnString = returnString + new String(reply2.getData()).trim();

			}
		} catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return returnString;
	}

	public boolean swapEvent(String customerID, String newEventID, String newEventType, String oldEventID,
			String oldEventType) {

		MontrealServerImplementation MtlServer1;
		boolean status = false;

		if (cancelEvent(customerID, oldEventID, oldEventType)) {
			if (bookEvent(customerID, newEventID, newEventType)) {
				LOGGER.info("Event is successfully swapped");
				status = true;
			} else {
				LOGGER.info("Couldn't able to book the new event : " + newEventID + "and event type " + newEventType);
				bookEvent(customerID, oldEventID, oldEventType);
				status = false;
			}

		} else {
			LOGGER.info("Event is couldn't able to swap because the event Id " + oldEventID + " and event type "
					+ oldEventType + " does not exist");
			status = false;
		}

		return status;

	}

}
