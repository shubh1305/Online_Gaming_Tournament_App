package com.example.pubgbattle;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.service.autofill.FieldClassification;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.MissingFormatArgumentException;
import java.util.Random;
import java.util.UUID;
import java.util.regex.MatchResult;

import am.appwise.components.ni.NoInternetDialog;

public class JoiningMatchActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private int NOTIFICATION_ID = 999;
    private static final String CHANNEL_ID = "default";
    private static final String CHANNEL_NAME = "InApp";
    private TextView tv_currentbalance, tv_entryfee, tv_msg, tv_join_dialog_error_msg;
    private String title, date, time, type, version, map, match_key, pubgUsername, name;
    private int entryfee, winning, perkill, currentbalance, matchplayed, occupiedSpot, totalspot;
    private Button btn_cancel, btn_buy, btn_next;
    private Button btn_join_dialog_cancel, btn_join_dialog_next;
    private EditText et_join_dialog_pubg_username;
    private AlertDialog join_dialog, join_dialog_done;
    private ProgressDialog progressDialogJoin;
    private NoInternetDialog noInternetDialog;
    private LinearLayout lin_buy, lin_next;
    private int count = 16;
    private int ref_coins;
    private String ref_uid;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joining_match);
        noInternetDialog = new NoInternetDialog.Builder(this)
                .setButtonColor(getResources().getColor(R.color.main))
                .build();

        CreateNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.rgb(13, 35, 71));
            window.setStatusBarColor(getResources().getColor(R.color.statusbar));
            //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_joining_match_toolbar);
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
        tv_currentbalance = findViewById(R.id.activity_joining_match_tv_currentbalance);
        tv_entryfee = findViewById(R.id.activity_joining_match_tv_entryfee);
        tv_msg = findViewById(R.id.activity_joining_match_msg);
        btn_cancel = findViewById(R.id.activity_joining_match_btn_cancel);
        btn_buy = findViewById(R.id.activity_joining_match_btn_buycoin);
        btn_next = findViewById(R.id.activity_joining_match_btn_nxt);
        lin_buy = findViewById(R.id.activity_joining_match_lin_buy);
        lin_next = findViewById(R.id.activity_joining_match_lin_next);

        progressDialogJoin = new ProgressDialog(this);
        progressDialogJoin.setMessage("Match Joining. Just a sec...");
        progressDialogJoin.setCancelable(false);
        progressDialogJoin.setCanceledOnTouchOutside(false);


        mAuth = FirebaseAuth.getInstance();
        //firebaseDatabase = FirebaseDatabase.getInstance();

        GetIntent();

        RetrieveUserData();
        Log.d(TAG, "onCreate: " + occupiedSpot);

        CheckIfFull();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateOccupiedSpot()){
                    DisplayJoinDialog();
                }
                else {
                    Toast.makeText(JoiningMatchActivity.this, "Match Full!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("InApp Notifications");
            channel.enableLights(true);
            channel.setVibrationPattern(new long[]{0, 100, 200, 300});
            channel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void CheckIfFull() {
        if(!validateOccupiedSpot()){
            btn_next.setEnabled(false);
            btn_next.setText("MATCH FULL");
            btn_next.setTextColor(Color.WHITE);
            btn_next.setBackgroundResource(R.drawable.my_button_cardview_matchfull);
        }
    }

    private void DisplayJoinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(JoiningMatchActivity.this);
        View view = getLayoutInflater().inflate(R.layout.join_dialog, null);

        btn_join_dialog_cancel = view.findViewById(R.id.join_dialog_btn_cancel);
        btn_join_dialog_next = view.findViewById(R.id.join_dialog_btn_join);
        et_join_dialog_pubg_username = view.findViewById(R.id.join_dialog_et_pubgusername);
        tv_join_dialog_error_msg = view.findViewById(R.id.join_dialog_tv_error_msg);


        btn_join_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join_dialog.dismiss();
            }
        });

        btn_join_dialog_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "occupied: "+occupiedSpot+"total: "+totalspot);
                if(et_join_dialog_pubg_username.getText().toString().trim().isEmpty()) {
                    // Log.d(TAG, "inside oncreate: "+ currentbalance);
                    tv_join_dialog_error_msg.setText("Please Enter Your PUBG Username");
                }else if(!et_join_dialog_pubg_username.getText().toString().trim().equals(pubgUsername)){
                    tv_join_dialog_error_msg.setText("PUBG Username Didn't Match. ");
                }else if(!validateOccupiedSpot()){
                    join_dialog.dismiss();
                    startActivity(new Intent(JoiningMatchActivity.this, MainActivity.class));
                    finish();
                    Toast.makeText(JoiningMatchActivity.this, "Match Full! Join other match.", Toast.LENGTH_SHORT).show();
                }
                else{
                    SendUserDataToMatches();
                    MainActivity.mainActivity.finish();
                }
            }
        });


        builder.setView(view);
        join_dialog = builder.create();
        join_dialog.show();
        join_dialog.setCancelable(false);
        join_dialog.setCanceledOnTouchOutside(false);
        join_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void FetchReferralData() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserProfile u = dataSnapshot.getValue(UserProfile.class);
                String promo_code = u.getPromoCode();
                int matchPlayed = u.getMatchPlayed();
                if(!promo_code.isEmpty()) {
                    UpdateReferralWallet(promo_code, matchPlayed);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UpdateReferralWallet(String promo_code, int matchplayed) {
        if (matchplayed == 1){
            DatabaseReference referringUser = FirebaseDatabase.getInstance().getReference("Users");
            referringUser.orderByChild("myReferCode").equalTo(promo_code).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot data: dataSnapshot.getChildren()) {
                        final UserProfile referringUser = data.getValue(UserProfile.class);
                        ref_coins = referringUser.getWalletCoins();
                        ref_uid = data.getKey();
                    }
                    //Update coin
                    HashMap<String, Object> referringUserHashmap = new HashMap<>();
                    referringUserHashmap.put("walletCoins", ref_coins+10);
                    referringUser.child(ref_uid).updateChildren(referringUserHashmap);

                    //update referral status
                    referringUser.child(ref_uid).child("myReferrals").child(mAuth.getUid()).child("status").setValue("Completed");

                    //Update transaction
                    DatabaseReference myReferTrans = FirebaseDatabase.getInstance().getReference("Users")
                            .child(ref_uid).child("transactions");
                    UpdateTransaction(myReferTrans);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void UpdateTransaction(DatabaseReference myReferTrans) {
        String format, transTime, transDate;
        String transId = generateString();
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH)+1;
        int year = c.get(Calendar.YEAR);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        long txnInt;
        String hourInt, minInt, dayInt, monthInt, txnStr;
        hourInt = String.valueOf(hour);
        minInt = String.valueOf(min);
        dayInt = String.valueOf(day);
        monthInt = String.valueOf(month+1);

        if(hour<10){
            hourInt = "0"+hourInt;
        }
        if(min<10){
            minInt = "0"+minInt;
        }
        if(day<10){
            dayInt = "0"+dayInt;
        }
        if (month<10){
            monthInt = "0"+monthInt;
        }
        txnStr = year+monthInt+dayInt+hourInt+minInt;
        txnInt = Long.parseLong(txnStr);
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        if(hour<10){
            if(min<10){
                transTime = "0"+hour+":"+"0"+min+" "+format;
            }
            else {
                transTime = "0"+hour+":"+min+" "+format;
            }
        }
        else {
            if(min<10){
                transTime = hour+":"+"0"+min+" "+format;
            }
            else {
                transTime = hour+":"+min+" "+format;
            }
        }
        if(day<10){
            if(month<10){
                transDate = "0"+day + "/" + "0"+month + "/" + year;
            }
            else {
                transDate = "0" + day + "/" + month + "/" + year;
            }
        }
        else {
            if(month<10){
                transDate = day + "/" + "0"+month + "/" + year;
            }
            else {
                transDate = day + "/" + month + "/" + year;
            }
        }

        final TransactionDetail transactionDetail = new TransactionDetail(transId,
                "CREDIT", "Referral bonus added", transDate, transTime, 10, txnInt);
        myReferTrans.child(transId).setValue(transactionDetail);
    }

    private boolean validateOccupiedSpot() {
        if(occupiedSpot == totalspot)
            return false;
        return true;
    }

    private void SendUserDataToMatches() {
        progressDialogJoin.show();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final DatabaseReference myRefMatch = firebaseDatabase.getReference("Matches").child(match_key)
                .child("participants").child(firebaseAuth.getUid());
        final DatabaseReference myRefUser = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid());
        final DatabaseReference myRefOccupiedSpot = firebaseDatabase.getReference("Matches")
                .child(match_key).child("matchOccupiedSpot");
        final DatabaseReference myRefTrans = myRefUser.child("transactions");
        final DatabaseReference myRefmatchTrans = firebaseDatabase.getReference("Matches").child(match_key);

        //Transaction
        //*****************************************************************************************************************************
        myRefOccupiedSpot.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                Integer currentValue = mutableData.getValue(Integer.class);
                if(currentValue!=null && currentValue<100){

                    mutableData.setValue(currentValue+1);

                    //Updating wallet coin and match played for User
                    final HashMap<String, Object> user = new HashMap<>();
                    user.put("walletCoins", currentbalance - entryfee);
                    user.put("matchPlayed", matchplayed+1);
                    myRefUser.updateChildren(user);

                    //Update Referral data
                    FetchReferralData();

                    //Send User Data to Match
                    myRefUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                            HashMap<String, Object> update = new HashMap<>();
                            update.put("totalKills", 0);
                            update.put("totalWinnings", 0);

                            myRefMatch.setValue(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                            /*Toast.makeText(JoiningMatchActivity.this, "Match Joined Successfully",
                                    Toast.LENGTH_SHORT).show();*/

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(JoiningMatchActivity.this, "Something Went wrong.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                            myRefMatch.updateChildren(update);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(JoiningMatchActivity.this, "Something went wrong inside myRefUser"
                                    , Toast.LENGTH_SHORT).show();
                            progressDialogJoin.dismiss();
                            join_dialog.dismiss();
                        }
                    });

                }
                else {
                    Toast.makeText(JoiningMatchActivity.this, "Match Full! Join other matches.", Toast.LENGTH_SHORT).show();
                    Transaction.abort();
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                Log.d("Result: ", "onComplete: "+b);
                //Log.d("Error: ", "onComplete: "+databaseError.getMessage());
                progressDialogJoin.dismiss();
                join_dialog.dismiss();
                DisplayJoinDoneDialog();
                ShowNotification();
            }
        });
        //**************************************************************************************************************************
        myRefmatchTrans.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final MatchDetail m = dataSnapshot.getValue(MatchDetail.class);
                String title = m.getMatchTitle();
                int amount = m.getMatchEntryFee();
                String transDate;
                String transTime;
                String format = "";
                String trimTitle = title.substring(title.lastIndexOf(" ")+1);
                StringBuilder builder = new StringBuilder();
                while (count-- != 0) {
                    int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
                    builder.append(ALPHA_NUMERIC_STRING.charAt(character));
                }
                String transId = generateString();
                Calendar c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH)+1;
                int year = c.get(Calendar.YEAR);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);
                long txnInt;
                String hourInt, minInt, dayInt, monthInt, txnStr;
                hourInt = String.valueOf(hour);
                minInt = String.valueOf(min);
                dayInt = String.valueOf(day);
                monthInt = String.valueOf(month+1);

                if(hour<10){
                    hourInt = "0"+hourInt;
                }
                if(min<10){
                    minInt = "0"+minInt;
                }
                if(day<10){
                    dayInt = "0"+dayInt;
                }
                if (month<10){
                    monthInt = "0"+monthInt;
                }
                txnStr = year+monthInt+dayInt+hourInt+minInt;
                txnInt = Long.parseLong(txnStr);
                if (hour == 0) {
                    hour += 12;
                    format = "AM";
                } else if (hour == 12) {
                    format = "PM";
                } else if (hour > 12) {
                    hour -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }
                if(hour<10){
                    if(min<10){
                        transTime = "0"+hour+":"+"0"+min+" "+format;
                    }
                    else {
                        transTime = "0"+hour+":"+min+" "+format;
                    }
                }
                else {
                    if(min<10){
                        transTime = hour+":"+"0"+min+" "+format;
                    }
                    else {
                        transTime = hour+":"+min+" "+format;
                    }
                }
                if(day<10){
                    if(month<10){
                        transDate = "0"+day + "/" + "0"+month + "/" + year;
                    }
                    else {
                        transDate = "0" + day + "/" + month + "/" + year;
                    }
                }
                else {
                    if(month<10){
                        transDate = day + "/" + "0"+month + "/" + year;
                    }
                    else {
                        transDate = day + "/" + month + "/" + year;
                    }
                }

                final TransactionDetail transactionDetail = new TransactionDetail(transId,
                        "DEBIT", "Match joined - "+trimTitle, transDate, transTime, amount, txnInt);
                myRefTrans.child(transId).setValue(transactionDetail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ShowNotification() {
        Random r = new Random();
        NOTIFICATION_ID = r.nextInt(1000000);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title+ " Successfully joined")
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setColor(getResources().getColor(R.color.statusbar))
                .setAutoCancel(true)
                .setContentText("You have successfully joined the match. Room ID and password will be shared before 15 minutes of match starting time.")
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.pubg))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        "You have successfully joined the match. Room ID and password will be shared before 15 minutes of match starting time."
                ))
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(
                        BitmapFactory.decodeResource(this.getResources(),
                                R.drawable.pubg)
                ).bigLargeIcon(null))
                .setPriority(NotificationCompat.PRIORITY_MAX);


        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    private void DisplayJoinDoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(JoiningMatchActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_join_done, null);
        Button btn_done = view.findViewById(R.id.dialog_join_done_btn_done);

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                join_dialog_done.dismiss();

                startActivity(new Intent(JoiningMatchActivity.this, MainActivity.class));
                finish();
            }
        });
        builder.setView(view);
        join_dialog_done = builder.create();
        if(!((Activity) JoiningMatchActivity.this).isFinishing())
        {
            //show dialog
            join_dialog_done.show();

        }
        join_dialog_done.setCancelable(false);
        join_dialog_done.setCanceledOnTouchOutside(false);
        join_dialog_done.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    private String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    private void GetIntent() {
        title = getIntent().getStringExtra("title");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");
        type = getIntent().getStringExtra("type");
        version = getIntent().getStringExtra("version");
        map = getIntent().getStringExtra("map");
        entryfee = getIntent().getIntExtra("entryfee",0);
        winning = getIntent().getIntExtra("winning",0);
        perkill = getIntent().getIntExtra("perkill",0);
        match_key = getIntent().getStringExtra("matchkey");
        totalspot = getIntent().getIntExtra("totalspot",0);
    }

    private void RetrieveUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference myRef1 = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid());
        DatabaseReference myRef2 = firebaseDatabase.getReference("Matches").child(match_key);
        myRef2.keepSynced(true);
        myRef1.keepSynced(true);
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final MatchDetail matchDetail = dataSnapshot.getValue(MatchDetail.class);
                occupiedSpot = matchDetail.getMatchOccupiedSpot();

                Log.d(TAG, "occupied :"+occupiedSpot+" total: "+totalspot);
                CheckIfFull();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                currentbalance = userProfile.getWalletCoins();
                matchplayed = userProfile.getMatchPlayed();
                pubgUsername = userProfile.getPubgUsername();
                name = userProfile.getName();
                tv_currentbalance.setText(""+ currentbalance);
                tv_entryfee.setText(""+ entryfee);
                if(currentbalance >= entryfee){
                    tv_msg.setText(R.string.joining_match_join);
                    tv_msg.setTextColor(getResources().getColor(R.color.green));
                    lin_buy.setVisibility(View.INVISIBLE);
                    lin_next.setVisibility(View.VISIBLE);
                    btn_buy.setEnabled(false);
                    btn_cancel.setEnabled(false);
                    btn_next.setEnabled(true);
                }
                else {
                    tv_msg.setText(R.string.joining_match_no_join);
                    tv_msg.setTextColor(getResources().getColor(R.color.googleRed));
                    lin_next.setVisibility(View.INVISIBLE);
                    lin_buy.setVisibility(View.VISIBLE);
                    btn_buy.setEnabled(true);
                    btn_cancel.setEnabled(true);
                    btn_next.setEnabled(false);

                }
                //Log.d(TAG, "inside myref: "+ currentbalance);
                /*tv_name.setText(userProfile.getName());
                tv_userName.setText(userProfile.getPubgUsername());
                tv_playCoins.setText(""+ userProfile.getWalletCoins());
                tv_matchPlayed.setText(""+ userProfile.getMatchPlayed());
                tv_totalKills.setText(""+ userProfile.getTotalKills());
                tv_totalWinnings.setText(""+ userProfile.getTotalWinnings());*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(JoiningMatchActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }

}
