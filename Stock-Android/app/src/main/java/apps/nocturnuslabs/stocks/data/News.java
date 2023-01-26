package apps.nocturnuslabs.stocks.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class News implements Serializable {
    private String source;
    private String head;
    private String date;
    private String url;
    private String eta;
    private String img;
    private String summary;

    public String getSummary() {
        return summary;
    }

    public String getSource() {
        return source;
    }

    public String getHead() {
        return head;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public String getEta() {
        return eta;
    }

    public String getImg() {
        return img;
    }

    public News(String source, String head, String date, String url, String img, String summary){
        this.source = source;
        this.head = head;
        this.date = date;
        this.url = url;
        this.img = img;
        this.summary = summary;
        this.eta = calculateETA(date);
    }

    private String calculateETA(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try{
            Date newsDate = simpleDateFormat.parse(date);
            Date now = new Date();
            long duration = now.getTime() - newsDate.getTime();
            int days = (int) (duration/(24*60*60*1000));
            int hours = (int) (duration/(60*60*1000));
            int mins = (int)(duration/(60*1000));

            if(days >= 1) {
                return String.format("%d day(s) ago", days);
            }else{
                if (hours >= 1){
                    return String.format("%d hour(s) ago", hours);
                }else{
                    return String.format("%d min(s) ago", mins);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }
}



