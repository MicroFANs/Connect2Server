package com.example.dpf_client.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView mRecordRecyclerView;//RecyclerView
    private RecordAdapter mRecordRecyclerAdapter;//适配器
    private ArrayList<Record> recordsList=new ArrayList<>(); //数据源,用list来存

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        initData(); //初始化数据
        initRecyclerView(root);//开启Recyclerview

        return root;
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
        mRecordRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
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
        //调用Model中的方法
        //recordsList=homeViewModel.getRecords();

        //调用父Activity中的方法和变量
        MainActivity activity = (MainActivity) getActivity();
        recordsList=activity.getRecords();



    }
}
