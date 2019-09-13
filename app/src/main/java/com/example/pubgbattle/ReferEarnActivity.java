package com.example.pubgbattle;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReferEarnActivity extends AppCompatActivity {
    private TextView tv_my_earnings, tv_referral_code;
    private ImageView iv_back;
    private Button btn_copy, btn_refer, btn_term;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_earn);
        Init();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
        GetReferCode(myRef);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Source Text", tv_referral_code.getText().toString().trim());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(ReferEarnActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        btn_term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReferEarnActivity.this, "Term and Condition", Toast.LENGTH_SHORT).show();
            }
        });

        btn_refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Playadda247");
                    String shareMessage= "Addicted to PUBG! Want to make some cash out of it? Try out PlayAdda247, an eSports Platform. ";
                    shareMessage = shareMessage + "Join daily PUBG matches & get rewards on each kill you score." ;
                    shareMessage = shareMessage+ "Get huge prize on getting Chicken Dinner. Just download the PlayAdda247 android app & register using the Referral Code given below to get Rs 5 free sign up bonus.\n";
                    shareMessage = shareMessage + "\nDownload Link: https://playadda247.com/download"+"\n\nReferral Code: "+tv_referral_code.getText().toString().trim();
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Share using"));
                }
                catch(Exception e) {
                    //e.toString();
                }
            }
        });

        tv_my_earnings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReferEarnActivity.this, MyReferralsActivity.class));
            }
        });
    }

    private void GetReferCode(DatabaseReference myRef) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserProfile user = dataSnapshot.getValue(UserProfile.class);
                tv_referral_code.setText(user.getMyReferCode());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Init() {
        tv_my_earnings = findViewById(R.id.activity_refer_earn_my_earning);
        tv_referral_code = findViewById(R.id.activity_refer_earn_referral_code);
        iv_back = findViewById(R.id.activity_refer_earn_back);
        btn_copy = findViewById(R.id.activity_refer_earn_taptocopy);
        btn_refer = findViewById(R.id.activity_refer_earn_refer_btn);
        btn_term = findViewById(R.id.activity_refer_earn_term);
        mAuth = FirebaseAuth.getInstance();
    }


}
