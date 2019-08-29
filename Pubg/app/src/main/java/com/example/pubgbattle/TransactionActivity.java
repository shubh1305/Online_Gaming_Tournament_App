package com.example.pubgbattle;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import am.appwise.components.ni.NoInternetDialog;

public class TransactionActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private NoInternetDialog noInternetDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private RecyclerView rv_transaction;
    private ArrayList<TransactionDetail> transactionDetails;
    private TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
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
        toolbar = findViewById(R.id.activity_transaction_toolbar);
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

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users")
                .child(firebaseAuth.getUid()).child("transactions");
        rv_transaction = findViewById(R.id.activity_transaction_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_transaction.setLayoutManager(linearLayoutManager);
        transactionDetails = new ArrayList<TransactionDetail>();
        transactionAdapter = new TransactionAdapter(this, transactionDetails);
        rv_transaction.setAdapter(transactionAdapter);
        AddToAdapter(databaseReference);
    }

    private void AddToAdapter(DatabaseReference databaseReference) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionDetails.clear();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    final TransactionDetail transDetail = d.getValue(TransactionDetail.class);
                    transactionDetails.add(transDetail);
                }
                Collections.sort(transactionDetails, new Comparator<TransactionDetail>(){
                    public int compare(TransactionDetail obj1, TransactionDetail obj2) {
                        // ## Ascending order
                        //return obj1.getCompanyName().compareToIgnoreCase(obj2.getCompanyName); // To compare string values
                        // return Integer.valueOf(obj1.getId()).compareTo(obj2.getId()); // To compare integer values

                        // ## Descending order
                        //return obj2.getTotalWinnings().compareToIgnoreCase(obj1.getCompanyName()); // To compare string values
                        return Long.valueOf(obj2.getTransactionDateInt()).compareTo(obj1.getTransactionDateInt()); // To compare integer values
                    }
                });
                transactionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
