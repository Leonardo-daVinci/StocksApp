package apps.nocturnuslabs.stocks.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import apps.nocturnuslabs.stocks.R;
import apps.nocturnuslabs.stocks.data.LatestPrice;
import apps.nocturnuslabs.stocks.data.SavedStock;

public class HomeActivity extends AppCompatActivity {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private String serverURL = "YOUR_SERVER_URL_HERE" ;

    TextView currentDate, finnhubText, homeNetWorth, homeBalance;
    Toolbar homeToolbar;
    SearchView searchView;
    SearchManager manager;
    SearchableInfo info;
    ComponentName component;
    ArrayList<SavedStock> portStocks = new ArrayList<>();
    ArrayList<SavedStock> favStocks = new ArrayList<>();
    ArrayList<LatestPrice> portList;
    ArrayList<LatestPrice> favList;
    float balance, worth;

    //Update in 15 sec
    Timer timer;
    TimerTask timerTask;

    private RecyclerView portRecycler, favRecycler;
    private SavedStockAdapter pAdapter, fAdapter;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Stocks);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        currentDate = findViewById(R.id.home_date);
        homeNetWorth = findViewById(R.id.home_networth);
        homeBalance = findViewById(R.id.home_cashbalance);
        finnhubText = findViewById(R.id.home_finnhub);
        homeToolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(homeToolbar);

        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFor = new SimpleDateFormat("dd MMMM yyyy");
        currentDate.setText(dateFor.format(date));

        portRecycler = findViewById(R.id.home_portfolio_recycler);
        favRecycler = findViewById(R.id.home_fav_recyclerview);


        finnhubText.setOnClickListener(view -> {
            Intent webIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.finnhub.io"));
            startActivity(webIntent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_toolbar, menu);
//        searchView = (SearchView) menu.findItem(R.id.home_search).getActionView();
//        searchView.setIconifiedByDefault(true);
//        searchView.setSubmitButtonEnabled(false);
//
//        manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        component = new ComponentName(this, DetailsActivity.class);
//        info = manager.getSearchableInfo(component);
//        searchView.setSearchableInfo(info);
//        return true;

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setIconifiedByDefault(true);
        searchView.setSearchableInfo(manager.getSearchableInfo(new ComponentName(this, DetailsActivity.class)));
        return true;
    }

    //lLocal Storage using Shared Preferences
    private void storeToLocalStorage(){
        SharedPreferences sharedPref = getSharedPreferences("LocalStorage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String portJSON = new Gson().toJson(portStocks);
        editor.putString("portStocks", portJSON);
        String favJSON = new Gson().toJson(favStocks);
        editor.putString("favStocks", favJSON);
        editor.apply();
    }

    private void getFromLocalStorage(){
        SharedPreferences sharedPref = getSharedPreferences("LocalStorage", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
        String portJSON = sharedPref.getString("portStocks", "[]");
        portStocks = new Gson().fromJson(portJSON, new TypeToken<ArrayList<SavedStock>>(){}.getType());
        String favJSON = sharedPref.getString("favStocks", "[]");
        favStocks = new Gson().fromJson(favJSON, new TypeToken<ArrayList<SavedStock>>(){}.getType());
        balance = sharedPref.getFloat("CashBalance", 25000.00f);
        worth = sharedPref.getFloat("NetWorth", 25000.00f);
    }

    @Override
    protected void onResume() {
        super.onResume();


        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        getFromLocalStorage();
//
//                        homeBalance.setText("$"+df.format(balance));
//                        homeNetWorth.setText("$"+df.format(worth));
//
//                        favList = new ArrayList<>();
//                        portList = new ArrayList<>();
//                        showRecyclerViews();
//                        queue = Volley.newRequestQueue(getApplicationContext());
//                        getPortfolio();
//                        getFavourites();
                    }
                });
            }
        };

