package com.jy.arouter_api.core;

import com.jy.annotation.RouterBean;

import java.util.Map;

/**
 * 路由组Group对应的详细Path加载数据接口
 * 比如:app分组对应有哪些类需要加载
 *
 * key:"/app/MainActivity",value:MainActivity.class信息封装到RouterBean中
 */
public interface ARouterLoadPath {

    Map<String, RouterBean> loadPath();
}
