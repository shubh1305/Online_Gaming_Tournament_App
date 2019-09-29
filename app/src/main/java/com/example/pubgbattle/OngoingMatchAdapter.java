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
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class OngoingMatchAdapter extends RecyclerView.Adapter<OngoingMatchAdapter.MyViewHolder> {

    Context context;
    ArrayList<MatchDetail> matchDetails;
    private FirebaseAuth firebaseAuth;

    public OngoingMatchAdapter(Context c, ArrayList<MatchDetail> m){
        context = c;
        matchDetails = m;
    }
    private int px, px2;

    @NonNull
    @Override
    public OngoingMatchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Resources r = viewGroup.getResources();
        px = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 12,r.getDisplayMetrics()));
        px2 = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 200,r.getDisplayMetrics()));
        return new OngoingMatchAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.ongoing_cardview, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final OngoingMatchAdapter.MyViewHolder myViewHolder, int i) {
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
        if(i == getItemCount()-1){
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) myViewHolder.cardView.getLayoutParams();
            layoutParams.topMargin = px;
            myViewHolder.cardView.requestLayout();
        }
        firebaseAuth = FirebaseAuth.getInstance();
        final DatabaseReference myRefParticipants = FirebaseDatabase.getInstance().getReference("Matches")
                .child(matchDetails.get(position).getMatchKey()).child("participants");
        myRefParticipants.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int flag=0;
                for(DataSnapshot d1: dataSnapshot.getChildren()){
                    String str = d1.getKey().toString();
                    Log.d(TAG, "On data change Uid: "+ str);
                    if(str.equals(firebaseAuth.getUid())){
                        flag=1;
                        break;
                    }
                }
                if(flag==0){
                    myViewHolder.btn_play.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+ position);
                Log.d(TAG, "onClick: "+ matchDetails.get(position).getMatchKey());
                Intent intent = new Intent(context, OngoingMatchDetailActivity.class);
                intent.putExtra("matchkey", matchDetails.get(position).getMatchKey());
                context.startActivity(intent);
            }
        });

        //Setting up image
        final DatabaseReference myRefImg = FirebaseDatabase.getInstance().getReference("Matches")
                .child(matchDetails.get(position).getMatchKey());
        myRefImg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //myViewHolder.progressBar.setVisibility(View.VISIBLE);
                final MatchDetail m = dataSnapshot.getValue(MatchDetail.class);
                String imgUrl = m.getMatchPic();
                //if image is received then set
                try {
                    Picasso.get().load(imgUrl).networkPolicy(NetworkPolicy.OFFLINE).into(myViewHolder.iv_ongoing
                            , new Callback() {
                                @Override
                                public void onSuccess() {
                                    myViewHolder.iv_ongoing.requestLayout();
                                    myViewHolder.iv_ongoing.getLayoutParams().height = px2;
                                    myViewHolder.avi.setVisibility(View.INVISIBLE);
                                   // myViewHolder.progressBar.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(imgUrl).into(myViewHolder.iv_ongoing, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            myViewHolder.iv_ongoing.requestLayout();
                                            myViewHolder.iv_ongoing.getLayoutParams().height = px2;
                                            myViewHolder.avi.setVisibility(View.INVISIBLE);
                                            //myViewHolder.progressBar.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            myViewHolder.iv_ongoing.requestLayout();
                                            myViewHolder.iv_ongoing.getLayoutParams().height = 0;
                                            myViewHolder.avi.setVisibility(View.INVISIBLE);
                                            //myViewHolder.progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            });
                }
                catch (Exception e){
                    //if there is any exception while getting image then set default
                    myViewHolder.iv_ongoing.requestLayout();
                    myViewHolder.iv_ongoing.getLayoutParams().height = 0;
                    myViewHolder.avi.setVisibility(View.INVISIBLE);
                    //myViewHolder.progressBar.setVisibility(View.INVISIBLE);
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
        CardView cardView;
        Button btn_spectate, btn_play;
        ImageView iv_ongoing;
        AVLoadingIndicatorView avi;
        //ProgressBar progressBar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.ongoing_cardview_tv_title);
            tv_date = itemView.findViewById(R.id.ongoing_cardview_tv_date);
            tv_time = itemView.findViewById(R.id.ongoing_cardview_tv_time);
            tv_winningPrize = itemView.findViewById(R.id.ongoing_cardview_tv_winingprize);
            tv_perkill = itemView.findViewById(R.id.ongoing_cardview_tv_perkill);
            tv_entryfee = itemView.findViewById(R.id.ongoing_cardview_tv_entryfee);
            tv_type = itemView.findViewById(R.id.ongoing_cardview_tv_type);
            tv_version = itemView.findViewById(R.id.ongoing_cardview_tv_version);
            tv_map = itemView.findViewById(R.id.ongoing_cardview_tv_map);
            cardView = itemView.findViewById(R.id.ongoing_cardview);
            btn_spectate = itemView.findViewById(R.id.ongoing_cardview_btn_spectate);
            btn_play = itemView.findViewById(R.id.ongoing_cardview_btn_play);
            iv_ongoing = itemView.findViewById(R.id.ongoing_cardview_img);
            avi = itemView.findViewById(R.id.ongoing_cardview_avi);

        }
    }
}
