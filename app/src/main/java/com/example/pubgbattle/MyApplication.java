package com.example.pubgbattle;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        FirebaseMessaging.getInstance().subscribeToTopic("global");

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        mInstance = this;

        /*if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
            //default value
            Map<String, Object> defaultValue = new HashMap<>();
            defaultValue.put(UpdateHelper.KEY_UPDATE_ENABLE, false);
            defaultValue.put(UpdateHelper.KEY_UPDATE_VERSION, "1.0");
            defaultValue.put(UpdateHelper.KEY_UPDATE_URL, "App_URL");

            remoteConfig.setDefaults(defaultValue);
            remoteConfig.fetch(5)  //fetch data from firebase every 5 seconds
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                remoteConfig.activateFetched();
                            }
                        }
                    });

        }*/

    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
