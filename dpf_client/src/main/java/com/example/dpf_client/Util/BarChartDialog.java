package com.example.dpf_client.Util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.dpf_client.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class BarChartDialog extends Dialog {
    private BarChart mBarChart;
    private List<BarEntry> chartList = new ArrayList<>();//数据
    private ArrayList<String> label = new ArrayList<>();//x轴坐标

    public BarChartDialog(@NonNull Context context, List<BarEntry> list, ArrayList<String> label) {
        super(context);
        this.chartList = list;
        this.label = label;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.chart_dialog, null);
        setContentView(view);
        mBarChart = findViewById(R.id.bar_chart);
        setChart();
    }

    private void setChart() {

        BarDataSet barDataSet = new BarDataSet(chartList, "频率估计值");
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setValueTextSize(10);
        mBarChart.setData(barData);//设置数据源


        mBarChart.setDragEnabled(true);
        mBarChart.setContentDescription("频率估计值");
        mBarChart.setPinchZoom(true);//缩放
        mBarChart.setDrawValueAboveBar(true);//显示数值
        mBarChart.setDrawGridBackground(true);//背景格线
        mBarChart.setVisibleXRangeMaximum(10);//最大显示20个柱子，多了可以滚动
        mBarChart.getDescription().setEnabled(false);//隐藏右下角英文
        mBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//X轴的位置 默认为上面
        mBarChart.getAxisRight().setEnabled(false);//隐藏右侧Y轴   默认是左右两侧都有Y轴
        mBarChart.animateXY(1000, 1000);


        //x坐标轴
        XAxis xl = mBarChart.getXAxis();
        xl.setValueFormatter(new XAxisFormatter(label));
        xl.setCenterAxisLabels(false);
        xl.setGranularity(1f);
        xl.setLabelCount(label.size(), false);
        xl.setCenterAxisLabels(true);
        xl.setLabelRotationAngle(-60f);
        Legend legend = mBarChart.getLegend();
        legend.setEnabled(false);  //不显示图例

    }
}
