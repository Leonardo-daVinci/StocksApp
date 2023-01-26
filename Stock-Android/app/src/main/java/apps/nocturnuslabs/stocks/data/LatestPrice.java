package apps.nocturnuslabs.stocks.data;

public class LatestPrice {
    private String ticker;
    private double currentPrice;
    private double changePrice;
    private double changePercent;
    private int shares;
    private double highPrice;
    private double lowPrice;
    private double openPrice;
    private double previousClose;

    public LatestPrice(String ticker, double currentPrice, double changePrice, double changePercent, int shares, double highPrice, double lowPrice, double openPrice, double previousClose) {
        this.ticker = ticker;
        this.currentPrice = currentPrice;
        this.changePrice = changePrice;
        this.changePercent = changePercent;
        this.shares = shares;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.openPrice = openPrice;
        this.previousClose = previousClose;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public String getTicker() {
        return ticker;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getChangePrice() {
        return changePrice;
    }

    public double getChangePercent() {
        return changePercent;
    }

    public int getShares() {
        return shares;
    }
}
