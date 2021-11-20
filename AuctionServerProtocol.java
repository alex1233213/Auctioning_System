public class AuctionServerProtocol { 
    private static BidItem bidItem;
    private static final int INITIAL = 0;
    private static final int AUCTION_MENU = 1;
    private static final int RECEIVE_BID = 2;
    private static final int RECEIVE_CHOICE_MENU = 3;
    private int state;
    private String clientName;
    private static String mainMenu;
    private static String auctionMenuMsg;
    


    public AuctionServerProtocol(String clientName) { 
        this.state = INITIAL;
        this.clientName = clientName;
        mainMenu = "------------------------------------------------------\n" +
                    "                  Auction System\n" + 
                    "------------------------------------------------------\n" + 
                    " * Enter 1 to join the auction\n" +
                    " * Enter 5 to quit\n\n\n\n>>";


        auctionMenuMsg = "\n * Enter 1 to place a bid on the item\n" + 
                                " * Enter 2 to leave the auction\n" + 
                                " * Enter 3 to list all auction items\n\n\n\n>>";
    }



    public static String getAuctionMenuMsg() {
        return auctionMenuMsg;
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
                
                output = AuctionSystem.getAuctionItems() + AuctionSystem.currentItemToStr();
                output += auctionMenuMsg;
                
                state = AUCTION_MENU;
            } else if( input.equals("5") ) {

                output = "QUIT";

            } else {  

                output = "Invalid entry\n" + mainMenu;
            }
        
        //check on the choice entered by the user
        } else if ( state == AUCTION_MENU) {

            if ( input.equals("1") ) {

                output = "Enter new bid for the item\n\n\n\n>>";
                state = RECEIVE_BID;

            } else if( input.equals("2") ) {

                output = "\nLeft Auction\n " + mainMenu;
                state = RECEIVE_CHOICE_MENU;
                
            } else if(input.equals("3")) { 

                output = AuctionSystem.getAuctionItems() + AuctionSystem.currentItemToStr() + getAuctionMenuMsg();

            } else {    

                output = "Invalid choice\n" + getDefaultMessage();

            }

            //************ */
        } else if(state == RECEIVE_BID) { 
            
            //value expected is the bid value
            try { 
                float bidEntered = Float.parseFloat(input);
                BidItem updatedBid = AuctionSystem.updateBidPrice(bidEntered, clientName);

                //when a bid could not be placed output will be null
                if(updatedBid == null) { 
                    output = String.format("Invalid entry. The value of the bid must be greater than current bid - %.2f euro." 
                                            , bidItem.getPrice());

                    output += auctionMenuMsg;
                    state = AUCTION_MENU;
                    
                } else { 
                    sendBidMsg(bidItem);
                    output = "\n\n" + getDefaultMessage();

                    state = AUCTION_MENU;
                }
                
                
            }   catch (Exception e) { 
                    output = "Invalid input.";
                    output += auctionMenuMsg;
                    
                    state = AUCTION_MENU;
            }           
        
        }
        
        return output;
    }




    public static String getDefaultMessage() { 
        return String.format("Current item for sale is %s - price is %.2f euros\n" + auctionMenuMsg
                                , AuctionSystem.getCurrentBidItem().getName()
                                , AuctionSystem.getCurrentBidItem().getPrice() );
    }



    static void sendSellMsg(BidItem soldItem) {
        
        //create messages for informing users of the sold item 
        String msgForBuyer = "**Notification: You have won the bid for the item " + soldItem.getName() + "\nCompleting transaction..\n\n";
        
        String msgForOthers = "**Notification: Item " + soldItem.getName() + " has been sold to " + soldItem.getHighestBidder() + 
                                " for " + soldItem.getPrice() + " euro\n\n";

        AuctionServer.notifyBidItemEvent(msgForBuyer, msgForOthers, soldItem);
    }

    

    static void sendBidMsg(BidItem bidItem) {
        
        //create messages for informing users of the sold item 
        String msgForBuyer = "**Notification: Your bid has been submitted for " + bidItem.getName() + "\n\n";
        
        String msgForOthers = String.format("**Notification: Bid for %s updated by %s. New selling price is %.2f.\nBid expires in %d seconds.\n\n",
                                            bidItem.getName(), bidItem.getHighestBidder(), bidItem.getPrice(), bidItem.getBidPeriod());

        AuctionServer.notifyBidItemEvent(msgForBuyer, msgForOthers, bidItem);
    }



}