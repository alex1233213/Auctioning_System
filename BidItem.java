public class BidItem {
    private String name;
    private float price;
    private int bidPeriod; //seconds;
    private boolean sold;


    public BidItem(String name, float price, int bidPeriod) {
        this.name = name;
        this.price = price;
        this.bidPeriod = bidPeriod;
        this.sold = false;
    }


    public boolean isSold() {
        return sold;
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
