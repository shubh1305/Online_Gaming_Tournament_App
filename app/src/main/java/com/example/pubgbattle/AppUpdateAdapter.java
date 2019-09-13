package com.example.pubgbattle;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AppUpdateAdapter extends RecyclerView.Adapter<AppUpdateAdapter.MyResultViewHolder>{

    Context context;
    ArrayList<AppUpdateDetailModel> list;

    public AppUpdateAdapter() {
    }

    public AppUpdateAdapter(Context context, ArrayList<AppUpdateDetailModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AppUpdateAdapter.MyResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AppUpdateAdapter.MyResultViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.app_update_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppUpdateAdapter.MyResultViewHolder myResultViewHolder, int i) {
        myResultViewHolder.tv_detail.setText(list.get(i).getValue());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class MyResultViewHolder extends RecyclerView.ViewHolder {
        TextView tv_detail;
        public MyResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_detail = itemView.findViewById(R.id.app_update_layout_tv);
        }
    }
}
