package com.example.pubgbattle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ReferralAdapter extends RecyclerView.Adapter<ReferralAdapter.MyResultViewHolder> {
    Context context;
    ArrayList<ReferralUser> refUser;

    public ReferralAdapter() {
    }

    public ReferralAdapter(Context context, ArrayList<ReferralUser> refUser) {
        this.context = context;
        this.refUser = refUser;
    }

    @NonNull
    @Override
    public ReferralAdapter.MyResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ReferralAdapter.MyResultViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.referral_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReferralAdapter.MyResultViewHolder myResultViewHolder, int i) {

        myResultViewHolder.tv_playername.setText(refUser.get(i).getName());
        myResultViewHolder.tv_date.setText(""+ refUser.get(i).getDate());
        myResultViewHolder.tv_status.setText(""+ refUser.get(i).getStatus());

        if(refUser.get(i).status.equals("Pending")){
            myResultViewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.googleRed));
        }
        else {
            myResultViewHolder.tv_status.setTextColor(context.getResources().getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return refUser.size();
    }
    class MyResultViewHolder extends RecyclerView.ViewHolder {
        TextView tv_playername, tv_date, tv_status;
        public MyResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_playername = itemView.findViewById(R.id.referral_layout_playername);
            tv_date = itemView.findViewById(R.id.referral_layout_date);
            tv_status = itemView.findViewById(R.id.referral_layout_status);
        }
    }
}
