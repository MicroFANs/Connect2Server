package com.example.dpf_client;

import android.os.Bundle;

import com.example.dpf_client.Util.ImageIdUtil;
import com.example.dpf_client.Util.Record;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_new)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }


    //设置在页面上显示的数据源,这里是在activity中的方法，被Fragment调用的例子
    public ArrayList<Record> getRecords(){
        ArrayList<Record> recordsList=new ArrayList<>();
        for (int j = 0; j <16 ; j++) {
            String name="icon"+(j+1)%4;
            Record record=new Record(j,name, ImageIdUtil.getImageByReflect(name),j-0.4);
            recordsList.add(record);
        }
        return recordsList;
    }
}
