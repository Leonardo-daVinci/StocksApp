package apps.nocturnuslabs.stocks.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import apps.nocturnuslabs.stocks.R;
import apps.nocturnuslabs.stocks.data.CompanyInfo;
import apps.nocturnuslabs.stocks.data.LatestPrice;
import apps.nocturnuslabs.stocks.data.News;
import apps.nocturnuslabs.stocks.data.SavedStock;
import apps.nocturnuslabs.stocks.data.Trade;
import apps.nocturnuslabs.stocks.ui.fragments.ViewPagerAdapter;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.hichartsclasses.*;
import com.highsoft.highcharts.core.HIChartView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class DetailsActivity extends AppCompatActivity
    implements TradingDialog.OnInputListener {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private String serverURL = "YOUR_SERVER_URL HERE";

    //Variables
    private String ticker;
    private RequestQueue queue;
    private CompanyInfo companyInfo;
    private LatestPrice companyLatest;
    private AtomicInteger counter;
    private ArrayList<News> newsArrayList;
    private int rMentions, tMentions, rPos, tPos, rNeg, tNeg;
    private Menu menu;
    private ArrayList<SavedStock> portStocks = new ArrayList<>();
    private ArrayList<SavedStock> favStocks = new ArrayList<>();
    private boolean inFavs;
    private int shares;
    private double cost;
    private ArrayList<Integer> trendBuy, trendHold, trendSell, trendStrongBuy, trendStrongSell;
    private ArrayList<String> trendPeriod;
    private ArrayList<String> peerList;

    //Results fom Trading dialog
    int tradeResult;
    float tradeCashBalance;

    //UI elements
    private NestedScrollView nestedStockInfo;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private MenuItem star;

    //Top section UI
    private TextView detailsTicker, detailsName, detailsPrice, detailsChangePrice;
    private ImageView detailsLogo, detailsChangeImg;

    //Charts Section
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter viewPagerAdapter;
    private int [] icons = {R.drawable.chart_line, R.drawable.clock_time_three};

    //Portfolio section UI
    private TextView portShares, portAvg, portChange, portMarket, portTotal;
    private Button tradeBtn;

    //Stats section UI
    private TextView detailOP, detailLP, detailHP, detailPC;

    //About section UI
    private TextView detailDate, detailIndustry, detailWeb;
    private LinearLayout detailPeer, detailPeerLayout;

    //Sentiment section UI
    private TextView detailsRTotal, detailsTTotal, detailsRPos, detailsRNeg, detailsTPos, detailsTNeg, sentiCompany;

    //Remaining Charts UI
    private HIChartView recommendChart, surpriseChart;

    //News section UI
    private RecyclerView recyclerView;
    private NewsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        toolbar = findViewById(R.id.details_toolbar);
        nestedStockInfo = findViewById(R.id.details_stockinfo);
        progressBar = findViewById(R.id.details_progress);

        //Top section
        detailsTicker = findViewById(R.id.details_ticker);
        detailsName = findViewById(R.id.details_name);
        detailsPrice = findViewById(R.id.details_price);
        detailsChangePrice = findViewById(R.id.details_changeprice);
        detailsLogo = findViewById(R.id.details_logo);
        detailsChangeImg = findViewById(R.id.details_changeimg);

        //charts1 section
        tabLayout = findViewById(R.id.details_chartsOne_tabLayout);
        viewPager2 = findViewById(R.id.details_chartsOne_viewpager);

        //Portfolio section
        portShares = findViewById(R.id.details_p_shares);
        portAvg = findViewById(R.id.details_p_avg);
        portTotal = findViewById(R.id.details_p_total);
        portChange = findViewById(R.id.details_p_change);
        portMarket = findViewById(R.id.details_p_market);
        tradeBtn = findViewById(R.id.details_p_trade_btn);

        //stats section
        detailOP = findViewById(R.id.details_s_op);
        detailHP = findViewById(R.id.details_s_hp);
        detailLP = findViewById(R.id.details_s_lp);
        detailPC = findViewById(R.id.details_s_pc);

        //About section
        detailDate = findViewById(R.id.details_a_date);
        detailIndustry = findViewById(R.id.details_a_industry);
        detailWeb = findViewById(R.id.details_a_webpage);
        detailPeer = findViewById(R.id.details_a_peers);
        detailPeerLayout = findViewById(R.id.details_a_peerlayout);

        //Sentiments Section
        detailsRTotal = findViewById(R.id.details_i_rtotal);
        detailsTTotal = findViewById(R.id.details_i_ttotal);
        detailsRNeg = findViewById(R.id.details_i_rneg);
        detailsRPos = findViewById(R.id.details_i_rpos);
        detailsTNeg = findViewById(R.id.details_i_tneg);
        detailsTPos = findViewById(R.id.details_i_tpos);
        sentiCompany = findViewById(R.id.details_i_companyname);

        //Remaining Charts Section
        recommendChart = findViewById(R.id.recommend_chart);
        surpriseChart = findViewById(R.id.surprise_chart);

        //News section
        recyclerView = findViewById(R.id.details_news_recyclerview);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic__back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nestedStockInfo.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        display(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_toolbar, menu);
        this.menu = menu;
        star = this.menu.findItem(R.id.details_star);
        inFavs = false;

        //read data from the local list
        getFromLocalStorage();
        //find if the stock is in favourites
        for (int i = 0; i<favStocks.size(); i++){
            if(favStocks.get(i).getTicker().equals(ticker)){
                star.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_star_full));
                inFavs = true;
                break;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.details_star) {
            inFavs = !inFavs;
            if (inFavs) {
                star.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_star_full));

                    //Add proper shares to the favstock.
                    int favShares = Integer.parseInt(portShares.getText().toString());

                    SavedStock newStock = new SavedStock(ticker, favShares, companyLatest.getCurrentPrice());
                    favStocks.add(newStock);
                Toast.makeText(this, ticker + " is added to favourites", Toast.LENGTH_LONG).show();
            } else {
                star.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_star_empty));
                for (int i = 0; i < favStocks.size(); i++) {
                    if (favStocks.get(i).getTicker().equals(ticker)) {
                        favStocks.remove(i);
                        break;
                    }
                }
                Toast.makeText(this, ticker + " was removed from favourites", Toast.LENGTH_LONG).show();
            }
            storeToLocalStorage();
        } else {
            onBackPressed();
        }
        return true;
    }

    private void storeToLocalStorage(){
        SharedPreferences sharedPref = getSharedPreferences("LocalStorage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String portJSON = new Gson().toJson(portStocks);
        editor.putString("portStocks", portJSON);
        String favJSON = new Gson().toJson(favStocks);
        editor.putString("favStocks", favJSON);
        editor.putFloat("CashBalance", tradeCashBalance);
        editor.apply();
    }

    private void getFromLocalStorage(){
        SharedPreferences sharedPref = getSharedPreferences("LocalStorage", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
        String portJSON = sharedPref.getString("portStocks", "[]");
        portStocks = new Gson().fromJson(portJSON, new TypeToken<ArrayList<SavedStock>>(){}.getType());
        String favJSON = sharedPref.getString("favStocks", "[]");
        favStocks = new Gson().fromJson(favJSON, new TypeToken<ArrayList<SavedStock>>(){}.getType());
        tradeCashBalance = sharedPref.getFloat("CashBalance", 25000.00f);
    }

    private void display(Intent intent) {

        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            ticker = intent.getStringExtra(SearchManager.QUERY);
            Log.i("TestDetails", ticker);
        }else{
            ticker = intent.getStringExtra("StockTicker").toUpperCase();
        }

        //SAVING THE TICKER IN SHARED PREFERENCES
        SharedPreferences sharedPref = getSharedPreferences("LocalStorage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("StockTicker", ticker);
        editor.apply();


        getSupportActionBar().setTitle(ticker);
        counter = new AtomicInteger(3);
        queue = Volley.newRequestQueue(this);

        //Setting up the charts section here
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setIcon(icons[position]);
                //change the color later
//                tab.getIcon().setTint(getResources().getColor(R.color.tab_color));
            }
        }).attach();

        //API Calls for each section
        getCompanyDetails();
        getSentiments();
        getCompanyPeers();
        getNews();
        getRecommendationChart();
    }

    private void showPortfolio(){
        shares = 0;
        cost = 0.00;
        for(int i=0; i<portStocks.size(); i++){
            if(portStocks.get(i).getTicker().equals(ticker)){
                shares = portStocks.get(i).getShares();
                cost = portStocks.get(i).getCost();
                break;
            }
        }
        if(shares!=0){
            portShares.setText(""+shares);
            portAvg.setText("$"+df.format((cost/shares)));
            portTotal.setText("$"+df.format(cost));
            double difference = (companyLatest.getCurrentPrice()*shares)-cost;
            portChange.setText("$"+df.format(difference));
            portMarket.setText("$"+df.format((companyLatest.getCurrentPrice()*shares)));
            if(difference>0){
                portChange.setTextColor(getResources().getColor(R.color.green));
                portMarket.setTextColor(getResources().getColor(R.color.green));
            }else if(difference<0){
                portChange.setTextColor(getResources().getColor(R.color.red));
                portMarket.setTextColor(getResources().getColor(R.color.red));
            }
        }
        else{
            portShares.setText("0");
            portAvg.setText("$0.00");
            portTotal.setText("$0.00");
            portChange.setText("$0.00");
            portMarket.setText("$0.00");
        }

        tradeBtn.setOnClickListener(view -> {
            //show trading dialog
            Trade item = new Trade(ticker, companyInfo.getName(), tradeCashBalance, companyLatest.getCurrentPrice(), shares);
            TradingDialog dialog = TradingDialog.newInstance(item);
            dialog.show(((AppCompatActivity) view.getContext()).getSupportFragmentManager(),"TradingDialog");
        });

    }

    private void getCompanyDetails() {
        String query = server+ticker.toUpperCase();

        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, query, null,
                        response -> {
                    try{
                        String name = response.getString("name");
                        String webUrl = response.getString("weburl");
                        String logo = response.getString("logo");
                        String industry = response.getString("finnhubIndustry");
                        String startDate = response.getString("ipo");
                        companyInfo = new CompanyInfo(ticker, name, webUrl, logo, industry, startDate);
                        Log.i("Testing123", response.toString());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                            getLatestPrice();
                        },
                        error -> error.printStackTrace());

        queue.add(request);
    }

    private void getLatestPrice(){
        String url = serverURL + ticker;
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null,
                        response -> {
                    try{
                        double currentPrice = response.getDouble("currentPrice");
                        double changePrice = response.getDouble("changePrice");
                        double changePercent = response.getDouble("changePercent");
                        double highPrice = response.getDouble("highPrice");
                        double lowPrice = response.getDouble("lowPrice");
                        double openPrice = response.getDouble("openPrice");
                        double previousClose = response.getDouble("previousClose");
                        companyLatest = new LatestPrice(ticker, currentPrice, changePrice, changePercent, 0, highPrice, lowPrice, openPrice, previousClose);
                        Log.i("Testing123", response.toString());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    nestedStockInfo.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    showTopSection();
                    showStatsSection();
                    showPortfolio();
                        }, error -> error.printStackTrace());

        queue.add(request);
    }

    private void showStatsSection(){
        detailOP.setText("$"+df.format(companyLatest.getOpenPrice()));
        detailHP.setText("$"+df.format(companyLatest.getHighPrice()));
        detailLP.setText("$"+df.format(companyLatest.getLowPrice()));
        detailPC.setText("$"+df.format(companyLatest.getPreviousClose()));

        showAboutSection();
    }


    private void getCompanyPeers(){
        String url = serverURL + "/data/peers/"+ticker.toUpperCase();
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET, url, null,
                        response -> {
                    try{
                        peerList = new ArrayList<>();
                        for(int i=0; i<response.length();i++){
                            JSONObject obj = response.getJSONObject(i);
                            peerList.add(obj.getString("peer"));
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                        }, error -> error.printStackTrace());
        queue.add(request);
    }

    private void showAboutSection(){
        detailDate.setText(companyInfo.getStartDate());
        detailIndustry.setText(companyInfo.getIndustry());

        String webUrl = companyInfo.getWebUrl();
        SpannableString ss = new SpannableString(webUrl);
        ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
        detailWeb.setText(ss);

        detailWeb.setOnClickListener(view -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)));
        });

        detailPeerLayout.removeAllViews();
        //Create a new textview for each peer
        for(int i=0; i<peerList.size(); i++){
            TextView textView = new TextView(this);

            String peer = peerList.get(i).toUpperCase();
            SpannableString sp = new SpannableString(peer);
            sp.setSpan(new UnderlineSpan(), 0, sp.length(), 0);
            textView.setText(sp);
            textView.setTextColor(getResources().getColor(R.color.link_blue));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
            params.setMargins(10, 0, 10, 10);
            textView.setLayoutParams(params);
            detailPeerLayout.addView(textView);

            textView.setOnClickListener(view -> {
                Intent detailsIntent = new Intent(this, DetailsActivity.class);
                detailsIntent.putExtra("StockTicker", peer);
                startActivity(detailsIntent);
            });
        }



    }

    //-------------------------------------------------------------------- SENTIMENT SECTION ---------------------------------------------------------//

    private void getSentiments(){
        String url = serverURL + "/data/social/" + ticker.toUpperCase();
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, url, null,
                        response -> {
                            try{
                                rMentions = response.getInt("totalReddit");
                                tMentions = response.getInt("totalTwitter");
                                rPos = response.getInt("posReddit");
                                tPos = response.getInt("posTwitter");
                                rNeg = response.getInt("negReddit");
                                tNeg = response.getInt("negTwitter");
                                Log.i("Testing123", response.toString());
                                showSentiment();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            showSentiment();
                        }, error -> error.printStackTrace());

        queue.add(request);
    }

    private void showSentiment(){
        sentiCompany.setText(companyInfo.getName());
        detailsRTotal.setText(""+rMentions);
        detailsTTotal.setText(""+tMentions);
        detailsRPos.setText(""+rPos);
        detailsRNeg.setText(""+rNeg);
        detailsTPos.setText(""+tPos);
        detailsTNeg.setText(""+tNeg);
    }


    //-------------------------------------------------------------------------- CHARTS SECTION ---------------------------------------------------------//

    private void getRecommendationChart(){
        Log.i("RecommendChart", "Chart Called");
        String url = serverURL + "/data/trends/"+ticker.toUpperCase();
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET, url, null,
                        response -> {
                            try{
                                trendBuy = new ArrayList<>();
                                trendSell = new ArrayList<>();
                                trendStrongBuy = new ArrayList<>();
                                trendStrongSell = new ArrayList<>();
                                trendHold = new ArrayList<>();
                                trendPeriod = new ArrayList<>();
                                Log.i("RecommendChart", response.toString());
                                for(int i=0; i<response.length(); i++){
                                    JSONObject obj = response.getJSONObject(i);
                                    trendBuy.add(obj.getInt("buy"));
                                    trendSell.add(obj.getInt("sell"));
                                    trendStrongSell.add(obj.getInt("strongSell"));
                                    trendStrongBuy.add(obj.getInt("strongBuy"));
                                    trendHold.add(obj.getInt("hold"));
                                    trendPeriod.add(obj.getString("period"));
                                }
                                showRecommendationChart();

                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }, error -> error.printStackTrace());
        queue.add(request);
    }

    private void showRecommendationChart(){
        HIOptions options = new HIOptions();

        HITitle title = new HITitle();
        title.setText("Recommendation Trends");
        options.setTitle(title);

        HIXAxis xaxis = new HIXAxis();
        xaxis.setCategories(trendPeriod);
        options.setXAxis(new ArrayList<>(Collections.singletonList(xaxis)));

        HIYAxis yaxis = new HIYAxis();
        yaxis.setMin(0);
        yaxis.setTitle(new HITitle());
        yaxis.getTitle().setText("#Analysis");
        yaxis.setStackLabels(new HIStackLabels());
        yaxis.getStackLabels().setEnabled(true);
        yaxis.getStackLabels().setStyle(new HICSSObject());
        yaxis.getStackLabels().getStyle().setFontWeight("bold");
        yaxis.getStackLabels().getStyle().setColor(HIColor.initWithName("grey"));
        options.setYAxis(new ArrayList<>(Collections.singletonList(yaxis)));

        HILegend legend = new HILegend();
        legend.setBorderColor(HIColor.initWithHexValue("ccc"));
        legend.setBorderWidth(1);
        options.setLegend(legend);

        HITooltip tooltip = new HITooltip();
        tooltip.setPointFormat("{series.name}: {point.y}<br/>Total: {point.stackTotal}");
        tooltip.setHeaderFormat("<b>{point.x}</b><br/>");
        options.setTooltip(tooltip);

        HIPlotOptions plotoptions = new HIPlotOptions();
        plotoptions.setColumn(new HIColumn());
        plotoptions.getColumn().setStacking("normal");
        options.setPlotOptions(plotoptions);

        HIColumn column1 = new HIColumn();
        column1.setName("Strong Buy");
        column1.setData(trendStrongBuy);

        HIColumn column2 = new HIColumn();
        column2.setName("Buy");
        column2.setData(trendBuy);

        HIColumn column3 = new HIColumn();
        column3.setName("Hold");
        column3.setData(trendHold);

        HIColumn column4 = new HIColumn();
        column4.setName("Sell");
        column4.setData(trendSell);

        HIColumn column5 = new HIColumn();
        column5.setName("Strong Sell");
        column5.setData(trendStrongSell);

        options.setSeries(new ArrayList<>(Arrays.asList(column1, column2, column3, column4, column5)));
        recommendChart.setOptions(options);
    }

    //-------------------------------------------------------------------------- NEWS SECTION ---------------------------------------------------------//

    private void getNews(){
        String url = serverURL + "/data/news/" + ticker.toUpperCase();
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET, url, null,
                        response -> {
                            try{
                                Log.i("Testing123", response.toString());
                                newsArrayList = new ArrayList<>();
                                for(int i = 0; i < response.length(); i++){
                                    JSONObject newsObj = response.getJSONObject(i);
                                    String source = newsObj.getString("source");
                                    String head = newsObj.getString("headline");
                                    String date = newsObj.getString("datetime");
                                    String web = newsObj.getString("url");
                                    String img = newsObj.getString("image");
                                    String summary = newsObj.getString("summary");
                                    News newsItem = new News(source, head, date, web , img, summary);
                                    newsArrayList.add(newsItem);
                                }
                                showNews();
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }, error ->
                            error.printStackTrace());
        queue.add(request);
    }

    private void showNews(){
        adapter = new NewsAdapter(newsArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void showTopSection() {
        detailsTicker.setText(ticker.toUpperCase());
        detailsName.setText(companyInfo.getName());
        detailsPrice.setText("$"+df.format(companyLatest.getCurrentPrice()));

        String changes = "$"+ df.format(companyLatest.getChangePrice()) + " (" + df.format(companyLatest.getChangePercent()) +"%)";
        detailsChangePrice.setText(changes);
        if(companyLatest.getChangePrice() > 0){
            detailsChangePrice.setTextColor(getResources().getColor(R.color.green));
            detailsChangeImg.setImageResource(R.drawable.ic_trending_up);
        }else if(companyLatest.getChangePrice() <0){
            detailsChangePrice.setTextColor(getResources().getColor(R.color.red));
            detailsChangeImg.setImageResource(R.drawable.ic_trending_down);
        }

        String url = Uri.parse(companyInfo.getLogo()).buildUpon().build().toString();
        Picasso.get().load(url).into(detailsLogo);

        showStatsSection();
    }


    //-------------------------------------------------------------------------- Trading SECTION ---------------------------------------------------------//

    @Override
    public void sendInput(int input, boolean buy) {
        tradeResult = input;
        Log.i("TradeTest", "No. of shares bought is :"+tradeResult);

        //update portfolio here - change the text + write to local storage
        updateLocalStorage(input, buy);


    }

    private void updateLocalStorage(int input, boolean buy) {
        ArrayList<String> names = new ArrayList<>();

        //If item is present in the lists
        for(int i=0; i<portStocks.size(); i++){
            names.add(portStocks.get(i).getTicker().toUpperCase());
            if(portStocks.get(i).getTicker().equals(ticker)){
                int prior = portStocks.get(i).getShares();
                int post;
                if(buy){
                    post = prior + input;
                }else{
                    post = prior - input;
                }
                portStocks.get(i).setShares(post);
                portStocks.get(i).setCost(post*companyLatest.getCurrentPrice());
            }
            if(portStocks.get(i).getShares()==0){
                portStocks.remove(i);
                break;
            }
        }
        for(int i=0; i<favStocks.size(); i++){
            if(favStocks.get(i).getTicker().equals(ticker)){
                int prior = favStocks.get(i).getShares();
                int post;
                if(buy){
                    post = prior + input;
                }else{
                    post = prior - input;
                }
                favStocks.get(i).setShares(post);
                favStocks.get(i).setCost(post*companyLatest.getCurrentPrice());
            }
        }

        //if item is not present in the list
        if(!names.contains(ticker.toUpperCase())){
            portStocks.add(new SavedStock(ticker, input, input*companyLatest.getCurrentPrice()));
        }

        if(buy){
            tradeCashBalance = (float) (tradeCashBalance - (input*companyLatest.getCurrentPrice()));
        }else{
            tradeCashBalance = (float)(tradeCashBalance + (input*companyLatest.getCurrentPrice()));
        }

        storeToLocalStorage();
        showPortfolio();

    }

    //-------------------------------------------------------------------------- SEND DATA ---------------------------------------------------------//

}