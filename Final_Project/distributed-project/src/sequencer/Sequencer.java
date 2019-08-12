package sequencer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import servers.rm1.ImplementRemoteInterface.Helpers;
import utility.Constants;

/* Design and implement a failure-free sequencer which receives a client
request from a FE, assigns a unique sequence number to the request and reliably
multicast the request with the sequence number and FE information to all the three
server replicas. */ 
public class Sequencer {

	static int seqNum = 0;
	
	static Runnable udp_task = new Runnable() {
		public void run() {
			udp_packet_recv();
		}
	};

	public static void run() {
		Thread thread = new Thread(udp_task);
		thread.start();
	}
		
	public static void udp_packet_recv() {
		DatagramSocket aSocket = null;
		int portNo = Constants.SQUENCER_PORT_NO;
		try {
			aSocket = new DatagramSocket(portNo);
			byte[] buffer = new byte[1000];// to stored the received data from
											// the client.
			
			System.out.println("-----------------------------------");
			System.out.println("Sequencer UDP Server Started on port: " + portNo);
			System.out.println("-----------------------------------");

			while (true) {// non-terminating loop as the server is always in
							// listening mode.
				Arrays.fill(buffer, (byte)0);

				DatagramPacket request = new DatagramPacket(buffer,
						buffer.length);
				// Server waits for the request to come
				aSocket.receive(request);// request received

				// Parse the request
				String receivedData = data(buffer).toString();

/*				
				String logMessages = "Toronto UDP Server: Request received from client: "
						+ receivedData;
				System.out.println(logMessages);
				Helpers.ServerLog("MTL", logMessages);
*/
				udp_multicast(receivedData);				
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
	
	private static void udp_multicast(String receivedParams) {

			DatagramSocket aSocket = null;

			try {
				aSocket = new DatagramSocket();
				
				InetAddress aHost = InetAddress.getByName("230.1.1.5");

				// Sequencer port number
				int rm1Port = Constants.MULTICAST_PORT_NO;

				seqNum++;
				byte[] message = (seqNum + "-" + receivedParams).getBytes();
				DatagramPacket request = new DatagramPacket(message, message.length, aHost, rm1Port);// request packet
				aSocket.send(request);// request sent out
		
//				seqNum++;
//				message = (seqNum + "-" + receivedParams).getBytes();
//				request = new DatagramPacket(message, message.length, aHost2, rm2Port);// request packet
//				aSocket.send(request);// request sent out
//			
//				seqNum++;
//				message = (seqNum + "-" + receivedParams).getBytes();
//				request = new DatagramPacket(message, message.length, aHost3, rm3Port);// request packet
//				aSocket.send(request);// request sent out
				
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // reference of the original socket
			catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public static StringBuilder data(byte[] a) {
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
