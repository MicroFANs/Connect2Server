package com.example.dpf_client.Util;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

public class XAxisFormatter implements IAxisValueFormatter {
    private ArrayList<String> mValues;

    public XAxisFormatter(ArrayList<String> mValues) {
        this.mValues = mValues;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (((int) value >= 0 && (int) value < mValues.size()))
            return mValues.get((int) value);
        else
            return "";
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
