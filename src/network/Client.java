package network;

import java.io.*;
import java.net.*;
import java.util.*;



public class Client {
	public static DatagramSocket clientsocket = null;
	public static InetAddress myIP;
	public static InetAddress serverIP;
	public static int PORT = 9876;
	public static String IPSTRING = "150.243.192.195";
	private static Hashtable<String, IPEntry> knownIPList = new Hashtable<>();
	private static Set<String> setOfNodeIPs; 
	
	
	public static void heartbeatToServer(Message requestMessage) {
		DatagramPacket request = requestMessage.createPacket(serverIP, PORT);
		
		try 
		{
			clientsocket.send(request);
			System.out.println("sending to" + serverIP.getHostAddress());
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		// Packet variable for receiving and holding server's response
		byte[] responsebuffer = new byte[65508];
		DatagramPacket response = new DatagramPacket(responsebuffer, responsebuffer.length);
		
		// Immediately catch the server's response, and discard it(run a loop to make
		// sure it is done)
		boolean flag = true;
		while (flag) 
		{
			try 
			{
				clientsocket.receive(response);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				continue;
			}
			//Output the text of the response packet from server.
			
			Message responsemessage = new Message(false, true, "", knownIPList);

			responsemessage = responsemessage.deserializer(response);

			knownIPList = responsemessage.getnodeList();
			setOfNodeIPs = knownIPList.keySet();
			for(String ip: setOfNodeIPs)
			{
				System.out.println(ip);
			}
			
			System.out.println(responsemessage.getText());
			flag = false;
		}
			
	}
	
	
	public static InetAddress findServerIP() 
	{
		// Find the IP address of server node.
		InetAddress hostaddress = null;
		try {
			hostaddress = InetAddress.getByName(IPSTRING);
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return hostaddress;
	}
	

	//The function to open the client socket.
	public static void openSocket() 
	{
		try 
		{
			clientsocket = new DatagramSocket(PORT);
		} 
		catch (SocketException e) 
		{
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		findServerIP();
		openSocket();
		Message requestInfo= new Message(false, true, "This is the client", null);
		heartbeatToServer(requestInfo);
	}
}
