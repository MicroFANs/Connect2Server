package com.example.dpf_client.Util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.dpf_client.R;

public class ChartDialog extends Dialog {
    public ChartDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.chart_dialog,null);
        setContentView(view);
    }
}
