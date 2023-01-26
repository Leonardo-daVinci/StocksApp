package apps.nocturnuslabs.stocks.utils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import apps.nocturnuslabs.stocks.data.News;

public class NewsDetails {
    private JsonArrayRequest request;
    private ArrayList<News> arrayList;
    private String ticker;

    public NewsDetails(String ticker) {
        this.ticker = ticker;
        this.arrayList = new ArrayList<>();
    }

    public JsonArrayRequest getCompanyNews() {
        this.request = new JsonArrayRequest(Request.Method.GET, "SOME_STRING", null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject newsJSON = response.getJSONObject(i);
                            String source = newsJSON.getString("source");
                            String head = newsJSON.getString("headline");
                            String date = newsJSON.getString("datetime");
                            String url = newsJSON.getString("url");
                            String img = newsJSON.getString("image");
                            String summary = newsJSON.getString("summary");
                            News newsItem = new News(source, head, date, url, img, summary);
                            this.arrayList.add(newsItem);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, error -> error.printStackTrace());
        return this.request;
    }

    public ArrayList<News> getArrayList() {
        return arrayList;
    }
}
