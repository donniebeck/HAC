package network;

import java.net.DatagramSocket;
import java.net.SocketException;

public class P2P
{
	private static final int PORT_NO = 9876;
	
	private static String ipList[];
	private static String myIP;
	private static DatagramSocket socket;

	public static void main(String[] args)
	{
		loadIPs();
		createSocket();
		
		
		
	}

	private static void createSocket()
	{
		try
		{
			socket = new DatagramSocket(PORT_NO);
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
		
		//Load IPs into array from txt file
		ipList = configReader.getIPListFromFile("ipList.txt");
	}

}
