package com.example.pubgbattle;


import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;

import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import io.supercharge.shimmerlayout.ShimmerLayout;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayFragment extends Fragment {

    public PlayFragment() {
        // Required empty public constructor
    }
    private Handler handler;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    //private TextView tv_wallet;
    private ShimmerLayout shimmerLayout;
    private NestedScrollView play_nsv;
    private LinearLayout lin;
    private NoInternetDialog noInternetDialog;



    private RecyclerView recyclerViewUpcoming;

    private ArrayList<MatchDetail> matchDetailsList;

    private UpcomingMatchAdapter adapterUpcoming;
    

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        noInternetDialog = new NoInternetDialog.Builder(this)
                .setButtonColor(getResources().getColor(R.color.main))
                .build();


        shimmerLayout = view.findViewById(R.id.fragment_play_shimmer_layout);
        //shimmerLayout.startShimmerAnimation();
        play_nsv = view.findViewById(R.id.fragment_play_nsv);
        lin = view.findViewById(R.id.fragment_play_lin);
        play_nsv.setVisibility(View.INVISIBLE);
        lin.setVisibility(View.INVISIBLE);
        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Matches");

        recyclerViewUpcoming = view.findViewById(R.id.fragment_play_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //Descending order
        //linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        recyclerViewUpcoming.setLayoutManager(linearLayoutManager);

        matchDetailsList = new ArrayList<MatchDetail>();
        adapterUpcoming = new UpcomingMatchAdapter(getActivity(), matchDetailsList);
        recyclerViewUpcoming.setAdapter(adapterUpcoming);

        AddToAdapter(databaseReference);
        return view;
    }

    private void AddToAdapter(DatabaseReference databaseReference) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shimmerLayout.startShimmerAnimation();
                matchDetailsList.clear();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    final MatchDetail matchDetail = d.getValue(MatchDetail.class);
                    if(matchDetail.getMatchStatus().equals("Upcoming")) {
                        matchDetailsList.add(matchDetail);
                        //Log.d(TAG, "onDataChange: " + d.getKey());
                    }
                }
                if (matchDetailsList.isEmpty()){
                    play_nsv.setVisibility(View.INVISIBLE);
                    lin.setVisibility(View.VISIBLE);
                    shimmerLayout.stopShimmerAnimation();
                    shimmerLayout.setVisibility(View.GONE);
                }
                else{
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapterUpcoming.notifyDataSetChanged();
                            play_nsv.setVisibility(View.VISIBLE);
                            lin.setVisibility(View.INVISIBLE);
                            shimmerLayout.stopShimmerAnimation();
                            shimmerLayout.setVisibility(View.GONE);
                        }
                    }, 500);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        shimmerLayout.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerLayout.stopShimmerAnimation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }

}
