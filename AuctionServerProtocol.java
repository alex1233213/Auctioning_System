public class AuctionServerProtocol { 
    private static BidItem bidItem;
    private static final int INITIAL = 0;
    private static final int AUCTION_MENU = 1;
    private static final int RECEIVE_BID = 2;
    private static final int RECEIVE_CHOICE_MENU = 3;
    private int state;
    private String clientName;
    private static String mainMenu;
    


    public AuctionServerProtocol(String clientName) { 
        this.state = INITIAL;
        this.clientName = clientName;
        mainMenu = "------------------------------------------------------\n" +
                    "                  Auction System\n" + 
                    "------------------------------------------------------\n" + 
                    " * Enter 1 to join the auction\n" +
                    " * Enter 5 to quit\n";
    }


    public boolean isReceivingOptionFromMenu() { 
        return this.state == RECEIVE_CHOICE_MENU;
    }

    
    public void changeStateToReceive() { 
        this.state = AUCTION_MENU;
    }

    
    //Method to determine the state of communication between client and server
    //The different states determine what message the client should receive.
    public String processInput(String input) { 
        String output = null;
        bidItem = AuctionSystem.getCurrentBidItem();

        if( state == INITIAL ) { 

            output = mainMenu;
            
            state = RECEIVE_CHOICE_MENU;
        
        //receiving option from main menu
        } else if( state == RECEIVE_CHOICE_MENU ) { 

            if( input.equals("1") ) { 
                
                output = AuctionSystem.getAuctionItems();
                output += "\n * Enter 1 to place a bid on the item\n" + 
                            " * Enter 2 to leave the auction\n";
                
                state = AUCTION_MENU;
            } else if( input.equals("5") ) {

                output = "QUIT";

            }
        
        //check on the choice entered by the user
        } else if ( state == AUCTION_MENU) {

            if ( input.equals("1") ) {

                output = "Enter new bid for the item";
                state = RECEIVE_BID;

            } else if( input.equals("2") ) {

                output = "\nLeft Auction\n " + mainMenu;
                state = RECEIVE_CHOICE_MENU;
                
            } else {

                output = "Invalid choice\n" + "* Enter 1 to place a bid on the item\n" + 
                            "* Enter 2 to leave auction\n";

            }

            //************ */
        } else if(state == RECEIVE_BID) { 
            
            //value expected is the bid value
            try { 
                float bidEntered = Float.parseFloat(input);
                output = AuctionSystem.updateBidPrice(bidEntered, clientName);

                //when a bid could not be placed output will be null
                if(output == null) { 
                    output = String.format("Invalid entry. The value of the bid must be greater than current bid - %.2f euro." 
                                            , bidItem.getPrice());

                    output += "\n * Enter 1 to place a bid on the item\n" + 
                                " * Enter 2 to leave the auction\n";
                    state = AUCTION_MENU;
                    
                } else { 
                    
                    //inform all users of the bid
                    AuctionServer.sendToAllParticipants(output);
                    
                    //message for the user
                    output = "\n\n" + "Bid has been successful\n" + getDefaultMessage();
                    state = AUCTION_MENU;
                }
                
                
            }   catch (Exception e) { 
                    output = "Invalid input.";
                    output += "\n * Enter 1 to place a bid on the item\n" + 
                                " * Enter 2 to leave the auction\n";
                    
                    state = AUCTION_MENU;
            }           
        
        }
        
        return output;
    }




    public String getDefaultMessage() { 
        return String.format("Current item for sale is %s - price is %.2f euros\n" + 
                " * Enter 1 to place a bid on the item\n" + 
                " * Enter 2 to leave the auction\n", AuctionSystem.getCurrentBidItem().getName()
                , AuctionSystem.getCurrentBidItem().getPrice() );
    }
}