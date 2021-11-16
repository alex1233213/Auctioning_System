/**
 * Implementation of the client side of the connection to the Auctioning Sytem.
 * 
 * 
 * 
 * 
 */


import java.io.*;
import java.net.*;
import java.util.*;

public class AuctionClient
{
	private static InetAddress host;
	private static final int PORT = 1234;

	public static void main(String[] args)
	{
		try
		{
			host = InetAddress.getLocalHost();
		}
		catch(UnknownHostException uhEx)
		{
			System.out.println("\nHost ID not found!\n");
			System.exit(1);
		}
		sendMessages();
	}

	private static void sendMessages()
	{
		Socket socket = null;

		try
		{
			socket = new Socket(host,PORT);

			//for reading from the server
			Scanner networkInput =
						new Scanner(socket.getInputStream());

			//for writing to the server
			PrintWriter networkOutput =
					new PrintWriter(
							socket.getOutputStream(),true);

			//Set up stream for keyboard entry...
			Scanner userEntry = new Scanner(System.in);

			String message, response;

			
			while(networkInput.hasNextLine()) { 
				response = networkInput.nextLine();
				System.out.print("\n" + response);
			}

			userEntry.close();
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}

		finally
		{
			try
			{
				System.out.println("\nClosing connection...");
				socket.close();
			}
			catch(IOException ioEx)
			{
				System.out.println("Unable to disconnect!");
				System.exit(1);
			}
		}
	}
}

