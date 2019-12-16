package com.jy.arouterdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jy.annotation.ARouter;

@ARouter(path = "/app/OrderActivity")
public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
    }
}
