package com.example.pubgbattle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.MyParticipantHolder> {

    Context context;
    ArrayList<UserProfile> participant;
    private FirebaseAuth firebaseAuth;

    public ParticipantAdapter(Context c, ArrayList<UserProfile> p){
        context = c;
        participant = p;
    }


    @NonNull
    @Override
    public MyParticipantHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyParticipantHolder(LayoutInflater.from(context).inflate(R.layout.participants, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyParticipantHolder myViewHolder, int i) {
        final int position = i;
        myViewHolder.tv_name.setText(participant.get(i).getName());
        myViewHolder.tv_pubgusername.setText(""+participant.get(i).getPubgUsername());
        myViewHolder.tv_number.setText(""+ (i+1+"."));
    }


    @Override
    public int getItemCount() {
        return participant.size();
    }

    class MyParticipantHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_pubgusername, tv_number;
        public MyParticipantHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.participant_name);
            tv_pubgusername = itemView.findViewById(R.id.participant_pubgusername);
            tv_number = itemView.findViewById(R.id.participant_number);
        }
    }

}
