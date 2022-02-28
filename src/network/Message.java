package network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Vector;



// Authors: Zhihao Jin and Donnie Beck
// version: 1.0
// Date modified: 02/27/2022

public class Message implements Serializable {
	// The serialVersionUID is a identifier that is used to 
	// serialize/deserialize an object of a Serializable class.
	// value could be 1,2,3,4L
	private static final long serialVersionUID = 1L;
	
	// Message properties.
	private double version = 1.0;
	private boolean isP2P;
    private boolean isHeartbeating;
    private String text;
    private Vector<InetAddress> IPlist;
    
    // The Message constructor.
    public Message(boolean isP2P, boolean isHeartbeating, String text, Vector<InetAddress> IPs) {
    	
    	this.isP2P = isP2P;  //True: P2P, False: Client-Server
    	this.isHeartbeating = isHeartbeating; 
        this.text = text;  // The information this variable contains
        this.IPlist = IPs; // The IP or IP lists the message may contain.
    }
    
   // Since all information has to be serialized before they go to packet.
    private byte[] serializedMessage() {
    	// Create a new message object to be serialized.
        Message serialMessageObj = new Message(this.isP2P, this.isHeartbeating, this.text, this.IPlist);
        
        // Serialize the message to be fitted into a buffer
        ByteArrayOutputStream bufferstream = new ByteArrayOutputStream();
        ObjectOutput messagebuffer;
        
        
        // write the message objects in the buffer.
        try {
          messagebuffer = new ObjectOutputStream(bufferstream);
          messagebuffer.writeObject(serialMessageObj);
          messagebuffer.close();
        } catch (IOException e1) {
          e1.printStackTrace();
          System.exit(-1);
        }
        byte[] requestbuffer = bufferstream.toByteArray();

        return requestbuffer;
      }
    
    
}
