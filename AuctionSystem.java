import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AuctionSystem {
    private static List<BidItem> bidItems = new ArrayList<>();
	private static BidItem currentBidItem;



    public AuctionSystem() { 
        bidItems.add(new BidItem("Bicycle", 100f, 3));
		bidItems.add(new BidItem("Keyboard", 10f, 60));
		bidItems.add(new BidItem("Mouse", 7.5f, 60));
		bidItems.add(new BidItem("Monitor", 120f, 60));
		bidItems.add(new BidItem("HDMI cable", 5.5f, 60));

        //start auctioning first item in the list
		currentBidItem = bidItems.get(0);
		
		countDownBidPeriod();
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
		Timer timer = new Timer();

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
						// try {
							// sendToAll(String.format("%s has been sold for %.2f euro",
							// 						 currentBidItem.getName(),
							// 						 currentBidItem.getPrice()) );

                        // }
						// } catch (IOException e) {
						// 	e.printStackTrace();
						// }
						
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
}
