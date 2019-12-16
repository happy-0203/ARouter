package com.jy.arouter_compiler;


import com.google.auto.service.AutoService;
import com.jy.annotation.ARouter;
import com.jy.annotation.RouterBean;
import com.jy.arouter_compiler.util.Constants;
import com.jy.arouter_compiler.util.EmptyUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes({Constants.AROUTER_ANNOTATION_TYPES})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions({Constants.AROUTER_MODULE_NAME, Constants.PACKAGENAME_FOR_APT})
public class ARouterProcressor extends AbstractProcessor {


    private Elements mElementUtils;
    private Types mTypeUtils;
    private Messager mMessager;
    private Filer mFiler;
    private String mModuleName;
    private String mPackageNameForApt;

    //临时map存储,用来存放路由组Group对应的Path类对象,生成路由类文件时遍历
    //key:组名(app),value:app组的路径"ARouter$$Path$$app.class"
    private Map<String, List<RouterBean>> tempPathMap = new HashMap<>();

    //key:组名,value:类名"ARouter$$Path$$app.class"
    private Map<String, String> tempGroupMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        //初始化
        //操作Element工具类(类,函数,属性都是Element)
        mElementUtils = processingEnv.getElementUtils();
        //type(类信息)工具类,包含用于操作TypeMirror的工具方法
        mTypeUtils = processingEnv.getTypeUtils();
        //Messager日志打印
        mMessager = processingEnv.getMessager();
        //文件生成器
        mFiler = processingEnv.getFiler();

