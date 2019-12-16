package com.jy.order;

import android.content.Context;
import android.widget.Toast;

import com.jy.annotation.ARouter;
import com.jy.common.interfaces.IOrder;

@ARouter(path = "/order/OrderImpl")
public class OrderImpl implements IOrder {
    @Override
    public void getOrderDetail(Context context) {
        Toast.makeText(context,"这是订单详细",Toast.LENGTH_SHORT).show();
    }
}
