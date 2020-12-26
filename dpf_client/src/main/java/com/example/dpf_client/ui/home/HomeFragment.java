package com.example.dpf_client.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dpf_client.MainActivity;
import com.example.dpf_client.R;
import com.example.dpf_client.Util.ImageIdUtil;
import com.example.dpf_client.Util.Record;
import com.example.dpf_client.Util.RecordAdapter;
import com.example.dpf_client.Util.RecyclerViewDivider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    public static String TAG="HomeFragment";
    private HomeViewModel homeViewModel;
    private RecyclerView mRecordRecyclerView;//RecyclerView
    private RecordAdapter mRecordRecyclerAdapter;//适配器
    private ArrayList<Record> recordsList=new ArrayList<>(); //数据源,用list来存放Record对象



    private SharedPreferences mReadSP;//读取记录
    private SharedPreferences.Editor mEditorSP; //保存记录


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        initView();//初始化控件
        initData(); //初始化数据
        initRecyclerView(root);//开启Recyclerview

        return root;
    }

    private void initView(){
        //getActivity()方法一定要放在onCreateView这个方法里才能生效，否则只能返回null，涉及到Fragment生命周期
        mReadSP=getActivity().getSharedPreferences("default",getActivity().MODE_PRIVATE); //初始化读
        mEditorSP=mReadSP.edit(); //初始化写


    }

    //初始化RecyclerView
    private void initRecyclerView(View view){
        //获取RecyclerView
        mRecordRecyclerView=view.findViewById(R.id.home_rv_record);
        //创建Adapter
        mRecordRecyclerAdapter=new RecordAdapter(recordsList);
        //给RecyclerView设置adapter
        mRecordRecyclerView.setAdapter(mRecordRecyclerAdapter);
        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        //参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        mRecordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //设置item的分割线
        //mRecordRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        mRecordRecyclerView.addItemDecoration(new RecyclerViewDivider(getActivity(),LinearLayoutManager.VERTICAL,20, R.color.black));
        //设置item添加和删除的动画
        mRecordRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义
        mRecordRecyclerAdapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Record data) {
                //此处进行监听事件的业务处理
                Toast.makeText(getActivity(),"我是item",Toast.LENGTH_SHORT).show();
            }
        });
    }


    //从Model里获取数据，既可以写在相应的Model中
    // 也可以写在父Activity中再调用，这样可以被其他的Fragment调用
    private void initData(){
        recordsList=loadRecordListFromJson(); //直接从Sharedpreference中找json还原对应的ArrayList
        if(recordsList.size()==0){//如果没有，则Model中的方法生成生成一个
            //调用Model中的方法
            recordsList = homeViewModel.getRecords();

//        //调用父Activity中的方法和变量
//        MainActivity activity = (MainActivity) getActivity();
//        recordsList=activity.getRecords();
        }


    }
    //将数据记录ArrayList<Record>保存到Json
    private void saveRecordList2Json(ArrayList<Record> list){
        Gson gson=new Gson();
        String json=gson.toJson(list);
        Log.d(TAG, "saveRecordList: "+json);
        mEditorSP.putString("recordListJson",json);
        mEditorSP.apply();
    }

    //将Json还原为ArrayList<Record>
    private ArrayList<Record> loadRecordListFromJson(){
        ArrayList<Record> list=new ArrayList<>();
        String json=mReadSP.getString("recordListJson",null);
        if(json!=null){
            Gson gson=new Gson();
            list=gson.fromJson(json,new TypeToken<ArrayList<Record>>(){}.getType());//从Json转换为List
        }
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveRecordList2Json(recordsList);
    }
}
