public class BidItem {
    private String name;
    


    private String price;
    private String bidPeriod;


    public BidItem(String name, String price, String bidPeriod) {
        this.name = name;
        this.price = price;
        this.bidPeriod = bidPeriod;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getPrice() {
        return price;
    }


    public void setPrice(String price) {
        this.price = price;
    }


    public String getBidPeriod() {
        return bidPeriod;
    }


    public void setBidPeriod(String bidPeriod) {
        this.bidPeriod = bidPeriod;
    }
}
