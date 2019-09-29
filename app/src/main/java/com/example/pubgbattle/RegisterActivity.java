package com.example.pubgbattle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import am.appwise.components.ni.NoInternetDialog;
import in.aabhasjindal.otptextview.OtpTextView;


public class RegisterActivity extends AppCompatActivity {
    //Defining UI Views
    private EditText et_name, et_pubgUsername, et_email, et_password, et_promoCode;
    private CheckBox checkBox;
    private Button btn_signup, btn_goToLogin, btn_visible;
    private NoInternetDialog noInternetDialog;

    private  String f_username, f_email, referral_code;
    //Defining Firebase Auth
    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog_signup;
    private int flag=1, flag2, flag3, flag4=0;

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
                if (!validatePassword()) {
                    return;
                }
                if(checkBox.isChecked()){
                    String pubgUsername = et_pubgUsername.getText().toString().trim();
                    String email = et_email.getText().toString().trim();
                    String promo = et_promoCode.getText().toString().trim();
                    progressDialog_signup.setMessage("Registering User...");
                    progressDialog_signup.setCancelable(false);
                    progressDialog_signup.setCanceledOnTouchOutside(false);
                    progressDialog_signup.show();
                    flag2 = 0; flag3 = 0; flag4 = 0;
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                final UserProfile userProfile = data.getValue(UserProfile.class);
                                referral_code = userProfile.getMyReferCode();
                                f_username = userProfile.getPubgUsername();
                                //f_mobile = userProfile.getMobileNo();
                                f_email = userProfile.getEmail();
                                if(f_username.equals(pubgUsername)){
                                    flag2=1;
                                    break;
                                }
                                if(f_email.equals(email)){
                                    flag3=1;
                                    break;
                                }
                                if(referral_code.equals(promo) || promo.isEmpty()){
                                    flag4=1;
                                    break;
                                }
                            }
                            if(flag2==1){
                                et_pubgUsername.setError("Pubg Username Already Exists.");
                                requestFocus(et_pubgUsername);
                                progressDialog_signup.dismiss();
                            }
                            else if(flag3==1){
                                et_email.setError("Email ID Already Exists.");
                                requestFocus(et_email);
                                progressDialog_signup.dismiss();
                            }
                            else if(flag4==0){
                                et_promoCode.setError("Referral Code Is Invalid.");
                                requestFocus(et_promoCode);
                                progressDialog_signup.dismiss();
                            }
                            else {
                                //et_mobile.setError(null);
                                et_promoCode.setError(null);
                                et_pubgUsername.setError(null);
                                et_email.setError(null);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog_signup.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, OtpVerificationActivity.class);
                                        intent.putExtra("name", et_name.getText().toString());
                                        intent.putExtra("pubgusername", et_pubgUsername.getText().toString());
                                        intent.putExtra("email", et_email.getText().toString());
                                        intent.putExtra("password", et_password.getText().toString());
                                        if(!et_promoCode.getText().toString().isEmpty())
                                            intent.putExtra("promo", et_promoCode.getText().toString());
                                        else
                                            intent.putExtra("promo", "");
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 3000);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    DynamicToast.makeWarning(RegisterActivity.this, "Please accept the Terms of Service", Toast.LENGTH_SHORT).show();
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

    //Initializing View UI
    private void SetupUIViews(){
        et_name = findViewById(R.id.activity_signup_et_name);
        et_pubgUsername = findViewById(R.id.activity_signup_et_pubg_username);
        et_email = findViewById(R.id.activity_signup_et_email);
        //et_mobile = findViewById(R.id.activity_signup_et_mobile_no);
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
        //et_mobile.addTextChangedListener(new MyTextWatcher(et_mobile));
        et_password.addTextChangedListener(new MyTextWatcher(et_password));
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
                /*case R.id.activity_signup_et_mobile_no:
                    validateMobile();
                    break;*/
                case R.id.activity_signup_et_password:
                    validatePassword();
                    break;
            }
        }
    }

    //Validating name
    private boolean validateName(){
        if(et_name.getText().toString().isEmpty()){
            et_name.setError("Please Enter Your Name");
            requestFocus(et_name);
            return false;
        }
        else if(et_name.getText().toString().length()<3){
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
        if(et_pubgUsername.getText().toString().isEmpty()){
            et_pubgUsername.setError("Please Enter Your PubG Username");
            requestFocus(et_pubgUsername);
            return false;
        }
        else if(et_pubgUsername.getText().toString().contains(" ")){
            et_pubgUsername.setError("No spaces allowed");
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
        if(et_email.getText().toString().isEmpty()){
            et_email.setError("Please Enter Your Email Address");
            requestFocus(et_email);
            return false;
        }
        else if(!et_email.getText().toString().matches(emailPattern)){
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
    /*private boolean validateMobile() {
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
    }*/

    //Validating Password
    private boolean validatePassword() {
        if(et_password.getText().toString().isEmpty()){
            et_password.setError("Please Enter a Password");
            requestFocus(et_password);
            return false;
        }
        else if(et_password.getText().toString().length()<6){
            et_password.setError("Password must be of atleast 6 characters");
            requestFocus(et_password);
            return false;
        }
        else if(et_password.getText().toString().contains(" ")){
            et_password.setError("No Spaces Allowed");
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
