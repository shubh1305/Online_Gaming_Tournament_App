package com.example.pubgbattle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "FirebaseInstanceID";
    public static Activity mainActivity;
    private String version;

    private TextView tv_wallet;

    private FirebaseAuth mAuth;

    private EarnFragment earnFragment;
    private OngoingFragment ongoingFragment;
    private PlayFragment playFragment;
    private ResultFragment resultFragment;
    private AccountFragment accountFragment;
    private Toolbar toolbar;
    private CardView cardView;
    private ConnectivityReceiver connectivityReceiver;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //private CurvedBottomNavigationView curvedBottomNavigationView;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        connectivityReceiver = new ConnectivityReceiver();

        try {
            version = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Version").child("versionName");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String versionName = (String) dataSnapshot.getValue();

                if(!versionName.equals(version)){
                    //Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();

                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("New Version Available")
                            .setMessage("Please update to new version to continue use")
                            .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(MainActivity.this, UpdateAppActivity.class));
                                    finish();
                                }
                            })
                            .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create();

                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);

                    alertDialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        mAuth = FirebaseAuth.getInstance();
        tv_wallet = findViewById(R.id.activity_main_tv_wallet);

        //check if user is logged in
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser==null){
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        //Init Fragments
        earnFragment = new EarnFragment();
        ongoingFragment = new OngoingFragment();
        playFragment = new PlayFragment();
        resultFragment = new ResultFragment();
        accountFragment = new AccountFragment();

        cardView = findViewById(R.id.activity_main_cardview);

        //Views
        bottomNavigationView = findViewById(R.id.bottom_nav);

        //bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
        bottomNavigationView.setSelectedItemId(R.id.menu_play);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WalletActivity.class));
            }
        });


        SetFragment(playFragment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.rgb(13, 35, 71));
            window.setStatusBarColor(getResources().getColor(R.color.statusbar));
            //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        DatabaseReference myrefUser = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
        myrefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                tv_wallet.setText(""+ userProfile.getWalletCoins());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public  void SetFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.menu_earn:
                SetFragment(earnFragment);
                getSupportActionBar().show();
                break;
            case R.id.menu_ongoing:
                SetFragment(ongoingFragment);
                getSupportActionBar().show();
                break;
            case R.id.menu_play:
                SetFragment(playFragment);
                getSupportActionBar().show();
                break;
            case R.id.menu_result:
                SetFragment(resultFragment);
                getSupportActionBar().show();
                break;
            case R.id.menu_me:
                SetFragment(accountFragment);
                getSupportActionBar().hide();
                break;
        }
        return true;
    }


    /*// Showing the status in Snackbar
    private void showToast(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        } else {
            message = "Sorry! Not connected to internet";
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }*/

    /*// Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showToast(isConnected);
    }*/

    /*@Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectivityReceiver, filter);
    }*/

    /*@Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(connectivityReceiver);
    }
    *//**
     * Callback will be triggered when there is change in
     * network connection
     *//*
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showToast(isConnected);
    }*/
}
