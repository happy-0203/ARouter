package com.jy.common.interfaces;

import android.content.Context;

import com.jy.arouter_api.core.Call;

public interface IOrder extends Call {

    void getOrderDetail(Context context);
}
