package com.concordia.client;
import com.concordia.clientInterface.ClientServerInterface;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.rmi.RemoteException;



public class Client {
	private static Logger logger = LogManager.getLogger("client");

	public static void main(String[] args) {

		try {
			logger.info("In client");

			Scanner sc = new Scanner(System.in);
			while(true)
			{
			logger.info("Welcome to Concordia Event Booking System");
			logger.info("Please enter your unique identification number");
			String userID = sc.nextLine();

			String Location = userID.substring(0, 3);
			String LoginType = userID.substring(3, 4);

			String eventID = "";
			String typeOfEvent = "";
			String customerID = "";
			Registry register;
			String URL = "";
			ClientServerInterface remoteObj;
			int k = 0;
			if(Location.equalsIgnoreCase("MTL")){
				k =1;
			}else if(Location.equalsIgnoreCase("OTW")){
				k =2;
			}else if(Location.equalsIgnoreCase("TOR")){
				k =3;
			}

		switch(k){

			case 1:				
				int MtlportNo = 1111;
				 register = LocateRegistry.getRegistry(MtlportNo);
				 URL = "rmi://localhost:1111";

				remoteObj = (ClientServerInterface) register.lookup(URL);

				if (LoginType.toUpperCase().equals("M")) {

					int bookingCapacity = 0;

					boolean stateCondtion = true;

					while (stateCondtion) {

						logger.info("Manager Menu:\n");
						logger.info("1->Add an Event\n" + "2->Remove Event\n" + "3->List Event Availability\n"
								+ "4->Book Event\n" + "5-> Get Booking Schedule \n" + "6->Cancel Event \n" + "7-> MultiThreading and Synchronization \n"+"8-> Exit");
						logger.info("Please enter your option");
						String c = sc.nextLine();
						int choice = Integer.parseInt(c);

						switch (choice) {
						case 1:
							logger.info("Add Event");
							logger.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
								continue;
							}
							logger.info("Enter the booking capacity");
							bookingCapacity = Integer.parseInt(sc.nextLine());


							if (remoteObj.AddEvent(eventID, typeOfEvent, bookingCapacity).equalsIgnoreCase("success")) {
								logger.info("Event Added Successfully from montreal server");
							} else {
								logger.info(" Event already exists! New Booking capacity is Updated");
							}
							
							logger.info("EventType : " + typeOfEvent+"\n" + "Event ID : " + eventID +"\n"
									+ "  booked with capacity " + bookingCapacity);
							remoteObj.ResultHashMap();
							stateCondtion = true;
							logger.info("Add event End");
							break;

						case 2:

							logger.info("Remove Event");
							logger.info("Enter the event ID");
							eventID = sc.nextLine();


							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
								continue;
							}

							if (remoteObj.RemoveEvent(eventID, typeOfEvent).equalsIgnoreCase("success")) {
								logger.info("EventType :" + typeOfEvent +"\n" + " with Event ID : " + eventID +"\n");
								logger.info("Successfully removed from Montreal server");
							} else {
								logger.info(
										"EventType :" + typeOfEvent + "with Event ID :" + eventID + " does not exist");
							}
							remoteObj.ResultHashMap();

							logger.info("Remove Event End");
							break;

						case 3:
							logger.info("Inter Server Communication");
							logger.info("List Avaliability");

							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
								continue;
							}

							String[] bookingArrayCompare;
							bookingArrayCompare = remoteObj.ListEventAvailability(typeOfEvent).split(",");

							List<String> avaliableEvents = new ArrayList<String>(Arrays.asList(bookingArrayCompare));
							logger.info("avaliableEvents size :"+avaliableEvents.size());

							if (avaliableEvents.size() > 0) {
								logger.info("Available Events");
								for (String s : avaliableEvents) {

									logger.info(s);
								}
							} else {
								logger.info("EventType : " + typeOfEvent + "is not avaliable");
							}


							logger.info("Event Availablity Call Ended");

							stateCondtion = true;

							break;
							case 4:

								logger.info("Inter Server Communication");
								logger.info("Book Event");

								logger.info("Enter the customer ID");
								customerID = sc.nextLine();

								logger.info("Enter the event ID");
								eventID = sc.nextLine();

								while(true){
									logger.info("Enter the event Type");
									logger.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										logger.info("Enter the valid Event Type!!");
									continue;
								}


								if(remoteObj.BookEvent(customerID, eventID, typeOfEvent).equalsIgnoreCase("success"))
								{
									logger.info("Customer with customer ID  :"+customerID   +" booked event type of  : "+typeOfEvent +" with event ID : "+eventID);
								}
								else
								{
									logger.info("Customer with customer ID  :"+customerID   +" could not book a event type of  : "+typeOfEvent +" with event ID : "+eventID);
								}

								remoteObj.ResultHashMap();
								stateCondtion = true;

								break;

							case 5:

								logger.info("Inter Server Communication");
								logger.info("Get Booking Schedule");

								logger.info("Enter the customer ID");
								customerID = sc.nextLine();


								String[] bookingArrayCompare1 = remoteObj.GetBookingSchedule(customerID).split("\\s*,\\s*");
								List<String> BookingArrList = new ArrayList<String>(Arrays.asList(bookingArrayCompare1));

								List<String> HashOfCompareList = new ArrayList<String>(new HashSet<String>(BookingArrList));

								if(HashOfCompareList.size() > 0  && HashOfCompareList.get(0).length() > 0)
								{
									for(String hashObj : HashOfCompareList )
									{
										logger.info("hashObj :"+hashObj.trim());
										if(hashObj.trim() != null && hashObj.length() > 0)
										{
											String[] hashVal =  hashObj.split(" ");
											logger.info("Customer ID : "+hashVal[0] +" Customer event type : "+hashVal[1] +" Customer event ID : "+hashVal[2] );
										}
									}
								}
								else
								{
									logger.info("There are no events for the customer with the id : "+customerID);
								}

								stateCondtion = true;

								break;

							case 6:

								logger.info("Inter Server Communication");
								logger.info("Cancel Event");
								logger.info("Enter the customer ID");
								customerID = sc.nextLine();

								logger.info("Enter the event ID");
								eventID = sc.nextLine();
								
								while(true){
									logger.info("Enter the event Type");
									logger.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										logger.info("Enter the valid Event Type!!");
									continue;
								}

								remoteObj.CancelEvent(customerID, eventID, typeOfEvent);

								remoteObj.ResultHashMap();
								logger.info("canvel event booking is done!! Status available in the respective server");
								stateCondtion = true;

								break;
								
							case 7:
								
								logger.info("Multithreading and synchronizaiton");
								Runnable task1 = () -> {
								try {
									if(remoteObj.BookEvent("MTLM123456", "MTLM100519", "S").equalsIgnoreCase("success")){
										logger.info("Thread one pass");
									};
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								};
								Runnable task2 = () -> {
								try {
									if(remoteObj.BookEvent("MTLM123456", "MTLM100519", "S").equalsIgnoreCase("success")){
										logger.info("Thread two pass");
									}else
										logger.info("Thread two fail");
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

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

								
								break;
								
							case 8:
							stateCondtion = false;

							break;

						default:
							logger.info("Invalid Option");
							stateCondtion = true;
							break;

						}

					}

				}

				else if (LoginType.toUpperCase().equals("C")) {

					boolean stateCondtion = true;

					while (stateCondtion) {

						logger.info("Customer Menu:\n");

						logger.info(
								"1->Book Event\n" + "2-> Get Booking Schedule \n" + "3->Cancel Event \n" + "4-> Exit");
						logger.info("Please enter your option");
						String c = sc.nextLine();
						int choice = Integer.parseInt(c);

						switch (choice) {
						case 1:

							logger.info("Inter Server Communication");
							logger.info("Book Event");

							logger.info("Enter the customer ID");
							customerID = sc.nextLine();

							logger.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
								continue;
							}

							if(remoteObj.BookEvent(customerID, eventID, typeOfEvent).equalsIgnoreCase("success"))
							{
								logger.info("Customer with customer ID  :"+customerID   +" booked event type of  : "+typeOfEvent +" with event ID : "+eventID);
							}
							else
							{
								logger.info("Customer with customer ID  :"+customerID   +" could not book a event type of  : "+typeOfEvent +" with event ID : "+eventID);
							}

							remoteObj.ResultHashMap();
							stateCondtion = true;

							break;

						case 2:

							logger.info("Inter Server Communication");
							logger.info("Get Booking Schedule");

							logger.info("Enter the customer ID");
							customerID = sc.nextLine();


							 String[] bookingArrayCompare = remoteObj.GetBookingSchedule(customerID).split("\\s*,\\s*");
							 List<String> BookingArrList = new ArrayList<String>(Arrays.asList(bookingArrayCompare));

							List<String> HashOfCompareList = new ArrayList<String>(new HashSet<String>(BookingArrList));

							if(HashOfCompareList.size() > 0 && HashOfCompareList.get(0).length() > 0)
							{
								for(String hashObj : HashOfCompareList )
								{
									logger.info("hashObj :"+hashObj.trim());
									if(hashObj.trim() != null && hashObj.length() > 0)
									{
									String[] hashVal =  hashObj.split(" ");
									logger.info("Customer ID : "+hashVal[0] +" Customer event type : "+hashVal[1] +" Customer event ID : "+hashVal[2] );
									}
								}
							}
							else
							{
								logger.info("There are no events for the customer with the id : "+customerID);
							}

							stateCondtion = true;

							break;

						case 3:

							logger.info("Inter Server Communication");

							logger.info("Cancel Event");

							logger.info("Enter the customer ID");
							customerID = sc.nextLine();

							logger.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
								continue;
							}
							
							remoteObj.CancelEvent(customerID, eventID, typeOfEvent);

							remoteObj.ResultHashMap();
							logger.info("canvel event booking is done!! Status available in the respective server");
							stateCondtion = true;

							break;

						case 4:
							stateCondtion = false;

							break;

						default:
							logger.info("Invalid Option");
							stateCondtion = true;
							break;

						}

					}

				}

					break;
			case 2 :

				int otwPortNo = 1112;
				register = LocateRegistry.getRegistry(otwPortNo);
				URL = "rmi://localhost:1112";

				 remoteObj = (ClientServerInterface) register.lookup(URL);
				if (LoginType.toUpperCase().equals("M")) {
					int bookingCapacity = 0;

					boolean stateCondtion = true;

					while (stateCondtion) {

						logger.info("Manager Menu:\n");
						logger.info("1->Add an Event\n" + "2->Remove Event\n" + "3->List Event Availability\n"
								+ "4->Book Event\n" + "5-> Get Booking Schedule \n" + "6->Cancel Event \n" + "7-> Exit");
						logger.info("Please enter your option");
						String c = sc.nextLine();
						int choice = Integer.parseInt(c);

						switch (choice) {

						case 1:

							logger.info("Add Event");


							while(true){
								logger.info("Enter the event ID");
								eventID = sc.nextLine();

								if(eventID.substring(0,3).equalsIgnoreCase("OTW")){
									break;
								}else{
									logger.info("Invalid Event ID!!\n");
									continue;
								}

							}
							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
								continue;
							}

							logger.info("Enter the booking capacity");
							bookingCapacity = Integer.parseInt(sc.nextLine());

							
							if (remoteObj.AddEvent(eventID, typeOfEvent, bookingCapacity).equalsIgnoreCase("success")) {
								logger.info("Event Added Successfully from Ottawa server");
							} else {
								logger.info(" Event already exists! New Booking capacity is Updated");
							}
							
							logger.info("EventType : " + typeOfEvent+"\n" + "Event ID : " + eventID +"\n"
									+ "  booked with capacity " + bookingCapacity);
							remoteObj.ResultHashMap();
							logger.info("Add event End");
							stateCondtion = true;

							break;

						case 2:

							logger.info("Remove Event");

							logger.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
								continue;
							}

							if (remoteObj.RemoveEvent(eventID, typeOfEvent).equalsIgnoreCase("success")) {
								logger.info("EventType :" + typeOfEvent +"\n" + " with Event ID : " + eventID +"\n");
								logger.info("Successfully removed from Ottawa server");
							} else {
								logger.info(
										"EventType :" + typeOfEvent + "with Event ID :" + eventID + " does not exist");
							}
							remoteObj.ResultHashMap();
							logger.info("Remove Event End");
							break;

						case 3:
							logger.info("Inter Server Communication");
							logger.info("List Avaliability");

							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
								continue;
							}

							String[] bookingArrayCompare;

							bookingArrayCompare = remoteObj.ListEventAvailability(typeOfEvent).split(",");
							List<String> avaliableEvents = new ArrayList<String>(Arrays.asList(bookingArrayCompare));
							logger.info("avaliableEvents size :"+avaliableEvents.size());
							if (avaliableEvents.size() > 0) {
								logger.info("Available Events");
								
								for (String str : avaliableEvents) {

									logger.info(str);
								}
							} else {
								logger.info("EventType : " + typeOfEvent + "is not avaliable");
							}


							logger.info("List Event Availablity Call Ended");

							stateCondtion = true;

							break;

							case 4:

								logger.info("Inter Server Communication");
								logger.info("Book Event");

								logger.info("Enter the customer ID");
								customerID = sc.nextLine();

								logger.info("Enter the event ID");
								eventID = sc.nextLine();

								while(true){
									logger.info("Enter the event Type");
									logger.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										logger.info("Enter the valid Event Type!!");
									continue;
								}

								if(remoteObj.BookEvent(customerID, eventID, typeOfEvent).equalsIgnoreCase("success"))
								{
									logger.info("Customer with customer ID  :"+customerID   +" booked event type of  : "+typeOfEvent +" with event ID : "+eventID);
								}
								else
								{
									logger.info("Customer with customer ID  :"+customerID   +" could not book a event type of  : "+typeOfEvent +" with event ID : "+eventID);
								}

								remoteObj.ResultHashMap();
								stateCondtion = true;

								break;

							case 5:

								logger.info("Inter Server Communication");
								logger.info("Get Booking Schedule");

								logger.info("Enter the customer ID");
								customerID = sc.nextLine();


								String[] bookingArrayCompare_mang = remoteObj.GetBookingSchedule(customerID).split("\\s*,\\s*");
								List<String> BookingArrList = new ArrayList<String>(Arrays.asList(bookingArrayCompare_mang));

								List<String> HashOfCompareList = new ArrayList<String>(new HashSet<String>(BookingArrList));

								if(HashOfCompareList.size() > 0 && HashOfCompareList.get(0).length() >0)
								{
									for(String hashObj : HashOfCompareList )
									{
										logger.info("hashObj :"+hashObj.trim());
										if(hashObj.trim() != null  && hashObj.length() > 0)
										{
											String[] hashVal =  hashObj.split(" ");
											logger.info("Customer ID : "+hashVal[0] +" Customer event type : "+hashVal[1] +" Customer event ID : "+hashVal[2] );
										}
									}
								}
								else
								{
									logger.info("There are no events for the customer with the id : "+customerID);
								}

								stateCondtion = true;

								break;

							case 6:

								logger.info("Inter Server Communication");
								logger.info("Cancel Event");
								logger.info("Enter the customer ID");
								customerID = sc.nextLine();
								logger.info("Enter the event ID");
								
								while(true){
									logger.info("Enter the event Type");
									logger.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										logger.info("Enter the valid Event Type!!");
									continue;
								}
								
								
								eventID = sc.nextLine();
								remoteObj.CancelEvent(customerID, eventID, typeOfEvent);
								remoteObj.ResultHashMap();
								
								logger.info("canvel event booking is done!! Status available in the respective server");
								stateCondtion = true;
								break;


							case 7:
							stateCondtion = false;

							break;

						default:
							logger.info("Invalid Option");
							stateCondtion = true;
							break;

						}

					}

				}

				else if (LoginType.toUpperCase().equals("C")) {

					boolean stateCondtion = true;

					while (stateCondtion) {

						logger.info("Customer Menu:\n");

						logger.info(
								"1->Book Event\n" + "2-> Get Booking Schedule \n" + "3->Cancel Event \n" + "4-> Exit");
						logger.info("Please enter your option");
						String c = sc.nextLine();
						int choice = Integer.parseInt(c);

						switch (choice) {
						case 1:

							logger.info("Inter Server Communication");
							logger.info("Book Event");

							logger.info("Enter the customer ID");
							customerID = sc.nextLine();

							logger.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
								continue;
							}
							if(remoteObj.BookEvent(customerID, eventID, typeOfEvent).equalsIgnoreCase("success"))
							{
								logger.info("Customer with customer ID  :"+customerID   +" booked event type of  : "+typeOfEvent +" with event ID : "+eventID);
							}
							else
							{
								logger.info("Customer with customer ID  :"+customerID   +" could not book a event type of  : "+typeOfEvent +" with event ID : "+eventID);
							}

							remoteObj.ResultHashMap();
							stateCondtion = true;

							break;

						case 2:

							logger.info("Inter Server Communication");
							logger.info("Get Booking Schedule");

							logger.info("Enter the customer ID");
							customerID = sc.nextLine();


							 String[] bookingArrayCompare = remoteObj.GetBookingSchedule(customerID).split("\\s*,\\s*");
							 List<String> BookingArrList = new ArrayList<String>(Arrays.asList(bookingArrayCompare));

							List<String> HashOfCompareList = new ArrayList<String>(new HashSet<String>(BookingArrList));

							if(HashOfCompareList.size() > 0 && HashOfCompareList.get(0).length() > 0)
							{
								for(String hashObj : HashOfCompareList )
								{
									logger.info("hashObj :"+hashObj.trim());
									if(hashObj.trim() != null && hashObj.length() > 0)
									{
									String[] hashVal =  hashObj.split(" ");
									logger.info("Customer ID : "+hashVal[0] +" Customer event type : "+hashVal[1] +" Customer event ID : "+hashVal[2] );
									}
								}
							}
							else
							{
								logger.info("There are no events for the customer with the id : "+customerID);
							}

							stateCondtion = true;

							break;

						case 3:

							logger.info("Inter Server Communication");

							logger.info("Cancel Event");

							logger.info("Enter the customer ID");
							customerID = sc.nextLine();

							logger.info("Enter the event ID");
							eventID = sc.nextLine();
							
							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
								continue;
							}

							remoteObj.CancelEvent(customerID, eventID, typeOfEvent);

							remoteObj.ResultHashMap();

							stateCondtion = true;
							logger.info("canvel event booking is done!! Status available in the respective server");
							break;

						case 4:
							stateCondtion = false;

							break;

						default:
							logger.info("Invalid Option");
							stateCondtion = true;
							break;

						}

					}

				}



					break;

			case 3:
				int torPortNo = 1114;
				 register = LocateRegistry.getRegistry(torPortNo);
				 URL = "rmi://localhost:1114";

				remoteObj = (ClientServerInterface) register.lookup(URL);


				if (LoginType.toUpperCase().equals("M")) {
					int bookingCapacity = 0;
					boolean stateCondtion = true;

					while (stateCondtion) {

						logger.info("Manager Menu:\n");
						logger.info("1->Add an Event\n" + "2->Remove Event\n" + "3->List Event Availability\n"
								+ "4->Book Event\n" + "5-> Get Booking Schedule \n" + "6->Cancel Event \n" + "7-> Exit");
						logger.info("Please enter your option");
						String c = sc.nextLine();
						int choice = Integer.parseInt(c);

						switch (choice) {

						case 1:

							logger.info("Add Event");

							logger.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
									continue;
							}

							logger.info("Enter the booking capacity");
							bookingCapacity = Integer.parseInt(sc.nextLine());

							if (remoteObj.AddEvent(eventID, typeOfEvent, bookingCapacity).equalsIgnoreCase("success")) {
								logger.info("Event Added Successfully from Toronto server");
							} else {
								logger.info(" Event already exists! New Booking capacity is Updated");
							}
							
							logger.info("EventType : " + typeOfEvent+"\n" + "Event ID : " + eventID +"\n"
									+ "  booked with capacity " + bookingCapacity);
							remoteObj.ResultHashMap();

							logger.info("Add event End");
							stateCondtion = true;

							break;

						case 2:

							logger.info("Remove Event");

							logger.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
								continue;
							}


							if (remoteObj.RemoveEvent(eventID, typeOfEvent).equalsIgnoreCase("success")) {
								logger.info("EventType :" + typeOfEvent +"\n" + " with Event ID : " + eventID +"\n");
								logger.info("Successfully removed from Toronto server");
							} else {
								logger.info(
										"EventType :" + typeOfEvent + "with Event ID :" + eventID + " does not exist");
							}
							remoteObj.ResultHashMap();

							logger.info("Remove Event End");
							break;

						case 3:
							logger.info("Inter Server Communication");
							logger.info("List Avaliability");

							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
								continue;
							}

							String[] bookingArrayCompare;
							bookingArrayCompare = remoteObj.ListEventAvailability(typeOfEvent).split(",");


							List<String> avaliableEvents = new ArrayList<String>(Arrays.asList(bookingArrayCompare));
							logger.info("avaliableEvents size :"+avaliableEvents.size());

							if (avaliableEvents.size() > 0) {
								logger.info("Available Events");
								for (String str : avaliableEvents) {

									logger.info(str);
								}
							} else {
								logger.info("EventType : " + typeOfEvent + "is not avaliable");
							}

							logger.info("List Event Availablity Call Ended");

							stateCondtion = true;

							break;

							case 4:

								logger.info("Inter Server Communication");
								logger.info("Book Event");

								logger.info("Enter the customer ID");
								customerID = sc.nextLine();

								logger.info("Enter the event ID");
								eventID = sc.nextLine();

								while(true){
									logger.info("Enter the event Type");
									logger.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										logger.info("Enter the valid Event Type!!");
									continue;
								}


								if(remoteObj.BookEvent(customerID, eventID, typeOfEvent).equalsIgnoreCase("success"))
								{
									logger.info("Customer with customer ID  :"+customerID   +" booked event type of  : "+typeOfEvent +" with event ID : "+eventID);
								}
								else
								{
									logger.info("Customer with customer ID  :"+customerID   +" could not book a event type of  : "+typeOfEvent +" with event ID : "+eventID);
								}

								remoteObj.ResultHashMap();
								stateCondtion = true;

								break;

							case 5:

								logger.info("Inter Server Communication");
								logger.info("Get Booking Schedule");

								logger.info("Enter the customer ID");
								customerID = sc.nextLine();


								String[] bookingArrayCompare1 = remoteObj.GetBookingSchedule(customerID).split("\\s*,\\s*");
								List<String> BookingArrList = new ArrayList<String>(Arrays.asList(bookingArrayCompare1));
								List<String> HashOfCompareList = new ArrayList<String>(new HashSet<String>(BookingArrList));
								
								
								if(HashOfCompareList.size() > 0  && HashOfCompareList.get(0).length() > 0)
								{
									for(String hashObj : HashOfCompareList )
									{
										logger.info("hashObj :"+hashObj);
										if(hashObj.trim() != null  && hashObj.length() > 0 )
										{
											String[] hashVal =  hashObj.split(" ");
											logger.info("Customer ID : "+hashVal[0] +" Customer event type : "+hashVal[1] +" Customer event ID : "+hashVal[2] );
										}
									}
								}
								else
								{
									logger.info("There are no events for the customer with the id : "+customerID);
								}

								stateCondtion = true;

								break;

							case 6:

								logger.info("Inter Server Communication");

								logger.info("Cancel Event");

								logger.info("Enter the customer ID");
								customerID = sc.nextLine();

								logger.info("Enter the event ID");
								eventID = sc.nextLine();
								
								while(true){
									logger.info("Enter the event Type");
									logger.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										logger.info("Enter the valid Event Type!!");
									continue;
								}

								remoteObj.CancelEvent(customerID, eventID, typeOfEvent);

								remoteObj.ResultHashMap();
								logger.info("canvel event booking is done!! Status available in the respective server");
								stateCondtion = true;

								break;


							case 7:
							stateCondtion = false;

							break;

						default:
							logger.info("Invalid Option");
							stateCondtion = true;
							break;

						}

					}

				}

				else if (LoginType.toUpperCase().equals("C")) {

					boolean stateCondtion = true;

					while (stateCondtion) {

						logger.info("Customer Menu:\n");

						logger.info(
								"1->Book Event\n" + "2-> Get Booking Schedule \n" + "3->Cancel Event \n" + "4-> Exit");
						logger.info("Please enter your option");
						String c = sc.nextLine();
						int choice = Integer.parseInt(c);

						switch (choice) {
						case 1:

							logger.info("Inter Server Communication");
							logger.info("Book Event");

							logger.info("Enter the customer ID");
							customerID = sc.nextLine();

							logger.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
								continue;
							}

							if(remoteObj.BookEvent(customerID, eventID, typeOfEvent).equalsIgnoreCase("succes"))
							{
								logger.info("Customer with customer ID  :"+customerID   +" booked event type of  : "+typeOfEvent +" with event ID : "+eventID);
							}
							else
							{
								logger.info("Customer with customer ID  :"+customerID   +" could not book a event type of  : "+typeOfEvent +" with event ID : "+eventID);
							}

							remoteObj.ResultHashMap();
							stateCondtion = true;

							break;

						case 2:

							logger.info("Inter Server Communication");
							logger.info("Get Booking Schedule");

							logger.info("Enter the customer ID");
							customerID = sc.nextLine();


							 String[] bookingArrayCompare = remoteObj.GetBookingSchedule(customerID).split("\\s*,\\s*");
							 List<String> BookingArrList = new ArrayList<String>(Arrays.asList(bookingArrayCompare));
							List<String> HashOfCompareList = new ArrayList<String>(new HashSet<String>(BookingArrList));
							
							
							if(HashOfCompareList.size() > 0  && HashOfCompareList.get(0).length() > 0)
							{
								for(String hashObj : HashOfCompareList )
								{
									logger.info("hashObj :"+hashObj);
									if(hashObj.trim() != null  && hashObj.length() > 0 )
									{
									String[] hashVal =  hashObj.split(" ");
									logger.info("Customer ID : "+hashVal[0] +" Event type : "+hashVal[1] +" Event ID : "+hashVal[2] );
									}
								}
							}
							else
							{
								logger.info("There are no events for the customer with the id : "+customerID);
							}

							stateCondtion = true;

							break;

						case 3:

							logger.info("Inter Server Communication");

							logger.info("Cancel Event");

							logger.info("Enter the customer ID");
							customerID = sc.nextLine();

							logger.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								logger.info("Enter the event Type");
								logger.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									logger.info("Enter the valid Event Type!!");
								continue;
							}
							
							remoteObj.CancelEvent(customerID, eventID, typeOfEvent);

							remoteObj.ResultHashMap();
							logger.info("canvel event booking is done!! Status available in the respective server");
							stateCondtion = true;

							break;

						case 4:
							stateCondtion = false;

							break;

						default:
							logger.info("Invalid Option");
							stateCondtion = true;
							break;

						}

					}

				}

						break;
			default:

				logger.info("Invalid Input!!\n");
			}

			logger.info("Do you want to continue : yes / no");
			String option = sc.nextLine();

			if(option.equalsIgnoreCase("yes"))
			{
				continue;
			}
			else
			{
				break;
			}
				}
		} catch (Exception e) {
			logger.info("Client Exception: " + e);
			e.printStackTrace();
		}


		}
	}




