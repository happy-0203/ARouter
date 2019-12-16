package com.jy.arouter_api;

import android.content.Context;
import android.os.Bundle;

import com.jy.arouter_api.core.Call;

/**
 * 跳转参数拼接
 */
public class BundleManager {

    private Bundle bundle = new Bundle();

    private boolean isResult;

    // 底层业务接口
    private Call call;

    /**
     * 携带字符串
     *
     * @param key
     * @param value
     * @return
     */
    public BundleManager withString(String key, String value) {
        bundle.putString(key, value);
        return this;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    /**
     * 携带int
     *
     * @param key
     * @param value
     * @return
     */
    public BundleManager withInt(String key, int value) {
        bundle.putInt(key, value);
        return this;
    }


    public BundleManager withBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }


    public Bundle getBundle() {
        return bundle;
    }

    /**
     * 跳转是否需要返回结果
     *
     * @param isResult
     * @return
     */
    public BundleManager setResult(boolean isResult) {
        this.isResult = isResult;
        return this;
    }

    public boolean isResult() {
        return isResult;
    }


    public Object navigation(Context context) {
        return navigation(context, -1);
    }

    /**
     * startActivityForResult
     *
     * @param context
     * @param code
     * @return
     */
    public Object navigation(Context context, int code) {
        return RouterManager.getInstance().navigation(context, this, code);
    }


}
