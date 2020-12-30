package com.example.dpf_client.ui.dashboard;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.dd.CircularProgressButton;
import com.example.dpf_client.Gson.GsonUtil;
import com.example.dpf_client.MainActivity;
import com.example.dpf_client.R;
import com.example.dpf_client.Util.HttpUtil;
import com.example.dpf_client.Util.Location;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static android.content.Context.NOTIFICATION_SERVICE;

public class DashboardFragment extends Fragment {

    private static final String HEAD = "http://"; //Url头
    private static final String PORT = ":8888"; //端口
    private static final String ROUTE_GET_LOCATION = "/getLocation"; //route，对应flask里的route
    private static final String ROUTE_GET_CLUSTER = "/getCluster"; //route，对应flask里的route
    private final OkHttpClient mOkClient = new OkHttpClient(); //单例，不用每次都创建新的Client
    private DashboardViewModel dashboardViewModel;
    private CircularProgressButton mStartBtn;
    private CircularProgressButton mLocationBtn;
    private ScatterChart mScatterChart;
    private int mLocationX;
    private int mLocationY;
    private float relativeX;
    private float relativeY;
    private LimitLine xLimitLine;
    private LimitLine yLimitLine;
    private int id;
    private String mIPAddress; //IP地址

    private SharedPreferences mReadSP;//读取记录
    private SharedPreferences.Editor mEditorSP; //保存记录

    private ArrayList<Location> locations = new ArrayList<>();//所有点的list

