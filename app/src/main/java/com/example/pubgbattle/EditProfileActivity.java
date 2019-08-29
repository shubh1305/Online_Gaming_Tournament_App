package com.example.pubgbattle;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

import am.appwise.components.ni.NoInternetDialog;

public class EditProfileActivity extends AppCompatActivity {
    private static final int DIALOG_ID = 0;
    private TextInputLayout til_name, til_username;
    private EditText et_name, et_pubgusername, et_email, et_mobile, et_dob, et_password_old, et_password_new, et_password_new_re;
    private TextView tv_success, tv_reset;
    private Button btn_save, btn_reset;
    private RadioGroup rg_gender;
    private RadioButton rb_male, rb_female;
    private int year_x, month_x, day_x, y, m, d;
    private Calendar c;
    private String f_username, pubgUsername;
    private int flag;
    private NoInternetDialog noInternetDialog;

    //firebase
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //progress dialog
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog2;
    private ProgressDialog progressDialog3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        c = Calendar.getInstance();
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

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_editprofile_toolbar);
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
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ///getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Initializing Views UI
        Init();

        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);

        et_name.addTextChangedListener(new EditProfileActivity.MyTextWatcher(et_name));
        et_pubgusername.addTextChangedListener(new EditProfileActivity.MyTextWatcher(et_pubgusername));
        //et_password_old.addTextChangedListener(new EditProfileActivity.MyTextWatcher(et_password_old));
        //et_password_new.addTextChangedListener(new EditProfileActivity.MyTextWatcher(et_password_new));
        //et_password_new_re.addTextChangedListener(new EditProfileActivity.MyTextWatcher(et_password_new_re));

        //Retrieving User's data
        RetrieveUserData();


        et_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateName())
                    return;
                if(!validatePubgUsername())
                    return;
                if(!rb_male.isChecked() && !rb_female.isChecked()){
                    Toast.makeText(EditProfileActivity.this, "Please Select Your Gender.",Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog2.show();
                //UpdateUserData();
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
                final DatabaseReference myRefSelf = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());
                myRefSelf.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserProfile user = dataSnapshot.getValue(UserProfile.class);
                        pubgUsername = user.getPubgUsername();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            final UserProfile userProfile = data.getValue(UserProfile.class);

                            f_username = userProfile.getPubgUsername();
                            if(f_username.equals(et_pubgusername.getText().toString().trim())){
                                flag=1;
                                break;
                            }
                            flag=0;
                        }
                        if(et_pubgusername.getText().toString().trim().equals(pubgUsername))
                            flag=0;
                        if(flag==1){
                            et_pubgusername.setError("Pubg Username Already Exists.");
                            progressDialog2.dismiss();
                            return;
                        }
                        else {
                            et_pubgusername.setError(null);
                            UpdateUserData();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateOldPassword())
                    return;
                if(!validateNewPassword())
                    return;
                if(!validateNewPasswordRe())
                    return;
                progressDialog3.show();
                UpdatePassword();

            }
        });
    }

    private void UpdatePassword() {
        final FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();
        String oldPassword = et_password_old.getText().toString().trim();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    user.updatePassword(et_password_new.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            DatabaseReference ref = FirebaseDatabase.getInstance()
                                    .getReference("Users").child(FirebaseAuth.getInstance().getUid());
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("password", et_password_new.getText().toString().trim());
                            ref.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    tv_reset.setText("Password Updated Successfully.");
                                    LinearLayout.LayoutParams textParam = new LinearLayout.LayoutParams
                                            (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                                    tv_reset.setLayoutParams(textParam);
                                    tv_reset.setTextColor(getResources().getColor(R.color.green));
                                    textParam.setMargins(0,10,0,0);
                                    et_password_new.setText("");
                                    et_password_new_re.setText("");
                                    et_password_old.setText("");
                                    requestFocus(et_password_old);
                                    progressDialog3.dismiss();
                                    //Toast.makeText(EditProfileActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog3.dismiss();
                                    Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog3.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Password Updation Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog3.dismiss();
                tv_reset.setText("Old Password Did Not Match.");
                tv_reset.setTextColor(getResources().getColor(R.color.googleRed));
                LinearLayout.LayoutParams textParam = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                tv_reset.setLayoutParams(textParam);
                textParam.setMargins(0,10,0,0);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DIALOG_ID) {
            y = c.get(Calendar.YEAR);
            m = c.get(Calendar.MONTH);
            d = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(this, dpickerListner, y, m, d);
        }
        else
            return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month+1;
            day_x = dayOfMonth;
            if(month_x <10){
                if(day_x <10)
                    et_dob.setText("0"+day_x+"/"+"0"+month_x+"/"+year_x);
                else
                    et_dob.setText(day_x+"/"+"0"+month_x+"/"+year_x);
            }
            else{
                if(day_x<10)
                    et_dob.setText("0"+day_x+"/"+month_x+"/"+year_x);
                else
                    et_dob.setText(day_x+"/"+month_x+"/"+year_x);
            }
            //Toast.makeText(EditProfileActivity.this, day_x+"/"+month_x+"/"+year_x,Toast.LENGTH_LONG).show();
        }
    };

    private void Init(){
        et_name = findViewById(R.id.activity_editprofile_et_name);
        et_pubgusername = findViewById(R.id.activity_editprofile_et_pubg_username);
        et_email = findViewById(R.id.activity_editprofile_et_email);
        et_mobile = findViewById(R.id.activity_editprofile_et_mobile);
        et_dob = findViewById(R.id.activity_editprofile_et_dob);
        et_password_old = findViewById(R.id.activity_editprofile_et_password_old);
        et_password_new = findViewById(R.id.activity_editprofile_et_password_new);
        et_password_new_re = findViewById(R.id.activity_editprofile_et_password_new_re);

        tv_success = findViewById(R.id.activity_editprofile_tv_success);
        tv_reset = findViewById(R.id.activity_editprofile_tv_reset);

        til_name = findViewById(R.id.activity_editprofile_til_name);

        btn_save = findViewById(R.id.activity_editprofile_btn_save);
        btn_reset = findViewById(R.id.activity_editprofile_btn_reset);

        rg_gender = findViewById(R.id.activity_editprofile_radioGroup);
        rb_male = findViewById(R.id.activity_editprofile_radioMale);
        rb_female = findViewById(R.id.activity_editprofile_radioFemale);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching User Info...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog2 = new ProgressDialog(this);
        progressDialog2.setMessage("Saving User Data");
        progressDialog2.setCancelable(false);
        progressDialog2.setCanceledOnTouchOutside(false);

        progressDialog3 = new ProgressDialog(this);
        progressDialog3.setMessage("Updating Password...");
        progressDialog3.setCanceledOnTouchOutside(false);
        progressDialog3.setCancelable(false);

    }

    private void RetrieveUserData() {
        progressDialog.show();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = firebaseDatabase.getReference("Users").child(mAuth.getUid());
        myRef.keepSynced(true);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                //Fetched user data string

                et_name.setText(userProfile.getName());
                et_pubgusername.setText(userProfile.getPubgUsername());
                et_email.setText(userProfile.getEmail());
                et_mobile.setText(userProfile.getMobileNo());
                et_dob.setText(userProfile.getDob());

                String gender = userProfile.getGender();

                if(gender.equals("M")){
                    rb_male.setChecked(true);
                    rb_female.setChecked(false);
                }
                else if(gender.equals("F")){
                    rb_male.setChecked(false);
                    rb_female.setChecked(true);
                }
                else{
                    rb_male.setChecked(false);
                    rb_female.setChecked(false);
                }

                progressDialog.dismiss();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateUI(FirebaseUser currentUser) {
        if(currentUser == null){
            startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
        }
    }


    //Sending data to firebase database
    private void UpdateUserData(){
        String name, pubgUsername, dob, gender;
        String uid = mAuth.getUid();
        name = et_name.getText().toString().trim();
        pubgUsername = et_pubgusername.getText().toString().trim();
        dob = et_dob.getText().toString().trim();
        if(rb_male.isChecked())
            gender = "M";
        else if(rb_female.isChecked())
            gender = "F";
        else
            gender = "";
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference().child("Users").child(uid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("pubgUsername", pubgUsername);
        hashMap.put("dob", dob);
        hashMap.put("gender", gender);
        myRef.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog2.dismiss();
                //et_pubgusername.setError(null);
                tv_success.setText("Profile Updated Successfully.");
                LinearLayout.LayoutParams textParam = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                tv_success.setLayoutParams(textParam);
                textParam.setMargins(0,-20,0,40);
                Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog2.dismiss();
                Toast.makeText(EditProfileActivity.this, "Profile Updating Failed", Toast.LENGTH_SHORT).show();
            }
        });

        /*myRef.child("name").setValue(name);
        myRef.child("pubgUsername").setValue(pubgUsername);
        myRef.child("dob").setValue(dob);
        myRef.child("gender").setValue(gender);*/
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
                case R.id.activity_editprofile_et_name:
                    validateName();
                    break;
                case R.id.activity_editprofile_et_pubg_username:
                    validatePubgUsername();
                    break;
                /*case R.id.activity_editprofile_et_password_old:
                    validateOldPassword();
                    break;
                case R.id.activity_editprofile_et_password_new:
                    validateNewPassword();
                    break;
                case R.id.activity_editprofile_et_password_new_re:
                    validateNewPasswordRe();
                    break;*/
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
            et_name.setError("Name must be of atleast 3 characters");
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
        if(et_pubgusername.getText().toString().trim().isEmpty()){
            et_pubgusername.setError("Please Enter Your PubG Username");
            requestFocus(et_pubgusername);
            return false;
        }
        else{
            et_pubgusername.setError(null);
        }
        return true;
    }

    //Validating Old Password
    private boolean validateOldPassword(){
        if(et_password_old.getText().toString().trim().isEmpty()){
            et_password_old.setError("Please Enter a Password");
            requestFocus(et_password_old);
            return false;
        }
        else
            et_password_old.setError(null);
        return true;
    }

    //Validating New Password
    private boolean validateNewPassword(){
        if(et_password_new.getText().toString().trim().isEmpty()){
            et_password_new.setError("Please Enter a Password");
            requestFocus(et_password_new);
            return false;
        }
        else if(et_password_new.getText().toString().trim().length()<6){
            et_password_new.setError("Password should be of atleast 6 characters");
            requestFocus(et_password_new);
            return false;
        }
        else
            et_password_old.setError(null);
        return true;
    }

    private boolean validateNewPasswordRe(){
        if(et_password_new_re.getText().toString().trim().isEmpty()){
            et_password_new_re.setError("Please Enter a Password");
            requestFocus(et_password_new_re);
            return false;
        }
        else if(et_password_new_re.getText().toString().trim().length()<6){
            et_password_new_re.setError("Password should be of atleast 6 characters");
            requestFocus(et_password_new_re);
            return false;
        }
        else if(!et_password_new_re.getText().toString().trim().equals(et_password_new.getText().toString().trim())){
            et_password_new_re.setError("Password didn't match");
            requestFocus(et_password_new_re);
            return false;
        }
        else
            et_password_new_re.setError(null);
        return true;
    }



    //Focus on edit text
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