//        timer.scheduleAtFixedRate(timerTask, 0, 1000);


        //You have portStocks and FavStocks
        getFromLocalStorage();

        homeBalance.setText("$"+df.format(balance));
        homeNetWorth.setText("$"+df.format(worth));

        favList = new ArrayList<>();
        portList = new ArrayList<>();
        showRecyclerViews();
        queue = Volley.newRequestQueue(this);
        getPortfolio();
        getFavourites();
    }

    private void showRecyclerViews() {
        //testing purposes
        pAdapter = new SavedStockAdapter(portList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        portRecycler.setLayoutManager(linearLayoutManager);
        portRecycler.setAdapter(pAdapter);
        enableSwipeToDeleteforPort();

        fAdapter = new SavedStockAdapter(favList);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        favRecycler.setLayoutManager(linearLayoutManager2);

        //this doesn't work as intended
//        ItemTouchHelper.Callback callback = new ItemMoveCallback(fAdapter);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(favRecycler);
        favRecycler.setAdapter(fAdapter);
        enableSwipeToDeleteforFav();
    }

    private void getPortfolio() {
        if(portStocks.size()==0){
            portList = new ArrayList<>();
        }
        for(int i = 0; i<portStocks.size(); i++){
            String ticker = portStocks.get(i).getTicker().toUpperCase();
            int share = portStocks.get(i).getShares();
            String url = serverURL + "/data/price/"+ticker;
            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.GET, url, null,
                            response -> {
                                try{
                                    String comp = response.getString("ticker");
                                    double currentPrice = response.getDouble("currentPrice");
                                    double changePrice = response.getDouble("changePrice");
                                    double changePercent = response.getDouble("changePercent");
                                    portList.add(new LatestPrice(comp, currentPrice, changePrice, changePercent, share, 0.00, 0.00, 0.00,0.00));
                                    Log.i("MainTest", response.toString());
                                }catch(Exception e){e.printStackTrace();}
                                showRecyclerViews();
                            }, error -> error.printStackTrace());
            queue.add(request);
        }
    }

    private void getFavourites() {
        if(favStocks.size()==0){
            favList = new ArrayList<>();
        }

        for(int i = 0; i<favStocks.size(); i++){
            String ticker = favStocks.get(i).getTicker().toUpperCase();
            int share = favStocks.get(i).getShares();
            String url = serverURL + "/data/price/"+ticker;
            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.GET, url, null,
                            response -> {
                                try{
                                    Log.i("MainTest", response.toString());
                                    String comp = response.getString("ticker");
                                    double currentPrice = response.getDouble("currentPrice");
                                    double changePrice = response.getDouble("changePrice");
                                    double changePercent = response.getDouble("changePercent");
                                    favList.add(new LatestPrice(comp, currentPrice, changePrice, changePercent, share, 0.00, 0.00, 0.00,0.00));
                                }catch(Exception e){e.printStackTrace();}
                                showRecyclerViews();
                            }, error -> error.printStackTrace());
            queue.add(request);
        }
    }

    private void enableSwipeToDeleteforFav(){
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final String ticker = fAdapter.getData().get(position).getTicker();

                Log.i("Swipe", ticker+ " from position");

                for(int i =0 ; i<favStocks.size(); i++){
                    if(favStocks.get(i).getTicker().equals(ticker)){
                        Log.i("Swipe", favStocks.get(i).getTicker());
                        favStocks.remove(i);
                        break;
                    }
                }
                storeToLocalStorage();
                Toast.makeText(getApplicationContext(), ticker+" was removed from the favourites", Toast.LENGTH_SHORT).show();
                fAdapter.removeItem(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(favRecycler);
    }

    private void enableSwipeToDeleteforPort(){
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final String ticker = pAdapter.getData().get(position).getTicker();

                Log.i("Swipe", ticker+ " from position");

//                for(int i =0 ; i<portStocks.size(); i++){
//                    if(portStocks.get(i).getTicker().equals(ticker)){
//                        Log.i("Swipe", portStocks.get(i).getTicker());
//                        portStocks.remove(i);
//                        break;
//                    }
//                }
                storeToLocalStorage();
                Toast.makeText(getApplicationContext(), ticker+" was removed from the portfolio", Toast.LENGTH_SHORT).show();
                pAdapter.removeItem(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(portRecycler);
    }
}