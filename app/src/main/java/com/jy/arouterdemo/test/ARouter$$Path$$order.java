package com.jy.arouterdemo.test;

import com.jy.annotation.RouterBean;
import com.jy.arouter_api.core.ARouterLoadPath;
import com.jy.order.Order_MainActivity;

import java.util.HashMap;
import java.util.Map;

public class ARouter$$Path$$order implements ARouterLoadPath {
    @Override
    public Map<String, RouterBean> loadPath() {
        Map<String, RouterBean> pathMap = new HashMap<>();

        RouterBean routerBean = RouterBean.create(RouterBean.Type.ACTIVITY,
                Order_MainActivity.class,
                "/order/Order_MainActivity",
                "order");
        pathMap.put("/order/Order_MainActivity", routerBean);
        return pathMap;
    }
}
