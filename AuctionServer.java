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
import java.util.Scanner;



public class AuctionServer
{
	private static ServerSocket serverSocket;
	private static final int PORT = 1234;
	private static List<ClientHandler> clientList = new ArrayList<>();


	/** 
	 * Method that sends a message to all clients that have joined the auction
	 */
	public static void sendToAllParticipants(String message) throws IOException {
        for(ClientHandler client : clientList) {
			if( client.getProtocol().isReceivingOptionFromMenu()  == false ) { 
				client.getOutput().writeUTF(message);
			}
		}
    }


	/*
		When an item is sold, users will be sent notification informing about 
		bidding for the new item listed. The state of the protocol for all clients
		is changed to respond to the notification sent. 
	*/
	public static void resetStateForAllClients() throws IOException {
		String notification = String.format("\n\nCurrent item for sale is " +
						 AuctionSystem.getCurrentBidItem().getName() + 
						 " - price is %.2f euro" +
						  "\n * Enter 1 to place a bid on the item\n" + 
						 " * Enter 2 to leave auction.", AuctionSystem.getCurrentBidItem().getPrice() );
		sendToAllParticipants(notification);

		
		for(ClientHandler client : clientList) {
			//reset bidding state for clients unless the client is in the main menu
			if( client.getProtocol().isReceivingOptionFromMenu()  == false ) {  
				client.getProtocol().changeStateToReceive();
			}
            
		}
	}


	//method to notify clients when a bid item is sold or when the bid has been raised
	//highest bidder can receive notification from any state of the protocol
	//all other clients are notified only if they have joined the auction
	static void notifyBidItemEvent(String msgForHighestBidder, String msgForOthers, BidItem bidItem) { 
		for(ClientHandler client: clientList) { 
			//custom message for the highest bidder or buyer
			if( client.getClientName().equals(bidItem.getHighestBidder() ) ) { 
				try {
					client.getOutput().writeUTF(msgForHighestBidder);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else { // for any other client, only inform them if they have joined the auction
				if( client.getProtocol().isReceivingOptionFromMenu()  == false ) { 
					try {
						client.getOutput().writeUTF(msgForOthers);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
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
		System.out.println("Started Auctioning Server");
		AddBidItemThread addBidItemThread = new AddBidItemThread();
		addBidItemThread.start();

		new AuctionSystem();

		try
		{
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


//Thread that allows including new items for sale in the auction server
class AddBidItemThread extends Thread { 
	Scanner addBidItemScanner = new Scanner(System.in);
	
	public void run() { 
		System.out.println("Enter '+' to add an item to the auction system");

		while(true) { 
			if(addBidItemScanner.nextLine().equals("+")) { 

				try { 
					System.out.println("Enter the name of the auction item");
					String name = addBidItemScanner.nextLine();

					System.out.println("Enter the price for the item");
					float price = addBidItemScanner.nextFloat();
	
					System.out.println("Enter the bid period for the item in seconds");
					int bidPeriod = addBidItemScanner.nextInt();
	
					if(bidPeriod > 60) { 
						System.out.println("Bid item could not be added. Max period must be 60 seconds\nEnter '+' to try again");
					} else { 
						AuctionSystem.getBidItems().add(new BidItem(name, price, bidPeriod)); 
						System.out.println("Successfully added item to auction system. Enter '+' to add another item");
					}
				} catch (Exception e) { 
					e.printStackTrace();
					System.out.println("Could not add a auction item");
				}
			}
		}
	}


}



class ClientHandler extends Thread
{
	private AuctionServerProtocol protocol;
	private Socket client;
	private DataInputStream input;
	private DataOutputStream output;
	private String clientName;


	public AuctionServerProtocol getProtocol() {
		return protocol;
	}

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

		protocol = new AuctionServerProtocol(clientName);

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
