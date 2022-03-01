package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;

public class P2P
{
	private static final int PORT_NO = 9876;
	private static final int MAX_TIME = 10;
	
	private static Vector<InetAddress> knownIP = new Vector<>();
	private static String myIP = "/";
	private static DatagramSocket socket;
	private static Message message = new Message(true, true, "Hello, this is a client ", knownIP);
	

	public static void main(String[] args)
	{
		loadIPs();
		createSocket();
		
		int timer = MAX_TIME;
		
		byte[]  buffer = new byte[65508];
		DatagramPacket recievedPacket = new DatagramPacket(buffer, buffer.length);
		Message recieved = new Message(false, false, "", knownIP);
		
		while (true)
		{
			if (timer == MAX_TIME)
			{
				timer = 0;
				sendToAll();
			}
			try
			{
				socket.receive(recievedPacket);
			} catch (IOException e)
			{
				timer++;
				System.out.println(timer + " No message recieved");
				continue;
			}
			recieved = recieved.deserializer(recievedPacket);
			System.out.println(timer + " " +recievedPacket.getAddress());
			
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				System.out.println("There was an error putting the thread to sleep");
				e.printStackTrace();
			}
		}
		
	}

	private static void sendToAll()
	{
		for (InetAddress address : knownIP)
		{	
			if(!address.toString().equals(myIP))
			{
				DatagramPacket packet = message.createPacket(address, PORT_NO);
				try
				{
					socket.send(packet);
				} catch (IOException e)
				{
					System.out.println("There was an error sending the packet to " + address);
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
		myIP += configReader.getSingleIP("myIP.txt");
		
		//Load all IPs into vector from txt file
		String ipList[] = configReader.getIPListFromFile("ipList.txt");
		for (String ip : ipList)
		{
			try
			{
				knownIP.add(InetAddress.getByName(ip));
			} catch (UnknownHostException e)
			{
				System.out.println("Could not convert IP string into InetAdress");
				e.printStackTrace();
			}
		}
	}

}
