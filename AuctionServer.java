/**
 * Program representing server of the Auctioning System.
 * 
 * 
 * 
 */

import java.io.*;
import java.net.*;
import java.util.*;


public class AuctionServer
{
	private static ServerSocket serverSocket;
	private static final int PORT = 1234;
	


	public static void main(String[] args) throws IOException
	{
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

			clientHandler.start();//As usual, this method calls run.
		}while (true);
	}


	
}


class ClientHandler extends Thread
{
	private Socket client;
	private DataInputStream input;
	private DataOutputStream output;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//****working  */
		// if( Objects.equals(userChoice, "1") ) { 

		// 	output.println("Enter your bid amount");
		// 	highestBid = input.nextLine();
			
		// 	//validate bid
		// 	// if() { 

		// 	// }

		// 	//print new bid to all users connected
		// 	output.println("The bid for bicycle is now " + highestBid);

		// } else if ( Objects.equals(userChoice, "5") ) { 
		// 	output.println("Closing connection..");
		// } else { 
		// 	output.println("Unrecognized command");
		// }


	



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
