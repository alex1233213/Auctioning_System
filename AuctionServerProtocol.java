public class AuctionServerProtocol { 
    private static BidItem bidItem;
    private static final int INITIAL = 0;
    private static final int RECEIVE_CHOICE = 1;
    private static final int RECEIVE_BID = 2;
    private static final int DEFAULT = 3;
    private int state;

    private static String defaltMsg;
    private AuctionServer auctionServer;
    


    public AuctionServerProtocol(AuctionServer auctionServer) { 
        this.state = INITIAL;

        this.auctionServer = auctionServer;

        bidItem = new BidItem("Bicycle", 50, 60);

        defaltMsg = String.format("Current item for sale is %s - price is %.2f euros\n" + 
                                    " * Enter 1 to place a bid on the item\n" + 
                                    " * Enter 5 to quit\n", bidItem.getName(), bidItem.getPrice());
    }



    public static BidItem getBidItem() {
        return bidItem;
    }


    public static void setBidItem(BidItem bidItem) {
        AuctionServerProtocol.bidItem = bidItem;
    }

    
    //Method to determine the state of communication between client and server
    //The different states determine what message the client should receive.
    public String processInput(String input) { 
        String output = null;

        if( state == INITIAL ) { 

            output = "------------------------------------------------------\n" +
                     "Successfully connected to auctioning system\n" + 
                     "------------------------------------------------------\n" + 
                      defaltMsg;
            
            state = RECEIVE_CHOICE;

        //check on the choice entered by the user
        } else if ( state == RECEIVE_CHOICE) {

            if (input.equals("1")) {

                output = "Enter new bid for the item";
                state = RECEIVE_BID;

            } else if(input.equals("5")) {

                output = "QUIT";

            } else {

                output = "Invalid choice\n" + "* Enter 1 to place a bid on the item\n" + 
                            "* Enter 5 to quit\n";

            }

            
        } else if(state == RECEIVE_BID) { 

            //value expected is the bid value

            try { 
                float bidEntered = Float.parseFloat(input);
                float currentPrice = bidItem.getPrice();

                if(bidEntered > currentPrice) { 
                    bidItem.setPrice(bidEntered);
                    output = String.format("Bid updated. New selling price is %.2f", bidItem.getPrice());
                    auctionServer.sendToAll(output);

                    state = DEFAULT;
                    //TODO send to all clients the new price of the item

                } else {
                    output = String.format("The value of the bid must be greater than current bid - %.2f euro. Try again.\n" + 
                                "Enter bid amount:\n", bidItem.getPrice());
                }

            } catch (Exception e) { 
                output = "Invalid input, try again";
            }
        
        } else if(state == DEFAULT) { 
            output = defaltMsg;

            state = RECEIVE_CHOICE;
        }
        
        
        
        return output;
    }
}