package com.example.pubgbattle;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import am.appwise.components.ni.NoInternetDialog;

public class MatchResultActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rv_full_result, rv_winner;
    private ArrayList<UserProfile> userDetail, userDetailWinner;
    private MatchResultLayoutAdapter adapter, adapter2;
    private FirebaseAuth firebaseAuth;
    private String matchkey;
    private ProgressDialog progressDialog;
    private TextView tv_title, tv_date, tv_time, tv_winprize, tv_perkill, tv_entryfee;
    private NoInternetDialog noInternetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_result);
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
        Init();
        GetIntent();
        DatabaseReference myRefMatch = FirebaseDatabase.getInstance().getReference("Matches")
                .child(matchkey);
        FetchData(myRefMatch);
        toolbar = findViewById(R.id.activity_match_result_toolbar);
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

        rv_full_result.setLayoutManager(new LinearLayoutManager(this));
        rv_winner.setLayoutManager(new LinearLayoutManager(this));

    }

    private void FetchData(DatabaseReference myRefMatch) {
        myRefMatch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final MatchDetail matchDetail = dataSnapshot.getValue(MatchDetail.class);
                tv_title.setText(matchDetail.getMatchTitle());
                tv_date.setText(matchDetail.getMatchDate());
                tv_time.setText(matchDetail.getMatchTime());
                tv_winprize.setText(""+ matchDetail.getMatchWinningPrize());
                tv_perkill.setText(""+ matchDetail.getMatchPerKill());
                tv_entryfee.setText(""+ matchDetail.getMatchEntryFee());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void GetIntent() {
        progressDialog.show();
        matchkey = getIntent().getStringExtra("matchkey");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Matches").child(matchkey)
                .child("participants");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userDetail.clear();
                userDetailWinner.clear();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    final UserProfile participant = d.getValue(UserProfile.class);
                    userDetail.add(participant);
                    final String str = d.child("winner").getValue().toString();
                    if(str.equals("y")){
                        userDetailWinner.add(participant);
                    }
                }
                Log.d("XXXXXXXXXXXXXXXXXXXXXXX", "onDataChange: "+userDetail);
                //Sorting
                Collections.sort(userDetail, new Comparator<UserProfile>(){
                    public int compare(UserProfile obj1, UserProfile obj2) {
                        // ## Ascending order
                        //return obj1.getCompanyName().compareToIgnoreCase(obj2.getCompanyName); // To compare string values
                        // return Integer.valueOf(obj1.getId()).compareTo(obj2.getId()); // To compare integer values

                        // ## Descending order
                        //return obj2.getTotalWinnings().compareToIgnoreCase(obj1.getCompanyName()); // To compare string values
                        return Integer.valueOf(obj2.getTotalWinnings()).compareTo(obj1.getTotalWinnings()); // To compare integer values
                    }
                });
                adapter = new MatchResultLayoutAdapter(MatchResultActivity.this, userDetail);
                rv_full_result.setAdapter(adapter);

                //Sorting
                Collections.sort(userDetailWinner, new Comparator<UserProfile>(){
                    public int compare(UserProfile obj1, UserProfile obj2) {
                        // ## Ascending order
                        //return obj1.getCompanyName().compareToIgnoreCase(obj2.getCompanyName); // To compare string values
                        // return Integer.valueOf(obj1.getId()).compareTo(obj2.getId()); // To compare integer values

                        // ## Descending order
                        //return obj2.getTotalWinnings().compareToIgnoreCase(obj1.getCompanyName()); // To compare string values
                        return Integer.valueOf(obj2.getTotalWinnings()).compareTo(obj1.getTotalWinnings()); // To compare integer values
                    }
                });
                adapter2 = new MatchResultLayoutAdapter(MatchResultActivity.this, userDetailWinner);
                rv_winner.setAdapter(adapter2);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(MatchResultActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.match_result_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_watch){

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Init() {
        toolbar = findViewById(R.id.activity_match_result_toolbar);
        rv_full_result = findViewById(R.id.activity_match_result_full_result_rv);
        rv_winner = findViewById(R.id.activity_match_result_winner_rv);
        userDetail = new ArrayList<UserProfile>();
        userDetailWinner = new ArrayList<UserProfile>();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Just a sec...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        tv_title = findViewById(R.id.activity_match_result_title);
        tv_date = findViewById(R.id.activity_match_result_date);
        tv_time = findViewById(R.id.activity_match_result_time);
        tv_winprize = findViewById(R.id.activity_match_result_winprize);
        tv_perkill = findViewById(R.id.activity_match_result_perkill);
        tv_entryfee = findViewById(R.id.activity_match_result_entryfee);

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


