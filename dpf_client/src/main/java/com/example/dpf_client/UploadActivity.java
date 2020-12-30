package com.example.dpf_client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dpf_client.Gson.GsonUtil;
import com.example.dpf_client.Util.BarChartDialog;
import com.example.dpf_client.Util.HttpUtil;
import com.example.dpf_client.Util.OLH;
import com.example.dpf_client.Util.PointProcessBar;
import com.example.dpf_client.Util.Record;
import com.example.dpf_client.Util.RecyclerViewDivider;
import com.example.dpf_client.Util.Response;
import com.example.dpf_client.Util.ResponseAdapter;
import com.example.dpf_client.Util.TimeUtil;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;

public class UploadActivity extends AppCompatActivity {

    //常量
    private static final String TAG = "StartActivity";
    private static final String HEAD = "http://"; //Url头
    private static final String PORT = ":8888"; //端口
    private static final String ROUTE_CONNECT = "/connect"; //route，对应flask里的route
    private static final String ROUTE_UP1 = "/upOneSample"; //route，对应flask里的route
    private static final String ROUTE_UP2 = "/upNumber"; //route，对应flask里的route
    private static final String ROUTE_UP3 = "/upOneSampleUE"; //route，对应flask里的route


    private final OkHttpClient mOkClient = new OkHttpClient(); //单例，不用每次都创建新的Client
    private final Gson gson = new Gson();

    private PointProcessBar pointProcessBar;//节点进度条
    private RecyclerView mResponseRecyclerView; //RecyclerView
    private ResponseAdapter mResponseAdapter; //Adapter

    private SharedPreferences mReadSP; //读取记录
    private SharedPreferences.Editor mEditorSP; //保存记录

    private FloatingActionButton mStartFaBtn;//开始上传
    private FloatingActionButton mCleanFaBtn;//清除数据
    private FloatingActionButton mChartFaBtn;//显示图表

    private BarChartDialog mBarChartDialog;//图表dialog

    private List<String> pointTitle; //节点文字
    private Set<Integer> progressIndex; //执行节点的编号
    private ArrayList<Record> recordArrayList = new ArrayList<>(); //用户拥有的项集
    private ArrayList<Response> responseArrayList = new ArrayList<>(); //操作步骤记录
    private List<BarEntry> chartList = new ArrayList<>();//图表数据
    private ArrayList<String> label = new ArrayList<>();//label数据标签
    private String[] mCandidateSet; //候选项集的key
    private String[] mEstimateData;//估计结果
    private int mPadLength; //填充项长度
    private double mEpsilon;//隐私预算
    private int hashDomain;//哈希域，就是g
    private String mSeed;//每个用户的哈希种子，其实就是用户id
    private String mIPAddress; //IP地址


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //得到homeFragment的list
        Intent intent = getIntent();
        recordArrayList = (ArrayList<Record>) intent.getSerializableExtra("recordsList");

        initView();

        mStartFaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleOne_upload(mOkClient, ROUTE_UP1);
            }
        });
        mCleanFaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeResponse();
            }
        });
        mChartFaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chartList.size() == 0) {
                    Toast.makeText(UploadActivity.this, "数据为空，请先上传记录", Toast.LENGTH_SHORT).show();
                } else {
                    mBarChartDialog = new BarChartDialog(UploadActivity.this, chartList, label);
                    mBarChartDialog.setCancelable(true);
                    mBarChartDialog.show();

                    //设置dialog的大小
                    Window dialogWindow = mBarChartDialog.getWindow();
                    WindowManager m = getWindowManager();
                    Display d = m.getDefaultDisplay();
                    WindowManager.LayoutParams p = dialogWindow.getAttributes();
                    p.height = (int) (d.getHeight() * 0.8);
                    p.width = (int) (d.getWidth() * 1);
                    dialogWindow.setAttributes(p);
                }
            }
        });
    }

    private void initView() {
        pointProcessBar = findViewById(R.id.upload_pointProgress);
        pointTitle = new ArrayList<>();
        pointTitle.add("Step1");
        pointTitle.add("Step2");
        pointTitle.add("Step3");
        pointTitle.add("Step4");
        pointTitle.add("Step5");
        pointTitle.add("Step6");
        progressIndex = new HashSet<>();
        progressIndex.add(0);
        pointProcessBar.show(pointTitle, progressIndex);

        mStartFaBtn = findViewById(R.id.upload_fabtn_start);
        mCleanFaBtn = findViewById(R.id.upload_fabtn_clear);
        mChartFaBtn = findViewById(R.id.upload_fabtn_result);

        mReadSP = getSharedPreferences("default", Context.MODE_PRIVATE);//初始化SharedPreferences读
        mEditorSP = mReadSP.edit();//初始化写
        mEpsilon = Double.parseDouble(mReadSP.getString("epsilon", "1"));//得到服务器的epsilon
        hashDomain = OLH.hashDomain(mEpsilon); //接着计算g
        mIPAddress = mReadSP.getString("ipAddress", "");//得到ip地址
        mSeed = mReadSP.getString("seed", "");//得到种子
        //得到之前的采集记录
        String responseArrayListJson = mReadSP.getString("responseArrayList", "");
        if (responseArrayListJson.isEmpty()) {
            responseArrayList = new ArrayList<>();
        } else {
            responseArrayList = gson.fromJson(responseArrayListJson, new TypeToken<List<Response>>() {
            }.getType());
        }

        //RecyclerView
        mResponseRecyclerView = findViewById(R.id.upload_rv_response);//获取RecyclerView
        mResponseAdapter = new ResponseAdapter(responseArrayList);//创建Adapter
        mResponseRecyclerView.setAdapter(mResponseAdapter);//给RecyclerView设置adapter
        mResponseRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));//设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局,参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        //mRecordRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));//设置item的分割线
        mResponseRecyclerView.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL, 25, R.color.black));//设置自定义item的分割线
        mResponseRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置item添加和删除的动画
        //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义
        mResponseAdapter.setOnItemClickListener(new ResponseAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Response data) {
                //此处进行监听事件的业务处理
            }
        });
    }

    //添加项
    private void addResponse(Response response) {
        responseArrayList.add(0, response);//每次都添加在第0行
        mResponseAdapter.notifyItemInserted(0);
        mResponseAdapter.notifyItemRangeChanged(0, responseArrayList.size());
        mResponseRecyclerView.scrollToPosition(0);//刷新显示第0行
    }

    //删除项
    private void removeResponse() {
        responseArrayList.clear();
        if (mResponseRecyclerView.getChildCount() > 0) {
            mResponseRecyclerView.removeAllViews();
            mResponseAdapter.notifyItemRangeChanged(0, responseArrayList.size());
        }
        progressIndex.clear();
        progressIndex.add(0);
        pointProcessBar.refreshSelectedIndexSet(progressIndex);

    }

    /* 阶段1 */
    //从用户项集中随机获取一个项并扰动
    private int sampleOne_perturbed(ArrayList<Record> list) {
        Random random = new Random();
        int n = random.nextInt(list.size());
        Record record = list.get(n);
        int key = record.getKey();
        int x = OLH.hash(String.valueOf(key), mEpsilon, hashDomain);
        double p = OLH.p_value(mEpsilon, hashDomain);
        int y = OLH.grr(p, x, hashDomain);
        return y;
    }

    //上传扰动的项
    private void sampleOne_upload(OkHttpClient client, String route) {
        //先计算扰动结果
        int perturbed_one_sample = sampleOne_perturbed(recordArrayList);//扰动结果（int）
        String perturbed_key = String.valueOf(perturbed_one_sample);

        addResponse(new Response("Step1：采样上传成功!", "扰动后key：" + perturbed_key + "\n哈希种子：" + mSeed, TimeUtil.getTime()));

        HashMap<String, String> map = new HashMap<>();//使用HashMap生成Json
        map.put("perturbed_key", perturbed_key);//扰动结果转为Sting类型
        map.put("seed", mSeed);//哈希种子
        String json = GsonUtil.map2json(map); //获得json
        //同时还要上传自己的hash的seed，聚合会用到
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("OneSample", json);
        FormBody body = formBuilder.build(); //封装

        //设置Callback
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UploadActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String responseJson = response.body().string();
                final HashMap<String, String> candidate = GsonUtil.json2Map(responseJson);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  Log.d(TAG, "onResponse: "+candidate.toString());
                        mCandidateSet = new String[candidate.size()];
                        for (int j = 0; j < candidate.size(); j++) {
                            mCandidateSet[j] = candidate.get("" + (j + 1));
                            label.add(candidate.get("" + (j + 1)));
                        }
                        addResponse(new Response("Step2：返回候选项集成功!", "候选项集为：\n" + Arrays.toString(mCandidateSet), TimeUtil.getTime()));//增加记录
                        //更新进度条
                        progressIndex.add(1);
                        pointProcessBar.refreshSelectedIndexSet(progressIndex);

                        //进行step3
                        number_upload(mOkClient, ROUTE_UP2);
                    }
                });

            }
        };
        String url = HEAD + mIPAddress + PORT + route;//生成url
        HttpUtil.sendOkHttpRequest(client, url, body, callback);//发送
    }

    /* 阶段2 */
    private ArrayList<Record> getIntersection(String[] candidate) {
        List<String> c = Arrays.asList(candidate);
        ArrayList<Record> intersection = new ArrayList<>();
        for (int j = 0; j < recordArrayList.size(); j++) {
            if (c.contains(String.valueOf(recordArrayList.get(j).getKey()))) {
                intersection.add(recordArrayList.get(j));
            }
        }
        return intersection;
    }

    //扰动交集的数目
    private void number_upload(final OkHttpClient client, String route) {
        ArrayList<Record> intersection = getIntersection(mCandidateSet);
        int num = intersection.size();
        int x = OLH.hash(String.valueOf(num), mEpsilon, hashDomain);
        double p = OLH.p_value(mEpsilon, hashDomain);
        int y = OLH.grr(p, x, hashDomain);
        addResponse(new Response("Step3：上传交集长度成功!", "扰动交集数目为：" + y, TimeUtil.getTime()));//增加记录
        //更新进度条
        progressIndex.add(2);
        pointProcessBar.refreshSelectedIndexSet(progressIndex);
        HashMap<String, String> map = new HashMap<>();//使用HashMap生成Json
        map.put("perturbed_number", String.valueOf(y));//扰动结果转为Sting类型
        String json = GsonUtil.map2json(map); //获得json
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("Number", json);
        FormBody body = formBuilder.build(); //封装

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                //返回填充长度
                final String responseJson = response.body().string();
                mPadLength = Integer.valueOf(responseJson);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addResponse(new Response("Step4：获取填充长度成功!", "填充长度为：" + responseJson, TimeUtil.getTime()));//增加记录
                        //更新进度条
                        progressIndex.add(3);
                        pointProcessBar.refreshSelectedIndexSet(progressIndex);
                        //进行Step5
                        OneSampleFromCandidate_upload(client, ROUTE_UP3);
                    }
                });

            }
        };
        String url = HEAD + mIPAddress + PORT + route;//生成url
        HttpUtil.sendOkHttpRequest(client, url, body, callback);//发送
    }

    /*阶段3*/
    private String padSetAndSample(int length, ArrayList<Record> records) {
        //填充

        Random r = new Random();
        for (int i = 0; i < length; i++) {
            records.add(new Record(5080 + (i + 1), null, 0, r.nextInt(11)));
            //mCandidateSet[l+i]=String.valueOf(5080+i);
        }
        //UE采样+扰动
        int[] vector = new int[records.size()];
        for (int j = 0; j < records.size(); j++) {
            vector[j] = r.nextInt(2);
        }
        return Arrays.toString(vector);
    }

    private void OneSampleFromCandidate_upload(OkHttpClient client, String route) {
        String perturbed_vector = padSetAndSample(mPadLength, recordArrayList);

        addResponse(new Response("Step5：采样上传成功!", "扰动向量为：\n" + perturbed_vector, TimeUtil.getTime()));
        progressIndex.add(4);
        pointProcessBar.refreshSelectedIndexSet(progressIndex);

        HashMap<String, String> map = new HashMap<>();//使用HashMap生成Json
        map.put("perturbed_vector", perturbed_vector);//扰动结果转为Sting类型
        String json = GsonUtil.map2json(map); //获得json

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("OneSampleUE", json);
        FormBody body = formBuilder.build(); //封装

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                String responseJson = response.body().string();
                final HashMap<String, String> estimateData = GsonUtil.json2Map(responseJson);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mEstimateData = new String[estimateData.size()];
                        for (int j = 0; j < estimateData.size(); j++) {
                            mEstimateData[j] = estimateData.get("" + (j + 1));
                            chartList.add(new BarEntry(j + 1, Float.valueOf(estimateData.get("" + (j + 1)))));
                        }
                        addResponse(new Response("Step6：返回候选项集成功!", "topk估计值为：\n" + Arrays.toString(mEstimateData), TimeUtil.getTime()));//增加记录
                        //更新进度条
                        progressIndex.add(5);
                        pointProcessBar.refreshSelectedIndexSet(progressIndex);
                    }
                });

            }

        };
        String url = HEAD + mIPAddress + PORT + route;//生成url
        HttpUtil.sendOkHttpRequest(client, url, body, callback);//发送
    }

    @Override
    protected void onDestroy() {
        //保存自定义对象ArrayList到SharedPreferences的方法
        String responseJson = gson.toJson(responseArrayList);
        mEditorSP.putString("responseArrayList", responseJson);
        mEditorSP.commit();
        super.onDestroy();


    }
}