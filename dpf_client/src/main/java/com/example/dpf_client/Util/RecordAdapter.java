package com.example.dpf_client.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dpf_client.R;

import java.util.ArrayList;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.myViewHolder> {
    private ArrayList<Record> allRecords;

    public RecordAdapter(ArrayList<Record> allRecords) {
        this.allRecords = allRecords;
    }

    @NonNull
    @Override
    public RecordAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //这里用的是每一个子项的xml
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item_recycle,parent,false);
        myViewHolder holder=new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordAdapter.myViewHolder holder, int position) {
        Record record=allRecords.get(position);
        holder.recordName.setText(record.getId());//设置文字
        //holder.recordImage.setImageResource(record.getImgId());//设置图片

    }

    @Override
    public int getItemCount() {
        return allRecords.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        ImageView recordImage;
        TextView recordName;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            recordImage=itemView.findViewById(R.id.record_img);
            recordName=itemView.findViewById(R.id.record_name);


            //点击事件放在adapter中使用，也可以写个接口在activity中调用
            //方法一：在adapter中设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //可以选择直接在本位置直接写业务处理
                    //Toast.makeText(context,"点击了xxx",Toast.LENGTH_SHORT).show();
                    //此处回传点击监听事件
                    if(onItemClickListener!=null){
                        onItemClickListener.OnItemClick(v, allRecords.get(getLayoutPosition()));
                    }
                }
            });

        }
    }
    public interface OnItemClickListener {
        /**
         * 接口中的点击每一项的实现方法，参数自己定义
         *
         * @param view 点击的item的视图
         * @param data 点击的item的数据
         */
        public void OnItemClick(View view, Record data);
    }

    //需要外部访问，所以需要设置set方法，方便调用
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
