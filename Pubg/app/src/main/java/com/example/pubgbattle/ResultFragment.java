package com.example.pubgbattle;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import io.supercharge.shimmerlayout.ShimmerLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultFragment extends Fragment {


    public ResultFragment() {
        // Required empty public constructor
    }
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;


    private RecyclerView recyclerViewResult;

    private ArrayList<MatchDetail> matchDetailsList;

    private ResultMatchAdapter adapterResult;
    private ShimmerLayout shimmerLayout;
    private NestedScrollView play_nsv;
    private LinearLayout lin;
    private NoInternetDialog noInternetDialog;
    private Handler handler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_result, container, false);
        noInternetDialog = new NoInternetDialog.Builder(this)
                .setButtonColor(getResources().getColor(R.color.main))
                .build();


        shimmerLayout = view.findViewById(R.id.fragment_result_shimmer_layout);
        //shimmerLayout.startShimmerAnimation();
        play_nsv = view.findViewById(R.id.fragment_result_nsv);
        lin = view.findViewById(R.id.fragment_result_lin);
        play_nsv.setVisibility(View.INVISIBLE);
        lin.setVisibility(View.INVISIBLE);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Matches");

        recyclerViewResult = view.findViewById(R.id.fragment_result_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //Descending order
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewResult.setLayoutManager(linearLayoutManager);

        matchDetailsList = new ArrayList<MatchDetail>();
        adapterResult = new ResultMatchAdapter(getActivity(), matchDetailsList);
        recyclerViewResult.setAdapter(adapterResult);
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
                    MatchDetail matchDetail = d.getValue(MatchDetail.class);
                    if(matchDetail.getMatchStatus().equals("Result"))
                        matchDetailsList.add(matchDetail);
                }
                if (matchDetailsList.isEmpty()){
                    play_nsv.setVisibility(View.INVISIBLE);
                    lin.setVisibility(View.VISIBLE);
                    shimmerLayout.stopShimmerAnimation();
                    shimmerLayout.setVisibility(View.GONE);
                }
                else {
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapterResult.notifyDataSetChanged();
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
    public void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
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
}
