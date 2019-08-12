package com.concordia.client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.concordia.clientServerInterface.ClientInterface;

import java.net.URL;

public class Client {
	private static Logger LOGGER = LogManager.getLogger("client");

	public static void main(String[] args) {

		try {
			LOGGER.info("In client");

			Scanner sc = new Scanner(System.in);
			while(true)
			{
			LOGGER.info("Welcome to Concordia Event Booking System");
			LOGGER.info("Please enter your unique identification number");
			String userID = sc.nextLine();

			String Location = userID.substring(0, 3);
			String LoginType = userID.substring(3, 4);

			String eventID = "";
			String typeOfEvent = "";
			String customerID = "";
			Registry register;
			String URL = "";
			ClientInterface remoteObj;
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
				URL addURL = new URL("http://localhost:1111/Montreal?wsdl");
				QName addQName = new QName("http://MontrealServer.server.concordia.com/", "MontrealServerImplementationService");
				Service addition = Service.create(addURL, addQName);
				remoteObj = addition.getPort(ClientInterface.class);
				

				if (LoginType.toUpperCase().equals("M")) {

					int bookingCapacity = 0;

					boolean stateCondtion = true;

					while (stateCondtion) {

						LOGGER.info("Manager Menu:\n");
						LOGGER.info("1->Add an Event\n" + "2->Remove Event\n" + "3->List Event Availability\n"
								+ "4->Book Event\n" + "5->Get Booking Schedule \n" + "6->Cancel Event \n" + "7->Swap Event \n"+ "8-> MultiThreading and Synchronization \n"+"9-> Exit");
						LOGGER.info("Please enter your option");
						String c = sc.nextLine();
						int choice = Integer.parseInt(c);

						switch (choice) {
						case 1:
							LOGGER.info("Add Event");
							
							while(true){
								LOGGER.info("Enter the event ID");
								eventID = sc.nextLine();

								if(eventID.substring(0,3).equalsIgnoreCase("MTL")){
									break;
								}else{
									LOGGER.info("The Event ID"+ eventID +"is Invalid!!");
									continue;
								}

							}
							
							
							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}
							LOGGER.info("Enter the booking capacity");
							bookingCapacity = Integer.parseInt(sc.nextLine());

							String Sucr = remoteObj.AddEvent(eventID, typeOfEvent, bookingCapacity); 
							if (Sucr.equalsIgnoreCase("success")) {
								LOGGER.info("Event Added Successfully from montreal server");
							} else {
								LOGGER.info(" Event already exists! New Booking capacity is Updated");
							}
							
							LOGGER.info("EventType : " + typeOfEvent+"\n" + "Event ID : " + eventID +"\n"
									+ "  booked with capacity " + bookingCapacity);
							remoteObj.ResultHashMap();
							stateCondtion = true;
							LOGGER.info("Add event End");
							break;

						case 2:

							LOGGER.info("Remove Event");
							LOGGER.info("Enter the event ID");
							eventID = sc.nextLine();


							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}

							if (remoteObj.RemoveEvent(eventID, typeOfEvent).equalsIgnoreCase("success")) {
								LOGGER.info("EventType :" + typeOfEvent +"\n" + " with Event ID : " + eventID +"\n");
								LOGGER.info("Successfully removed from Montreal server");
							} else {
								LOGGER.info(
										"EventType :" + typeOfEvent + "with Event ID :" + eventID + " does not exist");
							}
							remoteObj.ResultHashMap();

							LOGGER.info("Remove Event End");
							break;

						case 3:
							LOGGER.info("Inter Server Communication");
							LOGGER.info("List Avaliability");

							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}

							String[] bookingArrayCompare;
							bookingArrayCompare = remoteObj.ListEventAvailability(typeOfEvent).split(",");

							List<String> avaliableEvents = new ArrayList<String>(Arrays.asList(bookingArrayCompare));
							LOGGER.info("avaliableEvents size :"+avaliableEvents.size());

							if (avaliableEvents.size() > 0) {
								LOGGER.info("Available Events");
								for (String s : avaliableEvents) {

									LOGGER.info(s);
								}
							} else {
								LOGGER.info("EventType : " + typeOfEvent + "is not avaliable");
							}


							LOGGER.info("Event Availablity Call Ended");

							stateCondtion = true;

							break;
							case 4:

								LOGGER.info("Inter Server Communication");
								LOGGER.info("Book Event");

								LOGGER.info("Enter the customer ID");
								customerID = sc.nextLine();

								LOGGER.info("Enter the event ID");
								eventID = sc.nextLine();

								while(true){
									LOGGER.info("Enter the event Type");
									LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										LOGGER.info("Enter the valid Event Type!!");
									continue;
								}


								if(remoteObj.BookEvent(customerID, eventID, typeOfEvent).equalsIgnoreCase("success"))
								{
									LOGGER.info("Customer with customer ID  :"+customerID   +" booked event type of  : "+typeOfEvent +" with event ID : "+eventID);
								}
								else
								{
									LOGGER.info("Customer with customer ID  :"+customerID   +" could not book a event type of  : "+typeOfEvent +" with event ID : "+eventID);
								}

								remoteObj.ResultHashMap();
								stateCondtion = true;

								break;

							case 5:

								LOGGER.info("Inter Server Communication");
								LOGGER.info("Get Booking Schedule");

								LOGGER.info("Enter the customer ID");
								customerID = sc.nextLine();


								String[] bookingArrayCompare1 = remoteObj.GetBookingSchedule(customerID).split("\\s*,\\s*");
								List<String> BookingArrList = new ArrayList<String>(Arrays.asList(bookingArrayCompare1));

								List<String> HashOfCompareList = new ArrayList<String>(new HashSet<String>(BookingArrList));

								if(HashOfCompareList.size() > 0  && HashOfCompareList.get(0).length() > 0)
								{
									for(String hashObj : HashOfCompareList )
									{
										LOGGER.info("hashObj :"+hashObj.trim());
										if(hashObj.trim() != null && hashObj.length() > 0)
										{
											String[] hashVal =  hashObj.split(" ");
											LOGGER.info("Customer ID : "+hashVal[0] +" Customer event type : "+hashVal[1] +" Customer event ID : "+hashVal[2] );
										}
									}
								}
								else
								{
									LOGGER.info("There are no events for the customer with the id : "+customerID);
								}

								stateCondtion = true;

								break;

							case 6:

								LOGGER.info("Inter Server Communication");
								LOGGER.info("Cancel Event");
								LOGGER.info("Enter the customer ID");
								customerID = sc.nextLine();

								LOGGER.info("Enter the event ID");
								eventID = sc.nextLine();
								
								while(true){
									LOGGER.info("Enter the event Type");
									LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										LOGGER.info("Enter the valid Event Type!!");
									continue;
								}

								if(remoteObj.CancelEvent(customerID, eventID, typeOfEvent)) {
									LOGGER.info("Customer with customerID : "+customerID + "cancelled the event "+ typeOfEvent+ "with eventID "+ eventID);
								}else {
									LOGGER.info("cancelled event failed");
									LOGGER.info("Customer with customerID : "+customerID + "couldnot cancelled the event "+ typeOfEvent+ "with eventID "+ eventID);
								}

								remoteObj.ResultHashMap();
								stateCondtion = true;

								break;
							case 7:
								LOGGER.info("Swap Event");
								LOGGER.info("Enter the customer ID");
								customerID = sc.nextLine();
								
								LOGGER.info("Enter the new event ID");
								String newEventID = sc.nextLine();							
								LOGGER.info("Enter the new event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								String newEventType = sc.nextLine();
								LOGGER.info("Enter the old event ID");
								eventID = sc.nextLine();
								
								while(true){
									LOGGER.info("Enter the old event Type");
									LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										LOGGER.info("Enter the valid Event Type!!");
									continue;
								}
								
								LOGGER.info(remoteObj.SwapEvent(customerID, newEventID, newEventType, eventID, typeOfEvent));
								stateCondtion = true;
								break;
								
							case 8:
								
								LOGGER.info("Multithreading and synchronizaiton");
								
										Runnable task1 = () -> {
											runOne(remoteObj, "MTLM123456", "MTLM100519", "S");
										
										};
										Runnable task2 = () -> {
											runTwo(remoteObj,"MTLM123456", "MTLE100519", "C");
									
										};
										Runnable task3 =() ->{
											runThree(remoteObj, "MTLM123456", "MTLE100519", "C", "MTLM100519", "S");
										};
										
										Thread t1 = new Thread(task1);
										Thread t2 = new Thread(task2);
										
										Thread.sleep(5000);
										Thread t3 = new Thread(task3);
										t1.start();
										t2.start();
										t3.start();


								stateCondtion = true;
								break;
								
							case 9:
							stateCondtion = false;

							break;

						default:
							LOGGER.info("Invalid Option");
							stateCondtion = true;
							break;

						}

					}

				}

				else if (LoginType.toUpperCase().equals("C")) {

					boolean stateCondtion = true;

					while (stateCondtion) {

						LOGGER.info("Customer Menu:\n");

						LOGGER.info(
								"1->Book Event\n" + "2-> Get Booking Schedule \n" + "3->Cancel Event \n" + "4->Swap Event \n"+ "5-> Exit");
						LOGGER.info("Please enter your option");
						String c = sc.nextLine();
						int choice = Integer.parseInt(c);

						switch (choice) {
						case 1:

							LOGGER.info("Inter Server Communication");
							LOGGER.info("Book Event");

							LOGGER.info("Enter the customer ID");
							customerID = sc.nextLine();

							LOGGER.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}

							if(remoteObj.BookEvent(customerID, eventID, typeOfEvent).equalsIgnoreCase("success"))
							{
								LOGGER.info("Customer with customer ID  :"+customerID   +" booked event type of  : "+typeOfEvent +" with event ID : "+eventID);
							}
							else
							{
								LOGGER.info("Customer with customer ID  :"+customerID   +" could not book a event type of  : "+typeOfEvent +" with event ID : "+eventID);
							}

							remoteObj.ResultHashMap();
							stateCondtion = true;

							break;

						case 2:

							LOGGER.info("Inter Server Communication");
							LOGGER.info("Get Booking Schedule");

							LOGGER.info("Enter the customer ID");
							customerID = sc.nextLine();


							 String[] bookingArrayCompare = remoteObj.GetBookingSchedule(customerID).split("\\s*,\\s*");
							 List<String> BookingArrList = new ArrayList<String>(Arrays.asList(bookingArrayCompare));

							List<String> HashOfCompareList = new ArrayList<String>(new HashSet<String>(BookingArrList));

							if(HashOfCompareList.size() > 0 && HashOfCompareList.get(0).length() > 0)
							{
								for(String hashObj : HashOfCompareList )
								{
									LOGGER.info("hashObj :"+hashObj.trim());
									if(hashObj.trim() != null && hashObj.length() > 0)
									{
									String[] hashVal =  hashObj.split(" ");
									LOGGER.info("Customer ID : "+hashVal[0] +" Customer event type : "+hashVal[1] +" Customer event ID : "+hashVal[2] );
									}
								}
							}
							else
							{
								LOGGER.info("There are no events for the customer with the id : "+customerID);
							}

							stateCondtion = true;

							break;

						case 3:

							LOGGER.info("Inter Server Communication");

							LOGGER.info("Cancel Event");

							LOGGER.info("Enter the customer ID");
							customerID = sc.nextLine();

							LOGGER.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}
							
							if(remoteObj.CancelEvent(customerID, eventID, typeOfEvent)) {
								LOGGER.info("Customer with customerID : "+customerID + "cancelled the event "+ typeOfEvent+ "with eventID "+ eventID);
							}else {
								LOGGER.info("cancelled event failed");
								LOGGER.info("Customer with customerID : "+customerID + "couldnot cancelled the event "+ typeOfEvent+ "with eventID "+ eventID);
							}

							remoteObj.ResultHashMap();
							stateCondtion = true;

							break;
						case 4:
							LOGGER.info("Swap Event");
							LOGGER.info("Enter the customer ID");
							customerID = sc.nextLine();
							
							LOGGER.info("Enter the new event ID");
							String newEventID = sc.nextLine();
							
							LOGGER.info("Enter the new event Type");
							LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
							String newEventType =sc.nextLine();
							LOGGER.info("Enter the old event ID");
							eventID = sc.nextLine();
							
							while(true){
								LOGGER.info("Enter the old event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}
							LOGGER.info(remoteObj.SwapEvent(customerID, newEventID, newEventType, eventID, typeOfEvent));
							stateCondtion = true;
							break;
						case 5:
							stateCondtion = false;

							break;

						default:
							LOGGER.info("Invalid Option");
							stateCondtion = true;
							break;

						}

					}

				}

					break;
			case 2 :
				URL addURL1 = new URL("http://localhost:1112/Ottawa?wsdl");
				QName addQName1 = new QName("http://ottawaServer.server.concordia.com/", "OttawaServerImplementationService");
				Service addition1 = Service.create(addURL1, addQName1);
				remoteObj = addition1.getPort(ClientInterface.class);
				
				
				if (LoginType.toUpperCase().equals("M")) {
					int bookingCapacity = 0;

					boolean stateCondtion = true;

					while (stateCondtion) {

						LOGGER.info("Manager Menu:\n");
						LOGGER.info("1->Add an Event\n" + "2->Remove Event\n" + "3->List Event Availability\n"
								+ "4->Book Event\n" + "5-> Get Booking Schedule \n" + "6->Cancel Event \n" + "7->Swap Event \n"+ "8-> Exit");
						LOGGER.info("Please enter your option");
						String c = sc.nextLine();
						int choice = Integer.parseInt(c);

						switch (choice) {

						case 1:

							LOGGER.info("Add Event");


							while(true){
								LOGGER.info("Enter the event ID");
								eventID = sc.nextLine();

								if(eventID.substring(0,3).equalsIgnoreCase("OTW")){
									break;
								}else{
									LOGGER.info("The Event ID"+ eventID +"is Invalid!!");
									continue;
								}

							}
							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}

							LOGGER.info("Enter the booking capacity");
							bookingCapacity = Integer.parseInt(sc.nextLine());

							
							if (remoteObj.AddEvent(eventID, typeOfEvent, bookingCapacity).equalsIgnoreCase("success")) {
								LOGGER.info("Event Added Successfully from Ottawa server");
							} else {
								LOGGER.info(" Event already exists! New Booking capacity is Updated");
							}
							
							LOGGER.info("EventType : " + typeOfEvent+"\n" + "Event ID : " + eventID +"\n"
									+ "  booked with capacity " + bookingCapacity);
							remoteObj.ResultHashMap();
							LOGGER.info("Add event End");
							stateCondtion = true;

							break;

						case 2:

							LOGGER.info("Remove Event");

							LOGGER.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}

							if (remoteObj.RemoveEvent(eventID, typeOfEvent).equalsIgnoreCase("success")) {
								LOGGER.info("EventType :" + typeOfEvent +"\n" + " with Event ID : " + eventID +"\n");
								LOGGER.info("Successfully removed from Ottawa server");
							} else {
								LOGGER.info(
										"EventType :" + typeOfEvent + "with Event ID :" + eventID + " does not exist");
							}
							remoteObj.ResultHashMap();
							LOGGER.info("Remove Event End");
							break;

						case 3:
							LOGGER.info("Inter Server Communication");
							LOGGER.info("List Avaliability");

							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}

							String[] bookingArrayCompare;

							bookingArrayCompare = remoteObj.ListEventAvailability(typeOfEvent).split(",");
							List<String> avaliableEvents = new ArrayList<String>(Arrays.asList(bookingArrayCompare));
							LOGGER.info("avaliableEvents size :"+avaliableEvents.size());
							if (avaliableEvents.size() > 0) {
								LOGGER.info("Available Events");
								
								for (String str : avaliableEvents) {

									LOGGER.info(str);
								}
							} else {
								LOGGER.info("EventType : " + typeOfEvent + "is not avaliable");
							}


							LOGGER.info("List Event Availablity Call Ended");

							stateCondtion = true;

							break;

							case 4:

								LOGGER.info("Inter Server Communication");
								LOGGER.info("Book Event");

								LOGGER.info("Enter the customer ID");
								customerID = sc.nextLine();

								LOGGER.info("Enter the event ID");
								eventID = sc.nextLine();

								while(true){
									LOGGER.info("Enter the event Type");
									LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										LOGGER.info("Enter the valid Event Type!!");
									continue;
								}

								if(remoteObj.BookEvent(customerID, eventID, typeOfEvent).equalsIgnoreCase("success"))
								{
									LOGGER.info("Customer with customer ID  :"+customerID   +" booked event type of  : "+typeOfEvent +" with event ID : "+eventID);
								}
								else
								{
									LOGGER.info("Customer with customer ID  :"+customerID   +" could not book a event type of  : "+typeOfEvent +" with event ID : "+eventID);
								}

								remoteObj.ResultHashMap();
								stateCondtion = true;

								break;

							case 5:

								LOGGER.info("Inter Server Communication");
								LOGGER.info("Get Booking Schedule");

								LOGGER.info("Enter the customer ID");
								customerID = sc.nextLine();


								String[] bookingArrayCompare_mang = remoteObj.GetBookingSchedule(customerID).split("\\s*,\\s*");
								List<String> BookingArrList = new ArrayList<String>(Arrays.asList(bookingArrayCompare_mang));

								List<String> HashOfCompareList = new ArrayList<String>(new HashSet<String>(BookingArrList));

								if(HashOfCompareList.size() > 0 && HashOfCompareList.get(0).length() >0)
								{
									for(String hashObj : HashOfCompareList )
									{
										LOGGER.info("hashObj :"+hashObj.trim());
										if(hashObj.trim() != null  && hashObj.length() > 0)
										{
											String[] hashVal =  hashObj.split(" ");
											LOGGER.info("Customer ID : "+hashVal[0] +" Customer event type : "+hashVal[1] +" Customer event ID : "+hashVal[2] );
										}
									}
								}
								else
								{
									LOGGER.info("There are no events for the customer with the id : "+customerID);
								}

								stateCondtion = true;

								break;

							case 6:

								LOGGER.info("Inter Server Communication");
								LOGGER.info("Cancel Event");
								LOGGER.info("Enter the customer ID");
								customerID = sc.nextLine();
								LOGGER.info("Enter the event ID");
								
								while(true){
									LOGGER.info("Enter the event Type");
									LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										LOGGER.info("Enter the valid Event Type!!");
									continue;
								}
								
								
								eventID = sc.nextLine();
								if(remoteObj.CancelEvent(customerID, eventID, typeOfEvent)) {
									LOGGER.info("Customer with customerID : "+customerID + "cancelled the event "+ typeOfEvent+ "with eventID "+ eventID);
								}else {
									LOGGER.info("cancelled event failed");
									LOGGER.info("Customer with customerID : "+customerID + "couldnot cancelled the event "+ typeOfEvent+ "with eventID "+ eventID);
								}
								remoteObj.ResultHashMap();
								stateCondtion = true;
								break;

							case 7:
								LOGGER.info("Swap Event");
								LOGGER.info("Enter the customer ID");
								customerID = sc.nextLine();
								
								LOGGER.info("Enter the new event ID");
								String newEventID = sc.nextLine();
								
								LOGGER.info("Enter the new event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								String newEventType = sc.nextLine();
								
								LOGGER.info("Enter the old event ID");
								eventID = sc.nextLine();
								
								while(true){
									LOGGER.info("Enter the old event Type");
									LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										LOGGER.info("Enter the valid Event Type!!");
									continue;
								}
								LOGGER.info(remoteObj.SwapEvent(customerID, newEventID, newEventType, eventID, typeOfEvent));		
								stateCondtion = true;
								break;
								
							case 8:
							stateCondtion = false;

							break;

						default:
							LOGGER.info("Invalid Option");
							stateCondtion = true;
							break;

						}

					}

				}

				else if (LoginType.toUpperCase().equals("C")) {

					boolean stateCondtion = true;

					while (stateCondtion) {

						LOGGER.info("Customer Menu:\n");

						LOGGER.info(
								"1->Book Event\n" + "2-> Get Booking Schedule \n" + "3->Cancel Event \n" + "4->Swap Event \n" + "5-> Exit");
						LOGGER.info("Please enter your option");
						String c = sc.nextLine();
						int choice = Integer.parseInt(c);

						switch (choice) {
						case 1:

							LOGGER.info("Inter Server Communication");
							LOGGER.info("Book Event");

							LOGGER.info("Enter the customer ID");
							customerID = sc.nextLine();

							LOGGER.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}
							if(remoteObj.BookEvent(customerID, eventID, typeOfEvent).equalsIgnoreCase("success"))
							{
								LOGGER.info("Customer with customer ID  :"+customerID   +" booked event type of  : "+typeOfEvent +" with event ID : "+eventID);
							}
							else
							{
								LOGGER.info("Customer with customer ID  :"+customerID   +" could not book a event type of  : "+typeOfEvent +" with event ID : "+eventID);
							}

							remoteObj.ResultHashMap();
							stateCondtion = true;

							break;

						case 2:

							LOGGER.info("Inter Server Communication");
							LOGGER.info("Get Booking Schedule");

							LOGGER.info("Enter the customer ID");
							customerID = sc.nextLine();


							 String[] bookingArrayCompare = remoteObj.GetBookingSchedule(customerID).split("\\s*,\\s*");
							 List<String> BookingArrList = new ArrayList<String>(Arrays.asList(bookingArrayCompare));

							List<String> HashOfCompareList = new ArrayList<String>(new HashSet<String>(BookingArrList));

							if(HashOfCompareList.size() > 0 && HashOfCompareList.get(0).length() > 0)
							{
								for(String hashObj : HashOfCompareList )
								{
									LOGGER.info("hashObj :"+hashObj.trim());
									if(hashObj.trim() != null && hashObj.length() > 0)
									{
									String[] hashVal =  hashObj.split(" ");
									LOGGER.info("Customer ID : "+hashVal[0] +" Customer event type : "+hashVal[1] +" Customer event ID : "+hashVal[2] );
									}
								}
							}
							else
							{
								LOGGER.info("There are no events for the customer with the id : "+customerID);
							}

							stateCondtion = true;

							break;

						case 3:

							LOGGER.info("Inter Server Communication");

							LOGGER.info("Cancel Event");

							LOGGER.info("Enter the customer ID");
							customerID = sc.nextLine();

							LOGGER.info("Enter the event ID");
							eventID = sc.nextLine();
							
							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}

							if(remoteObj.CancelEvent(customerID, eventID, typeOfEvent)) {
								LOGGER.info("Customer with customerID : "+customerID + "cancelled the event "+ typeOfEvent+ "with eventID "+ eventID);
							}else {
								LOGGER.info("cancelled event failed");
								LOGGER.info("Customer with customerID : "+customerID + "couldnot cancelled the event "+ typeOfEvent+ "with eventID "+ eventID);
							}

							remoteObj.ResultHashMap();

							stateCondtion = true;
							break;
						case 4:
							LOGGER.info("Swap Event");
							LOGGER.info("Enter the customer ID");
							customerID = sc.nextLine();
							
							LOGGER.info("Enter the new event ID");
							String newEventID = sc.nextLine();
								
							LOGGER.info("Enter the new event Type");
							LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
							String newEventType = sc.nextLine();
							
							LOGGER.info("Enter the old event ID");
							eventID = sc.nextLine();
							
							while(true){
								LOGGER.info("Enter the old event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}
							
							LOGGER.info(remoteObj.SwapEvent(customerID, newEventID, newEventType, eventID, typeOfEvent));
							
							stateCondtion = true;
							break;
						case 5:
							stateCondtion = false;
							break;

						default:
							LOGGER.info("Invalid Option");
							stateCondtion = true;
							break;

						}

					}

				}



					break;

			case 3:

				URL addURL2 = new URL("http://localhost:1114/Toronto?wsdl");
				QName addQName2 = new QName("http://torontoServer.server.concordia.com/", "TorontoServerImplementationService");
				Service addition2 = Service.create(addURL2, addQName2);
				remoteObj = addition2.getPort(ClientInterface.class);

				if (LoginType.toUpperCase().equals("M")) {
					int bookingCapacity = 0;
					boolean stateCondtion = true;

					while (stateCondtion) {

						LOGGER.info("Manager Menu:\n");
						LOGGER.info("1->Add an Event\n" + "2->Remove Event\n" + "3->List Event Availability\n"
								+ "4->Book Event\n" + "5-> Get Booking Schedule \n" + "6->Cancel Event \n" + "7->Swap Event \n"+ "8-> Exit");
						LOGGER.info("Please enter your option");
						String c = sc.nextLine();
						int choice = Integer.parseInt(c);

						switch (choice) {

						case 1:

							LOGGER.info("Add Event");
							
							while(true){
								LOGGER.info("Enter the event ID");
								eventID = sc.nextLine();

								if(eventID.substring(0,3).equalsIgnoreCase("TOR")){
									break;
								}else{
									LOGGER.info("The Event ID"+ eventID +"is Invalid!!");
									continue;
								}

							}

							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
									continue;
							}

							LOGGER.info("Enter the booking capacity");
							bookingCapacity = Integer.parseInt(sc.nextLine());

							if (remoteObj.AddEvent(eventID, typeOfEvent, bookingCapacity).equalsIgnoreCase("success")) {
								LOGGER.info("Event Added Successfully from Toronto server");
							} else {
								LOGGER.info(" Event already exists! New Booking capacity is Updated");
							}
							
							LOGGER.info("EventType : " + typeOfEvent+"\n" + "Event ID : " + eventID +"\n"
									+ "  booked with capacity " + bookingCapacity);
							remoteObj.ResultHashMap();

							LOGGER.info("Add event End");
							stateCondtion = true;

							break;

						case 2:

							LOGGER.info("Remove Event");

							LOGGER.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}


							if (remoteObj.RemoveEvent(eventID, typeOfEvent).equalsIgnoreCase("success")) {
								LOGGER.info("EventType :" + typeOfEvent +"\n" + " with Event ID : " + eventID +"\n");
								LOGGER.info("Successfully removed from Toronto server");
							} else {
								LOGGER.info(
										"EventType :" + typeOfEvent + "with Event ID :" + eventID + " does not exist");
							}
							remoteObj.ResultHashMap();

							LOGGER.info("Remove Event End");
							break;

						case 3:
							LOGGER.info("Inter Server Communication");
							LOGGER.info("List Avaliability");

							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}

							String[] bookingArrayCompare;
							bookingArrayCompare = remoteObj.ListEventAvailability(typeOfEvent).split(",");


							List<String> avaliableEvents = new ArrayList<String>(Arrays.asList(bookingArrayCompare));
							LOGGER.info("avaliableEvents size :"+avaliableEvents.size());

							if (avaliableEvents.size() > 0) {
								LOGGER.info("Available Events");
								for (String str : avaliableEvents) {

									LOGGER.info(str);
								}
							} else {
								LOGGER.info("EventType : " + typeOfEvent + "is not avaliable");
							}

							LOGGER.info("List Event Availablity Call Ended");

							stateCondtion = true;

							break;

							case 4:

								LOGGER.info("Inter Server Communication");
								LOGGER.info("Book Event");

								LOGGER.info("Enter the customer ID");
								customerID = sc.nextLine();

								LOGGER.info("Enter the event ID");
								eventID = sc.nextLine();

								while(true){
									LOGGER.info("Enter the event Type");
									LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										LOGGER.info("Enter the valid Event Type!!");
									continue;
								}


								if(remoteObj.BookEvent(customerID, eventID, typeOfEvent).equalsIgnoreCase("success"))
								{
									LOGGER.info("Customer with customer ID  :"+customerID   +" booked event type of  : "+typeOfEvent +" with event ID : "+eventID);
								}
								else
								{
									LOGGER.info("Customer with customer ID  :"+customerID   +" could not book a event type of  : "+typeOfEvent +" with event ID : "+eventID);
								}

								remoteObj.ResultHashMap();
								stateCondtion = true;

								break;

							case 5:

								LOGGER.info("Inter Server Communication");
								LOGGER.info("Get Booking Schedule");

								LOGGER.info("Enter the customer ID");
								customerID = sc.nextLine();


								String[] bookingArrayCompare1 = remoteObj.GetBookingSchedule(customerID).split("\\s*,\\s*");
								List<String> BookingArrList = new ArrayList<String>(Arrays.asList(bookingArrayCompare1));
								List<String> HashOfCompareList = new ArrayList<String>(new HashSet<String>(BookingArrList));
								
								
								if(HashOfCompareList.size() > 0  && HashOfCompareList.get(0).length() > 0)
								{
									for(String hashObj : HashOfCompareList )
									{
										LOGGER.info("hashObj :"+hashObj);
										if(hashObj.trim() != null  && hashObj.length() > 0 )
										{
											String[] hashVal =  hashObj.split(" ");
											LOGGER.info("Customer ID : "+hashVal[0] +" Customer event type : "+hashVal[1] +" Customer event ID : "+hashVal[2] );
										}
									}
								}
								else
								{
									LOGGER.info("There are no events for the customer with the id : "+customerID);
								}

								stateCondtion = true;

								break;

							case 6:

								LOGGER.info("Inter Server Communication");

								LOGGER.info("Cancel Event");

								LOGGER.info("Enter the customer ID");
								customerID = sc.nextLine();

								LOGGER.info("Enter the event ID");
								eventID = sc.nextLine();
								
								while(true){
									LOGGER.info("Enter the event Type");
									LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										LOGGER.info("Enter the valid Event Type!!");
									continue;
								}

								if(remoteObj.CancelEvent(customerID, eventID, typeOfEvent)) {
									LOGGER.info("Customer with customerID : "+customerID + "cancelled the event "+ typeOfEvent+ "with eventID "+ eventID);
								}else {
									LOGGER.info("cancelled event failed");
									LOGGER.info("Customer with customerID : "+customerID + "couldnot cancelled the event "+ typeOfEvent+ "with eventID "+ eventID);
								}

								remoteObj.ResultHashMap();
								stateCondtion = true;

								break;

							case 7:
								LOGGER.info("Swap Event");
								LOGGER.info("Enter the customer ID");
								customerID = sc.nextLine();
								
								LOGGER.info("Enter the new event ID");
								String newEventID = sc.nextLine();
								
								LOGGER.info("Enter the new event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								String newEventType = sc.nextLine();
																
								LOGGER.info("Enter the old event ID");
								eventID = sc.nextLine();
								
								while(true){
									LOGGER.info("Enter the old event Type");
									LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
									typeOfEvent = sc.nextLine();
									if(typeOfEvent.equalsIgnoreCase("S")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("C")){
										break;
									}else if(typeOfEvent.equalsIgnoreCase("T")){
										break;
									}else
										LOGGER.info("Enter the valid Event Type!!");
									continue;
								}
								LOGGER.info(remoteObj.SwapEvent(customerID, newEventID, newEventType, eventID, typeOfEvent));
								stateCondtion = true;
								break;	
							case 8:
							stateCondtion = false;

							break;

						default:
							LOGGER.info("Invalid Option");
							stateCondtion = true;
							break;

						}

					}

				}

				else if (LoginType.toUpperCase().equals("C")) {

					boolean stateCondtion = true;

					while (stateCondtion) {

						LOGGER.info("Customer Menu:\n");

						LOGGER.info(
								"1->Book Event\n" + "2-> Get Booking Schedule \n" + "3->Cancel Event \n" + "4->Swap Event \n"+ "5-> Exit");
						LOGGER.info("Please enter your option");
						String c = sc.nextLine();
						int choice = Integer.parseInt(c);

						switch (choice) {
						case 1:

							LOGGER.info("Inter Server Communication");
							LOGGER.info("Book Event");

							LOGGER.info("Enter the customer ID");
							customerID = sc.nextLine();

							LOGGER.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}

							if(remoteObj.BookEvent(customerID, eventID, typeOfEvent).equalsIgnoreCase("success"))
							{
								LOGGER.info("Customer with customer ID  :"+customerID   +" booked event type of  : "+typeOfEvent +" with event ID : "+eventID);
							}
							else
							{
								LOGGER.info("Customer with customer ID  :"+customerID   +" could not book a event type of  : "+typeOfEvent +" with event ID : "+eventID);
							}

							remoteObj.ResultHashMap();
							stateCondtion = true;

							break;

						case 2:

							LOGGER.info("Inter Server Communication");
							LOGGER.info("Get Booking Schedule");

							LOGGER.info("Enter the customer ID");
							customerID = sc.nextLine();


							 String[] bookingArrayCompare = remoteObj.GetBookingSchedule(customerID).split("\\s*,\\s*");
							 List<String> BookingArrList = new ArrayList<String>(Arrays.asList(bookingArrayCompare));
							List<String> HashOfCompareList = new ArrayList<String>(new HashSet<String>(BookingArrList));
							
							
							if(HashOfCompareList.size() > 0  && HashOfCompareList.get(0).length() > 0)
							{
								for(String hashObj : HashOfCompareList )
								{
									LOGGER.info("hashObj :"+hashObj);
									if(hashObj.trim() != null  && hashObj.length() > 0 )
									{
									String[] hashVal =  hashObj.split(" ");
									LOGGER.info("Customer ID : "+hashVal[0] +" Event type : "+hashVal[1] +" Event ID : "+hashVal[2] );
									}
								}
							}
							else
							{
								LOGGER.info("There are no events for the customer with the id : "+customerID);
							}

							stateCondtion = true;

							break;

						case 3:

							LOGGER.info("Inter Server Communication");

							LOGGER.info("Cancel Event");

							LOGGER.info("Enter the customer ID");
							customerID = sc.nextLine();

							LOGGER.info("Enter the event ID");
							eventID = sc.nextLine();

							while(true){
								LOGGER.info("Enter the event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}
							
							if(remoteObj.CancelEvent(customerID, eventID, typeOfEvent)) {
								LOGGER.info("Customer with customerID : "+customerID + "cancelled the event "+ typeOfEvent+ "with eventID "+ eventID);
							}else {
								LOGGER.info("cancelled event failed");
								LOGGER.info("Customer with customerID : "+customerID + "couldnot cancelled the event "+ typeOfEvent+ "with eventID "+ eventID);
							}

							remoteObj.ResultHashMap();
							stateCondtion = true;

							break;
						case 4:
							LOGGER.info("Swap Event");
							LOGGER.info("Enter the customer ID");
							customerID = sc.nextLine();
							
							LOGGER.info("Enter the new event ID");
							String newEventID = sc.nextLine();
							
							LOGGER.info("Enter the new event Type");
							LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
							String newEventType = sc.nextLine();
							
							LOGGER.info("Enter the old event ID");
							eventID = sc.nextLine();
							
							while(true){
								LOGGER.info("Enter the old event Type");
								LOGGER.info("S->Seminar, C->Conference, T-> Trade Show");
								typeOfEvent = sc.nextLine();
								if(typeOfEvent.equalsIgnoreCase("S")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("C")){
									break;
								}else if(typeOfEvent.equalsIgnoreCase("T")){
									break;
								}else
									LOGGER.info("Enter the valid Event Type!!");
								continue;
							}
							LOGGER.info(remoteObj.SwapEvent(customerID, newEventID, newEventType, eventID, typeOfEvent));
							stateCondtion = true;
							break;	
						case 5:
							stateCondtion = false;

							break;

						default:
							LOGGER.info("Invalid Option");
							stateCondtion = true;
							break;

						}

					}

				}

						break;
			default:

				LOGGER.info("Invalid Input!!\n");
			}

			LOGGER.info("Do you want to continue : yes / no");
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
			LOGGER.info("Client Exception: " + e);
			e.printStackTrace();
		}


		}
	
	public static void runOne(ClientInterface remoteObj,String customerid,String eventid,String eventType)
	{
	try {
	remoteObj.AddEvent(eventid, eventType, 10);
	remoteObj.BookEvent(customerid, eventid, eventType);
	} catch (Exception e) {
	e.printStackTrace();
	}
	}

	public static void runTwo(ClientInterface remoteObj,String customerid,String eventid,String eventType)
	{
	try {
	remoteObj.AddEvent(eventid, eventType, 10);
	remoteObj.BookEvent(customerid, eventid, eventType);
	} catch (Exception e) {
	e.printStackTrace();
	}
	}

	public static void runThree(ClientInterface remoteObj,String customerid,String newEventId,String newEventType,String eventid,String eventType)
	{
	try {

	String s1 = remoteObj.SwapEvent(customerid, newEventId, newEventType, eventid, eventType);
	LOGGER.info(s1);
	
	String s2  = remoteObj.SwapEvent(customerid, newEventId, newEventType, eventid, eventType);
	LOGGER.info(s2);

	} catch (Exception e) {
	e.printStackTrace();
	}
	}
	}




