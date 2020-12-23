package com.example.dpf_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;


import com.dd.processbutton.iml.ActionProcessButton;


public class StartActivity extends AppCompatActivity {
    ActionProcessButton mConnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();
        mConnButton.setMode(ActionProcessButton.Mode.ENDLESS);
        mConnButton.setProgress(0);
        mConnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mConnButton.getProgress()!=1){
                    mConnButton.setProgress(1);
                }
                else {
                    mConnButton.setProgress(-1);
                }
            }
        });

    }

    private void initView() {
        mConnButton=findViewById(R.id.start_conn_btn);

    }


}
