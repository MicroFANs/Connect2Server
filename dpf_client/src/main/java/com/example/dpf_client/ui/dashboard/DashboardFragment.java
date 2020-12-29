package com.example.dpf_client.ui.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class DashboardFragment extends Fragment {

    private static final String HEAD = "http://"; //Url头
    private static final String PORT = ":8888"; //端口
    private static final String ROUTE_GET_LOCATION = "/getLocation"; //route，对应flask里的route

    private DashboardViewModel dashboardViewModel;
    private CircularProgressButton mStartBtn;
    private CircularProgressButton mLocationBtn;
    private ScatterChart mScatterChart;
    private int mLocationX;
    private int mLocationY;
    private int id;
    private final OkHttpClient mOkClient = new OkHttpClient(); //单例，不用每次都创建新的Client
    private String mIPAddress; //IP地址

    private SharedPreferences mReadSP;//读取记录
    private SharedPreferences.Editor mEditorSP; //保存记录

    private ArrayList<Location> locations=new ArrayList<>();//所有点的list
    private ArrayList<Entry> cluster0 = new ArrayList<Entry>();//原始数据
    private ArrayList<Entry> cluster1 = new ArrayList<Entry>();//簇1
    private ArrayList<Entry> cluster2 = new ArrayList<Entry>();//簇2
    private ArrayList<Entry> cluster3 = new ArrayList<Entry>();//簇3
    private ArrayList<Entry> cluster4 = new ArrayList<Entry>();//簇4
    private ArrayList<Entry> cluster5 = new ArrayList<Entry>();//簇5


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
                AlertDialog.Builder dialog=new AlertDialog.Builder((MainActivity)getActivity());
                dialog.setTitle("您的位置信息为：");
                dialog.setMessage("X："+mLocationX+"，Y："+mLocationY);
                dialog.setCancelable(true);
                dialog.show();

                String url = HEAD + mIPAddress + PORT + ROUTE_GET_LOCATION;//生成url
                getLocation(mOkClient, url);
                //mLocationBtn.setProgress(100);

            }
        });
        setChart();
        return root;
    }
    private void initView(View view){

        mLocationBtn=view.findViewById(R.id.dashboard_btn_location);
        mStartBtn =view.findViewById(R.id.dashboard_btn_start);
        mScatterChart=view.findViewById(R.id.scatterChart);
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
        mLocationX=142575;
        mLocationY=517378;
        mIPAddress= mReadSP.getString("ipAddress", "");//得到ip地址

    }

    private void initSharedPreference(){
        //Sharedpreferences
        //getActivity()方法一定要放在onCreateView这个方法里才能生效，否则只能返回null，涉及到Fragment生命周期
        mReadSP=getActivity().getSharedPreferences("default",getActivity().MODE_PRIVATE); //初始化读
        mEditorSP=mReadSP.edit(); //初始化写
    }

    private void setChart(){
        mScatterChart.getDescription().setEnabled(false);
       // mScatterChart.setOnChartValueSelectedListener();
        mScatterChart.setDrawGridBackground(false);
        mScatterChart.setTouchEnabled(true);
        mScatterChart.setMaxHighlightDistance(10f);
        // 支持缩放和拖动
        mScatterChart.setDragEnabled(true);
        mScatterChart.setScaleEnabled(true);
        mScatterChart.setMaxVisibleValueCount(10);
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
        setData();
    }

//    private void setData() {
//        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
//        ArrayList<Entry> yVals2 = new ArrayList<Entry>();
//        ArrayList<Entry> yVals3 = new ArrayList<Entry>();
//        for (int i = 0; i < 10; i++) {
//            float val = (float) (Math.random() * 10 + 3);
//            yVals1.add(new Entry(i, val));
//        }
//        for (int i = 0; i < 10; i++) {
//            float val = (float) (Math.random() * 10 + 3);
//            yVals2.add(new Entry(i + 0.33f, val));
//        }
//        for (int i = 0; i < 10; i++) {
//            float val = (float) (Math.random() * 10 + 3);
//            yVals3.add(new Entry(i + 0.66f, val));
//        }
//        //创建一个数据集,并给它一个类型
//        ScatterDataSet set1 = new ScatterDataSet(yVals1, "优秀");
//        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE);
//        //设置颜色
//        set1.setColor(ColorTemplate.COLORFUL_COLORS[0]);
//        ScatterDataSet set2 = new ScatterDataSet(yVals2, "及格");
//        set2.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
//        set2.setScatterShapeHoleColor(ColorTemplate.COLORFUL_COLORS[3]);
//        set2.setScatterShapeHoleRadius(3f);
//        set2.setColor(ColorTemplate.COLORFUL_COLORS[1]);
//
//        set1.setScatterShapeSize(8f);
//        set2.setScatterShapeSize(8f);
//        ArrayList<IScatterDataSet> dataSets = new ArrayList<IScatterDataSet>();
//        dataSets.add(set1);
//        dataSets.add(set2);
//        //创建一个数据集的数据对象
//        ScatterData data = new ScatterData(dataSets);
//        mScatterChart.setData(data);
//        mScatterChart.invalidate();
//    }
    private void setData() {

        ArrayList<Entry> yVals2 = new ArrayList<Entry>();
//        yVals2.add(new Entry(0f,0f));
//        yVals2.add(new Entry(0f,1f));
//        yVals2.add(new Entry(1f,0f));
//        yVals2.add(new Entry(1f,0f));


        for (int i = 0; i < 10; i++) {
            float val = (float) (Math.random() * 1 );
            yVals2.add(new Entry(0.1f*i + 0.33f, val));
        }

        //创建一个数据集,并给它一个类型
        ScatterDataSet set1 = new ScatterDataSet(cluster0, "原始数据");
        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE);
        //设置颜色
        set1.setColor(ColorTemplate.COLORFUL_COLORS[0]);
        ScatterDataSet set2 = new ScatterDataSet(yVals2, "及格");
        set2.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        set2.setScatterShapeHoleColor(ColorTemplate.COLORFUL_COLORS[3]);
        set2.setScatterShapeHoleRadius(3f);
        set2.setColor(ColorTemplate.COLORFUL_COLORS[1]);

        set1.setScatterShapeSize(8f);
        set2.setScatterShapeSize(8f);
        ArrayList<IScatterDataSet> dataSets = new ArrayList<IScatterDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);
        //创建一个数据集的数据对象
        ScatterData data = new ScatterData(dataSets);
        mScatterChart.setData(data);
        mScatterChart.invalidate();
    }

    public void getLocation(OkHttpClient client,String url){
        Callback callback=new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                HashMap<String,String> map= GsonUtil.json2Map(response.body().string());
                String jsonX=map.get("label_x");
                String jsonY=map.get("label_y");
                HashMap<String, String> label_x = GsonUtil.json2Map(jsonX);
                HashMap<String,String> label_y=GsonUtil.json2Map(jsonY);
                for (int i = 0; i <label_x.size() ; i++) {
                    float x=Float.valueOf(label_x.get(""+(i+1)));
                    float y=Float.valueOf(label_y.get(""+(i+1)));
                    locations.add(new Location(i,x ,y , 0));
                    cluster0.add((new Entry(x,y))); //加载所有原始数据
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLocationBtn.setProgress(100);
                    }
                });

                //得到所有位置Location的ArrayList
                Log.d("TTT", "onResponse: "+locations.get(1).getId()+","+locations.get(1).getX()+","+locations.get(1).getY());
            }
        };
        HttpUtil.sendOkHttpRequest(client,url,callback);
    }

}
