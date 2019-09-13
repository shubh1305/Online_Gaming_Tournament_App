package com.example.pubgbattle;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;

public class MyStatsActivity extends AppCompatActivity {
    private NoInternetDialog noInternetDialog;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private RecyclerView rv_stats;
    private ArrayList<MyStats> myStatsList;
    private MyStatsAdapter adapter;
    private ProgressDialog progressDialog;
    private LinearLayout lin;
    private NestedScrollView nsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stats);
        noInternetDialog = new NoInternetDialog.Builder(this)
                .setButtonColor(getResources().getColor(R.color.main))
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.rgb(13, 35, 71));
            window.setStatusBarColor(getResources().getColor(R.color.statusbar));
            //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        toolbar = findViewById(R.id.activity_my_stats_toolbar);
        setSupportActionBar(toolbar);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        //toolbar.setTitleTextColor(getResources().getColor(R.color.main));
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
        rv_stats = findViewById(R.id.activity_my_stats_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //Descending order
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rv_stats.setLayoutManager(linearLayoutManager);
        myStatsList = new ArrayList<MyStats>();
        adapter = new MyStatsAdapter(this, myStatsList);
        rv_stats.setAdapter(adapter);
        firebaseAuth = FirebaseAuth.getInstance();
        lin = findViewById(R.id.activity_my_stats_lin);
        nsv = findViewById(R.id.activity_my_stats_nsv);
        nsv.setVisibility(View.INVISIBLE);
        lin.setVisibility(View.INVISIBLE);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid()).child("matches");
        FetchData(myRef);

    }

    private void FetchData(DatabaseReference myRef) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myStatsList.clear();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    final MyStats myStats = d.getValue(MyStats.class);
                    myStatsList.add(myStats);
                }
                if(myStatsList.isEmpty()){
                    nsv.setVisibility(View.INVISIBLE);
                    lin.setVisibility(View.VISIBLE);
                }
                else {
                    adapter.notifyDataSetChanged();
                    nsv.setVisibility(View.VISIBLE);
                    lin.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
