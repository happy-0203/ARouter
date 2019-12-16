package com.jy.arouter_compiler.util;

public class Constants {

    public static final String AROUTER_ANNOTATION_TYPES = "com.jy.annotation.ARouter";

    /**
     * 模块名称key
     */
    public static final String AROUTER_MODULE_NAME = "moduleName";

    /**
     * 生成APT文件包名的key
     */
    public static final String PACKAGENAME_FOR_APT = "packageNameForAPT";

    //Activity全类名
    public static final String ACTIVITY = "android.app.Activity";

    //回调Call全类名
    public static final String CALL = "com.jy.arouter_api.core.Call";

    //String全类名
    public static final String STRING = "java.lang.String";

    //包名前缀
    public static final String BASE_PACKAGE = "com.jy.arouter_api";

    //路由Group加载的接口
    public static final String AROUTE_GROUP = BASE_PACKAGE+".core.ARouterLoadGroup";

    //路由Group加载的详细Path
    public static final String AROUTER_PATH = BASE_PACKAGE+".core.ARouterLoadPath";

    //路由组Group对应的详细Path,方法名
    public static final String PATH_METHOD_NAME = "loadPath";
    public static final String group_METHOD_NAME = "loadGroup";


    //路由组Group对应的详细Path,参数名
    public static final String PATH_PARAMETE_NAME = "pathMap";

    //Path类的前缀ARouter$$Path$$
    public static final String PATH_FILE_NAME = "ARouter$$Path$$";


    //路由组Group对应的详细Path,参数名
    public static final String GROUP_PARAMETE_NAME = "groupMap";

    //Path类的前缀ARouter$$Path$$
    public static final String GROUP_FILE_NAME = "ARouter$$Group$$";


}
