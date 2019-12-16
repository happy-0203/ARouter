package com.jy.personal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jy.annotation.ARouter;
import com.jy.arouter_api.RouterManager;
import com.jy.common.interfaces.IOrder;

@ARouter(path = "/personal/Personal_MainActivity")
public class Personal_MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_main);
    }

    public void getOrderDetail(View view) {
       IOrder order = (IOrder) RouterManager.getInstance()
               .build("/order/OrderImpl")
               .navigation(this);

       order.getOrderDetail(this);

    }
}
