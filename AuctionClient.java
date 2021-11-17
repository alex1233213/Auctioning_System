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
			DataInputStream networkInput =
						new DataInputStream(socket.getInputStream());

			//for writing to the server
			DataOutputStream networkOutput =
					new DataOutputStream(
							socket.getOutputStream());

			//Set up stream for keyboard entry...
			Scanner userEntry = new Scanner(System.in);

			String msgFromServer = "", msgToServer;

			

			//continue communication with server until "QUIT" message received
			while ( (msgFromServer = networkInput.readUTF() ) != null ) {
               
				System.out.println(msgFromServer);

                if (msgFromServer.equalsIgnoreCase("QUIT")) {
					break;
				}
                    
				System.out.print("\n>>");
                msgToServer = userEntry.nextLine();
                if (msgToServer != null) {
                    networkOutput.writeUTF(msgToServer);
                }

            }


			//*****prvious code ***/

			//read multiple lines from the server -> connection startup message
			// while(networkInput.hasNextLine()) { 
			// 	msgmsgFromServer = networkInput.nextLine();
			// 	System.out.print("\n>MSG FROM SERVER:" + msgmsgFromServer);

			// 	if(msgmsgFromServer.isEmpty()) { 
			// 		break;
			// 	}
			// }


			// //enter the choice and send to server
			// choice = userEntry.nextLine();
			// networkOutput.println(choice);

			// //server asking for bid amount
			// System.out.println(networkInput.nextLine());
			// networkOutput.println("60");

			// //get the highest bid msg
			// System.out.println(networkInput.nextLine());

			//close scanner
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

