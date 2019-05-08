import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

public class Server
{	
	ArrayList clientOutputStreams;
	
	ArrayList<String> activeUsers;
	
	public static void main(String[] args)
	{
		Server server = new Server();
	}
	
	public Server()
	{
		System.out.println("Within server cons");
		clientOutputStreams = new ArrayList();
		activeUsers = new ArrayList();
		try
		{
			ServerSocket serverSock = new ServerSocket(5000);
			
			while(true)
			{
				Socket clientSocket = serverSock.accept();
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				clientOutputStreams.add(writer);
				
				//System.out.println("Start server thread");
				Thread thread = new Thread(new ClientHandler(clientSocket));
				thread.start();
				//System.out.println("Server got a connection");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("4");
		}
		
		System.out.println("End server cons");
	}
	
	public void tellEveryone(String message)
	{
		Iterator it = clientOutputStreams.iterator();
		while(it.hasNext())
		{
			try
			{
				PrintWriter writer = (PrintWriter) it.next();
				writer.println(message);
				writer.flush();				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				System.out.println("1");
			}
		}
	}
	
	
	public class ClientHandler implements Runnable
	{
		BufferedReader bf;
		Socket sock;
		
		public ClientHandler(Socket clientSocket)
		{
			try
			{
				sock = clientSocket;
				InputStreamReader isr = new InputStreamReader(sock.getInputStream());
				bf = new BufferedReader(isr);
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
				System.out.println("2");
			}
		}
		
		public void run()
		{
			String message;
			try
			{
				while((message = bf.readLine()) != null)
				{
					if(message.contains("inittini#")) //If a user has joined the chat
					{
						String name = message.split("#")[1];
						activeUsers.add(name);
						System.out.println(name + " just joined the chat.");
						System.out.println(activeUsers.size() + " user(s) in this chat");
						tellEveryone("userjoinniojresu#" + name);
					}
					else if(message.contains("closeesolc#")) //If a user has left the chat
					{
						String name = message.split("#")[1];
						activeUsers.remove(name);
						System.out.println(name + " just left the chat.");
						System.out.println(activeUsers.size() + " user(s) in this chat");
						tellEveryone("usergoneenogresu#" + name);
					}
					else
					{
						//System.out.println("read " + message);
						tellEveryone(message);	
					}
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				System.out.println("3");
				
			}
		}
	}
}
