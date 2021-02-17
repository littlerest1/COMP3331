import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import java.util.Base64;

public class WebServer{
	private static byte[] byteImage;
	 public static void main(String[] args) throws Exception
	   {
	      // Get command line argument.
		  int port = 8000; 
	      if (args.length != 1) {
	         port = 8000;
	      }
	      else{
	    	  port = Integer.parseInt(args[0]);
	      }
	      
	      String clientSentence;
	      String capitalizeSentence;
	      String classpath = System.getProperty("java.class.path");
	      System.out.println(classpath);
	      ServerSocket welcome = new ServerSocket(port,10,InetAddress.getByName("127.0.0.1"));
	      
	      while(true) {
	    	  Socket connection = welcome.accept();
    	  
	    	  BufferedReader inFromClient = 
	    		new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    	  
	    	  DataOutputStream outToClient =
	    		new DataOutputStream(connection.getOutputStream());
	    	  
	    	  clientSentence = inFromClient.readLine();
	    	  
	    	  capitalizeSentence = clientSentence.toUpperCase() + '\n';
	    	  
	    	 // outToClient.writeBytes(capitalizeSentence);
	    	  int start = clientSentence.indexOf("/");
	    	  int end = clientSentence.indexOf("HTTP");
	    	  System.out.println(start + "	" + end);
	    	  String file = clientSentence.substring(start+1, end-1);
	    	  System.out.println(file);
	    	  
	    	  File temp = new File("./"+file);
	    	  if(temp.exists()) {
	    		  String imageString = "";
	    		  if(file.contains(".png")) {
	    			 // OutputStream outputStream = connection.getOutputStream();
	    			  System.out.println("png exist");
	    			  try
	    			    {
	    			      // the line that reads the image file
	    				  System.out.println("1");
	    				  File f = new File(file);
	    				  FileInputStream fis = new FileInputStream(f);
	    				
	    				 
	    			      BufferedImage image = ImageIO.read(fis);
	    			      ByteArrayOutputStream bScrn = new ByteArrayOutputStream();
	    			      ImageIO.write(image, "png", bScrn);
	    			      byte[] imageBytes = bScrn.toByteArray();
	    			      byte[] encodedBytes = Base64.getEncoder().encode(imageBytes);
	    			      String img = new String(encodedBytes);
	    			      String starting = "<html>\n"
	    			      		+ "<head><title>" + file+ "</title></head>\n" + "<body>";
	    			      String test = "<p>hello world</p>\n";
	    		          imageString = "<img src=\"data:image/png;base64," + img + "\">";
	    			      String ending = "\n</body>\n"
	    			      		+ "</html>";

	    		          PrintWriter out = new PrintWriter(connection.getOutputStream());


	    		          out.println("HTTP/1.1 200 OK");
	    		          out.println("Content-Type: text/html");
	    		          out.print("\r\n");
	    		          out.print(starting + imageString + ending);
	    		          out.flush();
	    			      out.close();			            
	    			    } 
	    			    catch (IOException e)
	    			    {
	    			      System.out.println("Error exists " + e);
	    			    }
	    		  }
	    		  else { 
	    			  System.out.println("exist");
	    			  	 
	    			  InputStream is = new FileInputStream(temp); 
	    			  BufferedReader buf = new BufferedReader(new InputStreamReader(is)); 
	    			  String line = buf.readLine(); 
	    			  StringBuilder sb = new StringBuilder(); 
	    			  while(line != null){ 
	    				  sb.append(line).append("\n"); 
	    				  line = buf.readLine(); 
	    			 } 
	    			  String fileAsString = sb.toString();
			  
	    			  PrintWriter out = new PrintWriter(connection.getOutputStream());
	    			  
    		          out.println("HTTP/1.1 200 OK");
    		          out.println("Content-Type: text/html");
    		          out.print("\r\n");
    		          out.print(fileAsString);
    		          out.flush();
	    			  out.close();
	    		  }
	    	  }
	    	  else {
	    		  outToClient.writeBytes("404 Not Found\n");
	    	  }
	    	//  connection.close();
	      }
	   }
	 
}
