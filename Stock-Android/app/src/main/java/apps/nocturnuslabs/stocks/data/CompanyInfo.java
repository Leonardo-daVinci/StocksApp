package apps.nocturnuslabs.stocks.data;

public class CompanyInfo {

    private String ticker;
    private String name;
    private String webUrl;
    private String logo;
    private String industry;
    private String startDate;

    public CompanyInfo(String ticker, String name, String webUrl, String logo, String industry, String startDate) {
        this.ticker = ticker;
        this.name = name;
        this.webUrl = webUrl;
        this.logo = logo;
        this.industry = industry;
        this.startDate = startDate;
    }

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getLogo() {
        return logo;
    }

    public String getIndustry() {
        return industry;
    }

    public String getStartDate() {
        return startDate;
    }
}

