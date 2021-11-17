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

	public static void main(String[] args) throws IOException
	{
		AuctionServer auctionServer = new AuctionServer();

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

			System.out.println("\nNew client accepted.\n");

			//Create a thread to handle communication with
			//this client and pass the constructor for this
			//thread a reference to the relevant socket...
			ClientHandler clientHandler = new ClientHandler(client);
			clientHandler.setAuctionServer(auctionServer);

			clientList.add(clientHandler);
			clientHandler.start();//As usual, this method calls run.
		}while (true);
	}


	public void sendToAll(String message) throws IOException{
        for(ClientHandler client : clientList) {
            client.getOutput().writeUTF(message);
		}
    }

}


class ClientHandler extends Thread
{
	private Socket client;
	private DataInputStream input;
	private DataOutputStream output;
	private AuctionServer auctionServer;


	public AuctionServer getAuctionServer() {
		return auctionServer;
	}


	public void setAuctionServer(AuctionServer auctionServer) {
		this.auctionServer = auctionServer;
	}


	public Socket getClient() {
		return client;
	}


	public void setClient(Socket client) {
		this.client = client;
	}


	public DataInputStream getInput() {
		return input;
	}


	public void setInput(DataInputStream input) {
		this.input = input;
	}


	public DataOutputStream getOutput() {
		return output;
	}


	public void setOutput(DataOutputStream output) {
		this.output = output;
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
		AuctionServerProtocol protocol = new AuctionServerProtocol(auctionServer);

		String clientInput;
		String outputLine;

		outputLine = protocol.processInput(null);
        try {
			output.writeUTF(outputLine);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			while ( ( clientInput = input.readUTF()) != null ) {
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
