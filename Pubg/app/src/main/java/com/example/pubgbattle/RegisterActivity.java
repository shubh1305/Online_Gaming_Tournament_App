package com.example.pubgbattle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import am.appwise.components.ni.NoInternetDialog;


public class RegisterActivity extends AppCompatActivity {
    //Defining UI Views
    private EditText et_name, et_pubgUsername, et_email, et_mobile, et_password, et_promoCode;
    private CheckBox checkBox;
    private Button btn_signup, btn_goToLogin, btn_visible;
    private NoInternetDialog noInternetDialog;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private  String f_username, f_mobile, referral_code, promo_code;
    //Defining Firebase Auth
    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog_signup;
    private int flag=1, flag2, flag3, flag4=0;
    private static int count = 5;
    private String refer_key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        noInternetDialog = new NoInternetDialog.Builder(this).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.rgb(40, 116, 238));
            window.setStatusBarColor(getResources().getColor(R.color.statusbar));
        }
        //Initializing Variables
        SetupUIViews();
        //Dynamic listening on text change
        TextWatcherSection();

        //On clicking the button
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateName()) {
                    return;
                }
                if (!validatePubgUsername()) {
                    return;
                }

                if (!validateEmail()) {
                    return;
                }
                if (!validateMobile()) {
                    return;
                }

                if (!validatePassword()) {
                    return;
                }

                if(checkBox.isChecked()){
                    final String pubgUsername = et_pubgUsername.getText().toString().trim();
                    final String mobile = et_mobile.getText().toString().trim();
                    final String email = et_email.getText().toString().trim();
                    final String pass = et_password.getText().toString().trim();
                    final String promo = et_promoCode.getText().toString().trim();
                    progressDialog_signup.setMessage("Registering User...");
                    progressDialog_signup.setCancelable(false);
                    progressDialog_signup.setCanceledOnTouchOutside(false);
                    progressDialog_signup.show();

                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                final UserProfile userProfile = data.getValue(UserProfile.class);
                                referral_code = userProfile.getMyReferCode();
                                f_username = userProfile.getPubgUsername();
                                f_mobile = userProfile.getMobileNo();
                                if(f_username.equals(pubgUsername)){
                                    flag2=1;
                                    break;
                                }
                                if(f_mobile.equals(mobile)){
                                    flag3=1;
                                    break;
                                }

                                if(referral_code.equals(promo) || promo.isEmpty()){
                                    flag4=1;
                                    break;
                                }

                                flag2=0;
                                flag3=0;
                                flag4=0;
                            }
                            if(flag2==1){
                                et_pubgUsername.setError("Pubg Username Already Exists.");
                                requestFocus(et_pubgUsername);
                                progressDialog_signup.dismiss();
                            }
                            else if(flag3==1){
                                et_mobile.setError("Mobile Number Already Exists.");
                                requestFocus(et_mobile);
                                progressDialog_signup.dismiss();
                            }
                            else if(flag4==0){
                                et_promoCode.setError("Referral code not found.");
                                requestFocus(et_promoCode);
                                progressDialog_signup.dismiss();
                            }
                            else {
                                et_mobile.setError(null);
                                et_promoCode.setError(null);
                                et_pubgUsername.setError(null);
                                DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("Users");

                                //Get the UID who sent referral
                                if (!promo.isEmpty()) {
                                    refUser.orderByChild("myReferCode").equalTo(promo)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                        refer_key = data.getKey();
                                                    }
                                                    //Log.d("INSIDE", "onDataChange: "+ refer_key);
                                                    Register(email, pass, refer_key);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                    //Register(email, pass);
                                    //Log.d("OUTSIDE", "onDataChange: "+ refer_key);
                                    //progressDialog_signup.dismiss();
                                }
                                else
                                    Register(email, pass);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Please accept the Terms of Service", Toast.LENGTH_LONG).show();
                }

            }
        });

        //On clicking button
        btn_goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(a);
                Animatoo.animateShrink(RegisterActivity.this);
                finish();
            }
        });

        //On clicking button
        btn_visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag++;
                if(flag%2==0){
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btn_visible.setBackgroundResource(R.drawable.ic_eye_visible);
                    et_password.setSelection(et_password.getText().length());
                }
                else{
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btn_visible.setBackgroundResource(R.drawable.ic_eye_invisible);
                    et_password.setSelection(et_password.getText().length());
                }
            }
        });

    }

    private void Register(String email, String pass) {
        //Register User with email and password
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Send user data to firebase database
                    SendUserData(0);
                    mAuth.signOut();
                    progressDialog_signup.dismiss();
                    Toast.makeText(RegisterActivity.this, "Successfully Registered!", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this,
                        "Something Went Wrong! Email already exists.", Toast.LENGTH_SHORT).show();
                progressDialog_signup.dismiss();
            }
        });
    }

    private void Register(String email, String pass, String refer_key) {
        //Register User with email and password
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Send user data to firebase database
                    SendUserData(5);
                    SendReferralData(refer_key);
                    DatabaseReference myRefTrans = FirebaseDatabase.getInstance().getReference("Users")
                            .child(mAuth.getUid()).child("transactions");
                    UpdateTransaction(myRefTrans);
                    mAuth.signOut();
                    progressDialog_signup.dismiss();
                    Toast.makeText(RegisterActivity.this, "Successfully Registered!", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this,
                        "Something Went Wrong! Email already exists.", Toast.LENGTH_SHORT).show();
                progressDialog_signup.dismiss();
            }
        });
    }
    private String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
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
                .child(refer_key).child("myReferrals").child(mAuth.getUid());
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
        ReferralUser referralUser = new ReferralUser(id, et_name.getText().toString().trim(),
                date, "Pending");
        refUser2.setValue(referralUser);
    }


    //Initializing View UI
    private void SetupUIViews(){
        et_name = findViewById(R.id.activity_signup_et_name);
        et_pubgUsername = findViewById(R.id.activity_signup_et_pubg_username);
        et_email = findViewById(R.id.activity_signup_et_email);
        et_mobile = findViewById(R.id.activity_signup_et_mobile_no);
        et_password = findViewById(R.id.activity_signup_et_password);
        et_promoCode = findViewById(R.id.activity_signup_et_promocode);
        checkBox = findViewById(R.id.activity_signup_checkbox);
        btn_signup = findViewById(R.id.activity_signup_btn_signUp);
        btn_goToLogin = findViewById(R.id.activity_signup_btn_Login);
        btn_visible = findViewById(R.id.activity_signup_btn_visible);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        progressDialog_signup = new ProgressDialog(this);
    }

    //Dynamically listning on text change
    private void TextWatcherSection(){
        et_name.addTextChangedListener(new MyTextWatcher(et_name));
        et_pubgUsername.addTextChangedListener(new MyTextWatcher(et_pubgUsername));
        et_email.addTextChangedListener(new MyTextWatcher(et_email));
        et_mobile.addTextChangedListener(new MyTextWatcher(et_mobile));
        et_password.addTextChangedListener(new MyTextWatcher(et_password));
    }

    //Sending data to firebase database
    private void SendUserData(int joiningBonus){
        FirebaseUser user = mAuth.getCurrentUser();
        final String name, pubgUsername, email, mobileNo, password, promoCode, uid, myReferCode, nameShort;
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        uid = mAuth.getUid();
        name = et_name.getText().toString().trim();
        nameShort = name.substring(0, 3).toUpperCase();
        pubgUsername = et_pubgUsername.getText().toString().trim();
        email = user.getEmail();
        mobileNo = et_mobile.getText().toString().trim();
        password = et_password.getText().toString().trim();
        promoCode = et_promoCode.getText().toString().trim();
        myReferCode = nameShort+builder.toString();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference().child("Users").child(uid);

        UserProfile userProfile = new UserProfile(name, pubgUsername, email, mobileNo, password, "01/01/1995", "",
                promoCode,"", 0, 0, 0, joiningBonus, uid, "n", myReferCode);
        myRef.setValue(userProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog_signup.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog_signup.dismiss();
            }
        });
    }

    //Check if user is already logged in
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        }
    }

    //********** Text Watcher for Validation *******************//
    private class MyTextWatcher implements TextWatcher {
        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.activity_signup_et_name:
                    validateName();
                    break;
                case R.id.activity_signup_et_pubg_username:
                    validatePubgUsername();
                    break;
                case R.id.activity_signup_et_email:
                    validateEmail();
                    break;
                case R.id.activity_signup_et_mobile_no:
                    validateMobile();
                    break;
                case R.id.activity_signup_et_password:
                    validatePassword();
                    break;
            }
        }
    }

    //Validating name
    private boolean validateName(){
        if(et_name.getText().toString().trim().isEmpty()){
            et_name.setError("Please Enter Your Name");
            requestFocus(et_name);
            return false;
        }
        else if(et_name.getText().toString().trim().length()<3){
            et_name.setError("Name should not be less then 3 characters");
            requestFocus(et_name);
            return false;
        }
        else{
            et_name.setError(null);
        }
        return true;
    }

    //Validating Pubg Username
    private boolean validatePubgUsername() {
        if(et_pubgUsername.getText().toString().trim().isEmpty()){
            et_pubgUsername.setError("Please Enter Your PubG Username");
            requestFocus(et_pubgUsername);
            return false;
        }
        else{
            et_pubgUsername.setError(null);
        }
        return true;
    }

    //Validating Email
    private boolean validateEmail() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(et_email.getText().toString().trim().isEmpty()){
            et_email.setError("Please Enter Your Email Address");
            requestFocus(et_email);
            return false;
        }
        else if(!et_email.getText().toString().trim().matches(emailPattern)){
            et_email.setError("Please Enter a Valid Email Address");
            requestFocus(et_email);
            return false;
        }
        else{
            et_email.setError(null);
        }
        return true;
    }

    //Validating Mobile Number
    private boolean validateMobile() {
        String phonePattern = "[0-9]{10}";
        if(et_mobile.getText().toString().trim().isEmpty()){
            et_mobile.setError("Please Enter Your Mobile Number");
            requestFocus(et_mobile);
            return false;
        }
        else if(et_mobile.getText().toString().trim().length()!=10 || !et_mobile.getText().toString().trim().matches(phonePattern)){
            et_mobile.setError("Please Enter a Valid Mobile Number");
            requestFocus(et_mobile);
            return false;
        }
        else{
            et_mobile.setError(null);
        }
        return true;
    }

    //Validating Password
    private boolean validatePassword() {
        if(et_password.getText().toString().trim().isEmpty()){
            et_password.setError("Please Enter a Password");
            requestFocus(et_password);
            return false;
        }
        else if(et_password.getText().toString().trim().length()<6){
            et_password.setError("Password must be of atleast 6 characters");
            requestFocus(et_password);
            return false;
        }
        else{
            et_password.setError(null);
        }
        return true;
    }

    //Focus on edit text
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}
