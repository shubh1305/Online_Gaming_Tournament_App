package com.example.pubgbattle;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.circulardialog.CDialog;
import com.example.circulardialog.extras.CDConstants;
import com.example.pubgbattle.paytm.Api;
import com.example.pubgbattle.paytm.Checksum;
import com.example.pubgbattle.paytm.Constants;
import com.example.pubgbattle.paytm.Paytm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends AppCompatActivity  implements PaytmPaymentTransactionCallback {

    private int amount, old_amt;
    private String transId;
    private FirebaseAuth mauth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRefWallet, myRefTxn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mauth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        amount = getIntent().getIntExtra("coin",0);
        generateCheckSum();
    }

    private void generateCheckSum() {
        //getting the tax amount first.
        String txnAmount = String.valueOf(amount);

        //creating a retrofit object.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //creating the retrofit api service
        Api apiService = retrofit.create(Api.class);

        //creating paytm object
        //containing all the values required
        final Paytm paytm = new Paytm(
                Constants.M_ID,
                Constants.CHANNEL_ID,
                txnAmount,
                Constants.WEBSITE,
                Constants.CALLBACK_URL,
                Constants.INDUSTRY_TYPE_ID
        );

        transId = paytm.getOrderId();

        //creating a call object from the apiService
        Call<Checksum> call = apiService.getChecksum(
                paytm.getmId(),
                transId,
                paytm.getCustId(),
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite(),
                paytm.getCallBackUrl(),
                paytm.getIndustryTypeId()
        );

        //making the call to generate checksum
        call.enqueue(new Callback<Checksum>() {
            @Override
            public void onResponse(Call<Checksum> call, Response<Checksum> response) {

                //once we get the checksum we will initiailize the payment.
                //the method is taking the checksum we got and the paytm object as the parameter
                initializePaytmPayment(response.body().getChecksumHash(), paytm);
            }

            @Override
            public void onFailure(Call<Checksum> call, Throwable t) {

            }
        });
    }

    private void initializePaytmPayment(String checksumHash, Paytm paytm) {
        //getting paytm service
        //PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for production
        PaytmPGService Service = PaytmPGService.getProductionService();

        //creating a hashmap and adding all the values required
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID", Constants.M_ID);
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("CHANNEL_ID", paytm.getChannelId());
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("CALLBACK_URL", paytm.getCallBackUrl());
        paramMap.put("CHECKSUMHASH", checksumHash);
        paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());


        //creating a paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder((HashMap<String, String>) paramMap);

        //intializing the paytm service
        Service.initialize(order, null);

        //finally starting the payment transaction
        Service.startPaymentTransaction(this, true, true,
                this);
    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {
        //Toast.makeText(this, inResponse.toString(), Toast.LENGTH_LONG).show();
        //Log.d("response", "onTransactionResponse: " +inResponse.toString());
        String status = inResponse.getString("STATUS");
        if(status.equals("TXN_SUCCESS")){
            new CDialog(this).createAlert("Success",
                    CDConstants.SUCCESS,   // Type of dialog
                    CDConstants.LARGE)    //  size of dialog
                    .setAnimation(CDConstants.SCALE_FROM_BOTTOM_TO_TOP)     //  Animation for enter/exit
                    .setDuration(2000)   // in milliseconds
                    .setTextSize(CDConstants.NORMAL_TEXT_SIZE)  // CDConstants.LARGE_TEXT_SIZE, CDConstants.NORMAL_TEXT_SIZE
                    .show();
            myRefWallet = firebaseDatabase.getReference("Users").child(mauth.getUid());
            myRefTxn = myRefWallet.child("transactions");
            UpdateUserWallet(myRefWallet, amount);
            UpdateTransaction(myRefTxn, amount);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 4000);
            //finish();
        }
        else {
            new CDialog(this).createAlert("Failed",
                    CDConstants.ERROR,   // Type of dialog
                    CDConstants.LARGE)    //  size of dialog
                    .setAnimation(CDConstants.SCALE_FROM_BOTTOM_TO_TOP)     //  Animation for enter/exit
                    .setDuration(2000)   // in milliseconds
                    .setTextSize(CDConstants.LARGE_TEXT_SIZE)  // CDConstants.LARGE_TEXT_SIZE, CDConstants.NORMAL_TEXT_SIZE
                    .show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 4000);
            //finish();
        }

    }

    private void UpdateTransaction(DatabaseReference myRefTxn, int amt) {
        String transDate;
        String transTime;
        String format = "";
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
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
                transDate = "0"+day + "/" + "0"+(month+1) + "/" + year;
            }
            else {
                transDate = "0" + day + "/" + (month + 1) + "/" + year;
            }
        }
        else {
            if(month<10){
                transDate = day + "/" + "0"+(month+1) + "/" + year;
            }
            else {
                transDate = day + "/" + (month + 1) + "/" + year;
            }
        }
        String title = "Purchased "+amt+" PlayCoins "+"at Rs "+amt;
        final TransactionDetail transactionDetail = new TransactionDetail(transId,
                "CREDIT", title, transDate, transTime, amt, txnInt);
        myRefTxn.child(transId).setValue(transactionDetail);
    }

    private void UpdateUserWallet(DatabaseReference myRefWallet, int amt) {
        myRefWallet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserProfile user = dataSnapshot.getValue(UserProfile.class);
                old_amt = user.getWalletCoins();
                int new_amt = old_amt + amt;
                myRefWallet.child("walletCoins").setValue(new_amt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        int new_amt = old_amt + amt;
        myRefWallet.child("walletCoins").setValue(new_amt);
    }


    @Override
    public void networkNotAvailable() {
        Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        Toast.makeText(this, inErrorMessage, Toast.LENGTH_LONG).show();
        Log.d("AuthenticationFailed", "clientAuthenticationFailed: "+inErrorMessage);
    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        Toast.makeText(this, inErrorMessage, Toast.LENGTH_LONG).show();
        Log.d("UIError", "someUIErrorOccurred: "+inErrorMessage);
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        Toast.makeText(this, inErrorMessage, Toast.LENGTH_LONG).show();
        Log.d("LoadingWebPage", "onErrorLoadingWebPage: "+inErrorMessage);
    }

    @Override
    public void onBackPressedCancelTransaction() {
        finish();
        Toast.makeText(this, "Back Pressed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        Toast.makeText(this, inErrorMessage + inResponse.toString(), Toast.LENGTH_LONG).show();
        Log.d("cancel", "onTransactionCancel: "+inErrorMessage);
    }
}
