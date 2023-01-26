package apps.nocturnuslabs.stocks.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import apps.nocturnuslabs.stocks.R;
import apps.nocturnuslabs.stocks.data.LatestPrice;

class SavedStockViewHolder extends RecyclerView.ViewHolder {

    ImageView img;
    TextView ticker, shares, price, change;
    View view;

    public SavedStockViewHolder(@NonNull View itemView, int viewType){
        super(itemView);
        img = itemView.findViewById(R.id.saved_change_img);
        ticker = itemView.findViewById(R.id.saved_ticker);
        shares = itemView.findViewById(R.id.saved_shares);
        price = itemView.findViewById(R.id.saved_price);
        change = itemView.findViewById(R.id.saved_change);
        view = itemView;
    }

}

public class SavedStockAdapter extends RecyclerView.Adapter<SavedStockViewHolder>
        implements ItemTouchHelperContract{

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private ArrayList<LatestPrice> showArrayList;
    private LatestPrice deleteditem;

    public SavedStockAdapter(ArrayList<LatestPrice> showArrayList){ this.showArrayList = showArrayList;}

    @NonNull
    @Override
    public SavedStockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.saved_item, parent, false);
        return new SavedStockViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedStockViewHolder holder, int position) {
        LatestPrice item = this.showArrayList.get(position);

        holder.ticker.setText(item.getTicker());
        holder.shares.setText(""+item.getShares()+" shares");
        holder.price.setText("$"+df.format(item.getCurrentPrice()));

        holder.change.setText("$"+df.format(item.getChangePrice())+" ("+df.format(item.getChangePercent())+"%)");
        if(item.getChangePrice() > 0){
            holder.change.setTextColor(Color.rgb(16, 161, 43));
            holder.img.setImageResource(R.drawable.ic_trending_up);
        }else if(item.getChangePrice() < 0){
            holder.change.setTextColor(Color.rgb(236, 64, 28));
            holder.img.setImageResource(R.drawable.ic_trending_down);
        }

        holder.view.setOnClickListener(v->{
            Intent detailsIntent = new Intent(v.getContext(), DetailsActivity.class);
//            detailIntent.setAction(Intent.ACTION_SEARCH);
//            detailIntent.putExtra(SearchManager.QUERY, item.getTicker());
            detailsIntent.putExtra("StockTicker", item.getTicker());
            v.getContext().startActivity(detailsIntent);
        });

    }

    @Override
    public int getItemCount() {
        return this.showArrayList.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(showArrayList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(showArrayList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(SavedStockViewHolder myViewHolder) {
        myViewHolder.view.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(SavedStockViewHolder myViewHolder) {
        myViewHolder.view.setBackgroundColor(Color.WHITE);
    }

    public ArrayList<LatestPrice> getData(){
        return showArrayList;
    }

    public void removeItem(int position){
        showArrayList.remove(position);
        notifyItemRemoved(position);
    }
}
