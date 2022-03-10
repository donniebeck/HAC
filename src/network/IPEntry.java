package network;

import java.net.InetAddress;
import java.time.LocalDateTime;

public class IPEntry 
{
	InetAddress nodeIP;
	Boolean isAlive;
	LocalDateTime timeStamp;
	
	public InetAddress getNodeIP() 
	{
		return this.nodeIP;
	}

	public void setNodeIP(InetAddress nodeIP) 
	{
		this.nodeIP = nodeIP;
	}

	public Boolean getIsAlive() 
	{
		return this.isAlive;
	}

	public void setIsAlive(Boolean isAlive) 
	{
		this.isAlive = isAlive;
	}

	public LocalDateTime getTimeStamp() 
	{
		return this.timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) 
	{
		this.timeStamp = timeStamp;
	}

	public IPEntry(InetAddress nodeIPAddress, Boolean status)
	{
		this.nodeIP = nodeIPAddress;
		this.isAlive = status;
		this.timeStamp = LocalDateTime.now();
	}
}