    private ArrayList<Entry> chartMap = new ArrayList<>();
    private ArrayList<Entry> cluster0 = new ArrayList<Entry>();//原始数据
    private ArrayList<Entry> cluster1 = new ArrayList<Entry>();//簇1
    private ArrayList<Entry> cluster2 = new ArrayList<Entry>();//簇2
    private ArrayList<Entry> cluster3 = new ArrayList<Entry>();//簇3
    private ArrayList<Entry> cluster4 = new ArrayList<Entry>();//簇4
    private ArrayList<Entry> cluster5 = new ArrayList<Entry>();//簇5
    private ArrayList<IScatterDataSet> dataSets = new ArrayList<>();//数据类型的list
    private ScatterData data;//数据源


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initSharedPreference();
        initView(root);
        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationBtn.setProgress(50);
                showNotification("您的位置信息为：", "X：" + mLocationX + "，Y：" + mLocationY, R.drawable.connectsuccess);
                AlertDialog.Builder dialog = new AlertDialog.Builder((MainActivity) getActivity());
                dialog.setTitle("您的位置信息为：");
                dialog.setMessage("X：" + mLocationX + "，Y：" + mLocationY);
                dialog.setCancelable(true);
                dialog.show();
                String url = HEAD + mIPAddress + PORT + ROUTE_GET_LOCATION;//生成url
                getLocation(mOkClient, url);
                mLocationBtn.setProgress(100);

            }
        });

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationBtn.setProgress(50);
                String url = HEAD + mIPAddress + PORT + ROUTE_GET_CLUSTER;//生成url
                getCluster(mOkClient, url);
                mStartBtn.setProgress(100);
            }
        });

        return root;
    }

    private void initView(View view) {

        mLocationBtn = view.findViewById(R.id.dashboard_btn_location);
        mStartBtn = view.findViewById(R.id.dashboard_btn_start);
        mScatterChart = view.findViewById(R.id.scatterChart);
        mStartBtn.setText("上传数据");
        mStartBtn.setCompleteText("上传成功");
        mStartBtn.setErrorText("上传失败");
        mStartBtn.setIdleText("上传数据");
        mStartBtn.setIndeterminateProgressMode(true);
        mLocationBtn.setText("获取位置");
        mLocationBtn.setCompleteText("获取成功");
        mLocationBtn.setErrorText("获取失败");
        mLocationBtn.setIdleText("获取位置");
        mLocationBtn.setIndeterminateProgressMode(true);
        mStartBtn.setProgress(0);
        mLocationBtn.setProgress(0);
        mLocationX = 371464;
        mLocationY = 328177;
        relativeX = 0.446f;
        relativeY = 0.636f;

        mIPAddress = mReadSP.getString("ipAddress", "");//得到ip地址

        initChartMap();//初始化散点图图表

    }

    private void initSharedPreference() {
        //Sharedpreferences
        //getActivity()方法一定要放在onCreateView这个方法里才能生效，否则只能返回null，涉及到Fragment生命周期
        mReadSP = getActivity().getSharedPreferences("default", getActivity().MODE_PRIVATE); //初始化读
        mEditorSP = mReadSP.edit(); //初始化写
    }


    private void getLocation(OkHttpClient client, String url) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                HashMap<String, String> map = GsonUtil.json2Map(response.body().string());
                String jsonX = map.get("label_x");
                String jsonY = map.get("label_y");
                HashMap<String, String> label_x = GsonUtil.json2Map(jsonX);
                HashMap<String, String> label_y = GsonUtil.json2Map(jsonY);
                for (int i = 0; i < label_x.size(); i++) {
                    float x = Float.valueOf(label_x.get("" + (i + 1)));
                    float y = Float.valueOf(label_y.get("" + (i + 1)));
                    locations.add(new Location(i, x, y, 0));
                    cluster0.add((new Entry(x, y))); //加载所有原始数据
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLocationBtn.setProgress(100);
                    }
                });
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        upData();
                    }
                });

                //得到所有位置Location的ArrayList
                Log.d("TTT", "onResponse: " + locations.get(1).getId() + "," + locations.get(1).getX() + "," + locations.get(1).getY());
            }
        };
        HttpUtil.sendOkHttpRequest(client, url, callback);
    }

    private void getCluster(OkHttpClient client, String url) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                HashMap<String, String> map = GsonUtil.json2Map(response.body().string());
                int iterate = map.size();
                for (int i = 0; i < iterate; i++) {
                    HashMap<String, String> temp = GsonUtil.json2Map(map.get("" + i));
                    for (int j = 0; j < temp.size(); j++) {
                        Double d = Double.valueOf(temp.get("" + (j + 1)));
                        int flag = (int) Math.ceil(d);
                        Location newLoc = locations.get(j);
                        newLoc.setFlag(flag);
                        locations.set(j, newLoc);
                    }
                    try {
                        Thread.sleep(1000);
                        upClusterData();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        HttpUtil.sendOkHttpRequest(client, url, callback);
    }

    private void initChartMap() {
        chartMap.add(new Entry(0f, 0f));
        chartMap.add(new Entry(0f, 1f));
        chartMap.add(new Entry(1f, 0f));
        chartMap.add(new Entry(1f, 1f));

        mScatterChart.getDescription().setEnabled(false);
        // mScatterChart.setOnChartValueSelectedListener();
        mScatterChart.setDrawGridBackground(false);
        mScatterChart.setTouchEnabled(true);
        mScatterChart.setMaxHighlightDistance(10f);
        // 支持缩放和拖动
        mScatterChart.setDragEnabled(true);
        mScatterChart.setScaleEnabled(true);
        mScatterChart.setMaxVisibleValueCount(4);
        mScatterChart.setPinchZoom(true);

        Legend l = mScatterChart.getLegend();
        l.setEnabled(false);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXOffset(5f);
        YAxis yl = mScatterChart.getAxisLeft();
        yl.setAxisMinimum(0f);
        mScatterChart.getAxisRight().setEnabled(false);
        XAxis xl = mScatterChart.getXAxis();
        xl.setDrawGridLines(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setAxisMinimum(0f);

        ScatterDataSet set = new ScatterDataSet(chartMap, "地图");
        set.setScatterShape(ScatterChart.ScatterShape.SQUARE);
        set.setColor(Color.rgb(255, 255, 255));
        set.setScatterShapeSize(8f);

        dataSets.add(set);
        data = new ScatterData(dataSets);
        mScatterChart.setData(data);
        mScatterChart.invalidate();
        mScatterChart.animateXY(1000, 1000);
    }

    private void upData() {
        YAxis yl = mScatterChart.getAxisLeft();
        XAxis xl = mScatterChart.getXAxis();
        xLimitLine = new LimitLine(relativeX, "Location_X");
        xLimitLine.setTextSize(10f);
        yLimitLine = new LimitLine(relativeY, "Location_Y");
        yLimitLine.setTextSize(10f);
        xl.addLimitLine(xLimitLine);
        yl.addLimitLine(yLimitLine);
        //创建一个数据集,并给它一个类型
        ScatterDataSet set0 = new ScatterDataSet(cluster0, "原始数据");
        set0.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        //设置颜色
        set0.setColor(ColorTemplate.COLORFUL_COLORS[3]);
        set0.setScatterShapeSize(15f);
        //以下是更新数据的代码
        data.addDataSet(set0);
        mScatterChart.setData(data);
        mScatterChart.notifyDataSetChanged();
        mScatterChart.invalidate();
    }

    private void upClusterData() {
        if (data.getDataSetCount() > 2) {
            for (int i = 0; i < 5; i++) {
                data.removeDataSet(data.getDataSetCount() - 1);
            }
            cluster1.clear();
            cluster2.clear();
            cluster3.clear();
            cluster4.clear();
            cluster5.clear();
        }

        for (Location loc : locations) {
            Entry e = new Entry(loc.getX(), loc.getY());
            switch (loc.getFlag()) {
                case 0:
                    cluster1.add(e);
                    break;
                case 1:
                    cluster2.add(e);
                    break;
                case 2:
                    cluster3.add(e);
                    break;
                case 3:
                    cluster4.add(e);
                    break;
                case 4:
                    cluster5.add(e);
                    break;
            }
        }
        ScatterDataSet set1 = new ScatterDataSet(cluster1, "簇1");
        ScatterDataSet set2 = new ScatterDataSet(cluster2, "簇2");
        ScatterDataSet set3 = new ScatterDataSet(cluster3, "簇3");
        ScatterDataSet set4 = new ScatterDataSet(cluster4, "簇4");
        ScatterDataSet set5 = new ScatterDataSet(cluster5, "簇5");

        set1.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        set2.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        set3.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        set4.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        set5.setScatterShape(ScatterChart.ScatterShape.CIRCLE);

        set1.setColor(ColorTemplate.JOYFUL_COLORS[0]);
        set2.setColor(ColorTemplate.JOYFUL_COLORS[1]);
        set3.setColor(ColorTemplate.JOYFUL_COLORS[2]);
        set4.setColor(ColorTemplate.JOYFUL_COLORS[3]);
        set5.setColor(ColorTemplate.JOYFUL_COLORS[4]);

        set1.setScatterShapeSize(15f);
        set2.setScatterShapeSize(15f);
        set3.setScatterShapeSize(15f);
        set4.setScatterShapeSize(15f);
        set5.setScatterShapeSize(15f);

        //以下是更新数据的代码
        data.addDataSet(set1);
        data.addDataSet(set2);
        data.addDataSet(set3);
        data.addDataSet(set4);
        data.addDataSet(set5);
        mScatterChart.setData(data);
        mScatterChart.notifyDataSetChanged();
        mScatterChart.invalidate();
    }

    public void showNotification(String title, String content, int icon) {
        NotificationManager mManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;

        //低于Android8.0
        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            notification = new NotificationCompat.Builder(getActivity())
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(icon)
                    .setShowWhen(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
        }
        //高于android8.0要设置channel
        else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelID = "channelID";
            String channelName = "channelName";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelID, channelName, importance);
            assert mManager != null;
            mManager.createNotificationChannel(channel);
            notification = new NotificationCompat.Builder(getActivity(), channelID)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(icon)
                    .setShowWhen(true)
                    .build();
        }
        mManager.notify(1, notification);
    }

}
