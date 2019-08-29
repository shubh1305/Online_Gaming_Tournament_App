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

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


public class UpcomingMatchAdapter extends RecyclerView.Adapter<UpcomingMatchAdapter.MyViewHolder> {

    Context context;
    ArrayList<MatchDetail> matchDetails;
    private FirebaseAuth firebaseAuth;

    public UpcomingMatchAdapter(Context c, ArrayList<MatchDetail> m){
        context = c;
        matchDetails = m;
    }
    private int px, px2;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Resources r = viewGroup.getResources();
        px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 12,r.getDisplayMetrics()));
        px2 = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 200,r.getDisplayMetrics()));
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.upcoming_cardview, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        final int position = i;
        myViewHolder.tv_title.setText(matchDetails.get(i).getMatchTitle());
        myViewHolder.tv_date.setText(matchDetails.get(i).getMatchDate());
        myViewHolder.tv_time.setText(matchDetails.get(i).getMatchTime());
        myViewHolder.tv_winningPrize.setText(""+ matchDetails.get(i).getMatchWinningPrize());
        myViewHolder.tv_perkill.setText(""+ matchDetails.get(i).getMatchPerKill());
        myViewHolder.tv_entryfee.setText(""+ matchDetails.get(i).getMatchEntryFee());
        myViewHolder.tv_type.setText(matchDetails.get(i).getMatchType());
        myViewHolder.tv_version.setText(matchDetails.get(i).getMatchVersion());
        myViewHolder.tv_map.setText(matchDetails.get(i).getMatchMap());
        myViewHolder.prog.setProgress(matchDetails.get(i).getMatchOccupiedSpot());
        myViewHolder.tv_totalspot.setText(""+ matchDetails.get(i).getMatchTotalSpot());
        myViewHolder.tv_occupiedspot.setText(""+ matchDetails.get(i).getMatchOccupiedSpot());
        if(matchDetails.get(i).getMatchOccupiedSpot() == matchDetails.get(i).getMatchTotalSpot()){
            myViewHolder.tv_spotstatus.setText("No Spot Left! Match is Full.");
            myViewHolder.tv_spotstatus.setTextColor(context.getResources().getColor(R.color.googleRed));
            myViewHolder.tv_spotstatus2.setText("");
            myViewHolder.tv_leftspot.setText("");
            myViewHolder.btn_join.setText("MATCH FULL");
            myViewHolder.btn_join.setTextColor(Color.WHITE);
            myViewHolder.btn_join.setBackgroundResource(R.drawable.my_button_cardview_matchfull);
            myViewHolder.btn_join.setEnabled(false);
        }else {
            myViewHolder.tv_leftspot.setText(""+ (matchDetails.get(i).getMatchTotalSpot()-matchDetails.get(i).getMatchOccupiedSpot()));
        }
        if(i == 0){
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) myViewHolder.cardView.getLayoutParams();
            layoutParams.topMargin = px;
            myViewHolder.cardView.requestLayout();
        }

        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: "+ position);
                //Log.d(TAG, "onClick: "+ matchDetails.get(position).getMatchKey());
                Intent intent = new Intent(context, UpcomingMatchDetailActivity.class);
                intent.putExtra("occupiedspot", matchDetails.get(position).getMatchOccupiedSpot());
                intent.putExtra("totalspot", matchDetails.get(position).getMatchTotalSpot());
                intent.putExtra("title", matchDetails.get(position).getMatchTitle());
                intent.putExtra("date", matchDetails.get(position).getMatchDate());
                intent.putExtra("time", matchDetails.get(position).getMatchTime());
                intent.putExtra("type", matchDetails.get(position).getMatchType());
                intent.putExtra("version", matchDetails.get(position).getMatchVersion());
                intent.putExtra("map", matchDetails.get(position).getMatchMap());
                intent.putExtra("entryfee", matchDetails.get(position).getMatchEntryFee());
                intent.putExtra("winning", matchDetails.get(position).getMatchWinningPrize());
                intent.putExtra("perkill", matchDetails.get(position).getMatchPerKill());
                intent.putExtra("matchkey", matchDetails.get(position).getMatchKey());
                context.startActivity(intent);
            }
        });

        myViewHolder.btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, JoiningMatchActivity.class);
                /*intent.putExtra("occupiedspot", matchDetails.get(position).getMatchOccupiedSpot());
                intent.putExtra("totalspot", matchDetails.get(position).getMatchTotalSpot());
                intent.putExtra("title", matchDetails.get(position).getMatchTitle());
                intent.putExtra("date", matchDetails.get(position).getMatchDate());
                intent.putExtra("time", matchDetails.get(position).getMatchTime());
                intent.putExtra("type", matchDetails.get(position).getMatchType());
                intent.putExtra("version", matchDetails.get(position).getMatchVersion());
                intent.putExtra("map", matchDetails.get(position).getMatchMap());
                intent.putExtra("entryfee", matchDetails.get(position).getMatchEntryFee());
                intent.putExtra("winning", matchDetails.get(position).getMatchWinningPrize());
                intent.putExtra("perkill", matchDetails.get(position).getMatchPerKill());*/
                intent.putExtra("matchkey", matchDetails.get(position).getMatchKey());
                context.startActivity(intent);
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        final DatabaseReference myRefParticipants = FirebaseDatabase.getInstance().getReference("Matches")
                .child(matchDetails.get(position).getMatchKey()).child("participants");
        myRefParticipants.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d1: dataSnapshot.getChildren()){
                    String str = d1.getKey();
                    Log.d(TAG, "On data change Uid: "+ str);
                    if(str.equals(firebaseAuth.getUid())){
                        Log.d(TAG, "onDataChange: Equal");
                        myViewHolder.btn_join.setEnabled(false);
                        myViewHolder.btn_join.setText("  JOINED  ");
                        myViewHolder.btn_join.setTextColor(Color.WHITE);
                        myViewHolder.btn_join.setBackgroundResource(R.drawable.my_button_blue);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Setting up image
        final DatabaseReference myRefImg = FirebaseDatabase.getInstance().getReference("Matches")
                .child(matchDetails.get(position).getMatchKey());
        myRefImg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final MatchDetail m = dataSnapshot.getValue(MatchDetail.class);
                String imgUrl = m.getMatchPic();
                //if image is received then set
                try {
                    Picasso.get().load(imgUrl).networkPolicy(NetworkPolicy.OFFLINE).into(myViewHolder.iv_upcoming
                            , new Callback() {
                                @Override
                                public void onSuccess() {
                                    myViewHolder.iv_upcoming.requestLayout();
                                    myViewHolder.iv_upcoming.getLayoutParams().height = px2;
                                    myViewHolder.avi.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(imgUrl).into(myViewHolder.iv_upcoming, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            myViewHolder.iv_upcoming.requestLayout();
                                            myViewHolder.iv_upcoming.getLayoutParams().height = px2;
                                            myViewHolder.avi.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            myViewHolder.iv_upcoming.requestLayout();
                                            myViewHolder.iv_upcoming.getLayoutParams().height = 0;
                                            myViewHolder.avi.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            });
                }
                catch (Exception e){
                    //if there is any exception while getting image then set default
                    myViewHolder.iv_upcoming.requestLayout();
                    myViewHolder.iv_upcoming.getLayoutParams().height = 0;
                    myViewHolder.avi.setVisibility(View.INVISIBLE);
                    //myViewHolder.progImg.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return matchDetails.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_date, tv_time, tv_winningPrize, tv_perkill, tv_entryfee, tv_type, tv_version, tv_map, tv_occupiedspot,
        tv_totalspot, tv_leftspot, tv_spotstatus, tv_spotstatus2;
        ProgressBar prog;// progImg;
        AVLoadingIndicatorView avi;
        ImageView iv_upcoming;
        public Button btn_join;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.upcoming_cardview_tv_title);
            tv_date = itemView.findViewById(R.id.upcoming_cardview_tv_date);
            tv_time = itemView.findViewById(R.id.upcoming_cardview_tv_time);
            tv_winningPrize = itemView.findViewById(R.id.upcoming_cardview_tv_winingprize);
            tv_perkill = itemView.findViewById(R.id.upcoming_cardview_tv_perkill);
            tv_entryfee = itemView.findViewById(R.id.upcoming_cardview_tv_entryfee);
            tv_type = itemView.findViewById(R.id.upcoming_cardview_tv_type);
            tv_version = itemView.findViewById(R.id.upcoming_cardview_tv_version);
            tv_map = itemView.findViewById(R.id.upcoming_cardview_tv_map);
            prog = itemView.findViewById(R.id.upcoming_cardview_pb);
            tv_totalspot = itemView.findViewById(R.id.upcoming_cardview_tv_totalspot);
            tv_occupiedspot = itemView.findViewById(R.id.upcoming_cardview_tv_occupiedspot);
            tv_leftspot = itemView.findViewById(R.id.upcoming_cardview_tv_leftspot);
            tv_spotstatus = itemView.findViewById(R.id.upcoming_cardview_tv_spotstatus);
            tv_spotstatus2 = itemView.findViewById(R.id.upcoming_cardview_tv_spotstatus2);
            cardView = itemView.findViewById(R.id.upcoming_cardview);
            btn_join = itemView.findViewById(R.id.upcoming_cardview_btn_join);
            iv_upcoming = itemView.findViewById(R.id.upcoming_cardview_img);
            avi = itemView.findViewById(R.id.upcoming_cardview_avi);
            //progImg = itemView.findViewById(R.id.upcoming_cardview_progress);
        }
    }

}
