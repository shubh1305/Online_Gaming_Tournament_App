package com.example.pubgbattle;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.circulardialog.CDialog;
import com.example.circulardialog.extras.CDConstants;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import am.appwise.components.ni.NoInternetDialog;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import id.zelory.compressor.Compressor;
import io.supercharge.shimmerlayout.ShimmerLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private Point p;;
    private static final String NUMBER = "+91 9380489153";
    private static final int REQUEST_IMAGE = 100;
    private static final String CHANNEL_ID = "default";
    private static final CharSequence CHANNEL_NAME = "InApp";
    //Views
    private TextView tv_name, tv_userName, tv_playCoins, tv_matchPlayed, tv_totalKills, tv_totalWinnings;
    private CardView cv_refer, cv_myprofile, cv_mystats, cv_mywallet,
            cv_top_players, cv_updates, cv_aboutus, cv_support, cv_shareapp, cv_logout;
    private ImageView iv_profilepic, iv_upload_profile_pic, iv_support_popup;
    private static final String TAG = MainActivity.class.getSimpleName();
    private NoInternetDialog noInternetDialog;
    private ShimmerLayout shimmerLayout;
    private Bitmap bitmap;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private NestedScrollView nestedScrollView;

    //progress dialog
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog2;

    //URI
    private Uri image_uri;
    private Handler handler;
    private int NOTIFICATION_ID=0;


    public AccountFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_account, container, false);
        noInternetDialog = new NoInternetDialog.Builder(this)
                .setButtonColor(getResources().getColor(R.color.main))
                .build();
        ButterKnife.bind(getActivity());
        bitmap = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.ic_eye_visible);
        handler = new Handler();

        //init ImageView
        iv_profilepic = view.findViewById(R.id.fragment_account_iv_profilepic);
        iv_upload_profile_pic = view.findViewById(R.id.fragment_account_iv_upload_pic);

        //Nested view
        nestedScrollView = view.findViewById(R.id.fragment_account_nsv);

        //init TextView
        tv_name = view.findViewById(R.id.fragment_account_tv_name);
        tv_userName = view.findViewById(R.id.fragment_account_tv_username_value);
        tv_playCoins = view.findViewById(R.id.fragment_account_tv_playcoin_value);
        tv_matchPlayed = view.findViewById(R.id.fragment_account_tv_match_played);
        tv_totalKills = view.findViewById(R.id.fragment_account_tv_total_kills);
        tv_totalWinnings = view.findViewById(R.id.fragment_account_tv_total_winnings);

        //init CardView
        cv_refer = view.findViewById(R.id.fragment_account_cv_refer);
        cv_myprofile =view.findViewById(R.id.fragment_account_cv_myprofile);
        cv_mystats = view.findViewById(R.id.fragment_account_cv_mystats);
        cv_mywallet = view.findViewById(R.id.fragment_account_cv_mywallet);
        cv_top_players = view.findViewById(R.id.fragment_account_cv_top_players);
        cv_updates = view.findViewById(R.id.fragment_account_cv_updates);
        cv_support = view.findViewById(R.id.fragment_account_cv_support);
        cv_shareapp = view.findViewById(R.id.fragment_account_cv_shareapp);
        cv_aboutus = view.findViewById(R.id.fragment_account_cv_aboutus);
        cv_logout = view.findViewById(R.id.fragment_account_cv_logout);
        shimmerLayout = view.findViewById(R.id.fragmant_account_shimmer_layout);
        iv_support_popup = view.findViewById(R.id.fragment_account_support_popup);


        //init firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        //init progressbar dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading Profile Picture");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog2 = new ProgressDialog(getActivity());
        progressDialog2.setMessage("Loading User Info...");
        progressDialog2.setCancelable(false);
        progressDialog2.setCanceledOnTouchOutside(false);

        //visibility
        nestedScrollView.setVisibility(View.INVISIBLE);

        //Retrieving User's data
        RetrieveUserData();

        cv_refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myEarnIntent = new Intent(getActivity(), ReferEarnActivity.class);
                startActivity(myEarnIntent);
            }
        });

        //My profile
        cv_myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myProfileIntent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(myProfileIntent);
            }
        });

        cv_shareapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Playadda247");
                    String shareMessage= "Do you know, you can win free paytm cash by playing PUBG.\n\n";
                    shareMessage = shareMessage + "Yes! Now get paid for every kill. Not only this, if you get Chicken Dinner, you really win big prizes.\n\n" ;
                    shareMessage = shareMessage+ "Just download & try PlayAdda247.\n\n";
                    shareMessage = shareMessage + "Download Link: https://playadda247.com/download\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Share using"));
                }
                catch(Exception e) {
                    //e.toString();
                }
            }
        });

        //Click handling on upload_profile_pic button
        iv_upload_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                Dexter.withActivity(getActivity())
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    showImagePickerOptions();
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        //Click handling on logout button
        cv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                getActivity().finish();
                startActivity(new Intent(view.getContext(), LoginActivity.class));
            }
        });
        cv_mywallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WalletActivity.class));
            }
        });
        cv_mystats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyStatsActivity.class));
            }
        });

        cv_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showPopupMenu(cv_support, true, R.style.MyPopupStyle);
                //init the wrapper with style
                Context wrapper = new ContextThemeWrapper(getActivity(), R.style.MyPopupStyle);

                //init the popup
                PopupMenu popup = new PopupMenu(wrapper, v);

                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //inflate menu
                popup.getMenuInflater().inflate(R.menu.support_menu, popup.getMenu());

                //implement click events
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.support_facebook:
                                Uri uri = Uri.parse("https://m.me/playadda247"); // missing 'http://' will cause crashed
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                //Toast.makeText(getActivity(), "Facebook clicked", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.support_whatsapp:
                                String url = "https://api.whatsapp.com/send?phone="+NUMBER;
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                                //Toast.makeText(getActivity(), "Whatsapp clicked", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();

            }
        });
        CreateNotificationChannel();
        return view;
    }

    /*private void showPopupMenu(View anchor, boolean isWithIcons, int style) {
        //init the wrapper with style
        Context wrapper = new ContextThemeWrapper(getActivity(), style);

        //init the popup
        PopupMenu popup = new PopupMenu(wrapper, anchor);

        // The below code in try catch is responsible to display icons
        if (isWithIcons) {
            try {
                Field[] fields = popup.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(popup);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //inflate menu
        popup.getMenuInflater().inflate(R.menu.support_menu, popup.getMenu());

        //implement click events
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.support_facebook:
                        Uri uri = Uri.parse("https://m.me/playadda247"); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        //Toast.makeText(getActivity(), "Contact us clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.support_whatsapp:
                        Toast.makeText(getActivity(), "Terms and Conditions clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        popup.show();

    }*/

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(getActivity(), new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_COMPRESSION_QUALITY, 10);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_COMPRESSION_QUALITY, 10);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);


        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                UploadProfilePic(uri);
            }
        }
    }

    private void RetrieveUserData() {
        //progressDialog2.show();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = firebaseDatabase.getReference("Users").child(mAuth.getUid());
        myRef.keepSynced(true);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                tv_name.setText(userProfile.getName());
                tv_userName.setText(userProfile.getPubgUsername());
                tv_playCoins.setText(""+ userProfile.getWalletCoins());
                tv_matchPlayed.setText(""+ userProfile.getMatchPlayed());
                tv_totalKills.setText(""+ userProfile.getTotalKills());
                tv_totalWinnings.setText(""+ userProfile.getTotalWinnings());


                try{

                    //if image is received then set
                    Picasso.get().load(userProfile.getProfilePic()).networkPolicy(NetworkPolicy.OFFLINE).into(iv_profilepic, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressDialog2.dismiss();
                            shimmerLayout.stopShimmerAnimation();
                            nestedScrollView.setVisibility(View.VISIBLE);
                            shimmerLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(userProfile.getProfilePic()).into(iv_profilepic, new Callback() {
                                @Override
                                public void onSuccess() {
                                    progressDialog2.dismiss();
                                    shimmerLayout.stopShimmerAnimation();
                                    nestedScrollView.setVisibility(View.VISIBLE);
                                    shimmerLayout.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                        }
                    });

                }catch (Exception e){
                    //if there is any exception while getting image then set default
                    Picasso.get().load(R.drawable.userprofile).into(iv_profilepic);
                    progressDialog2.dismiss();
                    shimmerLayout.stopShimmerAnimation();
                    nestedScrollView.setVisibility(View.VISIBLE);
                    shimmerLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
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
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void ShowNotification(int amount) {
        Random r = new Random();
        NOTIFICATION_ID = r.nextInt(1000000);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                .setContentTitle(amount+ "added to your account")
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setColor(getResources().getColor(R.color.statusbar))
                .setAutoCancel(true)
                //.setContentText("You have successfully joined the match. Room ID and password will be shared before 15 minutes of match starting time.")
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


        NotificationManagerCompat manager = NotificationManagerCompat.from(getActivity());
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    private void UploadProfilePic(Uri uri) {

        //show progress
        progressDialog.show();

        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://pubg-battle-b640c.appspot.com")
                .child(mAuth.getUid()).child("ProfilePic");
        storageReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image is uploaded to storage, now get it's url and store in user's database
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        //Check if image is uploaded or not and url is received
                        if(uriTask.isSuccessful()){
                            //image uploaded
                            //UserProfile userProfile = new UserProfile(downloadUri.toString());
                            HashMap<String, Object> results = new HashMap<>();
                            results.put("profilePic", downloadUri.toString());
                            DatabaseReference myRef;
                            myRef = firebaseDatabase.getReference("Users").child(mAuth.getUid());
                            myRef.updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //url in the database of user is added successfully
                                    //dismiss progressbar
                                    progressDialog.dismiss();
                                    progressDialog2.show();
                                    Toast.makeText(getContext(),"Profile Picture Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //error adding url to database of user
                                            //dismiss progressbar
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(),"Error in Uploading Profile Picture", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"Some error occurred",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerLayout.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerLayout.stopShimmerAnimation();
    }
}