        Map<String, String> options = processingEnv.getOptions();
        if (!EmptyUtils.isEmpty(options)) {
            //组名
            mModuleName = processingEnv.getOptions().get(Constants.AROUTER_MODULE_NAME);
            //生成calss文件的所在的包名
            mPackageNameForApt = processingEnv.getOptions().get(Constants.PACKAGENAME_FOR_APT);
            mMessager.printMessage(Diagnostic.Kind.NOTE, "moduleName====>" + mModuleName);
            mMessager.printMessage(Diagnostic.Kind.NOTE, "packageNameForApt====>" + mPackageNameForApt);
        }
        if (EmptyUtils.isEmpty(mModuleName) || EmptyUtils.isEmpty(mPackageNameForApt)) {
            throw new RuntimeException("注解需要moduleName或者packageNameForApt,请在build.gradle中配置");
        }


    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!EmptyUtils.isEmpty(annotations)) {
            //获取所有被ARouter注解的元素集合
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ARouter.class);
            if (!EmptyUtils.isEmpty(elements)) {
                //解析元素
                try {
                    parseElements(elements);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 解析所有被ARouter注解元素的集合
     *
     * @param elements
     */
    private void parseElements(Set<? extends Element> elements) throws IOException {

        //通过Element工具类,获取Activity类型
        TypeElement activityElement = mElementUtils.getTypeElement(Constants.ACTIVITY);
        //获取Call类型
        TypeElement callElement = mElementUtils.getTypeElement(Constants.CALL);


        TypeMirror activityMirror = activityElement.asType();

        TypeMirror callMirror = callElement.asType();
        for (Element element : elements) {
            TypeMirror elementMirror = element.asType();
            mMessager.printMessage(Diagnostic.Kind.NOTE,
                    "遍历的元素信息为:" + elementMirror.toString());
            //获取每个类上的ARouter注解,对应的path值
            ARouter aRouter = element.getAnnotation(ARouter.class);

            //路由详细信息封装到实体类
            RouterBean routerBean = new RouterBean.Builder()
                    .setGroup(aRouter.group())
                    .setPath(aRouter.path())
                    .setElement(element)
                    .build();
            //判断ARouter注解是否用在Activity类之上
            if (mTypeUtils.isSubtype(elementMirror, activityMirror)) {
                routerBean.setType(RouterBean.Type.ACTIVITY);
            } else if (mTypeUtils.isSubtype(elementMirror,callMirror)){
                routerBean.setType(RouterBean.Type.CALL);
            }else {
                throw new RuntimeException("ARouter注解目前只能仅限于Activity上");
            }

            //赋值临时的map存储以上信息,用与遍历生成代码
            valueOfPathMap(routerBean);
        }

        //ARouterLoadPath和ARouterLoadGroup类型,用来生成类文件时实现接口
        TypeElement groupType = mElementUtils.getTypeElement(Constants.AROUTE_GROUP);
        TypeElement pathType = mElementUtils.getTypeElement(Constants.AROUTER_PATH);

        //1.生成路由详细path类文件,如:ARouter$$Path$$app
        createPathFile(pathType);

        //2.生成路由组Group类文件 如:ARouter$$Group$$app
        createGroupFile(groupType, pathType);
    }

    /**
     * 生成路由组Group对应详细Path,如ARouter$$Path$$app
     *
     * @param pathType
     */
    private void createPathFile(TypeElement pathType) throws IOException {

        mMessager.printMessage(Diagnostic.Kind.NOTE, "开始生成Path");

        if (EmptyUtils.isEmpty(tempPathMap)) {

            mMessager.printMessage(Diagnostic.Kind.NOTE, "tempPathMap为空");
            return;
        }


        //方法返回值Map<String, RouterBean>
        TypeName typeName = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class));
        //遍历分组,每一个分组创建一个类文件,如ARouter$$Path$$app,
        // 生成public Map<String, RouterBean> loadPath()
        for (Map.Entry<String, List<RouterBean>> entry : tempPathMap.entrySet()) {
            MethodSpec.Builder methodBuilder =
                    MethodSpec.methodBuilder(Constants.PATH_METHOD_NAME)
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(typeName);//方法的返回值

            //不循环部分Map<String, RouterBean> pathMap = new HashMap<>();
            methodBuilder.addStatement("$T<$T,$T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    Constants.PATH_PARAMETE_NAME,
                    ClassName.get(HashMap.class));
            List<RouterBean> pathList = entry.getValue();
            mMessager.printMessage(Diagnostic.Kind.NOTE, "开始遍历");

            for (RouterBean routerBean : pathList) {
//                生成方法体中的内容
//                pathMap.put("/app/OrderActivity", RouterBean.create(RouterBean.Type.ACTIVITY,
//                        OrderActivity.class,
//                        "/app/OrderActivity",
//                        "app"));

                methodBuilder.addStatement("$N.put($S,$T.create($T.$L,$T.class,$S,$S))",
                        Constants.PATH_PARAMETE_NAME,
                        routerBean.getPath(),
                        ClassName.get(RouterBean.class),
                        ClassName.get(RouterBean.Type.class),
                        routerBean.getType(),
                        ClassName.get((TypeElement) routerBean.getElement()),
                        routerBean.getPath(),
                        routerBean.getGroup());

            }
            //return pathMap;
            methodBuilder.addStatement("return $N", Constants.PATH_PARAMETE_NAME);

            String finalClassName = Constants.PATH_FILE_NAME + entry.getKey();

            mMessager.printMessage(Diagnostic.Kind.NOTE, "生成的类文件为:" + finalClassName);

            TypeSpec typeSpec = TypeSpec.classBuilder(finalClassName)
                    .addSuperinterface(ClassName.get(pathType))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodBuilder.build())
                    .build();

            JavaFile javaFile = JavaFile.builder(mPackageNameForApt, typeSpec).build();
            javaFile.writeTo(mFiler);
            //路径文件生成出来了，才能赋值路由组tempGroupMap
            tempGroupMap.put(entry.getKey(), finalClassName);

        }
    }

    /**
     * 生成路由组Group,如ARouter$$Group$$app
     *
     * @param groupType
     * @param pathType
     */
    private void createGroupFile(TypeElement groupType, TypeElement pathType) throws IOException {

        mMessager.printMessage(Diagnostic.Kind.NOTE, "开始生成Group");

        if (EmptyUtils.isEmpty(tempPathMap) || EmptyUtils.isEmpty(tempGroupMap)) return;

        //方法返回值 Map<String, Class<? extends ARouterLoadPath>>
        TypeName typeReturnName = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class),
                // 第二个参数：Class<? extends ARouterLoadPath>
                // 某某Class是否属于ARouterLoadPath接口的实现类
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathType)))
        );

        //生成
        // @Override
        // public Map<String, Class<? extends ARouterLoadPath>> loadGroup()
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.group_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(typeReturnName);
        /*****************************生成方法体***********************************/
        //遍历之前 生成Map<String,Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
        methodBuilder.addStatement("$T<$T,$T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathType))),
                Constants.GROUP_PARAMETE_NAME,
                ClassName.get(HashMap.class));

        //生成方法内容
        for (Map.Entry<String, String> entry : tempGroupMap.entrySet()) {
            //groupMap.put("app",ARouter$$Path$$app.class);
            methodBuilder.addStatement("$N.put($S,$T.class)",
                    Constants.GROUP_PARAMETE_NAME,
                    entry.getKey(),
                    ClassName.get(mPackageNameForApt, entry.getValue()));
        }

        //生成返回值 return groupMap;
        methodBuilder.addStatement("return $N", Constants.GROUP_PARAMETE_NAME);

        /**************************************生成方法体************************************/

        /*********************************生成类**************************************************/

        String finalClassName = Constants.GROUP_FILE_NAME + mModuleName;
        TypeSpec typeSpec = TypeSpec.classBuilder(finalClassName)
                .addSuperinterface(ClassName.get(groupType))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();
        /*********************************生成类**************************************************/

        //导出文件
        JavaFile.builder(mPackageNameForApt, typeSpec).build().writeTo(mFiler);
    }


    /**
     * 临时map存储,用来存放路由组Group对应的Path类对象,生成路由类文件时遍历
     *
     * @param routerBean
     */
    private void valueOfPathMap(RouterBean routerBean) {

        if (checkRouterPath(routerBean)) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, routerBean.toString());
            List<RouterBean> routerBeans = tempPathMap.get(routerBean.getGroup());
            //从map集合中找不到key
            if (EmptyUtils.isEmpty(routerBeans)) {
                routerBeans = new ArrayList<>();
                routerBeans.add(routerBean);
                tempPathMap.put(routerBean.getGroup(), routerBeans);
            } else {
                routerBeans.add(routerBean);
//                for (RouterBean bean : routerBeans) {
//                    if (!routerBean.getPath().equalsIgnoreCase(bean.getPath())) {
//
//                    }
//                }
            }
        } else {
            mMessager.printMessage(Diagnostic.Kind.ERROR, "ARouter注解未按规范填写,@ARouter(path =/app/MainActivity)");
        }
    }

    /**
     * 检查路由path是否按规定的路径填写
     *
     * @param routerBean
     * @return
     */
    private boolean checkRouterPath(RouterBean routerBean) {
        String group = routerBean.getGroup();
        String path = routerBean.getPath();

        if (EmptyUtils.isEmpty(path) || !path.startsWith("/")) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, "ARouter注解未按规范填写,@ARouter(path = /app/MainActivity)");
            return false;
        }


        if (path.lastIndexOf("/") == 0) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, "ARouter注解未按规范填写,@ARouter(path =/app/MainActivity)");
            return false;
        }

        //从第一个/到第二个/截取出组名
        String finalGroup = path.substring(1, path.lastIndexOf("/"));
        if (finalGroup.contains("/")) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, "ARouter注解未按规范填写,@ARouter(path =/app/MainActivity)");
            return false;
        }

        //ARouter中有group有值,必须是模块的名字
        if (!EmptyUtils.isEmpty(group) && !group.equals(mModuleName)) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, "ARouter注解未按规范填写,@ARouter(path =/app/MainActivity)");
            return false;
        } else {
            routerBean.setGroup(finalGroup);
        }

        return true;
    }
}
