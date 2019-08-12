package client;

import java.util.*;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import frontendImpl.frontendApp;
import frontendImpl.frontendAppHelper;
import servers.rm1.ImplementRemoteInterface.Helpers;;

public class Client {

	static frontendApp frontEndObj;

	public static String get_event_type(String key, String userName) {
		String eventType = "";
		String message = "";

		if (key.equals("c"))
			eventType = "conferences";
		else if (key.equals("s"))
			eventType = "seminars";
		else if (key.equals("t"))
			eventType = "trade shows";
		else {
			System.out.println("Error: Invalid event type");
			eventType = "invalid";

			message = " Event type : " + key + " , " + "Action Performed : check_event" + " , "
					+ "Message : You have entered invalid event type\n";

			Helpers.ServerLog(userName.substring(0, 3), message);
			Helpers.usersLog(userName, message);
		}

		return eventType;
	}

	public String get_user(String name) throws Exception {

		if (Character.toString(name.charAt(3)).equals("C")) {
			return "user";
		} else if (Character.toString(name.charAt(3)).equals("M")) {
			return "manager";
		}
		return "invalid";
	}

	public void set_server_obj(NamingContextExt ncRef) throws Exception {

		frontEndObj = frontendAppHelper.narrow(ncRef.resolve_str("frontend"));

	}

	public static boolean check_event_id(String eventId, String userName) {
		String message = "";
		if (eventId.length() == 10 && eventId.matches("^(TOR|MTL|OTW)(A|M|E)\\d{6}$")) {

			return true;
		} else {
			message = " Manager ID : " + userName + " , " + "Event ID : " + eventId + " , "
					+ "Action Performed : check_event_id" + " , " + "Message : You have entered invalid event id.\n";

			System.out.println("You have entered invalid event id. Try again");
			Helpers.ServerLog(userName.substring(0, 3), message);
			Helpers.usersLog(userName, message);

			return false;
		}
	}

	public String checkUsername(String userName, NamingContextExt ncRef) throws Exception {

		// Validate user e.g. TORM2345
		if (userName.length() == 8 && userName.matches("^(TOR|MTL|OTW)(M|C)\\d{4}$")) {

			System.out.println("Username verified");

			// -----------------
			// Toronto Server
			// -----------------
			if (userName.substring(0, 4).equals("TORC") || userName.substring(0, 4).equals("TORM")) {

				set_server_obj(ncRef);
				return get_user(userName);
			}
			// -----------------
			// Ottawa Server
			// -----------------
			else if (userName.substring(0, 4).equals("OTWC") || userName.substring(0, 4).equals("OTWM")) {

				set_server_obj(ncRef);
				return get_user(userName);
			}
			// -----------------
			// Montreal Server
			// -----------------
			else if (userName.substring(0, 4).equals("MTLC") || userName.substring(0, 4).equals("MTLM")) {

				set_server_obj(ncRef);
				return get_user(userName);
			}
		}

		return "invalid";
	}

