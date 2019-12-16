package com.jy.arouterdemo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jy.annotation.ARouter;
import com.jy.annotation.RouterBean;

import com.jy.arouter_api.RouterManager;

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "zc===";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jumpToOrder(View view) {


        Bundle bundle = new Bundle();
        bundle.putString("name", "simon");
        bundle.putInt("age", 35);
        bundle.putBoolean("isSuccess", true);
        RouterManager.getInstance().build("/order/Order_MainActivity")
                .withString("username","zhangsan")
                .withBundle(bundle)
                .navigation(this);
    }

    public void jumpToPerson(View view) {



        Bundle bundle = new Bundle();
        bundle.putString("name", "simon");
        bundle.putInt("age", 35);
        bundle.putBoolean("isSuccess", true);
        RouterManager.getInstance().build("/personal/Personal_MainActivity")
                .withString("username","zhangsan")
                .withBundle(bundle)
                .navigation(this,100);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (requestCode == 100){
            }
        }
    }
}
