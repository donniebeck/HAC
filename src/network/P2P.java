package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class P2P
{
	private static final int PORT_NO = 9876;
	private static final int MAX_TIME = 10;
	
	private static Hashtable <String, IPEntry> nodeList = new Hashtable<>();
	private static Hashtable<String, IPEntry> recievedList = new Hashtable<>();
	private static Set<String> setOfNodeIPs; 
	private static String myIP;
	private static DatagramSocket socket;
	private static Message message = new Message(true, true, "Hello, this is a client ", nodeList);
	

	public static void main(String[] args)
	{
		loadIPs();
		createSocket();
		
		//Starting our timer
		int timer = 0;
		
		//Setting up our buffer and dummy message to load data into
		byte[]  buffer = new byte[65508];
		DatagramPacket recievedPacket = new DatagramPacket(buffer, buffer.length);
		Message recievedMessage = new Message(false, false, "", null);
		
		//Setting up our random message timer
		Random rand = new Random();
		int randomTimer = rand.nextInt(MAX_TIME-1);
		
		//Our initial heartbeat
		sendToAll();
		printNodesStatus();
		
		while (true)
		{
			if (timer == randomTimer)
			{
				sendToAll();
			}
			
			//when the timer expires, send to all knownIP, reset timer and random timer
			if (timer == MAX_TIME)
			{
				timer = 0;
				randomTimer = rand.nextInt(MAX_TIME-1);
				sendToAll();
				printNodesStatus();
				for (String ip : setOfNodeIPs)
				{
					if(!ip.equals(myIP))
					{
					nodeList.get(ip).setIsAlive(false);
					}
				}
			}
			
			
			//attempt to receive message
			try
			{
				socket.receive(recievedPacket);
				recievedMessage = recievedMessage.deserializer(recievedPacket);
				System.out.println(recievedPacket.getAddress() + " : " + recievedMessage.getText());
				
				
				recievedList = recievedMessage.getnodeList();
				
				if (recievedList != null)
				{
					Set<String> tempSetOfNodeIPs = recievedList.keySet(); 
					
					for (String tempNodeIP : tempSetOfNodeIPs)
					{
						if(recievedList.get(tempNodeIP).getTimeStamp().isAfter(nodeList.get(tempNodeIP).getTimeStamp()))
						{
							nodeList.get(tempNodeIP).setIsAlive(recievedList.get(tempNodeIP).getIsAlive());
						}
					}
				}
			} catch (IOException e)
			{
				timer++;
				System.out.println(timer + "\t No message recieved");
				continue;
			}	
			
			timer++;
		}
		
	}

	private static void printNodesStatus()
	{
		System.out.println("=================================");
		for (String ip : setOfNodeIPs)
		{
			System.out.println(ip + nodeList.get(ip).getStatusString());
		}
		System.out.println("=================================");
	}

	private static void sendToAll()
	{
		
		for (String ip : setOfNodeIPs)
		{
			if(!ip.equals(myIP))
			{
				try
				{
					message.updateTimestamp();
					DatagramPacket packet = message.createPacket(InetAddress.getByName(ip), PORT_NO);
					socket.send(packet);
				} catch (IOException e)
				{
					System.out.println("There was an error creating a packet for " + ip);
					e.printStackTrace();
				}
			}
		}
	}

	private static void createSocket()
	{
		try
		{
			socket = new DatagramSocket(PORT_NO);
			socket.setSoTimeout(1000);
		} catch (SocketException e)
		{
			System.out.println("The datagram socket could not be created");
			e.printStackTrace();
		}
	}

	private static void loadIPs()
	{
		//Load myIP from txt file
		ConfigReader configReader = new ConfigReader();
		myIP = configReader.getSingleIP("myIP.txt");
		
		//Load all IPs into hashtable from txt file
		String ipList[] = configReader.getIPListFromFile("ipList.txt");
		for (String ip : ipList)
		{
			if (ip.equals(myIP))
			{
				IPEntry newNode = new IPEntry(true);
				nodeList.put(ip, newNode);
			}
			else
			{
			IPEntry newNode = new IPEntry(false);
			nodeList.put(ip, newNode);
			}
		}
		
		setOfNodeIPs = nodeList.keySet();
	}

}