	public static void main(String args[]) throws Exception {

		ORB orb = ORB.init(args, null);
		// -ORBInitialPort 1050 -ORBInitialHost localhost
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

		Client cl = new Client();
		Scanner sc = new Scanner(System.in);
		String res = "start";

		System.out.println("Enter your Username");
		while (res.equalsIgnoreCase("invalid") || res.equals("start")) {
			String userName = sc.next();
			String prevId = userName ;
			res = cl.checkUsername(userName, ncRef);

			if (res.equals("invalid")) {
				System.out.println("Invalid user, please enter valid user name like MTLM1234, OTWC4567, TORM2587");
			}
			// ------------------------------
			// ------- Manager methods ------
			// ------------------------------
			else if (res.equals("manager")) {
				int choice = 0;

				do {
					System.out.println("Select any of the below entries\n" + "1. Add Event\n" + "2. Remove Event\n"
							+ "3. List Event availability\n" + "4. Book Event\n" + "5. Get Booking Schedulde\n"
							+ "6. cancel Event\n" + "7. Multi Threading\n" + "8. Crash Implementation (Book Event)\n"
							+ "9. Bug Implementation (Add Event)\n "+"10. Swap Event" );

					try {
						choice = sc.nextInt();
					} catch (Exception e) {
						System.out.println("Invalid Choice");
					}

					String eventId = "";
					String eventType = "";

					switch (choice) {

					case 1:
						System.out
								.print("(For event type enter: c - conferences, " + "t - trade-shows and s - seminars");
						System.out.println(" e.g. MTLE130519 c 3)");
						System.out.println("Enter eventId eventType and BookingCapacity to add");

						eventId = sc.next();
						eventType = sc.next();
						int bookCapacity = sc.nextInt();

						eventType = get_event_type(eventType, userName);

						if (bookCapacity < 0) {
							System.out.println("Invalid booking capacity!!.It must be greater than zero");
							break;
						}
						
						if (!eventId.substring(0, 3).equals(userName.substring(0, 3))) {

							System.out.println("Enter the valid event Id");
							break;
						}

						if (eventType.equals("invalid"))
							break;

						if (!check_event_id(eventId, userName))
							break;

						boolean addRes = frontEndObj.addEvent(eventId, eventType, bookCapacity, userName, false);
						if (addRes)
							System.out.println("Event has been added/updated successfully");
						else
							System.out.println("Error in adding the Event! Check server logs ...");
						break;

					case 2:
						System.out.println(
								"(For event type enter: c - conferences, " + "t - trade-shows and s - seminars");
						System.out.println("Enter eventId eventType to be deleted");

						String remove_eventId = sc.next();
						String remove_eventType = sc.next();

						remove_eventType = get_event_type(remove_eventType, userName);
						if (remove_eventType.equals("invalid"))
							break;

						if (!check_event_id(remove_eventId, userName))
							break;

						boolean remRes = frontEndObj.removeEvent(remove_eventId, remove_eventType, userName);

						if (remRes)
							System.out.println("Successfully deleted the eventId from the Event");
						else
							System.out.println("Could not delete the Event");
						break;

					case 3:
						System.out.println(
								"(For event type enter: c - conferences, " + "t - trade-shows and s - seminars");
						System.out.println("Enter any EventType");
						String list_eventType = sc.next();

						list_eventType = get_event_type(list_eventType, userName);
						if (list_eventType.equals("invalid"))
							break;

						String list_events = frontEndObj.listEventAvailability(list_eventType, userName);

						System.out.println("--------------------------");
						System.out.println(list_events);
						System.out.println("--------------------------");
						break;

					case 4:
						
						System.out.println("Enter the Customer Id you want to Book for");
						
						userName = sc.next();
						
						System.out.println(
								"(For event type enter: c - conferences, " + "t - trade-shows and s - seminars");

						System.out.println("Enter Event Id and Event Type ");
						eventId = sc.next();
						eventType = sc.next();

						if (!check_event_id(eventId, userName))
							break;
						
						eventType = get_event_type(eventType, userName);
						if (eventType.equals("invalid"))
							break;

						boolean bookRes = frontEndObj.bookEvent(userName, eventId, eventType, false);

						if (bookRes)
							System.out.println("You succesfully booked the event");
						else
							System.out.println("You have already booked the Event with the same EventId :" + eventId
									+ "\n or The event is not Present + \n or You are trying book outside city events more than thrice.");
						
						userName =prevId;
						
						break;

					case 5:
						
						System.out.println("Enter the Customer Id to get Booking Schedulde");
						
						userName = sc.next();
						
						System.out.println("-- Booking Schedule --");

						String[] output = frontEndObj.getBookingSchedule(userName).split(",");

						for (String str : output)
							System.out.println(str);

						userName =prevId;
						
						break;

					case 6:
						
						System.out.println("Enter the Customer Id to cancel the event");
						
						userName = sc.next();
						
						System.out.println(
								"(For event type enter: c - conferences, " + "t - trade-shows and s - seminars");
						System.out.println("Enter eventID and eventType which you want to cancel");
						String cancel_eventID = sc.next();
						eventType = sc.next();

						if (!check_event_id(cancel_eventID, userName))
							break;
						eventType = get_event_type(eventType, userName);
						if (eventType.equals("invalid"))
							break;

						boolean returnRes = frontEndObj.cancelEvent(userName, cancel_eventID, eventType);

						if (returnRes) {
							System.out.println("Event cancelled");
						} else
							System.out.println("Could not cancel vent");
						
						userName =prevId;
						break;

					case 7:
						System.out.println("Running Threads Now");
						Runnable cus4 = new Runnable() {
							public void run() {

								frontEndObj.bookEvent("TORC1237", "TORM123456", "conferences", false);
							}
						};

						Thread thread4 = new Thread(cus4);
						thread4.start();

						Runnable udp_task = new Runnable() {
							public void run() {

								frontEndObj.addEvent("TORM123456", "conferences", 2, "TORM1234", false);
							}
						};

						Thread thread = new Thread(udp_task);
						thread.start();

						Runnable cus = new Runnable() {
							public void run() {

								frontEndObj.bookEvent("TORC1234", "TORM123456", "conferences", false);
							}
						};

						Thread thread1 = new Thread(cus);
						thread1.start();

						Runnable cus2 = new Runnable() {
							public void run() {

								frontEndObj.bookEvent("TORC1235", "TORM123456", "conferences", false);
							}
						};

						Thread thread2 = new Thread(cus2);
						thread2.start();

						Runnable cus3 = new Runnable() {
							public void run() {

								frontEndObj.bookEvent("TORC1236", "TORM123456", "conferences", false);

							}
						};

						Thread thread3 = new Thread(cus3);
						thread3.start();

						break;

					case 8:
						
						System.out.println("Enter the Customer Id to book the event");
						
						userName = sc.next();
						System.out.println(
								"(For event type enter: c - conferences, " + "t - trade-shows and s - seminars");

						System.out.println("Enter Event Id and Event Type ");
						eventId = sc.next();
						eventType = sc.next();

						eventType = get_event_type(eventType, userName);
						if (eventType.equals("invalid"))
							break;

						if (!check_event_id(eventId, userName))
							break;
						
						boolean crash_bookRes = frontEndObj.bookEvent(userName, eventId, eventType, true);

						if (crash_bookRes)
							System.out.println("You succesfully booked the event");
						else
							System.out.println("You have already booked the Event with the same EventId :" + eventId
									+ "\n or The event is not Present + \n or You are trying book outside city events more than thrice.");
						
						userName =prevId;
						break;

					case 9:
						System.out
								.print("(For event type enter: c - conferences, " + "t - trade-shows and s - seminars");
						System.out.println(" e.g. MTLE130519 c 3)");
						System.out.println("Enter eventId eventType and BookingCapacity to add");

						eventId = sc.next();
						eventType = sc.next();
						int bugBookCapacity = sc.nextInt();

						eventType = get_event_type(eventType, userName);

						if (bugBookCapacity < 0) {
							System.out.println("Invalid booking capacity!!.It must be greater than zero");
							break;
						}
						
						if (!eventId.substring(0, 3).equals(userName.substring(0, 3))) {

							System.out.println("Enter the valid event Id");
							break;
						}

						if (eventType.equals("invalid"))
							break;

						if (!check_event_id(eventId, userName))
							break;

						boolean bugRes = frontEndObj.addEvent(eventId, eventType, bugBookCapacity, userName,
								true);
						
						if (bugRes)
							System.out.println("Event has been added/updated successfully");
						else
							System.out.println("Error in adding the Event! Check server logs ...");
						break;
						
					case 10:
						System.out.println("Enter the Customer Id for swapping");
						
						userName = sc.next();
						System.out.println(
								"(For event type enter: c - conferences, " + "t - trade-shows and s - seminars");
						System.out.println("Enter New Event Id, New Event Type, Old Event Id, Old Event Type");
						String new_eventID = sc.next();
						String new_eventType = sc.next();
						String old_eventID = sc.next();
						String old_eventType = sc.next();
						String full_new_eventType, full_old_eventType;

						if (!check_event_id(new_eventID, userName) || !check_event_id(old_eventID, userName))
							break;
						
						full_new_eventType = get_event_type(new_eventType, userName);
						full_old_eventType = get_event_type(old_eventType, userName);

						if (full_new_eventType.equals("invalid") || full_old_eventType.equals("invalid"))
							break;

						boolean swapRes = frontEndObj.swapEvent(userName, new_eventID, full_new_eventType, old_eventID,
								full_old_eventType);

						if (swapRes)
							System.out.println("You succesfully swap the event");
						else
							System.out.println("You cannot swap the event");
						
						userName =prevId;

						break;
						
						
						
					}

				} while (choice != 11);
			}

			// -------------------------------
			// ------- Customer methods ------
			// -------------------------------
			else if (res.equals("user")) {
				int userSel = 0;
				String customerId = userName;

				do {
					System.out.println("Select any of the below entries\n"
							+ "(Event types: conferences, trade-shows and seminars)\n" + "1. Book Event\n"
							+ "2. Get Booking Schedulde\n" + "3. cancel Event\n" + "4. swap Event\n" + "5. Exit");

					try {
						userSel = sc.nextInt();
					} catch (Exception e) {
						System.out.println("Invalid Choice");
					}

					switch (userSel) {

					case 1:
						System.out.println(
								"(For event type enter: c - conferences, " + "t - trade-shows and s - seminars");
						System.out.println("Enter Event Id and Event Type ");
						String eventID = sc.next();
						String eventType = sc.next();

						eventType = get_event_type(eventType, userName);
						if (eventType.equals("invalid"))
							break;

						if (!check_event_id(eventID, userName))
							break;
						
						boolean bookRes = frontEndObj.bookEvent(customerId, eventID, eventType, false);

						if (bookRes)
							System.out.println("You succesfully booked the event");
						else
							System.out.println("You have already booked the Event with the same EventId :" + eventID
									+ "\n or The event is not Present + \n or You are trying book outside city events more than thrice.");
						break;

					case 2:
						System.out.println("-- Booking Schedule --");
						System.out.println(frontEndObj.getBookingSchedule(customerId));
						break;

					case 3:
						System.out.println(
								"(For event type enter: c - conferences, " + "t - trade-shows and s - seminars");
						System.out.println("Enter eventID and eventType which you want to cancel");
						String cancel_eventID = sc.next();
						eventType = sc.next();
						

						if (!check_event_id(cancel_eventID, userName))
							break;
						
						eventType = get_event_type(eventType, userName);
						if (eventType.equals("invalid"))
							break;

						boolean returnRes = frontEndObj.cancelEvent(userName, cancel_eventID, eventType);

						if (returnRes) {
							System.out.println("Event cancelled");
						} else
							System.out.println("Could not cancel vent");
						break;

					case 4:
						System.out.println(
								"(For event type enter: c - conferences, " + "t - trade-shows and s - seminars");
						System.out.println("Enter New Event Id, New Event Type, Old Event Id, Old Event Type");
						String new_eventID = sc.next();
						String new_eventType = sc.next();
						String old_eventID = sc.next();
						String old_eventType = sc.next();
						String full_new_eventType, full_old_eventType;

						if (!check_event_id(new_eventID, userName) || !check_event_id(old_eventID, userName))
							break;
						
						full_new_eventType = get_event_type(new_eventType, userName);
						full_old_eventType = get_event_type(old_eventType, userName);

						if (full_new_eventType.equals("invalid") || full_old_eventType.equals("invalid"))
							break;

						boolean swapRes = frontEndObj.swapEvent(userName, new_eventID, full_new_eventType, old_eventID,
								full_old_eventType);

						if (swapRes)
							System.out.println("You succesfully swap the event");
						else
							System.out.println("You cannot swap the event");

						break;
					}

				} while (userSel != 5);
			} else {
				System.out.println("Wrong user ID entered");
			}
		}
	}
}
