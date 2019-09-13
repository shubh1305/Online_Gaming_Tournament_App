package com.example.pubgbattle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.awt.font.TextAttribute;

import am.appwise.components.ni.NoInternetDialog;


public class LoginActivity extends AppCompatActivity /*implements UpdateHelper.OnUpdateCheckListner*/ {
    //defining views
    private EditText et_email, et_password;
    private Button btn_frgt, btn_signIn, btn_visible, btn_reset, btn_cancel, btn_done;
    private TextView tv_goToSignUp, tv_error;
    private String email, pass;
    private AlertDialog dialog_reset, dialog_reset_done;
    private EditText et_reset_email;
    private TextInputLayout til_reset_email;
    private ProgressDialog progressDialog_reset, progressDialog_login;
    private NoInternetDialog noInternetDialog;

    //firebase
    private FirebaseAuth mAuth;

    //for password view hide & unhide
    private int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        noInternetDialog = new NoInternetDialog.Builder(this).build();

        //UpdateHelper.with(this).onUpdateCheck(this).check();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.rgb(40, 116, 238));
            window.setStatusBarColor(getResources().getColor(R.color.statusbar));
        }

        Initialize();

        //check if user is logged in
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        //Dynamic listening on text changed
        et_email.addTextChangedListener(new LoginActivity.MyTextWatcher(et_email));
        et_password.addTextChangedListener(new LoginActivity.MyTextWatcher(et_password));



        //when button is clicked
        tv_goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                Animatoo.animateShrink(LoginActivity.this);
                /*Animatoo.animateZoom(context);
                Animatoo.animateFade(context);
                Animatoo.animateWindmill(context);
                Animatoo.animateSpin(context);
                Animatoo.animateDiagonal(context);
                Animatoo.animateSplit(context);
                Animatoo.animateShrink(context);
                Animatoo.animateCard(context);
                Animatoo.animateInAndOut(context);
                Animatoo.animateSwipeLeft(context);
                Animatoo.animateSwipeRight(context);
                Animatoo.animateSlideLeft(context);
                Animatoo.animateSlideRight(context);
                Animatoo.animateSlideDown(context);
                Animatoo.animateSlideUp(context);*/
                finish();
            }
        });

        btn_frgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayResetDialog();
            }
        });

        //when Login button is clicked
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validating Email Address
                if (!validateEmail()) {
                    return;
                }
                //Validating Password
                if (!validatePassword()) {
                    return;
                }
                progressDialog_login.setMessage("Authenticating User...");
                progressDialog_login.setCanceledOnTouchOutside(false);
                progressDialog_login.setCancelable(false);
                progressDialog_login.show();
                email = et_email.getText().toString();
                pass = et_password.getText().toString();
                /*Log.d(email,"Email");
                Log.d(pass, "Password");*/
                Login(email, pass);
            }



        });

        //when show password button is clicked
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

    private void DisplayResetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View view = getLayoutInflater().inflate(R.layout.reset_password_dialog, null);

        btn_reset = view.findViewById(R.id.dialog_btn_reset);
        btn_cancel = view.findViewById(R.id.dialog_btn_cancel);
        et_reset_email =view.findViewById(R.id.dialog_et_reset_registered_email);
        til_reset_email = view.findViewById(R.id.dialog_til_reset_registered_email);
        tv_error = view.findViewById(R.id.dialog_tv_error_msg);




        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_reset.dismiss();
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if(et_reset_email.getText().toString().trim().isEmpty() || !et_reset_email.getText().toString().trim().matches(emailPattern)){
                    tv_error.setText("Enter a Valid Email Address");
                    Toast.makeText(LoginActivity.this,"Please Enter a Valid Email Address",Toast.LENGTH_SHORT).show();

                    //et_reset_email.setError("");
                    //til_reset_email.setError("Enter a valid email address");
                }

                else {
                    tv_error.setText("");
                    DisplayResetDoneDialog();
                }
            }
        });
        builder.setView(view);
        dialog_reset = builder.create();
        dialog_reset.show();
        dialog_reset.setCancelable(false);
        dialog_reset.setCanceledOnTouchOutside(false);
        dialog_reset.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void DisplayResetDoneDialog() {
        progressDialog_reset.setMessage("Just a sec...");
        progressDialog_reset.setCancelable(false);
        progressDialog_reset.setCanceledOnTouchOutside(false);
        progressDialog_reset.show();
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_reset_done, null);
        btn_done = view.findViewById(R.id.btn_reset_dialog_done);
        String email = et_reset_email.getText().toString().trim();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog_reset.dismiss();
                    dialog_reset_done.show();
                    dialog_reset.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tv_error.setText("Email Address Not Found");
                Toast.makeText(LoginActivity.this,"Invalid Email Address",Toast.LENGTH_SHORT).show();
                progressDialog_reset.dismiss();
            }
        });
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_reset_done.dismiss();
            }
        });
        builder.setView(view);
        dialog_reset_done = builder.create();
        dialog_reset_done.setCancelable(false);
        dialog_reset_done.setCanceledOnTouchOutside(false);
        dialog_reset_done.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    //Initializing UI View variables
    private void Initialize() {
        //UI
        et_email = findViewById(R.id.activity_login_et_email);
        et_password = findViewById(R.id.activity_login_et_password);
        btn_frgt = findViewById(R.id.activity_login_btn_frgt);
        btn_signIn = findViewById(R.id.activity_login_btn_signin);
        tv_goToSignUp = findViewById(R.id.activity_login_btn_gotosignup);
        btn_visible = findViewById(R.id.activity_login_btn_visible);

        //progress bar
        progressDialog_reset = new ProgressDialog(this);
        progressDialog_login = new ProgressDialog(this);
        //firebase
        mAuth = FirebaseAuth.getInstance();

    }

    //login using email and password using firebase
    private void Login(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //progressDialog_login.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    //Animatoo.animateInAndOut(LoginActivity.this);
                    finish();
                }
                else{
                    progressDialog_login.dismiss();
                    Toast.makeText(LoginActivity.this, "Email/Password is Incorrect", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*@Override
    public void onUpdateCheckListner(String urlApp) {
        //create alert dialog
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("New Version Available")
                .setMessage("Please update to new version to continue use")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        alertDialog.show();
    }*/

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
                case R.id.activity_login_et_email:
                    validateEmail();
                    break;
                case R.id.activity_login_et_password:
                    validatePassword();
                    break;
            }
        }
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


    //Validating Password
    private boolean validatePassword() {
        if(et_password.getText().toString().trim().isEmpty()){
            et_password.setError("Please Enter a Password");
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
