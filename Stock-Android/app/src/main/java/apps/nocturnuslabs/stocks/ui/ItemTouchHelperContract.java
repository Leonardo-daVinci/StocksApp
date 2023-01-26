package apps.nocturnuslabs.stocks.ui;

public interface ItemTouchHelperContract {
    void onRowMoved(int fromPosition, int toPosition);

    void onRowSelected(SavedStockViewHolder myViewHolder);

    void onRowClear(SavedStockViewHolder myViewHolder);
}
