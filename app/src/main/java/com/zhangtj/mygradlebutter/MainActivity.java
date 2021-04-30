package com.zhangtj.mygradlebutter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.zhangtj.apt_annotation.BindView;
import com.zhangtj.apt_sdk.MkButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.butter_btn)
    Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MkButterKnife.bindView(this);

        mButton.setText("New Text");
    }
}
