import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;

public class Server 
{
    // Create a socket for the server 
    private static ServerSocket Socket = null;
    // Create a socket for the user 
    // Maximum number of users 
    private static int MaxUsers = 5;
    // An array of threads for users
    private static List<ClientThread> ClientThreads;

    public static void main(String args[]) 
    {
        ClientThreads =  Collections.synchronizedList(new ArrayList<ClientThread>());
        // The default port number.
        int portNumber = 8000;

        try
        {
            Socket = new ServerSocket(portNumber);
        }
        catch (IOException e)
        {
            System.out.println("error creating socket");
            System.exit(-1);
        }

        System.out.println("Server running on " +portNumber);

        Runtime.getRuntime().addShutdownHook(new ShutdownHook(Socket));


        while (true) 
        {
            try
            {
                Socket userSocket = Socket.accept();
                PrintStream output_stream = new PrintStream(userSocket.getOutputStream(), true);
                System.out.println("Server has Accepted");
                
                if(ClientThreads.size() < MaxUsers)
                {
                    BufferedReader input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
                    
                    ClientThread client = new ClientThread(userSocket, ClientThreads);
                    client.start();
                }
                else
                {
                        output_stream.println("Too Many Clients.  Forceful Disconnect\n");
                        userSocket.close();
                }

            } 
            catch (IOException e)
            {
                System.out.println("Error Connecting to Socket");
                 try
        {
            Socket.close();
        }
        catch(Exception s){
        }
                System.exit(1);

            }
        }

    }
}

class ShutdownHook extends Thread 
{
    
    private ServerSocket Server = null;
    
    public ShutdownHook(ServerSocket Server) 
    {
        this.Server = Server;
       
    }

    public void run() 
    {
        System.out.println("Shutdown Hook executing");
        try
        {
            this.Server.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
            System.exit(-1);
        }
    }
        
    }



class ClientThread extends Thread 
{
    
    private String UserName = null;
    private BufferedReader Input = null;
    public PrintStream Output = null;
    private Socket Socket = null;
    private List<ClientThread> ClientThreads;

    // only relevant for Part IV: adding friendship
    ArrayList<String> friends = new ArrayList<String>();
    ArrayList<String> friendrequests = new ArrayList<String>();  //keep track of sent friend requests 
    
    public ClientThread(Socket Socket, List<ClientThread> ClientThreads) 
    {
        this.Socket = Socket;
        this.ClientThreads = ClientThreads;
        this.ClientThreads.add(this);
        try
        {
            Input = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
            Output = new PrintStream(Socket.getOutputStream(), true);

            
        }
        catch (IOException e)
        {
            System.out.println(e);
            System.exit(1);

        }
    }

    private void BroadcastMessage(String Message)
    {
        System.out.println("Made it to broadcast message");
        for(Iterator<ClientThread> i = ClientThreads.iterator(); i.hasNext();)
        {
            ClientThread client = i.next();
            
            if(client != this)
            {
                client.Output.println(Message);
            }
        }
    }

    private void DirectMessage(String Message)
    {
        if(Message.split(" ").length == 1)
        {
            System.out.println("No message sent!");
            return;
        }
        String userName = Message.substring(0, Message.indexOf(' '));
        String message = Message.substring(Message.indexOf(' ') + 1);

        System.out.println("Made it to the direct message\n UserName: " + userName + "\n" + "Message: " + message);

        for(Iterator<ClientThread> i = ClientThreads.iterator(); i.hasNext();)
        {
            ClientThread client = i.next();
            System.out.println("Searching Client UserName: " + ("@" + client.UserName.toLowerCase()) + "against" + userName);
            System.out.println("Evaluation = " + ("@" + client.UserName.toLowerCase()).equals(userName.toLowerCase()));
            
            if(("@" + client.UserName.toLowerCase()).equals(userName.toLowerCase()))
            {
                System.out.println("Found a match with " + client.UserName);
                client.Output.println("\033[31m" + this.UserName + ": " + message + "\033[0m");
                this.Output.println("\033[32m" + "Direct Message successfully sent." + "\033[0m");
            }
        }
    }
    
    public void run() {

        System.out.println("Client #" + this.ClientThreads.size() + "started");
        Output.println("Enter your Username:");
        
        while(true)
        {
            try{
                String input;
                while((input = Input.readLine()) != null)
                {
                               
                System.out.println("Got some input: " + input);
                
                if(this.UserName ==  null)
                {
                
                    this.UserName = input;

                    this.BroadcastMessage(this.UserName + " has connected to the chatroom");
             }
             else if(!input.startsWith("@") && input.length() > 0)
             {
                 this.BroadcastMessage(this.UserName + ": " + input);
             }
            //  else if( input.length() == 1)
            //  {
            //      char c = input.charAt(0);
            //      Integer i = new Integer(c);
            //      String value = i.toString();
            //      System.out.println("Here is the character: " + value);
            //  }
             else
             {
                 this.DirectMessage(input);
             }
            }
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
            String leftText = this.UserName + " has left the chat room.";
            System.out.println(leftText);
            this.BroadcastMessage(leftText);
            this.ClientThreads.remove(this);
            return;

        }
        
    }
}