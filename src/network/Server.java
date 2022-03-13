package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class Server
{
	private static final int PORT_NO = 9876;
	private static final int MAX_TIME = 20;

	private static DatagramSocket socket;
	
	
	private static Hashtable <String, IPEntry> clientList = new Hashtable<>();
	private static Set<String> setOfClientIPs = new HashSet<String>(); 
	private static Message message = new Message(false, false, "Hello, this is the server ", clientList);

	public static void main(String[] args)
	{
		runServer();
	}
	
	public Server(Hashtable <String, IPEntry> takeoverList)
	{
		this.clientList = takeoverList;
		this.setOfClientIPs = clientList.keySet();
	}



	public static void runServer()
	{
		createSocket();
		
		//Starting our timer
		int timer = 0;
		
		//Setting up our buffer and dummy message to load data into
		byte[]  buffer = new byte[65508];
		DatagramPacket recievedPacket = new DatagramPacket(buffer, buffer.length);
		Message recievedMessage = new Message(false, false, "", null);
		
		
		while(true)
		{
			if(timer == MAX_TIME)
			{
				timer = 0;
				for(String ip : setOfClientIPs)
				{
					if (clientList.get(ip).getTimeToLive() <= 0)
					{
						clientList.get(ip).setIsAlive(false);
					}
				}
				printClientStatus();
				sendToAll();
			}
			
			
			try
			{
				socket.receive(recievedPacket);
				recievedMessage = recievedMessage.deserializer(recievedPacket);
				String tempIPString = recievedPacket.getAddress().toString().substring(1);
				System.out.println(tempIPString + " : " + recievedMessage.getText());
				
				if(!setOfClientIPs.contains(tempIPString))
				{
					IPEntry newNode = new IPEntry(true);
					clientList.put(tempIPString, newNode);
					setOfClientIPs.add(tempIPString);
				}
				clientList.get(tempIPString).setIsAlive(true);
				clientList.get(tempIPString).setTimeToLive(MAX_TIME);
				sendToAll();
			} catch (IOException e)
			{
				System.out.println(timer + "\t No message recieved");
			}
			
			
			timer++;
			for (String ip : setOfClientIPs)
			{
				clientList.get(ip).setTimeToLive(clientList.get(ip).getTimeToLive()-1);
				System.out.println(ip + " : ttl = "+clientList.get(ip).getTimeToLive());			}
		}
	}



	private static void sendToAll()
	{
		for (String ip : setOfClientIPs)
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
	
	private static void printClientStatus()
	{
		System.out.println("=================================");
		for (String ip : setOfClientIPs)
		{
			System.out.println(ip + clientList.get(ip).getStatusString());
		}
		System.out.println("=================================");
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
}
