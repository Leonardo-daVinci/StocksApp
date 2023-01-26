package apps.nocturnuslabs.stocks.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import apps.nocturnuslabs.stocks.R;
import apps.nocturnuslabs.stocks.data.News;

public class ShareDialog extends DialogFragment {
    public News news;
    public View view;
    TextView source, date, head, summary;
    ImageView chrome, twitter, fb;


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        news = (News) getArguments().getSerializable("data");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.news_share, container, false);
        source = view.findViewById(R.id.news_share_source);
        date = view.findViewById(R.id.news_share_date);
        head = view.findViewById(R.id.news_share_head);
        summary = view.findViewById(R.id.news_share_summary);
        chrome = view.findViewById(R.id.news_share_chrome);
        twitter = view.findViewById(R.id.news_share_twitter);
        fb = view.findViewById(R.id.news_share_fb);

        source.setText(news.getSource());
        date.setText(news.getDate());
        head.setText(news.getHead());
        summary.setText(news.getSummary());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        chrome.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(news.getUrl())));
        });


        twitter.setOnClickListener(v -> {
            String url;
            try {
                url = "https://twitter.com/intent/tweet?text=" + URLEncoder.encode(news.getHead(), "UTF-8") + "&url=" + URLEncoder.encode(news.getUrl(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                url = news.getUrl();
            }
            Uri uri = Uri.parse(url);
            Intent twitterIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(twitterIntent);
        });

        fb.setOnClickListener(v -> {
            String url;
                url = "https://www.facebook.com/sharer/sharer.php?u=" + news.getUrl();
            Uri uri = Uri.parse(url);
            Intent twitterIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(twitterIntent);
        });


    }

    static ShareDialog newInstance(News news){
        ShareDialog shareDialog = new ShareDialog();
        Bundle args = new Bundle();
        args.putSerializable("data", (Serializable) news);
        shareDialog.setArguments(args);
        return shareDialog;
    }
}
