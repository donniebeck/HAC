package network;

import java.io.Serializable;
import java.time.LocalDateTime;

public class IPEntry implements Serializable
{
	Boolean isAlive;
	LocalDateTime timeStamp;

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
	
	public void setTimeStampNow()
	{
		this.timeStamp = LocalDateTime.now();
	}

	public IPEntry(Boolean status)
	{
		this.isAlive = status;
		this.timeStamp = LocalDateTime.now();
	}
	
	public String getStatusString()
	{
		if(isAlive)
		{
			return " is alive!";
		}
		return " is dead.";
	}
	
	
}
