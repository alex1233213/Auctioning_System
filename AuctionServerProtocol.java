public class AuctionServerProtocol { 
    private static BidItem bidItem;
    private static final int INITIAL = 0;
    private static final int RECEIVE_CHOICE = 1;
    private static final int RECEIVE_BID = 2;
    private static final int DEFAULT = 3;
    private int state;

    private static String defaltMsg;
    


    public AuctionServerProtocol() { 
        this.state = INITIAL;

        defaltMsg = String.format("Current item for sale is %s - price is %.2f euros\n" + 
                                    " * Enter 1 to place a bid on the item\n" + 
                                    " * Enter 5 to quit\n", AuctionSystem.getCurrentBidItem().getName(), AuctionSystem.getCurrentBidItem().getPrice());
    }




    
    //Method to determine the state of communication between client and server
    //The different states determine what message the client should receive.
    public String processInput(String input) { 
        String output = null;
        bidItem = AuctionSystem.getCurrentBidItem();

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
                output = AuctionSystem.updateBidPrice(bidEntered);

                if(output == null) { 
                    output = String.format("The value of the bid must be greater than current bid - %.2f euro. Try again.\n" + 
                                            "Enter bid amount:\n", bidItem.getPrice());
                } else { 
                    output = output + "\n\n" + getDefaultMessage();
                    state = RECEIVE_CHOICE;
                }
                
                
            }   catch (Exception e) { 
                    output = "Invalid input, try again\nEnter new price greater than "
                                 + bidItem.getPrice();
            }           
        
        } else if(state == DEFAULT) { 
            defaltMsg = getDefaultMessage();
            output = defaltMsg;

            state = RECEIVE_CHOICE;
        }
        
        
        
        return output;
    }




    public String getDefaultMessage() { 
        return String.format("Current item for sale is %s - price is %.2f euros\n" + 
                " * Enter 1 to place a bid on the item\n" + 
                " * Enter 5 to quit\n", AuctionSystem.getCurrentBidItem().getName()
                , AuctionSystem.getCurrentBidItem().getPrice());
    }
}