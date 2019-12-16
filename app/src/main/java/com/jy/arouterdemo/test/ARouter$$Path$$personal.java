package com.jy.arouterdemo.test;

import com.jy.annotation.RouterBean;
import com.jy.arouter_api.core.ARouterLoadPath;
import com.jy.personal.Personal_MainActivity;

import java.util.HashMap;
import java.util.Map;

public class ARouter$$Path$$personal implements ARouterLoadPath {
    @Override
    public Map<String, RouterBean> loadPath() {
        Map<String, RouterBean> pathMap = new HashMap<>();

        RouterBean routerBean = RouterBean.create(RouterBean.Type.ACTIVITY,
                Personal_MainActivity.class,
                "personal/Personal_MainActivity",
                "personal");
        pathMap.put("personal/Personal_MainActivity", routerBean);
        return pathMap;
    }
}
