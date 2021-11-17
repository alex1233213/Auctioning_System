public class AuctionServerProtocol { 
    private static BidItem bidItem;
    private static final int INITIAL = 0;
    private static final int RECEIVE_CHOICE = 1;
    private static final int RECEIVE_BID = 2;
    // private static final 
    private static int state = INITIAL;
    
    // private static final int ;
    // private static final int;

    public AuctionServerProtocol() { 
        bidItem = new BidItem("Bicycle", 50, 60);
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
                     "Current item for sale is Bicycle at The price %s euros\n" + 
                     " * Enter 1 to place a bid on the item\n" + 
                     " * Enter 5 to quit\n";
            
            state = RECEIVE_CHOICE;

        //check on the choice entered by the user
        } else if ( state == RECEIVE_CHOICE) {

            if (input.equalsIgnoreCase("1")) {

                output = "Enter new bid for the item";
                state = RECEIVE_BID;

            } else if(input.equalsIgnoreCase("QUIT")) {

                output = "Connection has been terminated";

            } else {

                output = "Invalid choice\n" + "* Enter 1 to place a bid on the item\n" + 
                            "* Enter 5 to quit\n";
                state = RECEIVE_CHOICE;

            }

            
        } 
        
        // else if(state == RECEIVE_BID) { 

        //     //value expected is the new auction
            
        //     // //check new bid value entered by the user
        //     // if( bidItem.getPrice() ) {
        //     //     ;
        //     // }
        //     // float valueSent = input.
        //     // if (input.equalsIgnoreCase("1")) {
        //     //     output = "Enter new bid for the item";
        //     //     state = RECEIVE_BID;
        //     // } else if(input.equalsIgnoreCase("QUIT")) {
        //     //     output = "Connection has been terminated";
        //     // }

        //     state = BID_RECEIVED;
        // }
        // } else if(state == RECEIVE_CHOICE) { 
        //     output = "";
        // } 
        
        return output;
    }
}