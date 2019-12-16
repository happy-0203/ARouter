package com.jy.arouterdemo.test;

import com.jy.annotation.RouterBean;
import com.jy.arouter_api.core.ARouterLoadPath;
import com.jy.arouterdemo.MainActivity;
import com.jy.arouterdemo.OrderActivity;

import java.util.HashMap;
import java.util.Map;

public class ARouter$$Path$$app implements ARouterLoadPath {
    @Override
    public Map<String, RouterBean> loadPath() {
        Map<String, RouterBean> pathMap = new HashMap<>();

        RouterBean routerBean = RouterBean.create(RouterBean.Type.ACTIVITY,
                MainActivity.class,
                "/app/MainActivity",
                "app");
        pathMap.put("/app/MainActivity", routerBean);

        pathMap.put("/app/OrderActivity", RouterBean.create(RouterBean.Type.ACTIVITY,
                OrderActivity.class,
                "/app/OrderActivity",
                "app"));


        return pathMap;
    }
}
