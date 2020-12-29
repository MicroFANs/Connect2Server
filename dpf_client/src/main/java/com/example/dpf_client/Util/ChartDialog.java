package com.example.dpf_client.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.dpf_client.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class ChartDialog extends Dialog {
    private BarChart mBarChart;
    private List<BarEntry> chartList=new ArrayList<>();

    public ChartDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.chart_dialog,null);
        setContentView(view);
        mBarChart=findViewById(R.id.bar_chart);
        setChart();
    }

    private void setChart(){
        chartList.add(new BarEntry(1,11));
        chartList.add(new BarEntry(2,12));
        chartList.add(new BarEntry(3,2));
        chartList.add(new BarEntry(4,1));
        chartList.add(new BarEntry(5,11));
        chartList.add(new BarEntry(6,12));
        chartList.add(new BarEntry(7,2));
        chartList.add(new BarEntry(8,1));
        chartList.add(new BarEntry(9,11));
        chartList.add(new BarEntry(10,12));
        chartList.add(new BarEntry(11,11));
        chartList.add(new BarEntry(12,12));
        chartList.add(new BarEntry(13,2));
        chartList.add(new BarEntry(14,1));
        chartList.add(new BarEntry(15,11));
        chartList.add(new BarEntry(16,12));
        chartList.add(new BarEntry(17,2));
        chartList.add(new BarEntry(18,1));
        chartList.add(new BarEntry(19,11));
        chartList.add(new BarEntry(20,12));

        BarDataSet barDataSet=new BarDataSet(chartList,"估计值");
        BarData barData=new BarData(barDataSet);
        mBarChart.setData(barData);

        mBarChart.getDescription().setEnabled(false);//隐藏右下角英文
        mBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//X轴的位置 默认为上面
        mBarChart.getAxisRight().setEnabled(false);//隐藏右侧Y轴   默认是左右两侧都有Y轴

    }
}
