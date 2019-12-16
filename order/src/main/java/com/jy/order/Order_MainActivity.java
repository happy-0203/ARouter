package com.jy.order;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jy.annotation.ARouter;

@ARouter(path = "/order/Order_MainActivity")
public class Order_MainActivity extends AppCompatActivity {

    public static final String TAG = "zc===";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order__main);

        String username = getIntent().getStringExtra("username");
        String name = getIntent().getStringExtra("name");
        int age = getIntent().getIntExtra("age",0);
        boolean isSuccess = getIntent().getBooleanExtra("isSuccess",false);

        Log.e(TAG, "Order_MainActivity: "+" username:"+username+" name:"+name+" age:"+age+" isSuccess:"+isSuccess);
    }
}
