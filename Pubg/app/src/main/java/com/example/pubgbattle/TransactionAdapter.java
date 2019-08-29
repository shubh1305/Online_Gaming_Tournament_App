package com.example.pubgbattle;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {

    Context context;
    ArrayList<TransactionDetail> transactionDetails;
    private FirebaseAuth firebaseAuth;

    public TransactionAdapter(Context c, ArrayList<TransactionDetail> m){
        context = c;
        transactionDetails = m;
    }
    private int px, px2;

    @NonNull
    @Override
    public TransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Resources r = viewGroup.getResources();
        px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,r.getDisplayMetrics()));
        px2 = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 180,r.getDisplayMetrics()));
        return new TransactionAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.transaction_cardview,
                viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final TransactionAdapter.MyViewHolder myViewHolder, int i) {
        final int position = i;
        myViewHolder.tv_title.setText(transactionDetails.get(i).getTransactionTitle());
        myViewHolder.tv_date.setText(transactionDetails.get(i).getTransactionDate());
        myViewHolder.tv_time.setText(transactionDetails.get(i).getTransactionTime());
        myViewHolder.tv_type.setText(transactionDetails.get(i).getTransactionType());
        //myViewHolder.tv_transId.setText(transactionDetails.get(i).getTransactionId());

        myViewHolder.tv_amount.setText(""+ transactionDetails.get(i).getTransactionAmount());
        if(i == 0){
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) myViewHolder.cardView.getLayoutParams();
            layoutParams.topMargin = px;
            myViewHolder.cardView.requestLayout();
        }
        if(transactionDetails.get(i).transactionType.equals("DEBIT")){
            myViewHolder.tv_type.setTextColor(context.getResources().getColor(R.color.googleRed));
            myViewHolder.tv_sign.setText("-");
            myViewHolder.tv_sign.setTextColor(context.getResources().getColor(R.color.googleRed));
            myViewHolder.tv_amount.setTextColor(context.getResources().getColor(R.color.googleRed));
        }
        else {
            myViewHolder.tv_type.setTextColor(context.getResources().getColor(R.color.green));
            myViewHolder.tv_sign.setText("+");
            myViewHolder.tv_sign.setTextColor(context.getResources().getColor(R.color.green));
            myViewHolder.tv_amount.setTextColor(context.getResources().getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return transactionDetails.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_type, tv_title, tv_date, tv_time, tv_amount, tv_transId, tv_sign;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.transaction_cv_title);
            tv_date = itemView.findViewById(R.id.transaction_cv_date);
            tv_time = itemView.findViewById(R.id.transaction_cv_time);
            tv_type = itemView.findViewById(R.id.transaction_cv_type);
            tv_amount = itemView.findViewById(R.id.transaction_cv_amount);
            //tv_transId = itemView.findViewById(R.id.transaction_cv_transId);
            tv_sign = itemView.findViewById(R.id.transaction_cv_sign);
            cardView = itemView.findViewById(R.id.transaction_cv);
        }
    }
}
