package com.example.qmuitest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ProgressBar;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static String TAG="TAG";
    Button mButton1;
    Button mButton2;
    Button mButton3;
    CircularProgressButton cp;
    ProgressBar bar;
    Button add;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton1=findViewById(R.id.btn);
        mButton2=findViewById(R.id.qm_btn);
        mButton3=findViewById(R.id.qm_btn_1);
        add=findViewById(R.id.add);
        bar=findViewById(R.id.progress);
        cp=(CircularProgressButton) findViewById(R.id.processbtn);
        cp.setText("链接");

        cp.setCompleteText("Success");
        cp.setErrorText("ERROR");
        cp.setIdleText("链接");


        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        add.setOnClickListener(this);

        cp.setIndeterminateProgressMode(true);


        cp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircularProgressButton btn=(CircularProgressButton) v;
                int progress=btn.getProgress();
                if (progress == 0) { // 初始时progress = 0
                    btn.setProgress(50); // 点击后开始不精准进度，不精准进度的进度值一直为50
                } else if (progress == 100) { // 如果当前进度为100，即完成状态，那么重新回到未完成的状态

                    btn.setProgress(0);

                } else if (progress == 50) { // 如果当前进度为50，那么点击后就显示完成的状态
                    btn.setProgress(100);
                }

            }
        });
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
                break;
            case R.id.add:

                break;

        }
    }



}
