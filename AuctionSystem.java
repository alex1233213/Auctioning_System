import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class AuctionSystem {
    private static List<BidItem> bidItems = new ArrayList<>();
	private static BidItem currentBidItem;
	private static int defaultBidPeriod = 60;
	static Timer timer;

    public AuctionSystem() { 
        bidItems.add( new BidItem("Bicycle", 100f, 20) );
		bidItems.add( new BidItem("Keyboard", 10f, 20) );
		bidItems.add( new BidItem("Mouse", 7.5f, 20) );
		bidItems.add( new BidItem("Monitor", 120f, 20) );
		bidItems.add( new BidItem("HDMI cable", 5.5f, defaultBidPeriod) );

        //start auctioning first item in the list
		currentBidItem = bidItems.get(0);
		
		countDownBidPeriod();
    }



	static void addBidItem(BidItem bidItem) { 
		bidItems.add(bidItem);

		if(currentBidItem == null) {
			currentBidItem = bidItem;
			countDownBidPeriod();
		}
	}


	public static List<BidItem> getBidItems() {
		return bidItems;
	}


    //scan through the bid items list and return the next item that is not sold
	//method will iterate through the list of bid items starting from the position of 
	// the current bid item
	static BidItem getNextBidItem(BidItem currItem) { 
		
		List<BidItem> otherItems = bidItems.stream()
												.filter( item -> item.isSold() == false && !item.equals(currItem) )
												.collect( Collectors.toList() );
		
		//no more items to sell
		if(otherItems.size() == 0 && currItem.isSold() ) { 
			return null;
		} else if( otherItems.size() == 0 && !currItem.isSold() ) { //if there is only one item that's not sold
			return currItem;
		} else { //get another item to sell
			return otherItems.get( (int)Math.random() * otherItems.size() );
		}

	}


    public static BidItem getCurrentBidItem() {
		return currentBidItem;
	}



    static void countDownBidPeriod() { 
		timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			int seconds = currentBidItem.getBidPeriod();

			public void run() {
				// System.out.println(currentBidItem.getName() + " " + seconds--);
				seconds--;

				//bid time expires
				if ( seconds < 0 ) {
					
					//if the price is different than initial price means then mark product as sold 
					//and get the next product
					if( currentBidItem.getPrice() != currentBidItem.getListingPrice() ) { 
						
						currentBidItem.setSold(true);

						//send a message to clients that the item is sold
						AuctionServerProtocol.sendSellMsg(currentBidItem);							
						
					}
					
					BidItem prevItem = currentBidItem;

					//get the next item to sell, if there are any
					if( getNextBidItem(currentBidItem) != null ) { 
						currentBidItem = getNextBidItem(currentBidItem);
						seconds = currentBidItem.getBidPeriod();

						//only reset state for clients if the current bid item has changed
						if( !currentBidItem.equals(prevItem) ) {
							try {
								AuctionServer.resetStateForAllClients();
							} catch (IOException e2) {
								e2.printStackTrace();
							}
						}
						

					} else { //all items have been sold

						currentBidItem = null;
						timer.cancel();

						try {
							AuctionServer.endAuction();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
					

				//notify clients every 15 seconds about the bid time remaining
				} else if( (seconds % 15) == 0 && seconds != 0) { 
					if( currentBidItem.getPrice() != currentBidItem.getListingPrice() ) { 
						try {
							AuctionServer.sendToAllParticipants(seconds + " seconds remaining for bidding on item " + currentBidItem.getName() + "\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
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


	static String currentItemToStr() { 
		if(currentBidItem == null) { 
			return null;
		} else { 
			return "\nCurrent item for sale is " + currentBidItem.getName() + " - price is " + currentBidItem.getPrice() + " euro";
		}
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

		return result;
	}


}
