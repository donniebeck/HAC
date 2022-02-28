package network;


public class P2P
{
//	private static final int PORT_NO = 9876;

	public static void main(String[] args)
	{
		//Load myIP from txt file
		ConfigReader configReader = new ConfigReader();
		String myIP = configReader.getSingleIP("myIP.txt");
		
		//Load IPs into array from txt file
		String ipList[] = configReader.getIPListFromFile("ipList.txt");
		
		
	}

}
