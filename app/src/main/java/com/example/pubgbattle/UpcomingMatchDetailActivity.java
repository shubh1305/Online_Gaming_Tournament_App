package com.example.pubgbattle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Set;

import am.appwise.components.ni.NoInternetDialog;
import kotlin.UseExperimental;


import static android.support.constraint.Constraints.TAG;

public class UpcomingMatchDetailActivity extends AppCompatActivity {

    private TextView tv_title, tv_date, tv_time, tv_type, tv_version, tv_entryfee, tv_map, tv_winning, tv_perkill;
    private String title, date, time, type, version, map, matchkey;
    int entryfee, winning, perkill, occupiedspot, totalspot;
    private Button btn_join, btn_load, btn_refresh;
    private FirebaseAuth firebaseAuth;
    private RecyclerView rvParticipant;
    private NestedScrollView nsv;
    private ProgressDialog progressDialog;
    private LinearLayout lin_participant;
    private ArrayList<UserProfile> list_rv;
    private NoInternetDialog noInternetDialog;
    private ImageView iv_upcoming;

    private ParticipantAdapter adapter_rv;
    private int visibleItemCount, totalItemCount, pastVisiblesItems;
    private String pic;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_match_detail);
        noInternetDialog = new NoInternetDialog.Builder(this)
                .setButtonColor(getResources().getColor(R.color.main))
                .build();
        firebaseAuth = FirebaseAuth.getInstance();
        Init();

        GetIntent();
        rvParticipant = findViewById(R.id.activity_upcoming_match_detail_rv);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvParticipant.setLayoutManager(linearLayoutManager);
        //rvParticipant.setHasFixedSize(true);
        list_rv = new ArrayList<UserProfile>();

        //SetText();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.rgb(13, 35, 71));
            window.setStatusBarColor(getResources().getColor(R.color.statusbar));
            //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = findViewById(R.id.activity_upcoming_match_detail_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        //toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.activity_upcoming_match_detail_collapsing_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.activity_upcoming_match_detail_appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Upcoming Match Detail");
                    collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
        btn_refresh.setVisibility(View.INVISIBLE);
        /*CheckIfJoined();
        CheckIfFull();*/
        lin_participant = findViewById(R.id.lin_participant);
        lin_participant.setVisibility(View.INVISIBLE);
        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Matches").child(matchkey)
                        .child("participants");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot d: dataSnapshot.getChildren()){
                            final UserProfile participant = d.getValue(UserProfile.class);
                            list_rv.add(participant);
                        }
                        lin_participant.setVisibility(View.VISIBLE);
                        adapter_rv = new ParticipantAdapter(UpcomingMatchDetailActivity.this, list_rv);
                        rvParticipant.setAdapter(adapter_rv);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(UpcomingMatchDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
                btn_refresh.setVisibility(View.VISIBLE);
                btn_load.setVisibility(View.GONE);
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                list_rv.clear();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Matches").child(matchkey)
                        .child("participants");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot d: dataSnapshot.getChildren()){
                            final UserProfile participant = d.getValue(UserProfile.class);
                            list_rv.add(participant);
                        }
                        adapter_rv = new ParticipantAdapter(UpcomingMatchDetailActivity.this, list_rv);
                        rvParticipant.setAdapter(adapter_rv);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(UpcomingMatchDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpcomingMatchDetailActivity.this, JoiningMatchActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("type", type);
                intent.putExtra("version", version);
                intent.putExtra("map", map);
                intent.putExtra("entryfee", entryfee);
                intent.putExtra("winning", winning);
                intent.putExtra("perkill", perkill);
                intent.putExtra("matchkey", matchkey);
                intent.putExtra("totalspot", totalspot);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        GetIntent();
    }

    private void CheckIfFull() {
        if(occupiedspot == totalspot){
            btn_join.setEnabled(false);
            btn_join.setText("MATCH FULL");
            btn_join.setTextColor(Color.WHITE);
            btn_join.setBackgroundColor(getResources().getColor(R.color.gray));
        }
        else {
            btn_join.setEnabled(true);
            btn_join.setText("JOIN NOW");
            btn_join.setTextColor(Color.WHITE);
            btn_join.setBackgroundColor(getResources().getColor(R.color.playzon_blue_detail));
        }
    }

    private void CheckIfJoined() {
        final DatabaseReference myRefParticipants = FirebaseDatabase.getInstance().getReference("Matches")
                .child(matchkey).child("participants");
        myRefParticipants.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d1: dataSnapshot.getChildren()){
                    String str = d1.getKey().toString();
                    Log.d(TAG, "On data change Uid: "+ str);
                    if(str.equals(firebaseAuth.getUid())){
                        Log.d(TAG, "onDataChange: Equal");
                        btn_join.setEnabled(false);
                        btn_join.setText("MATCH JOINED");
                        btn_join.setTextColor(Color.WHITE);
                        btn_join.setBackgroundColor(getResources().getColor(R.color.gray));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void SetText() {
        tv_title.setText(title);
        tv_date.setText(date);
        tv_time.setText(time);
        tv_type.setText(type);
        tv_version.setText(version);
        tv_entryfee.setText(""+ entryfee);
        tv_map.setText(map);
        tv_winning.setText(""+ winning);
        tv_perkill.setText(""+ perkill);
    }

    private void GetIntent() {
        /*title = getIntent().getStringExtra("title");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        type = getIntent().getStringExtra("type");
        version = getIntent().getStringExtra("version");
        map = getIntent().getStringExtra("map");
        entryfee = getIntent().getIntExtra("entryfee", 0);
        winning = getIntent().getIntExtra("winning",0);
        perkill = getIntent().getIntExtra("perkill",0);
        occupiedspot = getIntent().getIntExtra("occupiedspot",0);
        totalspot = getIntent().getIntExtra("totalspot",0);*/
        matchkey = getIntent().getStringExtra("matchkey");
        final DatabaseReference myref = FirebaseDatabase.getInstance().getReference("Matches").child(matchkey);
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final MatchDetail m = dataSnapshot.getValue(MatchDetail.class);
                title = m.getMatchTitle();
                date = m.getMatchDate();
                time = m.getMatchTime();
                type = m.getMatchType();
                version = m.getMatchVersion();
                map = m.getMatchMap();
                entryfee = m.getMatchEntryFee();
                winning = m.getMatchWinningPrize();
                perkill = m.getMatchPerKill();
                occupiedspot = m.getMatchOccupiedSpot();
                totalspot = m.getMatchTotalSpot();
                pic = m.getMatchPic();
                SetPic(pic);
                SetText();
                CheckIfJoined();
                CheckIfFull();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SetPic(String pic) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Picasso.get().load(pic).networkPolicy(NetworkPolicy.OFFLINE).into(iv_upcoming
                    , new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(pic).into(iv_upcoming, new Callback() {
                                @Override
                                public void onSuccess() {
                                    progressBar.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Picasso.get().load(R.drawable.pubg).into(iv_upcoming);
                                }
                            });
                        }
                    });
        }
        catch (Exception e){
            //if there is any exception while getting image then set default
            Picasso.get().load(R.drawable.pubg).into(iv_upcoming);
            progressBar.setVisibility(View.INVISIBLE);

        }
    }


    private void Init() {
        tv_title = findViewById(R.id.activity_upcoming_match_detail_tv_title);
        tv_date = findViewById(R.id.activity_upcoming_match_detail_tv_date);
        tv_time = findViewById(R.id.activity_upcoming_match_detail_tv_time);
        tv_type = findViewById(R.id.activity_upcoming_match_detail_tv_type);
        tv_version = findViewById(R.id.activity_upcoming_match_detail_tv_version);
        tv_map = findViewById(R.id.activity_upcoming_match_detail_tv_map);
        tv_winning = findViewById(R.id.activity_upcoming_match_detail_tv_winningprize);
        tv_entryfee = findViewById(R.id.activity_upcoming_match_detail_tv_entryfee);
        tv_perkill = findViewById(R.id.activity_upcoming_match_detail_tv_perkill);
        btn_join = findViewById(R.id.activity_upcoming_match_detail_btn_join);
        btn_load = findViewById(R.id.activity_upcoming_match_detail_btn_load);
        btn_refresh = findViewById(R.id.activity_upcoming_match_detail_btn_refresh);
        nsv = findViewById(R.id.activity_upcoming_match_detail_nsv);
        iv_upcoming = findViewById(R.id.activity_upcoming_match_detail_img);
        progressBar = findViewById(R.id.activity_upcoming_match_detail_progress);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Just a sec...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);


    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}
