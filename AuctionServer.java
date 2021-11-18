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


	public static BidItem getCurrentBidItem() {
		return currentBidItem;
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


	static void countDownBidPeriod() { 
		Timer timer = new Timer();
		
		//start at the first item in the list
		currentBidItem = bidItems.get(0);

		float originalPrice = currentBidItem.getPrice();

		timer.scheduleAtFixedRate(new TimerTask() {
			int seconds = currentBidItem.getBidPeriod();
				

			public void run() {
				System.out.println(currentBidItem.getName() + " " + seconds--);
				
				//timer expires
				if ( seconds < 0 ) {
					
					//if the price is different than initial price means then mark product as sold 
					//and get the next product
					if( currentBidItem.getPrice() != originalPrice ) { 
						currentBidItem.setSold(true);
						try {
							sendToAll(String.format("%s has been sold for %.2f euro",
													 currentBidItem.getName(),
													 currentBidItem.getPrice()) );

						} catch (IOException e) {
							e.printStackTrace();
						}
						
						//get the next item in the product list, if there is any
						if( getNextBidItem() != null ) { 
							currentBidItem = getNextBidItem();
							seconds = currentBidItem.getBidPeriod();
						}
					} else { //when price is the same, reset the timer to original product's bid period
						seconds = currentBidItem.getBidPeriod();
					}
				}
			}
		}, 0, 1000);
	}



	public static void main(String[] args) throws IOException
	{

		bidItems.add(new BidItem("Bicycle", 100f, 3));
		bidItems.add(new BidItem("Keyboard", 10f, 60));
		bidItems.add(new BidItem("Mouse", 7.5f, 60));
		bidItems.add(new BidItem("Monitor", 120f, 60));
		bidItems.add(new BidItem("HDMI cable", 5.5f, 60));
		
		countDownBidPeriod();

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
			// clientHandler.setAuctionServer(auctionServer);

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



	public Socket getClient() {
		return client;
	}


	public void setClient(Socket client) {
		this.client = client;
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
