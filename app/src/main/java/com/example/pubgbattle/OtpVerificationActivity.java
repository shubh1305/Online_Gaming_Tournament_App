package com.example.pubgbattle;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OtpTextView;

public class OtpVerificationActivity extends AppCompatActivity {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private EditText et_code, et_number;
    private TextView tv_number;
    private OtpTextView otp;
    private Button btn_send_code, btn_verify, btn_done, btn_manual;
    private LinearLayout lin_send_code, lin_verify, lin_done;
    private AVLoadingIndicatorView progressBar;
    private String uid, name, pubgusername, email, mobile, password, promo, myReferralCode, mVerificationId;
    private String refer_key;
    private FirebaseAuth firebaseAuth;
    private String f_mobile;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        Init();

        GetIntent();

        btn_send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                btn_send_code.setText("");
                btn_send_code.setEnabled(false);
                flag = 0;
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            final UserProfile userProfile = data.getValue(UserProfile.class);
                            f_mobile = userProfile.getMobileNo();
                            if(f_mobile.equals(et_number.getText().toString())){
                                flag=1;
                                break;
                            }
                        }
                        if(flag==1){
                            et_number.setError("Mobile Number Already Exists!");
                            requestFocus(et_number);
                            progressBar.setVisibility(View.INVISIBLE);
                            btn_send_code.setText("Get The Code");
                            btn_send_code.setEnabled(true);
                        }
                        else if (et_number.getText().toString().isEmpty() || et_number.getText().toString().length()!=10 || et_number.getText().toString().contains(" ")){
                            progressBar.setVisibility(View.INVISIBLE);
                            btn_send_code.setText("Get The Code");
                            btn_send_code.setEnabled(true);
                            et_number.setError("Please Enter a Valid Number");
                        }
                        else {
                            et_number.setError(null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mobile = et_number.getText().toString();
                                    tv_number.setText("+91 "+mobile);
                                    lin_send_code.setVisibility(View.GONE);
                                    btn_send_code.setVisibility(View.GONE);
                                    lin_verify.setVisibility(View.VISIBLE);
                                    btn_verify.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    sendVerificationCode(mobile);
                                }
                            }, 3000);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btn_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.INVISIBLE);
                btn_verify.setText("Verify The code");
                btn_verify.setEnabled(true);
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_verify.setText("");
                btn_verify.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                String code = otp.getOTP();
                verifyVerificationCode(code);
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_done.setText("");
                btn_done.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(OtpVerificationActivity.this, MainActivity.class));
                        finish();
                    }
                }, 3000);

            }
        });

    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            btn_verify.setText("");
            btn_verify.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                otp.setOTP(code);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //verifying the code
                        verifyVerificationCode(code);
                    }
                }, 3000);

            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            btn_verify.setText("Verify The Code");
            btn_verify.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(OtpVerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {

        DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("Users");
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        if(!promo.isEmpty()){
            refUser.orderByChild("myReferCode").equalTo(promo)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                refer_key = data.getKey();
                                signInWithPhoneAuthCredential(credential, email, password, refer_key);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        else {
            signInWithPhoneAuthCredential(credential, email, password);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, String email, String password, String refer_key) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            //Send user data to firebase database
                            SendUserData(5);
                            SendReferralData(refer_key);
                            DatabaseReference myRefTrans = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(firebaseAuth.getUid()).child("transactions");
                            UpdateTransaction(myRefTrans);

                            AuthCredential credentialEmail = EmailAuthProvider.getCredential(email, password);
                            firebaseAuth.getCurrentUser().linkWithCredential(credentialEmail);
                            otp.showSuccess();
                            btn_verify.setVisibility(View.GONE);
                            lin_verify.setVisibility(View.GONE);
                            progressBar.setVisibility(View.INVISIBLE);
                            lin_done.setVisibility(View.VISIBLE);
                            btn_done.setVisibility(View.VISIBLE);
                        } else {

                            //verification unsuccessful.. display an error message
                            otp.showError();
                            btn_verify.setText("Verify The Code");
                            btn_verify.setEnabled(true);
                            progressBar.setVisibility(View.INVISIBLE);
                            DynamicToast.makeWarning(OtpVerificationActivity.this, "OTP Is Incorrect.", 3000).show();
                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, String email, String password) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            SendUserData(0);
                            AuthCredential credentialEmail = EmailAuthProvider.getCredential(email, password);
                            firebaseAuth.getCurrentUser().linkWithCredential(credentialEmail);
                            otp.showSuccess();
                            btn_verify.setVisibility(View.GONE);
                            lin_verify.setVisibility(View.GONE);
                            progressBar.setVisibility(View.INVISIBLE);
                            lin_done.setVisibility(View.VISIBLE);
                            btn_done.setVisibility(View.VISIBLE);
                        }
                        else {

                            //verification unsuccessful.. display an error message
                            otp.showError();
                            DynamicToast.makeWarning(OtpVerificationActivity.this, "OTP Is Incorrect.", 3000).show();
                            btn_verify.setText("Verify The Code");
                            btn_verify.setEnabled(true);
                            progressBar.setVisibility(View.INVISIBLE);
                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    private void UpdateTransaction(DatabaseReference myRefTrans) {
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
                "CREDIT", "Joining bonus added.", transDate, transTime, 5, txnInt);
        myRefTrans.child(transId).setValue(transactionDetail);
    }

    private void SendReferralData(String refer_key) {
        DatabaseReference refUser2 = FirebaseDatabase.getInstance().getReference("Users")
                .child(refer_key).child("myReferrals").child(firebaseAuth.getUid());
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH)+1;
        int year = c.get(Calendar.YEAR);

        String date_id;

        String date;
        if(day<10){
            if(month<10){
                date = "0"+day + "/" + "0"+month + "/" + year;
                date_id = year+"0"+year+"0"+day;
            }
            else {
                date = "0" + day + "/" + month + "/" + year;
                date_id = year+""+month+"0"+day;
            }
        }
        else {
            if(month<10){
                date = day + "/" + "0"+month + "/" + year;
                date_id = year+"0"+month+""+day;
            }
            else {
                date = day + "/" + month + "/" + year;
                date_id = year+""+month+""+day;
            }
        }
        int id = Integer.parseInt(date_id);
        ReferralUser referralUser = new ReferralUser(id, name,
                date, "Pending");
        refUser2.setValue(referralUser);
    }

    private void Init() {
        et_code = findViewById(R.id.activity_otp_verifictaion_code);
        et_number = findViewById(R.id.activity_otp_verifictaion_mobile_no);
        otp = findViewById(R.id.activity_otp_verifictaion_otp);
        btn_send_code = findViewById(R.id.activity_otp_verifictaion_btn_send);
        btn_verify = findViewById(R.id.activity_otp_verifictaion_btn_verify);
        btn_done = findViewById(R.id.activity_otp_verifictaion_btn_done);
        lin_send_code = findViewById(R.id.activity_otp_verifictaion_layout_send);
        lin_verify = findViewById(R.id.activity_otp_verifictaion_layout_verify);
        lin_done = findViewById(R.id.activity_otp_verifictaion_layout_done);
        progressBar = findViewById(R.id.activity_otp_verifictaion_avi);
        btn_manual = findViewById(R.id.activity_otp_verifictaion_btn_manual);
        tv_number = findViewById(R.id.activity_otp_verifictaion_tv_number);
        //otp.requestFocusOTP();
        lin_send_code.setVisibility(View.VISIBLE);
        lin_verify.setVisibility(View.INVISIBLE);
        lin_done.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        btn_send_code.setVisibility(View.VISIBLE);
        btn_verify.setVisibility(View.INVISIBLE);
        btn_done.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    private void GetIntent(){
        name = getIntent().getStringExtra("name");
        pubgusername = getIntent().getStringExtra("pubgusername");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        promo = getIntent().getStringExtra("promo");
        int count = 5;
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        String nameShort = name.substring(0, 3).toUpperCase();
        myReferralCode = nameShort+builder.toString();

    }

    private String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    private void SendUserData(int joiningBonus){
        uid = firebaseAuth.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference().child("Users").child(uid);

        UserProfile userProfile = new UserProfile(name, pubgusername, email, mobile, password, "01/01/1995", "",
                promo,"", 0, 0, 0, joiningBonus, uid, "n", myReferralCode);
        myRef.setValue(userProfile);
    }

    //Focus on edit text
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
