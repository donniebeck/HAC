package network;

import java.io.IOException;

public class test
{

	public static void main(String[] args) throws IOException
	{
		ConfigReader configReader = new ConfigReader();
		String[] ipList = configReader.getIPListFromFile();
		for (String ip : ipList)
		{
			System.out.println(ip);
		}
	}

}
