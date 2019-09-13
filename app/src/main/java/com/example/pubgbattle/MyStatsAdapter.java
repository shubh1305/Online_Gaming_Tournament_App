package com.example.pubgbattle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyStatsAdapter extends RecyclerView.Adapter<MyStatsAdapter.MyViewHolder> {
    Context context;
    ArrayList<MyStats> myStats;

    public MyStatsAdapter() {
    }

    public MyStatsAdapter(Context context, ArrayList<MyStats> myStats) {
        this.context = context;
        this.myStats = myStats;
    }

    @NonNull
    @Override
    public MyStatsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyStatsAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.stats_info_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyStatsAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.tv_count.setText(""+ (myStats.size()-i)+ ".");
        myViewHolder.tv_title.setText(myStats.get(i).getTitle());
        myViewHolder.tv_date.setText(myStats.get(i).getDate());
        myViewHolder.tv_kill.setText(""+ myStats.get(i).getKills());
        myViewHolder.tv_winning.setText(""+ myStats.get(i).getWinning());
    }

    @Override
    public int getItemCount() {
        return myStats.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_date, tv_kill, tv_winning, tv_count;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_count = itemView.findViewById(R.id.stats_info_layout_count);
            tv_title = itemView.findViewById(R.id.stats_info_layout_title);
            tv_date = itemView.findViewById(R.id.stats_info_layout_date);
            tv_kill = itemView.findViewById(R.id.stats_info_layout_kills);
            tv_winning = itemView.findViewById(R.id.stats_info_layout_winning);
        }
    }
}
