package com.example.pubgbattle;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class BuyCoinAdapter extends RecyclerView.Adapter<BuyCoinAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Coin> coins;
    private int px;

    public BuyCoinAdapter(Context context, ArrayList<Coin> coins) {
        this.context = context;
        this.coins = coins;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv1, tv2;
        CardView cv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.buy_coin_layout_tv);
            tv2 = itemView.findViewById(R.id.buy_coin_layout_tv2);
            cv = itemView.findViewById(R.id.buy_coin_layout_cv);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Resources r = viewGroup.getResources();
        px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,r.getDisplayMetrics()));
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.buy_coin_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final int position = i;
        myViewHolder.tv1.setText(""+ coins.get(i).getValue());
        myViewHolder.tv2.setText(""+ coins.get(i).getValue());
        if(i == 0){
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) myViewHolder.cv.getLayoutParams();
            layoutParams.topMargin = px;
            myViewHolder.cv.requestLayout();
        }

        myViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PaymentActivity.class);
                intent.putExtra("coin", coins.get(position).getValue());
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return coins.size();
    }
}
