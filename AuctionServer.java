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
import java.util.Timer;
import java.util.TimerTask;



public class AuctionServer
{
	private static ServerSocket serverSocket;
	private static final int PORT = 1234;
	private static List<ClientHandler> clientList = new ArrayList<>();
	private static List<BidItem> bidItems = new ArrayList<>();
	private static BidItem currentBidItem;

	public static void main(String[] args) throws IOException
	{
		AuctionServer auctionServer = new AuctionServer();

		bidItems.add(new BidItem("Bicycle", 100f, 3));
		bidItems.add(new BidItem("Keyboard", 10f, 5));
		bidItems.add(new BidItem("Mouse", 7.5f, 60));
		bidItems.add(new BidItem("Monitor", 120f, 60));
		bidItems.add(new BidItem("HDMI cable", 5.5f, 60));

		

		Timer timer = new Timer();
		
		//start at the first item in the list
		currentBidItem = bidItems.get(0);

		timer.scheduleAtFixedRate(new TimerTask() {
		int seconds = currentBidItem.getBidPeriod();
			

			public void run() {
				System.out.println(currentBidItem.getName() + " " + seconds--);
				
				//timer expires and item is sold
				if ( seconds < 0 && currentBidItem.isSold()) {
					
					currentBidItem = getNextBidItem();

					//when there are no items left to sell, inform all users
					if(currentBidItem == null) { 
						try {
							sendToAll("All items are sold. Please check later on for new bids");
							timer.cancel();
						} catch (IOException e) {
							e.printStackTrace();
						}
						// System.out.prinln(" ")
					} else  {
						seconds = currentBidItem.getBidPeriod();
					}
					
				//bid period ends and item has not been sold 
				} else if( seconds < 0 && (currentBidItem.isSold() ) == false)  {
					//reset timer for the current item
					seconds = currentBidItem.getBidPeriod();
				}
			}
		}, 0, 1000);
		

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


	public static void sendToAll(String message) throws IOException {
        for(ClientHandler client : clientList) {
            client.getOutput().writeUTF(message);
		}
    }

	//scan through the bid items list and return the next item that is not sold
	static BidItem getNextBidItem() { 

		for(int i = 0 ; i < bidItems.size(); ++i) { 
			if( bidItems.get(i).isSold() == false ) { 
				return bidItems.get(i);
			}
		}

		return null;
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
