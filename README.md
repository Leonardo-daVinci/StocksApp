# StocksApp
This project is an Android app that allows users to gather information about stocks and simulate buying and trading functionality. The app uses real-time stock market data to provide users with up-to-date information on stock prices, volume, and other relevant metrics.  

The app is built using the **Java** programming language and the **Android Studio** development environment. It also uses an API to retrieve stock market data. The backend for the app is built using **Node.js** with **Express.js** framework which is deployed on **Google Cloud Platform**.

![StocksApp banner](stocksApp.png?raw=true "StocksApp Banner")

## Description
### Server Side 
1. Utilises __Node.js__ and __Express.js__  
2. All API calls are done through the Node.js server.  
3. Utilises nine different API calls from [__Finnhub.io__](https://finnhub.io/)  
    - Company Profile
    - Company Historical Data
    - Company Real-time Quote
    - Company Stock Candles (OHLCV)
    - Company News
    - Company Peers
    - Company Recommendation Trends
    - Company Social Sentiment
    - Company Earnings
  
### Client Side
1. Utilises __Java__, __JSON__, __Android Lifecycle__ and __Android Studio__ for Android app development.  
2. Designed using __Google's Material Design__ rules for Android apps.  
3. Uses [__HighCharts API__](https://www.highcharts.com/blog/products/android/) to generate graphs for stock prices.

## Details

### Server
1. **Express.JS** framework for handling API requests.
2. **Cors** library for handling cross-origin resource sharing issues.
3. **Fetch API** for making asynchronous calls to Finnhub API.
4. Sends JSON response in required format to frontend.
5. Deployed on Google Cloud Platform.

### Android App
1.  __Splash screen__ with App icon

2.  __Home Page__  
Implemented using **RecyclerView** with **SectionedRecyclerViewAdapter**.  
Each stock listing implemented using **ContraintLayout** with **TextView** and **ImageView**.    
Portfolio and Favorites sections have swipe to delete functionality and drag & reorder functionality.  
Consists of following components:
    - Search Bar
    - Date
    - Portfolio section
      - Net Worth and Cash Balance
      - Stocks Bought with amount, stock price and change
    - Favorites section
      - Favourite stocks with their price and change
    - __Powered by Finnhub__ at bottom  

3. __Details Page__  
Consists of following components:
    - Top bar with star icon for favourites.
    - Stock details with Name, Symbol, Price and change.
    - Charts Section built using HighChats API.
    - Portfolio Section with Trade button for buying and selling stocks
    - Stats Section
    - About Section
    - Insights Section
      - Social sentiments
      - Recommendation Trends
    - News Section
        
## Note
The project is intended for educational and research purposes. The app does not provide any real buying and trading functionality, it is for simulation only.

We hope you enjoy the project and learn something new about Android app development and stock market data!

  
    
      
