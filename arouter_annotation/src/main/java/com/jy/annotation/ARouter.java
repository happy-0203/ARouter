package com.jy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ARouter {

    //详细路由路径(必填),如 "/app/MainActivity"
    String path();
    //路由组名(选填,如果开发者不填,可以从path中截取)
    String group() default "";
}
