package com.example.connect2server;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingDeque;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HHHTTP";
    private static final String HEAD="http://";
    private static final String PORT=":8888";
    private static final String CONNECT="/connect";
    private static final String REGISTER="/register";
    private static final String UPLOAD="/upload";
    private static final String UPONESAMPEL="/upOneSample";

    private Button mConnect;
    private Button mAdd;
    private Button mDelete;
    private Button mSend;
    private Button mStart;
    private EditText mIPAddress;
    private EditText mKey;
    private EditText mValue;
    private TextView mShowData;
    private TextView mShowProgress;
    private TextView mStatus;
    private ImageView mIcon;
    private ProgressBar mProgress;



    private String mIP;
    private String mUrl;
    private String mJson;
    private double epsilon;
    private StringBuilder mShow=new StringBuilder(1024);
    public final OkHttpClient client = new OkHttpClient();
    private SharedPreferences mRead;//用来读
    private SharedPreferences.Editor mEditor;//用来存

    private HashMap<Integer,Double> dataMap=new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews(){
        mSend = findViewById(R.id.btn_send);
        mKey = findViewById(R.id.et_key);
        mValue = findViewById(R.id.et_value);
        mConnect=findViewById(R.id.btn_connect);
        mAdd=findViewById(R.id.btn_add);
        mDelete=findViewById(R.id.btn_delete);
        mIcon=findViewById(R.id.img_icon);
        mProgress=findViewById(R.id.progress);
        mShowProgress=findViewById(R.id.tv_progress);
        mStatus=findViewById(R.id.tv_status);
        mStart=findViewById(R.id.btn_start);

        //输入ip地址
        mIPAddress=findViewById(R.id.et_ip_address);
        mIPAddress.setInputType(InputType.TYPE_CLASS_NUMBER);
        String digits = "0123456789.";
        mIPAddress.setKeyListener(DigitsKeyListener.getInstance(digits));

        mShowData=findViewById(R.id.tv_showdata);
        mShowData.setMovementMethod(ScrollingMovementMethod.getInstance());

        //SharedPerferences
        mRead= PreferenceManager.getDefaultSharedPreferences(this);
        //读取上次保存的IP地址，填写到edittext
        mIP=mRead.getString("ipAddress","");
        mIPAddress.setText(mIP);

        //读取上次发送成功的json，还原成HashMap
        mJson=mRead.getString("datajson","");
        if(!mJson.equals("")){
        dataMap=Utils.json2Map(mJson);}
        Log.d(TAG, "initViews: "+dataMap.toString());
        ShowData();

        mConnect.setOnClickListener(this);
        mAdd.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        mSend.setOnClickListener(this);
        mStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String ipInput=mIPAddress.getText().toString();
        switch (view.getId()){
            case R.id.btn_connect:
                if(ipInput.equals("")){
                    Toast.makeText(this,"请输入ip地址",Toast.LENGTH_SHORT).show();
                }
                else {
                    mIP=ipInput;
                    Connect(mIP,CONNECT);
                }
                break;
            case R.id.btn_add:
                String key = String.valueOf(mKey.getText());
                String value = String.valueOf(mValue.getText());
                if(key.equals("") || value.equals("")){
                    Toast.makeText(this,"数据不能为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    int k=Integer.parseInt(key);
                    double v=Double.parseDouble(value);
                    if(dataMap.containsKey(k)){
                        Toast.makeText(this,"key值已存在",Toast.LENGTH_SHORT).show();
                    }
                    else dataMap.put(k,v);
                    Utils.showToast(this,"添加数据成功");
                    Log.d(TAG, "Hashmap:"+dataMap.toString());
                    ShowData();
                    mKey.setText("");
                    mValue.setText("");
                }
                break;
            case R.id.btn_delete:
                key = String.valueOf(mKey.getText());
                if(key.equals("")){
                    Toast.makeText(this,"key不能为空",Toast.LENGTH_SHORT).show();
                }
                else {
                    int k=Integer.parseInt(key);
                    if(dataMap.containsKey(k)){
                        dataMap.remove(k);
                        Utils.showToast(this,"删除数据成功");
                        ShowData();
                        mKey.setText("");
                        mValue.setText("");
                    }
                    else {
                        Utils.showToast(this,"key值不存在");
                    }
                }
                break;
            case R.id.btn_send:
                mJson=Utils.map2Json(dataMap);
                Log.d(TAG, ""+mJson);
                sendOriginData(mIP,"datajson",mJson,UPLOAD);
                break;
            case R.id.btn_start:
                upOneSample(mIP,"oneSample",UPONESAMPEL);
                break;
        }
    }

    private void ShowData(){
        mShow.setLength(0);
        for(int i :dataMap.keySet()){
            mShow.append("| key: ")
                    .append(i)
                    .append(" | value: ")
                    .append(dataMap.get(i))
                    .append(" |\n");
        }
        mShowData.setText(mShow.toString());
    }

    private void Connect(final String ip,String route){
        mUrl=HEAD+ip+PORT+route;
        Request request=new Request.Builder().url(mUrl).build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"连接服务器失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
                        mIcon.setImageResource(R.drawable.green);
                        mStatus.setText("服务器连接成功");
                        //设置进度条
                        showProgress(10);

                    }
                });

                //成功则将IP地址保存到本地
                mEditor=mRead.edit();
                mEditor.putString("ipAddress",ip);
                mEditor.apply();
            }
        });
    }
    private void SendMessage(final String ip, final String userName, String passWord) {
       // OkHttpClient client = new OkHttpClient();
        mUrl=HEAD+ip+PORT+REGISTER;
        Log.d(TAG, "onClick: "+mUrl);
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("username", userName);
        formBuilder.add("password", passWord);
        Request request = new Request.Builder().url(mUrl).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                mEditor=mRead.edit();
                mEditor.putString("ipAddress",ip);
                mEditor.apply();

                final String res = response.body().string();
                Log.d(TAG, "onResponse: "+res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals("0")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "该用户名已被注册", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mShowData.setText(res);
                                    Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    }
                });
            }
        });

    }
    private void sendOriginData(final String ip, final String name, final String json, String route) {
        mUrl=HEAD+ip+PORT+route;
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add(name, json);
        Request request = new Request.Builder().url(mUrl).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String res = response.body().string();
                Log.d(TAG, "onResponse: "+res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();
                        mStatus.setText("发送原始数据");
                    }
                });

                //成功则将json保存到本地,下次直接读取
                mEditor=mRead.edit();
                mEditor.putString(name,json);
                mEditor.apply();
            }
        });

    }


    //OLH扰动
    private int OLH_Perturbation(double epsilon){
        //hash
        //grr
        return 1;
    }

    //从自己的项集中选择一个项，扰动后上传,并返回候选集
    private void upOneSample(final String ip,String name,String route){
        //随机获取一个kv对
        Random random=new Random();
        Integer[] keys = dataMap.keySet().toArray(new Integer[0]);
        Integer randomKey = keys[random.nextInt(keys.length)];
        Double randomValue = dataMap.get(randomKey);

        // TODO:  添加扰动
        randomKey=12;

        //同时还要上传自己的hash的seed，聚合会用到
        JsonObject msgObj = new JsonObject();
        msgObj.addProperty("key", randomKey);
        msgObj.addProperty("hashseed", 111);
        String json= msgObj.toString();

        mUrl=HEAD+ip+PORT+route;
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add(name, json);
        Request request = new Request.Builder().url(mUrl).post(formBuilder.build()).build();
        Call call = client.newCall(request);


        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String res = response.body().string();

                HashMap<Integer,Integer> candidate=Utils.json2IntegerMap(res);
                Log.d(TAG, "onResponse: "+candidate.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "成功返回候选集", Toast.LENGTH_SHORT).show();
                        showProgress(20);
                        mStatus.setText("上传采样数据，获取候选集");
                    }
                });

            }
        });


    }

    //根据候选项集获取交集个数
    private int getIntersectionSize(){
        return 0;
    }

    //上传扰动后交集个数,并返回填充项长度L
    private void SendIntersectionSize(final String ip,final int size){

    }

    //根据填充项长度填充或截断本地项集
    private void getUPKV(){}

    //PCKV扰动，并上传
    private void SendKV(final String ip,String json){

    }

    //需要的时候在这里手动保存Json，省的一个一个手动输入，在onCreate里执行一下就行了
    private void saveJson(){
        String input="{"+669+":1.0,"+2274+":0.6,"+176+":1.0,"+786+":1.0,"+228+":1.0,"+4616+":-0.2,"+1682+":1.0}";
        //成功则将json保存到本地,下次直接读取
        mEditor=mRead.edit();
        mEditor.putString("datajson",input);
        mEditor.apply();
    }

    private void showProgress(int progress){
        mProgress.setProgress(progress);
        mShowProgress.setText(progress+"%");
    }

   }
