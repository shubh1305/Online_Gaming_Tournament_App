package com.example.pubgbattle;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;




public class RedeemAdapter extends RecyclerView.Adapter<RedeemAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Coin> redeem_coin;
    private int px;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private AlertDialog join_dialog_done;

    public RedeemAdapter(Context context, ArrayList<Coin> coins) {
        this.context = context;
        this.redeem_coin = coins;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv1, tv2, tv3;
        CardView cv;
        LinearLayout lin;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.redeem_coin_layout_tv1);
            tv2 = itemView.findViewById(R.id.redeem_coin_layout_tv2);
            tv3 = itemView.findViewById(R.id.redeem_coin_layout_tv3);
            cv = itemView.findViewById(R.id.redeem_coin_layout_cv);
            lin = itemView.findViewById(R.id.redeem_coin_layout_lin1);
        }

    }
    @NonNull
    @Override
    public RedeemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Resources r = viewGroup.getResources();
        AlertDialog redeem_dialog_done;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid());
        px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,r.getDisplayMetrics()));
        return new RedeemAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.redeem_coin_layout, viewGroup, false));
    }
    @Override
    public void onBindViewHolder(@NonNull RedeemAdapter.MyViewHolder myViewHolder, int i) {
        final int position = i;
        myViewHolder.tv1.setText("" + redeem_coin.get(i).getValue());
        myViewHolder.tv2.setText("" + redeem_coin.get(i).getValue());
        myViewHolder.tv3.setText("" + redeem_coin.get(i).getValue());
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        myViewHolder.lin.setBackgroundColor(color);
        if (i%2 == 0) {
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) myViewHolder.cv.getLayoutParams();
            layoutParams.leftMargin = px;
            myViewHolder.cv.requestLayout();
        }
        else {
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) myViewHolder.cv.getLayoutParams();
            layoutParams.rightMargin = px;
            myViewHolder.cv.requestLayout();
        }

        if(i == 0 || i==1){
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) myViewHolder.cv.getLayoutParams();
            layoutParams.topMargin = px;
            myViewHolder.cv.requestLayout();
        }

        if(i == redeem_coin.size()-1 || i== redeem_coin.size()-2){
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) myViewHolder.cv.getLayoutParams();
            layoutParams.bottomMargin = px;
            myViewHolder.cv.requestLayout();
        }
        myViewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        int wallet = userProfile.getWalletCoins();
                        if(wallet<redeem_coin.get(position).getValue()){
                            Toast.makeText(context, "Insufficient fund", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(context);
                            builder.setMessage("Are you sure, you want to redeem "+ redeem_coin.get(position).getValue()+" coins")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            ShowDoneDialog();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.setTitle("Confirmation");
                            alertDialog.show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void ShowDoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_redeem_done, null);
        Button btn_done = view.findViewById(R.id.dialog_redeem_done_btn);

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                join_dialog_done.dismiss();

            }
        });
        builder.setView(view);
        join_dialog_done = builder.create();
        join_dialog_done.setCancelable(false);
        join_dialog_done.setCanceledOnTouchOutside(false);
        join_dialog_done.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        join_dialog_done.show();
        Toast.makeText(context, "Request Submitted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return redeem_coin.size();
    }
}
