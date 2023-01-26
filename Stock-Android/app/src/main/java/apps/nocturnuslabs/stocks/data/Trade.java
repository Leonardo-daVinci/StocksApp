package apps.nocturnuslabs.stocks.data;

import java.io.Serializable;

public class Trade  implements Serializable {
    private String ticker;
    private String name;
    private double balance;
    private double price;
    private int shares;

    public Trade(String ticker, String name, double balance, double price, int shares) {
        this.ticker = ticker;
        this.name = name;
        this.balance = balance;
        this.price = price;
        this.shares = shares;
    }

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public double getPrice() {
        return price;
    }

    public int getShares() {
        return shares;
    }
}
