package frontend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import frontendImpl.frontendApp;
import frontendImpl.frontendAppHelper;
import frontendImpl.frontendAppPOA;
import servers.rm1.ImplementRemoteInterface.MontrealClass;
import utility.Constants;

public class FrontEndImplementation extends frontendAppPOA {

	String data;
	static ORB orb;

	public static void main(String[] args) {
		try {
			// create and initialize the ORB //
			orb = ORB.init(args, null);

			// get reference to rootpoa &amp; activate
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant and register it with the ORB
			FrontEndImplementation frontEndImpl = new FrontEndImplementation();
			frontEndImpl.setORB(orb);

			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(frontEndImpl);

			// and cast the reference to a CORBA reference
			frontendApp href = frontendAppHelper.narrow(ref);

			// get the root naming context
			// NameService invokes the transient name service
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

			// Use NamingContextExt, which is part of the
			// Interoperable Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// bind the Object Reference in Naming
			NameComponent path[] = ncRef.to_name("frontend");
			ncRef.rebind(path, href);

			System.out.println("Frontend CORBA service started");
			udpReceive();
		} catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

	}

	private static void udpReceive() {
		// TODO Auto-generated method stub
		byte[] buffer = new byte[1000];
		DatagramSocket aSocket = null;
		Arrays.fill(buffer, (byte) 0);
		try {
			aSocket = new DatagramSocket(Constants.FE_PORT_NO_DUMMY);
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setORB(ORB orb_val) {
		// TODO Auto-generated method stub
		orb = orb_val;

	}

	private synchronized Map<String, String> udp_send_to_sequencer(String info) {
		// TODO Auto-generated method stub
		byte[] message = info.getBytes();
		int resp_count = 3;
		DatagramSocket aSocket = null;
		DatagramSocket recvSocket = null;
		Map<String, String> rmReponse = new HashMap<String, String>();
		try {
			aSocket = new DatagramSocket();
			recvSocket = new DatagramSocket(Constants.FE_PORT_NO);
			InetAddress aHost = InetAddress.getByName(Constants.SQUENCER_IP_ADDR);

			// Sequencer port number
			int serverPort = Constants.SQUENCER_PORT_NO;

			DatagramPacket request = new DatagramPacket(message, message.length, aHost, serverPort);// request packet
			aSocket.send(request);// request sent out

			byte[] buffer = new byte[1000];
			String retVal;
			boolean check = false;

			System.out.println("----------------------------------");

			for (int i = 0; i < resp_count; i++) {
				Arrays.fill(buffer, (byte) 0);
				check = false;
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

				try {
					recvSocket.setSoTimeout(4000);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					continue;
				} // reference of the original socket
				finally {

				}

				recvSocket.receive(reply);// reply received and will populate reply

				// packet now.
				retVal = data(buffer).toString();
				String retArray[] = retVal.split(";");

				System.out.println("UDP Recv: Seq No: " + retArray[2] + " Response From " + retArray[0] + " : "
						+ retArray[3] + " : " + retArray[1]);

				rmReponse.put(retArray[0], retArray[1]);
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} finally {
			recvSocket.close();
		}
		return rmReponse;

	}

	@Override
	public boolean addEvent(String eventID, String eventType, int bookingCapacity, String managerID, boolean bugValue) {

		data = managerID + "-" + "addEvent" + "-" + eventID + "-" + eventType + "-" + bookingCapacity + "-" + bugValue;
		HashMap<String, String> res = (HashMap<String, String>) udp_send_to_sequencer(data);

		Set<String> valueSet = new HashSet<>(res.values());

		if (bugValue) {
			//System.out.println("RM1: Bug has been found on this server ...");
			int freq_true = Collections.frequency(res.values(), "true");
			int freq_false = Collections.frequency(res.values(), "false");

			if (freq_true > freq_false)
				return true;
			else
				return false;
		}

		if (valueSet.size() > 1)
			return false;
		else
			return Boolean.parseBoolean(valueSet.iterator().next());
	}

	@Override
	public boolean removeEvent(String eventID, String eventType, String managerID) {

		data = managerID + "-" + "removeEvent" + "-" + eventID + "-" + eventType;
		HashMap<String, String> res = (HashMap<String, String>) udp_send_to_sequencer(data);

		Set<String> valueSet = new HashSet<>(res.values());

		if (valueSet.size() > 1)
			return false;
		else
			return Boolean.parseBoolean(valueSet.iterator().next());
		// return true;

	}

	@Override
	public String listEventAvailability(String eventType, String managerID) {

		data = managerID + "-" + "listEventAvailability" + "-" + eventType;
		HashMap<String, String> res = (HashMap<String, String>) udp_send_to_sequencer(data);

		// System.out.println(res);
		ArrayList<ArrayList<String>> rmList = new ArrayList<>();

		for (Map.Entry<String, String> mapVal : res.entrySet()) {
			ArrayList<String> subList = new ArrayList<>();

			String mValue = mapVal.getValue().split("-")[1];
			String[] splittedVal = mValue.split(",");
			for (int i = 0; i < splittedVal.length; i++) {
				subList.add(splittedVal[i]);
			}

			Collections.sort(subList); // sort the list
			rmList.add(subList); // add list of events in array list
		}
		
		String result= "";	
			
		Set<ArrayList<String>> valueSet = new HashSet<>(rmList);
		
		if(valueSet.size() > 1) { //servers return different response
				int count;
				for(ArrayList<String> setList : valueSet) {
					count = 0;
				for(ArrayList<String> listStr : rmList) {
					if(setList.equals(listStr)){
						count++;
					}
				}
				if(count == 2) {
					for(String list : setList) { 
						result += list + ","; 
					}
					break;
				}
			}
			
		}else {// if all 3 servers return same response
			for(String list : valueSet.iterator().next()) { 
				result += list + ","; 
			}
		}

		return result;
	}

	@Override
	public boolean bookEvent(String customerID, String eventID, String eventType, boolean crashValue) {

		data = customerID + "-" + "bookEvent" + "-" + eventID + "-" + eventType + "-" + crashValue;
		HashMap<String, String> res = (HashMap<String, String>) udp_send_to_sequencer(data);
		Set<String> valueSet = new HashSet<>(res.values());

		if (valueSet.size() > 1)
			return false;
		else
			return Boolean.parseBoolean(valueSet.iterator().next());
	}

	@Override
	public String getBookingSchedule(String customerID) {

		data = customerID + "-" + "getBookingSchedule";
		HashMap<String, String> res = (HashMap<String, String>) udp_send_to_sequencer(data);

		ArrayList<ArrayList<String>> rmList = new ArrayList<>();

		for (Map.Entry<String, String> mapVal : res.entrySet()) {
			ArrayList<String> subList = new ArrayList<>();
			String[] splittedVal = mapVal.getValue().split(",");

			for (int i = 0; i < splittedVal.length; i++) {
				subList.add(splittedVal[i]);
			}
			Collections.sort(subList); // sort the list
			rmList.add(subList); // add list of events in array list
		}
		
		String result= "";	
		Set<ArrayList<String>> valueSet = new HashSet<>(rmList);
			
		if(valueSet.size() > 1) { //servers return different response
				int count;
				for(ArrayList<String> setList : valueSet) {
					count = 0;
				for(ArrayList<String> listStr : rmList) {
					if(setList.equals(listStr)){
						count++;
					}
				}
				if(count == 2) {
					for(String list : setList) { 
						result += list + ","; 
					}
					break;
				}
			}
			
		}else {// if all 3 servers return same response
			for(String list : valueSet.iterator().next()) { 
				result += list + ","; 
			}
		}

		return result;
	}

	@Override
	public boolean cancelEvent(String customerID, String eventID, String eventType) {

		data = customerID + "-" + "cancelEvent" + "-" + eventID + "-" + eventType;
		HashMap<String, String> res = (HashMap<String, String>) udp_send_to_sequencer(data);
		Set<String> valueSet = new HashSet<>(res.values());

		if (valueSet.size() > 1)
			return false;
		else
			return Boolean.parseBoolean(valueSet.iterator().next());
	}

	@Override
	public boolean swapEvent(String customerID, String newEventID, String newEventType, String oldEventID,
			String oldEventType) {

		data = customerID + "-" + "swapEvent" + "-" + newEventID + "-" + newEventType + "-" + oldEventID + "-"
				+ oldEventType;
		HashMap<String, String> res = (HashMap<String, String>) udp_send_to_sequencer(data);
		Set<String> valueSet = new HashSet<>(res.values());

		if (valueSet.size() > 1)
			return false;
		else
			return Boolean.parseBoolean(valueSet.iterator().next());
	}

	@Override
	public void shutdown() {
		orb.shutdown(false);
	}

	public StringBuilder data(byte[] a) {
		if (a == null)
			return null;
		StringBuilder ret = new StringBuilder();
		int i = 0;
		while (a[i] != 0) {
			ret.append((char) a[i]);
			i++;
		}
		return ret;
	}
}
