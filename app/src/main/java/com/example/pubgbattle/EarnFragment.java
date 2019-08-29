package com.example.pubgbattle;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import am.appwise.components.ni.NoInternetDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class EarnFragment extends Fragment {


    public EarnFragment() {
        // Required empty public constructor
    }
    private NoInternetDialog noInternetDialog;
    private CardView cv1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_earn, container, false);
        noInternetDialog = new NoInternetDialog.Builder(this)
                .setButtonColor(getResources().getColor(R.color.main))
                .build();

        cv1 = view.findViewById(R.id.fragment_earn_cv1);
        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ReferEarnActivity.class));
            }
        });

       return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        noInternetDialog.onDestroy();
    }

}
