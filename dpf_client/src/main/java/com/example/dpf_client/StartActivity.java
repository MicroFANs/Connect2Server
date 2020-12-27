package com.example.dpf_client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.example.dpf_client.Gson.GsonUtil;
import com.example.dpf_client.Util.HttpUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import scut.carson_ho.diy_view.SuperEditText;


public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    //常量
    private static final String TAG = "StartActivity";
    private static final String HEAD = "http://"; //Url头
    private static final String PORT = ":8888"; //端口
    private static final String ROUTE_CONNECT = "/connect"; //route，对应flask里的route

    //控件
    private CircularProgressButton mConnectBtn; //ProgressButton
    private SuperEditText mIPAddressEt;
    private SharedPreferences mReadSP; //读取记录
    private SharedPreferences.Editor mEditorSP; //保存记录


    private final OkHttpClient mOkClient = new OkHttpClient(); //单例，不用每次都创建新的Client

    //变量
    private String mIPAddress; //IP地址
    private String mIPInput; //输入框中的字符串
    private String mUrl; //http连接url
    private String mJson; //Json数据
    private int mProgress; //ProgressButton进度数值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();  //初始化控件

        //监听输入框，点击输入框时改变按钮状态为待连接状态
        mIPAddressEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mConnectBtn.setProgress(0);
                return false;
            }
        });
    }

    //初始化控件
    private void initView() {
        // ProgressButton; -1:ERROR; 0:Idle; 100:SUCCESS
        mConnectBtn = findViewById(R.id.start_btn_progress);
        mConnectBtn.setText("CONNECT");
        mConnectBtn.setCompleteText("SUCCESS");
        mConnectBtn.setErrorText("ERROR");
        mConnectBtn.setIdleText("CONNECT");
        mConnectBtn.setIndeterminateProgressMode(true);
        mConnectBtn.setOnClickListener(this);

        //IP地址输入框
        mIPAddressEt = findViewById(R.id.start_et_address);
        mIPAddressEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        String digits = "0123456789.";
        mIPAddressEt.setKeyListener(DigitsKeyListener.getInstance(digits)); //只能输入数字和.

        //初始化SharedPreferences读
        mReadSP=getSharedPreferences("default", Context.MODE_PRIVATE);
        //初始化写
        mEditorSP=mReadSP.edit();

        //读取上次保存的IP地址，填写到EditText
        mIPAddress = mReadSP.getString("ipAddress", "");
        mIPAddressEt.setText(mIPAddress);


    }

    //点击事件
    @Override
    public void onClick(View v) {
        mIPInput = mIPAddressEt.getText().toString(); //获取输入字符串
        mProgress = mConnectBtn.getProgress(); //进度

        switch (v.getId()) {
            case R.id.start_btn_progress:
                if (mIPInput.equals("")) {
                    Toast.makeText(this, "请输入IP地址", Toast.LENGTH_SHORT).show();
                } else {
                    mIPAddress = mIPInput;
                    mConnectBtn.setProgress(1);
                    Toast.makeText(this, mIPAddress, Toast.LENGTH_SHORT).show();
                    String url=HEAD+mIPAddress+PORT+ROUTE_CONNECT;
                    Connect2Server(mOkClient,url);
                }
                break;
        }
    }

    //连接服务器
    public void Connect2Server(OkHttpClient client,String url) {
        //回调函数
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mConnectBtn.setProgress(-1);
                String title="连接失败";
                String content="请输入正确的IP地址";
                showNotification(title,content,R.drawable.connectfail);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                mConnectBtn.setProgress(100);
                //解析json,这里是将json解析进String的Hashmap里，也可以定义一个实体类，直接解析成相应的对象
                HashMap<String,String> map= GsonUtil.json2Map(response.body().string());
                String title="服务器连接成功！";
                String content="您的ID为："+map.get("user_id")+"，上传通道为："+map.get("channel")+"，隐私预算为："+map.get("epsilon");
                showNotification(title,content,R.drawable.connectsuccess);
                //连接成功将IP地址保存到本地

                mEditorSP.putString("ipAddress",mIPAddress);
                mEditorSP.putString("epsilon", map.get("epsilon"));//保存隐私预算
                mEditorSP.putString("seed", map.get("user_id"));//保存用户id作为哈希种子
                mEditorSP.putString("channel", map.get("channel"));//保存用户上传信道
                mEditorSP.apply();

                //成功登录，跳转页面
                Intent intent=new Intent(StartActivity.this,MainActivity.class);
                startActivity(intent);
            }
        };
        HttpUtil.sendOkHttpRequest(client,url,callback);
    }

    //构建通知
    public void showNotification(String title,String content,int icon){
        NotificationManager mManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification=null;

        //低于Android8.0
        if(Build.VERSION.SDK_INT<android.os.Build.VERSION_CODES.O){
            notification=new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setSmallIcon(icon)
                    .setShowWhen(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
        }
        //高于android8.0要设置channel
        else if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            String channelID="channelID";
            String channelName="channelName";
            int importance=NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel=new NotificationChannel(channelID,channelName,importance);
            assert mManager != null;
            mManager.createNotificationChannel(channel);
            notification=new NotificationCompat.Builder(this, channelID)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setSmallIcon(icon)
                    .setShowWhen(true)
                    .build();
        }
        mManager.notify(1, notification);
    }
}
