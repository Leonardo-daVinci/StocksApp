package apps.nocturnuslabs.stocks.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import apps.nocturnuslabs.stocks.R;

public class HistoryFragment extends Fragment {

    private WebView historyWeb;
    private String ticker;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_history, container, false);
        historyWeb = view.findViewById(R.id.history_webView);
        getChartOne();
        ticker = "AAPL";

        return view;
    }

    private void getChartOne(){
        historyWeb.setWebViewClient(new WebViewClient());
        WebSettings settings = historyWeb.getSettings();
        settings.setJavaScriptEnabled(true);
        historyWeb.clearCache(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowFileAccess(true);

        historyWeb.loadUrl("file:///android_asset/history.html");
        historyWeb.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String historyUrl = "";
                view.loadUrl("javascript:setTicker('" + ticker + "', '" + historyUrl + "')");
            }
        });
    }
}