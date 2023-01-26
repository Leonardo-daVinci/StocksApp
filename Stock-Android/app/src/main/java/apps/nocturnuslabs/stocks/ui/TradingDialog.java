package apps.nocturnuslabs.stocks.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.Serializable;
import java.text.DecimalFormat;

import apps.nocturnuslabs.stocks.R;
import apps.nocturnuslabs.stocks.data.Trade;

public class TradingDialog extends DialogFragment {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public interface OnInputListener{
        void sendInput(int input, boolean buy);
    }
    public OnInputListener inputListener;

    public Trade item;
    public View view;
    TextView tComp, tEst, tBal;
    Button buyBtn, sellBtn;
    EditText tEdit;
    double total = 0.00;
    int val = 0;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        item = (Trade) getArguments().getSerializable("trading");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.trade, container, false);
        tComp = view.findViewById(R.id.trade_company);
        tEst= view.findViewById(R.id.trade_estimate);
        tBal = view.findViewById(R.id.trade_bal);
        buyBtn = view.findViewById(R.id.trade_buy_btn);
        sellBtn = view.findViewById(R.id.trade_sell_btn);
        tEdit = view.findViewById(R.id.trade_edit_shares);

        tComp.setText("Trade "+item.getName()+" shares");
        tBal.setText("$"+df.format(item.getBalance())+" to buy "+item.getTicker());
        tEst.setText("0*$"+item.getPrice()+"/share = 0.00");

        //Mess with the buttons
        TextWatcher inputWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().isEmpty()){
                    tEst.setText("0*$"+df.format(item.getPrice())+"/share = 0.00");
                }else{
                    val = Integer.parseInt(editable.toString());
                    total = item.getPrice()*val;
                    tEst.setText(""+val+"*$"+df.format(item.getPrice())+"/share = "+df.format(total));
                }
            }
        };
        tEdit.addTextChangedListener(inputWatcher);

        //Buy Button Functionality
        buyBtn.setOnClickListener(v1 -> {
            if(total == 0.00){
                Toast.makeText(getContext(), "Cannot buy non-positive shares", Toast.LENGTH_SHORT).show();
            }else if(total > item.getBalance()){
                Toast.makeText(getContext(), "Not enough money to buy", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(), "Congratulations!\nYou have successfully bought "+val+" shares of "+item.getTicker(), Toast.LENGTH_SHORT).show();
                inputListener.sendInput(val, true);
                dismiss();
            }

        });

        sellBtn.setOnClickListener(v1-> {
            if(val == 0){
                Toast.makeText(getContext(), "Cannot sell non-positive shares", Toast.LENGTH_SHORT).show();
            }else if(val > item.getShares()){
                Toast.makeText(getContext(), "Not enough shares to sell", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getContext(), "Congratulations!\nYou have successfully sold "+val+" shares of "+item.getTicker(), Toast.LENGTH_SHORT).show();
                inputListener.sendInput(val, false);
                dismiss();
            }

        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            inputListener = (OnInputListener) getActivity();
        }catch(ClassCastException e){
            e.getMessage();
        }
    }

    static TradingDialog newInstance(Trade item){
        TradingDialog tradingDialog = new TradingDialog();
        Bundle args = new Bundle();
        args.putSerializable("trading", (Serializable) item);
        tradingDialog.setArguments(args);
        return tradingDialog;
    }
}

