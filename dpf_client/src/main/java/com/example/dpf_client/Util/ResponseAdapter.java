package com.example.dpf_client.Util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dpf_client.R;

import java.util.ArrayList;

public class ResponseAdapter extends RecyclerView.Adapter<ResponseAdapter.myViewHolder> {
    private ArrayList<Response> responseArrayList;

    public ResponseAdapter(ArrayList<Response> responseArrayList) {
        this.responseArrayList = responseArrayList;
    }

    @NonNull
    @Override
    public ResponseAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.response_item_recycle,parent,false);
        myViewHolder holder=new myViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ResponseAdapter.myViewHolder holder, int position) {
        Response response=responseArrayList.get(position);
        holder.responseTitle.setText(response.getTitle());
        holder.responseContent.setText(response.getContent());
        holder.responseTime.setText(response.getTime());
    }

    @Override
    public int getItemCount() {
        return responseArrayList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView responseTitle;
        TextView responseContent;
        TextView responseTime;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            responseTitle=itemView.findViewById(R.id.response_title);
            responseContent=itemView.findViewById(R.id.response_content);
            responseTime=itemView.findViewById(R.id.response_time);

            //点击事件放在adapter中使用，也可以写个接口在activity中调用
            //方法一：在adapter中设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //可以选择直接在本位置直接写业务处理
                    //Toast.makeText(context,"点击了xxx",Toast.LENGTH_SHORT).show();
                    //此处回传点击监听事件
                    if(onItemClickListener!=null){
                        onItemClickListener.OnItemClick(v, responseArrayList.get(getLayoutPosition()));
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
         * @param response 点击的item的数据
         */
        public void OnItemClick(View view, Response response);
    }

    //需要外部访问，所以需要设置set方法，方便调用
    private ResponseAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(ResponseAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
