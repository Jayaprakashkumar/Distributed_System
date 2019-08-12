package servers.rm1.ImplementRemoteInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utility.Constants;

public class RepManager1 {

	static Helpers hlp = new Helpers();
	static int bug_cnt = 0;
	static List<String[]> dataList = new ArrayList<>();

	static Runnable udp_task = new Runnable() {
		public void run() {
			udpReceive();
		}
	};

	public static void run() {
		Thread thread = new Thread(udp_task);
		thread.start();
	}

	private synchronized static void udpReceive() {
		// TODO Auto-generated method stub
		MontrealClass montreal = null;
		OttawaClass ottawa = null;
		TorontoClass toronto = null;
		try {
			montreal = new MontrealClass();
			ottawa = new OttawaClass();
			toronto = new TorontoClass();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MulticastSocket aSocket = null;
		try {

			aSocket = new MulticastSocket(Constants.MULTICAST_PORT_NO);
			aSocket.joinGroup(InetAddress.getByName(Constants.MULTICAST_IP_ADDR));
			// aSocket = new DatagramSocket(Constants.RM1_PORT_NO);
			byte[] buffer = new byte[1000];

			System.out.println("-----------------------------------");
			System.out.println("RM 1 Server listening on port " + Constants.MULTICAST_PORT_NO);

			while (true) {// non-terminating loop as the server is always in
				// listening mode.
				Arrays.fill(buffer, (byte) 0);
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				// Server waits for the request to come
				aSocket.receive(request);// request received

				// Parse the request
				String receivedData = hlp.data(buffer).toString();

				/*
				 * String logMessages = "Montreal UDP Server: Request received from client: " +
				 * receivedData; System.out.println(logMessages); Helpers.ServerLog("MTL",
				 * logMessages);
				 */

				String[] receivedParams = receivedData.split("-");
				DatagramPacket reply;
				String response = "";
				if (receivedParams[1].substring(0, 3).equalsIgnoreCase("MTL")) {
					call_specific_server(montreal, receivedParams);
				} else if (receivedParams[1].substring(0, 3).equalsIgnoreCase("OTW")) {
					call_specific_server(ottawa, receivedParams);
				} else if (receivedParams[1].substring(0, 3).equalsIgnoreCase("TOR")) {
					call_specific_server(toronto, receivedParams);
				}

			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}

	}

	private static void call_specific_server(Object server, String[] receivedParams) {
		// TODO Auto-generated method stub
		boolean ret;
		String retStr;
		if (receivedParams[1].substring(0, 3).equalsIgnoreCase("MTL")) {
			MontrealClass montreal_obj = (MontrealClass) server;

			switch (receivedParams[2]) {

			case "addEvent":
				if (!Boolean.parseBoolean(receivedParams[6])) {

						ret = montreal_obj.addEvent(receivedParams[3], receivedParams[4], Integer.parseInt(receivedParams[5]),
								receivedParams[1]);
						udpSend("RM1", ret + ";" + receivedParams[0] + ";" + receivedParams[2]);

				} else {

						dataList.add(receivedParams);
						bug_cnt++;

						if (bug_cnt <= 3)
						{
							udpSend("RM1", "false" + ";" + receivedParams[0] + ";" + receivedParams[2]);
						}
						else
						{
							for (String[] tempParams : dataList) {

								ret = montreal_obj.addEvent(tempParams[3], tempParams[4], Integer.parseInt(tempParams[5]),
										tempParams[1]);
								udpSend("RM1", ret + ";" + tempParams[0] + ";" + tempParams[2]);
							}
							bug_cnt = 0;
							dataList.clear();
						}
					
				}
				break;
			case "removeEvent":
				ret = montreal_obj.removeEvent(receivedParams[3], receivedParams[4], receivedParams[1]);
				udpSend("RM1", "" + ret + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "listEventAvailability":
				retStr = montreal_obj.listEventAvailability(receivedParams[3], receivedParams[1]);
				udpSend("RM1", retStr + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "bookEvent":
				ret = montreal_obj.bookEvent(receivedParams[1], receivedParams[3], receivedParams[4]);
				udpSend("RM1", "" + ret + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "cancelEvent":
				ret = montreal_obj.cancelEvent(receivedParams[1], receivedParams[3], receivedParams[4]);
				udpSend("RM1", "" + ret + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "swapEvent":
				ret = montreal_obj.swapEvent(receivedParams[1], receivedParams[3], receivedParams[4], receivedParams[5],
						receivedParams[6]);
				udpSend("RM1", "" + ret + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "getBookingSchedule":
				retStr = montreal_obj.getBookingSchedule(receivedParams[1]);
				udpSend("RM1", retStr + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;

			}

		} else if (receivedParams[1].substring(0, 3).equalsIgnoreCase("OTW")) {
			OttawaClass ottawa_obj = (OttawaClass) server;
			switch (receivedParams[2]) {

			case "addEvent":

				ret = ottawa_obj.addEvent(receivedParams[3], receivedParams[4], Integer.parseInt(receivedParams[5]),
						receivedParams[1]);
				udpSend("RM1", "" + ret + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "removeEvent":
				ret = ottawa_obj.removeEvent(receivedParams[3], receivedParams[4], receivedParams[1]);
				udpSend("RM1", "" + ret + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "listEventAvailability":
				retStr = ottawa_obj.listEventAvailability(receivedParams[3], receivedParams[1]);
				udpSend("RM1", retStr + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "bookEvent":
				ret = ottawa_obj.bookEvent(receivedParams[1], receivedParams[3], receivedParams[4]);
				udpSend("RM1", "" + ret + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "cancelEvent":
				ret = ottawa_obj.cancelEvent(receivedParams[1], receivedParams[3], receivedParams[4]);
				udpSend("RM1", "" + ret + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "swapEvent":
				ret = ottawa_obj.swapEvent(receivedParams[1], receivedParams[3], receivedParams[4], receivedParams[5],
						receivedParams[6]);
				udpSend("RM1", "" + ret + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "getBookingSchedule":
				retStr = ottawa_obj.getBookingSchedule(receivedParams[1]);
				udpSend("RM1", retStr + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;

			}

		} else if (receivedParams[1].substring(0, 3).equalsIgnoreCase("TOR")) {
			TorontoClass toronto_obj = (TorontoClass) server;
			switch (receivedParams[2]) {

			case "addEvent":
				ret = toronto_obj.addEvent(receivedParams[3], receivedParams[4], Integer.parseInt(receivedParams[5]),
						receivedParams[1]);
				udpSend("RM1", "" + ret + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "removeEvent":
				ret = toronto_obj.removeEvent(receivedParams[3], receivedParams[4], receivedParams[1]);
				udpSend("RM1", "" + ret + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "listEventAvailability":
				retStr = toronto_obj.listEventAvailability(receivedParams[3], receivedParams[1]);
				udpSend("RM1", retStr + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "bookEvent":
				ret = toronto_obj.bookEvent(receivedParams[1], receivedParams[3], receivedParams[4]);
				udpSend("RM1", "" + ret + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "cancelEvent":
				ret = toronto_obj.cancelEvent(receivedParams[1], receivedParams[3], receivedParams[4]);
				udpSend("RM1", "" + ret + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "swapEvent":
				ret = toronto_obj.swapEvent(receivedParams[1], receivedParams[3], receivedParams[4], receivedParams[5],
						receivedParams[6]);
				udpSend("RM1", "" + ret + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			case "getBookingSchedule":
				retStr = toronto_obj.getBookingSchedule(receivedParams[1]);
				udpSend("RM1", retStr + ";" + receivedParams[0] + ";" + receivedParams[2]);
				break;
			}

		}

	}

	private static void udpSend(String replicaManagerNumber, String response) {
		// TODO Auto-generated method stub
		DatagramSocket aSocket = null;

		byte[] message = null;

		try {
			aSocket = new DatagramSocket(); // reference of the original socket

			message = (replicaManagerNumber + ";" + response).getBytes();

			InetAddress aHost = InetAddress.getByName(Constants.FE_IP_ADDR);
			int serverPort = Constants.FE_PORT_NO;

			DatagramPacket request = new DatagramPacket(message, message.length, aHost, serverPort);// request packet
			aSocket.send(request);// request sent out

			/*
			 * String logMessages =
			 * "Montreal UDP Server: Reply received from the server is: " + retVal;
			 * System.out.println(logMessages);// print reply Helpers.ServerLog("MTL",
			 * logMessages);
			 */
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}

	}
}
