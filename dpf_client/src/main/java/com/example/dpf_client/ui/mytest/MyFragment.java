package com.example.dpf_client.ui.mytest;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dpf_client.R;

public class MyFragment extends Fragment {

    private MyViewModel mViewModel;

    public static MyFragment newInstance() {
        return new MyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel=ViewModelProviders.of(this).get(MyViewModel.class);
        View root=inflater.inflate(R.layout.my_fragment, container, false);
        final TextView textView = root.findViewById(R.id.text_my);
        //下面代码中observe()方法里的this，可能在IDE里是有红色波浪线的，但是却是可以编译的。
        //也可以直接把this改成getViewLifecycleOwner() 就不会报错了
        mViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }



}
