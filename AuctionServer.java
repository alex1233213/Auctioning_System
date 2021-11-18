/**
 * Program representing server of the Auctioning System.
 * 
 * 
 * 
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;



public class AuctionServer
{
	private static ServerSocket serverSocket;
	private static final int PORT = 1234;
	private static List<ClientHandler> clientList = new ArrayList<>();


	public static void sendToAll(String message) throws IOException {
        for(ClientHandler client : clientList) {
            client.getOutput().writeUTF(message);
		}
    }


	//will return true if the client is already registered to the system
	public static boolean clientExists(String clientName) {
		
		for(int i = 0; i < clientList.size() ; ++i) { 
			if(clientList.get(i).getClientName() == null) { 
				continue;
			}

			if( clientList.get(i).getClientName().equals(clientName) ) { 
				return true;
		    }
		}

		return false;
	}



	public static void main(String[] args) throws IOException
	{

		new AuctionSystem();

		try
		{
			System.out.println("Started Auctioning Server");
			serverSocket = new ServerSocket(PORT);
		}
		catch (IOException ioEx)
		{
			System.out.println("\nUnable to set up port!");
			System.exit(1);
		}

		do
		{
			//Wait for client...
			Socket client = serverSocket.accept();

			//Create a thread to handle communication with
			//this client and pass the constructor for this
			//thread a reference to the relevant socket...
			ClientHandler clientHandler = new ClientHandler(client);
			clientList.add(clientHandler);
			clientHandler.start();//As usual, this method calls run.
		}while (true);
	}

}


class ClientHandler extends Thread
{
	private Socket client;
	private DataInputStream input;
	private DataOutputStream output;
	private String clientName;


	public String getClientName() {
		return clientName;
	}

	public DataOutputStream getOutput() {
		return output;
	}



	public ClientHandler(Socket socket)
	{
		
		//Set up reference to associated socket...
		client = socket;
		

		try
		{
			input = new DataInputStream( socket.getInputStream());
			output = new DataOutputStream( socket.getOutputStream());
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
	}

	
	public void run()
	{	
		AuctionServerProtocol protocol = new AuctionServerProtocol();

		//get client's name when they connect to server
		try {
			String registeringClient = input.readUTF();

			if( AuctionServer.clientExists(registeringClient) == false ) {
				clientName = registeringClient;
			} else { 
				output.writeUTF("Client " + registeringClient + " is already registered to the system");
				output.writeUTF("QUIT");
				interrupt();
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		if(clientName != null) { 
			System.out.println(clientName + " has been registered to the system");
		}
		

		String clientInput;
		String outputLine;

		//send the initial menu to the client
		outputLine = protocol.processInput(null);
        try {
			output.writeUTF(outputLine);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			while ( ( clientInput = input.readUTF() ) != null ) {
				outputLine = protocol.processInput(clientInput);

				try {
					output.writeUTF(outputLine);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if (outputLine.equals("QUIT") ) {
					break;
				}
					
			}
		} catch (IOException e) {
			e.printStackTrace();
		}


		try
		{
			if (client!=null)
			{
				System.out.println(
							"Closing down connection...");
				
				client.close();
			}
		}
		catch(IOException ioEx)
		{
			System.out.println("Unable to disconnect!");
		}
	}

}
