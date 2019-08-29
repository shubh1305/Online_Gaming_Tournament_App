package com.example.pubgbattle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MatchResultLayoutAdapter extends RecyclerView.Adapter<MatchResultLayoutAdapter.MyResultViewHolder> {
    Context context;
    ArrayList<UserProfile> userDetail;
    private FirebaseAuth firebaseAuth;

    public MatchResultLayoutAdapter(Context context, ArrayList<UserProfile> userDetail) {
        this.context = context;
        this.userDetail = userDetail;
    }

    @NonNull
    @Override
    public MatchResultLayoutAdapter.MyResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MatchResultLayoutAdapter.MyResultViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.match_result_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MatchResultLayoutAdapter.MyResultViewHolder myResultViewHolder, int i) {
        myResultViewHolder.tv_number.setText(""+ (i+1)+ ".");
        myResultViewHolder.tv_playername.setText(userDetail.get(i).getName());
        myResultViewHolder.tv_kills.setText(""+ userDetail.get(i).getTotalKills());
        myResultViewHolder.tv_winning.setText(""+ userDetail.get(i).getTotalWinnings());
        myResultViewHolder.tv_username.setText(userDetail.get(i).getPubgUsername());
    }

    @Override
    public int getItemCount() {
        return userDetail.size();
    }

    class MyResultViewHolder extends RecyclerView.ViewHolder {
        TextView tv_number, tv_playername, tv_kills, tv_winning, tv_username;
        public MyResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_number = itemView.findViewById(R.id.match_result_layout_count);
            tv_playername = itemView.findViewById(R.id.match_result_layout_playername);
            tv_kills = itemView.findViewById(R.id.match_result_layout_kills);
            tv_winning = itemView.findViewById(R.id.match_result_layout_winning);
            tv_username = itemView.findViewById(R.id.match_result_layout_username);
        }
    }

}
