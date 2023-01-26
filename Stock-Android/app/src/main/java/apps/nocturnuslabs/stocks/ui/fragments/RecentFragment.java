package apps.nocturnuslabs.stocks.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.nocturnuslabs.stocks.R;
import apps.nocturnuslabs.stocks.ui.DetailsActivity;
import apps.nocturnuslabs.stocks.ui.HomeActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.hichartsclasses.*;
import com.highsoft.highcharts.core.*;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class RecentFragment extends Fragment {

    RequestQueue queue;
    String ticker;
    long unixTime;
    ArrayList<Long> timestamp;
    ArrayList<Double> closePrice;
    Number [][] series;

    HIChartView chartView;

    public RecentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_recent, container, false);
        chartView= v.findViewById(R.id.recent_hchart);

        SharedPreferences preferences = getActivity().getSharedPreferences("LocalStorage", Context.MODE_PRIVATE);
        ticker = preferences.getString("StockTicker", "TSLA");

        Date date = new Date();
        unixTime = date.getTime() / 1000L;

        queue = Volley.newRequestQueue(getActivity());

        getHourly();

        return v;
    }

    private void getHourly(){
        String url = "https://assign8-nodejs-nocturnus.wl.r.appspot.com//data/hist/"+ticker.toUpperCase()+"/date/"+unixTime+"/res/5";
//        Log.i("RecentFragement", url);
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET, url, null,
                        response -> {
                            try{
                                timestamp = new ArrayList<>();
                                closePrice = new ArrayList<>();
                                series = new Number[response.length()][response.length()];

                                for(int i=0; i<response.length(); i++){
                                    JSONObject obj = response.getJSONObject(i);
//                                    timestamp.add(obj.getLong("timestamp")*1000);
//                                    closePrice.add(obj.getDouble("closePrice"));
                                    series[i][0] = obj.getLong("timestamp")*1000;
                                    series[i][1] = obj.getDouble("closePrice");
                                }
                                showHourly();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }, error -> error.printStackTrace());
        queue.add(request);
    }

    private void showHourly(){
        HIOptions options = new HIOptions();
//
        HITitle title = new HITitle();
        title.setText(""+ticker+" Hourly Price Variation");
        options.setTitle(title);
//
        HIXAxis xaxis = new HIXAxis();
        //Put the dates here
//        xaxis.getDateTimeLabelFormats().setHour(new HIHour());
        xaxis.setType("datetime");
//        xaxis.setCategories(new ArrayList<>(Arrays.asList("Date1","Date2","Date3","Date4")));
        options.setXAxis(new ArrayList<>(Collections.singletonList(xaxis)));
//
        HIYAxis yaxis = new HIYAxis();
        yaxis.setOpposite(true);
        yaxis.setTitle(new HITitle());
        yaxis.getTitle().setText("");
        options.setYAxis(new ArrayList<>(Collections.singletonList(yaxis)));

        HILegend legend = new HILegend();
        legend.setEnabled(false);

        HITooltip tooltip = new HITooltip();
        tooltip.setValueDecimals(2);
        options.setTooltip(tooltip);

        HILine line = new HILine();
        line.setName(ticker);
        line.setData(new ArrayList<>(Arrays.asList(series)));
        line.setColor(HIColor.initWithName("Red"));

        options.setSeries(new ArrayList<>(Collections.singletonList(line)));
        chartView.setOptions(options);
    }
}