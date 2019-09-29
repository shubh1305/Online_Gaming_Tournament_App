package com.example.pubgbattle;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import am.appwise.components.ni.NoInternetDialog;

public class RedeemActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NoInternetDialog noInternetDialog;
    private Button btn;
    private RecyclerView rv_redeem;
    private ArrayList<Coin> redeem_coins;
    private RedeemAdapter adapter;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
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
        toolbar = findViewById(R.id.activity_redeem_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(getResources().getColor(R.color.main));
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("just a sec...");
        progressDialog.show();

        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("RedeemCoin");
        //initialising recyclerview
        rv_redeem = findViewById(R.id.activity_redeem_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        rv_redeem.setLayoutManager(gridLayoutManager);
        //initializing arraylist
        redeem_coins = new ArrayList<>();
        //initializing adapter
        adapter = new RedeemAdapter(RedeemActivity.this, redeem_coins);
        //setting adapter into recyclerview
        rv_redeem.setAdapter(adapter);
        SetUpRecyclerView(myRef);
    }

    private void SetUpRecyclerView(DatabaseReference myRef) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                redeem_coins.clear();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    final Coin coin = d.getValue(Coin.class);
                    redeem_coins.add(coin);
                }
                //Sorting
                Collections.sort(redeem_coins, new Comparator<Coin>(){
                    public int compare(Coin obj1, Coin obj2) {
                        // ## Ascending order
                        //return obj1.getCompanyName().compareToIgnoreCase(obj2.getCompanyName); // To compare string values
                        return Integer.valueOf(obj1.getValue()).compareTo(obj2.getValue()); // To compare integer values

                        // ## Descending order
                        //return obj2.getTotalWinnings().compareToIgnoreCase(obj1.getCompanyName()); // To compare string values
                        //return Integer.valueOf(obj2.getValue()).compareTo(obj1.getValue()); // To compare integer values
                    }
                });
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RedeemActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
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
