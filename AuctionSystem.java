import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionSystem {
    private static List<BidItem> bidItems = new ArrayList<>();
	private static BidItem currentBidItem;
	private static int bidPeriod = 60;
	static Timer timer;

    public AuctionSystem() { 
        bidItems.add(new BidItem("Bicycle", 100f, 5));
		bidItems.add(new BidItem("Keyboard", 10f, 5));
		bidItems.add(new BidItem("Mouse", 7.5f, 5));
		bidItems.add(new BidItem("Monitor", 120f, bidPeriod));
		bidItems.add(new BidItem("HDMI cable", 5.5f, bidPeriod));

        //start auctioning first item in the list
		currentBidItem = bidItems.get(0);
		
		countDownBidPeriod();
    }


	public static List<BidItem> getBidItems() {
		return bidItems;
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


    public static BidItem getCurrentBidItem() {
		return currentBidItem;
	}



    static void countDownBidPeriod() { 
		timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			int seconds = currentBidItem.getBidPeriod();

			public void run() {
				System.out.println(currentBidItem.getName() + " " + seconds--);
				
				//timer expires
				if ( seconds < 0 ) {
					
					//if the price is different than initial price means then mark product as sold 
					//and get the next product
					if( currentBidItem.getPrice() != currentBidItem.getListingPrice() ) { 
						
						currentBidItem.setSold(true);

						
						AuctionServerProtocol.sendSellMsg(currentBidItem);							
						
                        //get the next item to sell
						if( getNextBidItem() != null ) { 
							currentBidItem = getNextBidItem();
							seconds = currentBidItem.getBidPeriod();

							try {
								AuctionServer.resetStateForAllClients();
							} catch (IOException e2) {
								e2.printStackTrace();
							}
	
						} else {

							currentBidItem = null;
							timer.cancel();

							try {
								AuctionServer.sendToAllParticipants("All items have been sold");
							} catch (IOException e) {
								e.printStackTrace();
							}

						}
					} else { //when price is the same, reset the timer to original product's bid period
						seconds = currentBidItem.getBidPeriod();
					}
				}
			}
		}, 0, 1000);
	}


    //method returns string if bid price is updated successfully 
    //or returns null if an error occurred when updating the bid price
    static BidItem updateBidPrice(float price, String client) { 
       

        if(price > currentBidItem.getPrice()) { 
            currentBidItem.setPrice(price);
			currentBidItem.setHighestBidder(client);

			//restart timer
			timer.cancel();
			countDownBidPeriod();

			return currentBidItem;

        }         

        return null;
    }



	static String getAuctionItems() {
		String result = "Items in the auction\n-----------------------------\n";

		for(BidItem bidItem: bidItems) { 
			String status;

			if( bidItem.isSold() != true ) { 
				if( currentBidItem.equals(bidItem) ) { 
					status = "started";
				} else { //item is queued for sale
					status = "not started";
				}
			} else { 
				status = "sold";
			}
			result += " * " + bidItem.getName()  + "\t\tprice: " + bidItem.getPrice() + "\t\tstatus: " + status + "\n";
		}


		result += "\nCurrent item for sale is " + currentBidItem.getName() + " - price is " + currentBidItem.getPrice() + " euro";

		return result;
	}


}
