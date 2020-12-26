package com.example.dpf_client.ui.newthing;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dpf_client.R;
import com.example.dpf_client.Util.ImageIdUtil;
import com.example.dpf_client.Util.Record;
import com.example.dpf_client.Util.RecordAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class NewFragment extends Fragment {

    private NewViewModel mViewModel;
    private static  int i=1;
//    public static NewFragment newInstance() {
//        return new NewFragment();
//    }

    private RecyclerView mRecordRecyclerView;
    //数据源,用list来存
    private ArrayList<Record> recordsList=new ArrayList<>();
    //适配器
    private RecordAdapter mRecordRecyclerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel= ViewModelProviders.of(this).get(NewViewModel.class);
        View root= inflater.inflate(R.layout.new_fragment, container, false);
        final TextView textView=root.findViewById(R.id.text_new);
        mViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });

        final Button button=root.findViewById(R.id.add_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //textView.setText(mViewModel.getData());
                textView.setText("次数i:"+i++);//这和MVP的设计模式其实是一样的，和UI有关的逻辑写在
                // Fragment里，其他的就写在Model里，甚至可以不用Model，直接在Fragment里写
            }
        });
        initRecyclerView(root);
        initData();
        return root;
    }

    //初始化RecyclerView
    private void initRecyclerView(View view){
        //获取RecyclerView
        mRecordRecyclerView=view.findViewById(R.id.record_recycle_view);
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

    //设置数据源
    private void initData(){
        for (int j = 0; j <4 ; j++) {
            String name="icon"+(j+1);
            Record record=new Record(j,name, ImageIdUtil.getImageByReflect(name),j-0.4);
            recordsList.add(record);
        }

    }



}
