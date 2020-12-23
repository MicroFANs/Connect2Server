package com.example.dpf_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.dd.processbutton.iml.ActionProcessButton;


public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    //控件

    private CircularProgressButton mConnectBtn; //ProgressButton
    private EditText mIPAddressEt;

    private SharedPreferences mReadSP; //读取记录
    private SharedPreferences.Editor mEditorSP; //保存记录


    //变量
    private String mIPAddress; //IP地址
    private String mIPInput; //输入框中的字符串
    private int mProgress; //ProgressButton进度数值


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();  //初始化控件

    }

    //初始化控件
    private void initView() {

        //mConnButton=findViewById(R.id.start_conn_btn);
        // ProgressButton; -1:ERROR; 0:Idle; 100:SUCCESS
        mConnectBtn=findViewById(R.id.start_btn_progress);
        mConnectBtn.setText("CONNECT");
        mConnectBtn.setCompleteText("SUCCESS");
        mConnectBtn.setErrorText("ERROR");
        mConnectBtn.setIdleText("CONNECT");
        mConnectBtn.setIndeterminateProgressMode(true);
        mConnectBtn.setOnClickListener(this);

        //IP地址输入框
        mIPAddressEt=findViewById(R.id.start_et_address);
        mIPAddressEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        String digits = "0123456789.";
        mIPAddressEt.setKeyListener(DigitsKeyListener.getInstance(digits)); //只能输入数字和.

        //SharedPreferences读写记录
        mReadSP =PreferenceManager.getDefaultSharedPreferences(this);

        //读取上次保存的IP地址，填写到EditText
        mIPAddress=mReadSP.getString("ipAddress","");
        mIPAddressEt.setText(mIPAddress);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        mIPInput=mIPAddressEt.getText().toString(); //获取输入字符串
        mProgress=mConnectBtn.getProgress(); //进度
        switch (v.getId()){
            case R.id.start_btn_progress:
                if(mIPInput.equals("")){
                    Toast.makeText(this,"请输入IP地址",Toast.LENGTH_SHORT).show();
                }
                else {
                    mIPAddress=mIPInput;
                    mConnectBtn.setProgress(1);
                    Toast.makeText(this,mIPAddress,Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
