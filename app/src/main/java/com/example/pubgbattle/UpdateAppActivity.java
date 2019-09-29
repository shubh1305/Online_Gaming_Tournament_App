package com.example.pubgbattle;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class UpdateAppActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, LifecycleObserver {
    private static final int WRITE_REQUEST_CODE = 300;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String url;
    private DatabaseReference myRef, myRef2, myRef3;
    private Context context;
    private TextView tv_version, tv_date;
    private Button btn_update;
    private int flag = 0;
    private RecyclerView rv_update_detail;
    private ArrayList<AppUpdateDetailModel> detailList;
    private AppUpdateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);
        myRef = FirebaseDatabase.getInstance().getReference("Version").child("appURL");
        myRef2 = FirebaseDatabase.getInstance().getReference("Version").child("details");
        myRef3 = FirebaseDatabase.getInstance().getReference("Version");

        context = this;
        btn_update = findViewById(R.id.activity_update_app_btn);
        tv_version = findViewById(R.id.activity_update_app_version);
        tv_date = findViewById(R.id.activity_update_app_date);

        //initialize recycleview
        rv_update_detail = findViewById(R.id.activity_update_app_rv);
        rv_update_detail.setLayoutManager(new LinearLayoutManager(this));

        //initialize arraylist
        detailList = new ArrayList<>();

        //initializing adapter
        adapter = new AppUpdateAdapter(UpdateAppActivity.this, detailList);
        //setting adapter into recyclerview
        rv_update_detail.setAdapter(adapter);
        FetchData(myRef3);
        SetUpRecyclerView(myRef2);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if SD card is present or not
                if (CheckForSDCard.isSDCardPresent()) {

                    //check if app has permission to write to the external storage.
                    if (EasyPermissions.hasPermissions(UpdateAppActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //Get the URL entered
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                url = (String) dataSnapshot.getValue();
                                new DownloadFile().execute(url);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        //If permission is not present request for the same.
                        EasyPermissions.requestPermissions(UpdateAppActivity.this, getString(R.string.write_file), WRITE_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                    }


                } else {
                    Toast.makeText(getApplicationContext(),
                            "SD Card not found", Toast.LENGTH_LONG).show();

                }
            }
        });


    }

    private void ShowAlertDialog(File file) {
        AlertDialog alertDialog = new AlertDialog.Builder(UpdateAppActivity.this)
                //.setTitle("Downloading finished")
                .setMessage("Do you want to install the app update now!")
                .setPositiveButton("INSTALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Install_apk(file);
                        //startActivity(new Intent(UpdateAppActivity.this, UpdateAppActivity.class));
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

    private void FetchData(DatabaseReference myRef3) {
        myRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String version = (String) dataSnapshot.child("versionName").getValue();
                String date = (String) dataSnapshot.child("updatedOn").getValue();

                tv_version.setText(version);
                tv_date.setText(date);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SetUpRecyclerView(DatabaseReference myRef2) {
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                detailList.clear();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    final AppUpdateDetailModel detail  = d.getValue(AppUpdateDetailModel.class);
                    detailList.add(detail);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, UpdateAppActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //Download the file once permission is granted
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                url = (String) dataSnapshot.getValue();
                new DownloadFile().execute(url);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }
    /**
     * Async Task to download file from URL
     */
    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private File folder2;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.progressDialog = new ProgressDialog(UpdateAppActivity.this);
            //this.progressDialog.setTitle("Downloading...");
            this.progressDialog.setMessage("Downloading... 0%");
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.progressDialog.setCancelable(false);
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.show();
        }
        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try
            {
                URL url = new URL(f_url[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // getting file length
                //connection.setRequestProperty("Accept-Encoding", "identity");
                long lengthOfFile = 12500000;
                //long lengthOfFile = Long.parseLong(connection.getHeaderField("13631488"));
                Log.d(TAG, "Length og file: " + lengthOfFile);


                // input stream to read file - with 8k buffer
                //InputStream input = new BufferedInputStream(url.openStream(), 8192);
                InputStream input = new BufferedInputStream(connection.getInputStream());

                //String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());


                //Append timestamp to file name
                //fileName = timestamp + "_" + fileName;

                //External directory path to save file
                //folder = Environment.getExternalStorageDirectory() + File.separator + "download/";
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

                    folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator;

                    //Create Downloads folder if it does not exist
                    File directory = new File(folder);

                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    // Output stream to write file
                    OutputStream output = new FileOutputStream(folder + fileName);

                    byte data[] = new byte[4*1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) (total * 100 / lengthOfFile));
                        //Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();
                    return "Downloaded at: " + folder + fileName;
                }
                else
                {
                    folder2 = UpdateAppActivity.this.getExternalFilesDir(null);

                    //Create androiddeft folder if it does not exist

                    /*if (!folder2.exists()) {
                        folder2.mkdirs();
                    }*/

                    // Output stream to write file
                    OutputStream output = new FileOutputStream(folder2 + File.separator + fileName);

                    byte data[] = new byte[4*1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) (total * 100 / lengthOfFile));
                        //Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();
                    return "Downloaded at: " + folder2 + fileName;
                }

            }
            catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }
        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            //this.progressDialog.setProgress(Integer.parseInt(progress[0]));
            if(Integer.parseInt(progress[0])<=100)
                this.progressDialog.setMessage("Downloading... "+ Integer.parseInt(progress[0]) + "%");
            else
                this.progressDialog.setMessage("Finishing...");
            Log.d(TAG, "Progress: " + Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            // Display File path after downloading
            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            //String file_path = Environment.getExternalStorageDirectory() + File.separator + "download/playadda.apk";

            String file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + fileName;
            String file_path2 = folder2 + File.separator + fileName;
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                Install_apk(new File(file_path));
            else {
                if (getPackageManager().canRequestPackageInstalls()){
                    Install_apk(new File(file_path2));
                }
                else {
                    //ShowAlertDialog(new File(file_path2));
                    DynamicToast.makeWarning(UpdateAppActivity.this, "Please allow Playadda247 to install the updste.", 5000).show();
                    startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:com.example.pubgbattle")));
                    flag = 1;
                }
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(flag == 1){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                if (getPackageManager().canRequestPackageInstalls()){
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            url = (String) dataSnapshot.getValue();
                            String fileName = url.substring(url.lastIndexOf('/') + 1);
                            File file_path = UpdateAppActivity.this.getExternalFilesDir(null);
                            String file_path_str = file_path + File.separator + fileName;
                            //ShowAlertDialog(new File(file_path_str));
                            Install_apk(new File(file_path_str));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
            else {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        url = (String) dataSnapshot.getValue();
                        String fileName = url.substring(url.lastIndexOf('/') + 1);
                        File file_path = UpdateAppActivity.this.getExternalFilesDir(null);
                        String file_path_str = file_path + File.separator + fileName;
                        //ShowAlertDialog(new File(file_path_str));
                        Install_apk(new File(file_path_str));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            flag = 0;
        }
    }

    private void Install_apk(File file) {
        try {
            if (file.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri uri = getFileUri(context, file);
                    Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(uri,
                            "application/vnd.android.package-archive");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file),
                            "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                //file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri getFileUri(Context context, File file) {
        return FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".provider"
                , file);
    }
}
