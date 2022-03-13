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
	
	public static void updateNodes(Hashtable <String, IPEntry> recievedList)
	{
		setOfNodeIPs = recievedList.keySet(); 
		knownNodeList = recievedList;
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
		boolean serverIsUp = true;
		while (serverIsUp)
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
			
			if (blackOutTimer >= MAX_TIME*1.5)
			{

				if (setOfNodeIPs.isEmpty() || setOfNodeIPs.stream().findFirst().get().equals(myIP))
				{
					System.out.println("Taking over as server");
					knownNodeList.remove(myIP);
					serverIsUp = false;
				}
				else
				{
					try
					{
						serverIP = InetAddress.getByName(setOfNodeIPs.stream().findFirst().get());
					} catch (UnknownHostException e)
					{
						System.out.println("There was an error changing serverIP to " + setOfNodeIPs.stream().findFirst().get());
					}
				}
			}
			
			timer++;
		}
		clientsocket.close();
		Server server = new Server(knownNodeList);
		server.runServer();
		
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
