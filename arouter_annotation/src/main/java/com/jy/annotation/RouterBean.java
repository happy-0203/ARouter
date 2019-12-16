package com.jy.annotation;

import javax.lang.model.element.Element;

public class RouterBean {

    public enum Type {
        ACTIVITY,
        CALL
    }

    //枚举类型
    private Type type;
    //类节点
    private Element element;

    //被@ARouter注解的类对象
    private Class<?> clazz;
    //路由组名
    private String group;
    //路由地址
    private String path;


    private RouterBean(Builder builder) {
        this.clazz = builder.clazz;
        this.element = builder.element;
        this.group = builder.group;
        this.path = builder.path;
        this.type = builder.type;
    }

    private RouterBean(Type type, Class<?> clazz, String path, String group) {
        this.clazz = clazz;
        this.group = group;
        this.path = path;
        this.type = type;
    }


    // 对外提供简易版构造方法，主要是为了方便APT生成代码
    public static RouterBean create(Type type, Class<?> clazz, String path, String group) {
        return new RouterBean(type, clazz, path, group);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Type getType() {
        return type;
    }

    public Element getElement() {
        return element;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getGroup() {
        return group;
    }

    public String getPath() {
        return path;
    }

    public final static class Builder {
        //枚举类型
        private Type type;
        //类节点
        private Element element;

        //被@ARouter注解的类对象
        private Class<?> clazz;
        //路由组名
        private String group;
        //路由地址
        private String path;

        public Builder setElement(Element element) {
            this.element = element;
            return this;
        }

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        public Builder setClazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public RouterBean build() {
            return new RouterBean(this);
        }
    }

    @Override
    public String toString() {
        return "RouterBean{" +
                "type=" + type +
                ", element=" + element +
                ", clazz=" + clazz +
                ", group='" + group + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}

