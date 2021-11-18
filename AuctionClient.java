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
	private static Socket socket;

	final static Thread receiveMsgThread = new Thread() {
		
		@Override
		public void run() {

			try
			{
	
				//for reading from the server
				DataInputStream networkInput =
							new DataInputStream(socket.getInputStream());
	
	
				//Set up stream for keyboard entry...
				Scanner userEntry = new Scanner(System.in);
	
				String msgFromServer = "";
	
	
				//continue communication with server until "QUIT" message received
				while ( (msgFromServer = networkInput.readUTF() ) != null ) {
				   
					System.out.println(msgFromServer);
	
					if (msgFromServer.equalsIgnoreCase("QUIT")) {
						break;
					}
	
				}
	
	
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
		};
	};


	
	final static Thread sendMsgThread = new Thread() {
		
		@Override
		public void run() {

		
			try {

				//for writing to the server
				DataOutputStream networkOutput = new DataOutputStream(socket.getOutputStream());

				//Set up stream for keyboard entry...
				Scanner userEntry = new Scanner(System.in);
				String msgToServer = "";

				//continue communication with server until "QUIT" message received
				while ( !msgToServer.equals("5") ) {
					System.out.print("\n>>");
					msgToServer = userEntry.nextLine();
					networkOutput.writeUTF(msgToServer);
				}

				userEntry.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};




	public static void main(String[] args) throws IOException
	{
		try
		{
			host = InetAddress.getLocalHost();
			socket = new Socket(host,PORT);
		}
		catch(UnknownHostException uhEx)
		{
			System.out.println("\nHost ID not found!\n");
			System.exit(1);
		}

		receiveMsgThread.start();
		sendMsgThread.start();
	}
}







	// 		//for reading from the server
	// 		DataInputStream networkInput =
	// 					new DataInputStream(socket.getInputStream());

	// 		//for writing to the server
	// 		DataOutputStream networkOutput =
	// 				new DataOutputStream(
	// 						socket.getOutputStream());

	// 		//Set up stream for keyboard entry...
	// 		Scanner userEntry = new Scanner(System.in);

	// 		String msgFromServer = "", msgToServer;


	// 		//continue communication with server until "QUIT" message received
	// 		while ( (msgFromServer = networkInput.readUTF() ) != null ) {
               
	// 			System.out.println(msgFromServer);

    //             if (msgFromServer.equalsIgnoreCase("QUIT")) {
	// 				break;
	// 			}
                    
	// 			System.out.print("\n>>");
    //             msgToServer = userEntry.nextLine();
    //             if (msgToServer != null) {
    //                 networkOutput.writeUTF(msgToServer);
    //             }

    //         }


	// 		//close scanner
	// 		userEntry.close();
	// 	}
	// 	catch(IOException ioEx)
	// 	{
	// 		ioEx.printStackTrace();
	// 	}

	// 	finally
	// 	{
	// 		try
	// 		{
	// 			System.out.println("\nClosing connection...");
	// 			socket.close();
	// 		}
	// 		catch(IOException ioEx)
	// 		{
	// 			System.out.println("Unable to disconnect!");
	// 			System.exit(1);
	// 		}
	// 	}
	// }