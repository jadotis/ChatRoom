import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;

public class User extends Thread {
    
    // The user socket
    private static Socket Socket = null;

    private static String UserName = null;
    // The SocketOutput stream
    private static PrintStream SocketOutput = null;
    // The SocketInput stream
    private BufferedReader SocketInput = null;
    
    private static BufferedReader ClientInput = null;

    public User(BufferedReader InputStream)
    {
        SocketInput = InputStream;
    }
    
    public static void main(String[] args) 
    {        
        // The default port.
        int portNumber = 8000;
        
        // The default host.
       String host = "localhost";
       BufferedReader socketInput = null;

       if(args.length != 2)
       {
           System.out.println("Not Enough arguments supplied: need host then port");
           System.exit(-1);
       }
       else
       {
           portNumber = Integer.parseInt(args[1]);
           host = args[0];
       }
        try 
        {
		    Socket = new Socket(host, portNumber);
            // SocketInput stream from the user/command line
            socketInput = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
            SocketOutput = new PrintStream(Socket.getOutputStream(),true);
            // create SocketInput stream attached to socket/server
            ClientInput = new BufferedReader(new InputStreamReader(System.in));
        } 
        catch (Exception e) 
        {
            System.err.println("Don't know about host " + host);
        } 

        User stdOutThread = new User(socketInput);
        stdOutThread.start();
        try
        {
            String input;
           while((input = ClientInput.readLine()) != null)
           {
               if(UserName == null)
               {
                   UserName = input;
               }
               else if(input.toLowerCase().equals("logout"))
               {
                   System.out.println("### Bye " + UserName + " ###");
                   stdOutThread.kill();
                   
               }

               SocketOutput.println(input);
           }
        }
        catch(Exception e)
        {
            System.out.println("Problem reading from StdIn");
            System.exit(-1);
        }
    }

    public void kill()
    {
        System.exit(1);
      
    }
    
 
    public void run() 
    {
        String input;
          try
            {
           while((input = SocketInput.readLine()) != null)
           {
                System.out.println(input);
            }
            }
            catch(Exception e)
            {
                System.out.println("Error reading message: " + e);
            }

            System.exit(-1);

        
    }
}



