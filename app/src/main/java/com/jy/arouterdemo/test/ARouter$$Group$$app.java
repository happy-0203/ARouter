package com.jy.arouterdemo.test;

import com.jy.arouter_api.core.ARouterLoadGroup;
import com.jy.arouter_api.core.ARouterLoadPath;

import java.util.HashMap;
import java.util.Map;

public class ARouter$$Group$$app implements ARouterLoadGroup {
    @Override
    public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
        Map<String,Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
        groupMap.put("app",ARouter$$Path$$app.class);
        return groupMap;
    }
}
