package apps.nocturnuslabs.stocks.data;

public class SavedStock {
    private String ticker;
    private int shares;
    private double cost;

    public SavedStock(String ticker, int shares, double cost) {
        this.ticker = ticker;
        this.shares = shares;
        this.cost = cost;
    }

    public String getTicker() {
        return ticker;
    }

    public int getShares() {
        return shares;
    }

    public double getCost() {
        return cost;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
