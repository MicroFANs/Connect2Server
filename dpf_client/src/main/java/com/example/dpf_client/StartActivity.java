package com.example.dpf_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;


import com.dd.CircularProgressButton;
import com.dd.processbutton.iml.ActionProcessButton;


public class StartActivity extends AppCompatActivity {
    //ProgressButton
    //ActionProcessButton mConnButton;
    CircularProgressButton mConnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();  //初始化控件
//        mConnButton.setMode(ActionProcessButton.Mode.ENDLESS);
//        mConnButton.setProgress(0);
//        mConnButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mConnButton.getProgress()!=1){
//                    mConnButton.setProgress(1);
//                }
//                else {
//                    mConnButton.setProgress(-1);
//                }
//            }
//        });

        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircularProgressButton btn=(CircularProgressButton) v;
                int progress=btn.getProgress();
                if (progress == 0) { // 初始时progress = 0
                    btn.setProgress(50); // 点击后开始不精准进度，不精准进度的进度值一直为50
                } else if (progress == 100) { // 如果当前进度为100，即完成状态，那么重新回到未完成的状态

                    btn.setProgress(0);

                } else if (progress == 50) { // 如果当前进度为50，那么点击后就显示完成的状态
                    btn.setProgress(100);
                }

            }
        });

    }

    //初始化控件
    private void initView() {
        //初始化ProgressButton
        //mConnButton=findViewById(R.id.start_conn_btn);
        mConnectButton=findViewById(R.id.start_progress_btn);
        mConnectButton.setText("CONNECT");
        mConnectButton.setCompleteText("Success");
        mConnectButton.setErrorText("ERROR");
        mConnectButton.setIdleText("CONNECT");
        mConnectButton.setIndeterminateProgressMode(true);
    }


}
