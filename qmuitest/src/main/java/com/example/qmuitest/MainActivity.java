package com.example.qmuitest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogBuilder;

import scut.carson_ho.searchview.ICallBack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static String TAG="TAG";
    Button mButton1;
    Button mButton2;
    Button mButton3;
    SearchView mSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton1=findViewById(R.id.btn);
        mButton2=findViewById(R.id.qm_btn);
        mButton3=findViewById(R.id.qm_btn_1);
        mSearch=(SearchView) findViewById(R.id.search_view);

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn:
                Log.d(TAG, "默认dialog");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("默认dialog")
                        .setMessage("内容")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.qm_btn:
                new QMUIDialog.MessageDialogBuilder(this)
                        .setTitle("QMUIDialog")
                        .setCancelable(false)
                        .setMessage("内容")
                        .addAction("Cancel", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("OK", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.qm_btn_1:
                final String [] items=new String[]{"选项1","选项2","选项3"};
                new QMUIDialog.MenuDialogBuilder(this)
                        .setCanceledOnTouchOutside(false)
                        .setTitle("xx")
                        .addItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplication(),"你选择了"+items[which],Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .show();

        }
    }



}
