package com.jy.arouter_api.core;

import java.util.Map;

/**
 * 路由组Group对外提供加载数据接口
 */
public interface ARouterLoadGroup {

    /**
     * 加载路由组Group数据
     *
     * 比如:key:"app",value:"app分组对应的路由详细对象类"
     */
    Map<String, Class<? extends ARouterLoadPath>> loadGroup();
}
