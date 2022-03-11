package network;

import java.io.*;
import java.net.*;
import java.util.*;



public class Client {
	public static DatagramSocket clientsocket = null;
	public static String myIP = "150.243.144.254";
	public static InetAddress serverIP;
	public static int PORT = 9876;
	public static String SERVERIPSTRING = "150.243.192.195";
	private static Hashtable<String, IPEntry> knownNodeList = new Hashtable<>();
	private static Set<String> setOfNodeIPsPrint; 
	private static Set<String> setOfNodeIPs; 
	
	
	public static void heartbeatToServer(Message requestMessage) {
		DatagramPacket request = requestMessage.createPacket(serverIP, PORT);
		
		try 
		{
			clientsocket.send(request);
			System.out.println("sending to: " + serverIP.getHostAddress());
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
			
			Message responsemessage = new Message(false, true, "", knownNodeList);

			responsemessage = responsemessage.deserializer(response);

			updateNodes(knownNodeList);
			setOfNodeIPsPrint = knownNodeList.keySet();
			
			
			
			System.out.println("The server responsed with those ips:");
			for(String ip: setOfNodeIPsPrint)
			{
				System.out.println(ip);
			}
			
//			System.out.println(responsemessage.getText());
			flag = false;
		}
			
	}
	
	
	public static void updateNodes(Hashtable <String, IPEntry> recievedList)
	{
		if (knownNodeList != null)
		{
			Set<String> tempSetOfNodeIPs = recievedList.keySet(); 
			
			for (String tempNodeIP : tempSetOfNodeIPs)
			{
				if (!setOfNodeIPs.contains(tempNodeIP))
				{
					IPEntry newNode = new IPEntry(true);
					knownNodeList.put(tempNodeIP, newNode);
					setOfNodeIPs = knownNodeList.keySet();
					knownNodeList.get(tempNodeIP).setIsAlive(recievedList.get(tempNodeIP).getIsAlive());
				} 
				else if(recievedList.get(tempNodeIP).getTimeStamp().isAfter(knownNodeList.get(tempNodeIP).getTimeStamp()) &&
						!tempNodeIP.equals(myIP))
				{
					knownNodeList.get(tempNodeIP).setIsAlive(recievedList.get(tempNodeIP).getIsAlive());
				}
			}
		}
	}
	
	public static void findServerIP() 
	{
		// Find the IP address of server node.
		InetAddress hostaddress = null;
		try 
		{
			hostaddress = InetAddress.getByName(SERVERIPSTRING);
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
		serverIP = hostaddress;
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
