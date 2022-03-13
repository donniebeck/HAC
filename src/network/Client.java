package network;

import java.io.*;
import java.net.*;
import java.util.*;



public class Client {
	public static DatagramSocket clientsocket = null;
	public static String myIP;
	public static InetAddress serverIP;
	public static int PORT = 9876;
	private static Hashtable<String, IPEntry> knownNodeList = new Hashtable<>();
	private static Set<String> setOfNodeIPs = new HashSet<String>(); 
	private static int MAX_TIME = 20;
	
	
//	public static void heartbeatToServer(Message requestMessage) {
//		DatagramPacket request = requestMessage.createPacket(serverIP, PORT);
//		
//		try 
//		{
//			clientsocket.send(request);
//			System.out.println("sending to: " + serverIP.getHostAddress());
//		}
//		catch (IOException e) 
//		{
//			e.printStackTrace();
//		}
//		
//		// Packet variable for receiving and holding server's response
//		byte[] responsebuffer = new byte[65508];
//		DatagramPacket response = new DatagramPacket(responsebuffer, responsebuffer.length);
//		
//		// Immediately catch the server's response, and discard it(run a loop to make
//		// sure it is done)
//		boolean flag = true;
//		Message responsemessage = new Message(false, true, "", knownNodeList);
//		
//		try 
//		{
//			clientsocket.receive(response);
//		} 
//		catch (IOException e) 
//		{
//			e.printStackTrace();
//		}
//		//Output the text of the response packet from server.
//
//		responsemessage = responsemessage.deserializer(response);
//
//		updateNodes(responsemessage.getnodeList());
//	}
//	
	
	public static void updateNodes(Hashtable <String, IPEntry> recievedList)
	{
		if (recievedList != null)
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
	
	public static void loadIPs() 
	{
		//Load myIP from txt file
		ConfigReader configReader = new ConfigReader();
		myIP = configReader.getSingleIP("myIP.txt");
		
		// Find the IP address of server node.
		InetAddress hostaddress = null;
		try 
		{
			hostaddress = InetAddress.getByName(configReader.getSingleIP("serverIP.txt"));
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
		serverIP = hostaddress;
	}
	

	//The function to open the client socket.
	private static void createSocket()
	{
		try
		{
			clientsocket = new DatagramSocket(PORT);
			clientsocket.setSoTimeout(1000);
		} catch (SocketException e)
		{
			System.out.println("The datagram socket could not be created");
			e.printStackTrace();
		}
	}
	
	
	
	
	public static void main(String[] args) throws InterruptedException 
	{
		loadIPs();
		createSocket();
		
		Message heartbeat= new Message(false, true, "Hi, This is the client", null);
		DatagramPacket request = heartbeat.createPacket(serverIP, PORT);
		
		// Packet variable for receiving and holding server's response
		byte[] responsebuffer = new byte[65508];
		DatagramPacket response = new DatagramPacket(responsebuffer, responsebuffer.length);
		Message responsemessage = new Message(false, true, "", knownNodeList);
		
		//Setting up our random message timer
		Random rand = new Random();
		int randomTimer = rand.nextInt(MAX_TIME-1);
		
		int timer = 0;
		int blackOutTimer = 0;
		while (true)
		{
			if(timer == randomTimer)
			{
				sendToServer(request);
			}
			
			if (timer == MAX_TIME)
			{
				timer = 0;
				randomTimer = rand.nextInt(MAX_TIME-1);
				sendToServer(request);
			}
			
			if (blackOutTimer == MAX_TIME*1.5)
			{
				//if myIP is first, take over
				//if not, change serverIP to first elemenet
			}
			
			try 
			{
				clientsocket.receive(response);
				responsemessage = responsemessage.deserializer(response);
				updateNodes(responsemessage.getnodeList());
				blackOutTimer = 0;
				System.out.println("=================================");
				for(String ip: setOfNodeIPs)
				{
					System.out.println(ip + knownNodeList.get(ip).getStatusString());
				}	
				System.out.println("=================================");
			} 
			catch (IOException e) 
			{
				System.out.println(timer + "\tNo message recieved");
				blackOutTimer++;
			}
			timer++;
		}
	}

	private static void sendToServer(DatagramPacket request)
	{
		try 
		{
			clientsocket.send(request);
			System.out.println("sending to: " + serverIP.getHostAddress());
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
