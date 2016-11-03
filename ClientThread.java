package Test;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;

public class ClientThread extends Thread 
{
    
    private String UserName = null;
    private BufferedReader Input = null;
    public PrintStream Output = null;
    private Socket Socket = null;
    private final ArrayList<ClientThread> ClientThreads;

    // only relevant for Part IV: adding friendship
    ArrayList<String> friends = new ArrayList<String>();
    ArrayList<String> friendrequests = new ArrayList<String>();  //keep track of sent friend requests 
    
    public ClientThread(Socket Socket, ArrayList<ClientThread> ClientThreads) 
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
        for(Iterator<ClientThread> i = ClientThreads.iterator(); i.hasNext();)
        {
            ClientThread client = i.next();
            client.Output.println("Incoming Message: " + Message);
        }
    }
    
    public void run() {

        System.out.println("Client #" + this.ClientThreads.size() + "started");
        Output.println("Enter your Username:");
        
        while(true)
        {
            try{
                while(Input.readLine() != null)
                {
                
                String input = Input.readLine();
                
                if(this.UserName ==  null)
                {
                
                    this.UserName = input;

                    System.out.println("trying to print the username: " + this.UserName);
             }
             else
             {
                 this.BroadcastMessage(input);
             }
            }
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
 
            System.out.println("Made it through the run statement");
            System.exit(-1);

        }
        
    }
}