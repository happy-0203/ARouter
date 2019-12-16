package com.jy.arouter_api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.jy.annotation.RouterBean;
import com.jy.arouter_api.core.ARouterLoadGroup;
import com.jy.arouter_api.core.ARouterLoadPath;
import com.jy.arouter_api.core.Call;

import java.util.Map;


public class RouterManager {


    public static final String TAG = "zc===";

    //路由组名
    private String group;
    //路由路径
    private String path;

    private static RouterManager instance;

    // APT生成的路由组Group源文件前缀名
    private static final String GROUP_FILE_PREFIX_NAME = ".ARouter$$Group$$";
    private final LruCache<String, ARouterLoadGroup> mGroupCache;
    private final LruCache<String, ARouterLoadPath> mPathCache;

    /**
     * 获取RouterManager单例
     *
     * @return
     */
    public static RouterManager getInstance() {
        if (instance == null) {
            synchronized (RouterManager.class) {
                if (instance == null) {
                    instance = new RouterManager();
                }
            }
        }
        return instance;
    }

    private RouterManager() {
        //初始化,缓存路径
        mGroupCache = new LruCache<>(100);
        mPathCache = new LruCache<>(100);
    }

    public BundleManager build(String path) {

        //断路由的path是否符合规范
        if (TextUtils.isEmpty(path)) {
            throw new IllegalArgumentException("path is null");
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("未按规范配置，如：/app/MainActivity");
        }
        this.group = getGroupFromPath(path);
        this.path = path;
        return new BundleManager();
    }

    /**
     * 获取路由组 group
     *
     * @param path
     * @return
     */
    private String getGroupFromPath(String path) {

        if (path.lastIndexOf("/") == 0) {
            throw new IllegalArgumentException("@ARouter注解未按规范配置，如：/app/MainActivity");
        }

        String tempGroup = path.substring(1, path.lastIndexOf("/"));

        if (TextUtils.isEmpty(tempGroup)) {
            throw new IllegalArgumentException("@ARouter注解未按规范配置，如：/app/MainActivity");
        }

        if (tempGroup.contains("/")) {
            throw new IllegalArgumentException("@ARouter注解未按规范配置，如：/app/MainActivity");
        }

        return tempGroup;
    }

    public Object navigation(Context context, BundleManager bundleManager, int code) {

        String groupClassName = context.getPackageName() + ".apt" + GROUP_FILE_PREFIX_NAME + group;
        Log.e(TAG, "navigation: " + groupClassName);

        try {

            //从缓存中获取路由组表
            ARouterLoadGroup groupLoad = mGroupCache.get(this.group);
            if (groupLoad == null) {
                Class<?> aClass = Class.forName(groupClassName);
                //获取路由组管理类
                groupLoad = (ARouterLoadGroup) aClass.newInstance();
                mGroupCache.put(group, groupLoad);
            }
            Map<String, Class<? extends ARouterLoadPath>> loadGroupMap = groupLoad.loadGroup();
            if (loadGroupMap.isEmpty()) {
                throw new RuntimeException("路由加载失败");
            }

            //从缓存中获取路由组的详细path
            ARouterLoadPath aRouterLoadPath = mPathCache.get(path);
            if (aRouterLoadPath == null) {
                //获取路由组详细Path
                Class<? extends ARouterLoadPath> loadPathClass = loadGroupMap.get(group);
                if (loadPathClass != null) {
                    aRouterLoadPath = loadPathClass.newInstance();
                }
                mPathCache.put(path, aRouterLoadPath);
            }
            if (aRouterLoadPath != null) {
                Map<String, RouterBean> loadPathMap = aRouterLoadPath.loadPath();
                if (loadPathMap.isEmpty()) {
                    throw new RuntimeException("路由加载失败");
                }
                RouterBean routerBean = loadPathMap.get(path);

                if (routerBean != null) {
                    switch (routerBean.getType()) {
                        case ACTIVITY:
                            Intent intent = new Intent(context, routerBean.getClazz());
                            intent.putExtras(bundleManager.getBundle());
                            if (bundleManager.isResult()) {
                                //跳转需要返回结果
                            }
                            if (code > 0) {
                                ((Activity) context).startActivityForResult(intent, code);
                            } else {
                                ((Activity) context).startActivity(intent);
                            }
                            break;
                        case CALL:
                            // 对象实现
                            Class<?> clazz = routerBean.getClazz();
                            Call call = (Call) clazz.newInstance();
                            bundleManager.setCall(call);
                            return call;
                    }
                }


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
