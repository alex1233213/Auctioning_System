public class BidItem {
    private String name;
    private float price;
    private int bidPeriod; //seconds;
    private boolean sold;
    private float listingPrice;
    private String highestBidder;


    public BidItem(String name, float price, int bidPeriod) {
        this.name = name;
        this.price = price;
        this.bidPeriod = bidPeriod;
        this.listingPrice = this.price;
        this.sold = false;
    }


    public String getHighestBidder() {
        return highestBidder;
    }


    public void setHighestBidder(String highestBidder) {
        this.highestBidder = highestBidder;
    }


    public boolean isSold() {
        return sold;
    }


    public float getListingPrice() {
        return listingPrice;
    }


    public void setSold(boolean sold) {
        this.sold = sold;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public float getPrice() {
        return price;
    }


    public void setPrice(float price) {
        this.price = price;
    }


    public int getBidPeriod() {
        return bidPeriod;
    }


    public void setBidPeriod(int bidPeriod) {
        this.bidPeriod = bidPeriod;
    }
}
