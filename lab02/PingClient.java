import java.io.*;
import java.net.*;
import java.util.*;

/*
 * Server to process ping requests over UDP. 
 * The server sits in an infinite loop listening for incoming UDP packets. 
 * When a packet comes in, the server simply sends the encapsulated data back to the client.
 */

public class PingClient
{
   private static final double LOSS_RATE = 0.3;
   private static final int AVERAGE_DELAY = 0;  // milliseconds

   public static void main(String[] args) throws Exception
   {
      // Get command line argument.
      if (args.length != 2) {
         System.out.println("Required arguments: server port");
         return;
      }
      int port = Integer.parseInt(args[1]);
      InetAddress host = InetAddress.getByName(args[0]);

      // Create random number generator for use in simulating 
      // packet loss and network delay.
      Random random = new Random();

      // Create a datagram socket for receiving and sending UDP packets
      // through the port specified on the command line.
	  DatagramSocket socket = new DatagramSocket(port);
      // Processing loop.
      int x = 0;
      long min = 1000;
      long max = 0;
      long total = 0;
      int count = 0;
      while (x < 10) {
    	  DatagramPacket p = new DatagramPacket(new byte[1024], 1024,host,port);
    	  long start = new Date().getTime();
    	  socket.send(p);
    	 try {
    		 socket.setSoTimeout(1000);
    		 DatagramPacket response = new DatagramPacket(new byte[1024],1024);
    		 socket.receive(response);
    		 long end = new Date().getTime();
    		 long rtt = end - start;
    		 if (rtt <= min) {
    			 min = rtt;
    		 }
    		 else if(rtt > max) {
    			 max = rtt;
    		 }
    		 total += rtt;
    		 count ++;
    		 System.out.println("Ping to " + args[0] + ":" + port + " seq = " + x + " rtt = " + rtt + "ms");
    	 }
    	 catch(IOException e) {
    		 System.out.println("Ping to " + args[0] + ":" + port + " seq = " + x + " timeout");
    	 }
         x ++;
      }
      long aver = 0;
      if(count != 0){
        aver = total/count;
        System.out.println("Min = " + min + "ms Max = " + max + "ms Aver = " + aver + "ms");
      }
      else{
        System.out.println("Could not be connected with server");
      }
   }

   /* 
    * Print ping data to the standard output stream.
    */
   private static void printData(DatagramPacket request) throws Exception
   {
      // Obtain references to the packet's array of bytes.
      byte[] buf = request.getData();

      // Wrap the bytes in a byte array input stream,
      // so that you can read the data as a stream of bytes.
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);

      // Wrap the byte array output stream in an input stream reader,
      // so you can read the data as a stream of characters.
      InputStreamReader isr = new InputStreamReader(bais);

      // Wrap the input stream reader in a bufferred reader,
      // so you can read the character data a line at a time.
      // (A line is a sequence of chars terminated by any combination of \r and \n.) 
      BufferedReader br = new BufferedReader(isr);

      // The message data is contained in a single line, so read this line.
      String line = br.readLine();

      // Print host address and data received from it.
      System.out.println(
         "Received from " + 
         request.getAddress().getHostAddress() + 
         ": " +
         new String(line) );
   }
}

