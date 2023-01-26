package apps.nocturnuslabs.stocks.ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import apps.nocturnuslabs.stocks.R;
import apps.nocturnuslabs.stocks.data.News;

class NewsViewHolder extends RecyclerView.ViewHolder{
    ImageView img;
    TextView source;
    TextView eta;
    TextView head;
    View view;

    public NewsViewHolder(@NonNull View itemView, int viewType) {
        super(itemView);
        img = itemView.findViewById(R.id.news_img);
        source = itemView.findViewById(R.id.news_source);
        eta = itemView.findViewById(R.id.news_eta);
        head = itemView.findViewById(R.id.news_head);
        view = itemView;
    }
}

public class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder>{

    private ArrayList<News> newsArrayList;

    public NewsAdapter(ArrayList<News> newsArrayList){
        this.newsArrayList = newsArrayList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(context).inflate(R.layout.news_item_special, parent, false);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        }
        return new NewsViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News newsItem = this.newsArrayList.get(position);
        String imgUrl = Uri.parse(newsItem.getImg()).buildUpon().build().toString();
        Picasso.get().load(imgUrl).into(holder.img);

        holder.source.setText(newsItem.getSource());
        holder.eta.setText(newsItem.getEta());
        holder.head.setText(newsItem.getHead());

        //Implement NewsDialog
        holder.view.setOnClickListener(view -> {
//                    Toast.makeText(view.getContext(), "News Item clicked", Toast.LENGTH_LONG).show();
            ShareDialog shareDialog = ShareDialog.newInstance(newsItem);
            shareDialog.show(((AppCompatActivity)(holder.view.getContext())).getSupportFragmentManager(), "ShareDialog");
        });
    }

    @Override
    public int getItemCount() {
        return this.newsArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        if (position == 0){
            return 0;
        }else{
            return 1;
        }
    }
}
