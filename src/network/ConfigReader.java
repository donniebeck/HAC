package network;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigReader
{
	public String[] getIPListFromFile() throws IOException
	    {
	        // list that holds strings of a file
	        List<String> listOfStrings = new ArrayList<String>();
	       
	        // load data from file
	        BufferedReader bf = new BufferedReader(new FileReader("ip.txt"));
	       
	        // read entire line as string
	        String line = bf.readLine();
	       
	        // checking for end of file
	        while (line != null) 
	        {
	            listOfStrings.add(line);
	            line = bf.readLine();
	        }
	       
	        // closing bufferreader object
	        bf.close();
	       
	        // storing the data in arraylist to array
	        return listOfStrings.toArray(new String[0]);
	    }
}