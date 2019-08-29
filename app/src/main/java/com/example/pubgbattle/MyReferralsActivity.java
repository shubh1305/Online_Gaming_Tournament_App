package com.example.pubgbattle;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MyReferralsActivity extends AppCompatActivity {

    private TextView tv_referral_count, tv_total_earning;
    private int ref_count = 0, ref_earning = 0;
    private Toolbar toolbar;
    private ArrayList<ReferralUser> referralUsers;
    private ReferralAdapter referralAdapter;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_referrals);

        toolbar = findViewById(R.id.activity_my_referral_toolbar);
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
        firebaseAuth = FirebaseAuth.getInstance();

        tv_referral_count = findViewById(R.id.activity_my_referral_count);
        tv_total_earning = findViewById(R.id.activity_my_referral_earnings);
        recyclerView = findViewById(R.id.activity_my_referral_rv);
        referralUsers = new ArrayList<ReferralUser>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        referralAdapter = new ReferralAdapter(MyReferralsActivity.this, referralUsers);
        recyclerView.setAdapter(referralAdapter);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseAuth.getUid()).child("myReferrals");

        FetchDetails(myRef);

    }

    private void FetchDetails(DatabaseReference myRef) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                referralUsers.clear();
                ref_earning = 0;
                ref_count = 0;
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    final ReferralUser ref = d.getValue(ReferralUser.class);
                    referralUsers.add(ref);
                    ref_count++;
                    final String str = d.child("status").getValue().toString();
                    if(str.equals("Completed")){
                        ref_earning++;
                    }

                }
                tv_referral_count.setText(""+ ref_count);
                tv_total_earning.setText(""+ (ref_earning*10));
                //Sorting
                Collections.sort(referralUsers, new Comparator<ReferralUser>(){
                    public int compare(ReferralUser obj1, ReferralUser obj2) {
                        // ## Ascending order
                        //return obj1.getCompanyName().compareToIgnoreCase(obj2.getCompanyName); // To compare string values
                        // return Integer.valueOf(obj1.getId()).compareTo(obj2.getId()); // To compare integer values

                        // ## Descending order
                        //return obj2.getTotalWinnings().compareToIgnoreCase(obj1.getCompanyName()); // To compare string values
                        return Integer.valueOf(obj2.getId()).compareTo(obj1.getId()); // To compare integer values
                    }
                });
                //referralAdapter = new ReferralAdapter(MyReferralsActivity.this, referralUsers);
                //recyclerView.setAdapter(referralAdapter);
                referralAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
