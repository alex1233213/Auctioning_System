public class BidItem {
    private String name;
    private float price;
    private int bidPeriod; //seconds;


    public BidItem(String name, float price, int bidPeriod) {
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
