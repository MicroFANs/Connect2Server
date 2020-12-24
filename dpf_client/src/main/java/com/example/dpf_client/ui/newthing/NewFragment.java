package com.example.dpf_client.ui.newthing;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dpf_client.R;

public class NewFragment extends Fragment {

    private NewViewModel mViewModel;
    private static  int i=1;
//    public static NewFragment newInstance() {
//        return new NewFragment();
//    }

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

        final Button button=root.findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //textView.setText(mViewModel.getData());
                textView.setText("次数i:"+i++);//这和MVP的设计模式其实是一样的，和UI有关的逻辑写在
                // Fragment里，其他的就写在Model里，甚至可以不用Model，直接在Fragment里写
            }
        });
        return root;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mViewModel = ViewModelProviders.of(this).get(NewViewModel.class);
//        // TODO: Use the ViewModel
//    }

}
