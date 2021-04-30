package com.zhangtj.apt_processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.zhangtj.apt_annotation.BindView;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * @ProjectName: MyGradleButter
 * @Package: com.zhangtj.apt_processor
 * @ClassName: BindViewProcessor
 * @Description: java类作用描述
 * @Author: ztj
 * @CreateDate:021/4/30 2:11 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/4/30 2:11 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
@AutoService(Process.class)
public class BindViewProcessor extends AbstractProcessor {
    private Elements mElementUtils;
    private Map<String, ClassFactory> mClassCreatorFactoryMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
    }

    /**
     * 处理包含指定注解对象的代码元素
     * 获取控件变量的引用以及对应的viewId,先遍历出每个Activity所包含的所有注解对象
     *
     * @param set              Set<? extends TypeElement>
     * @param roundEnvironment RoundEnvironment 所有注解的集合
     * @return true
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mClassCreatorFactoryMap.clear();
        //得到所有的注解
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : elements) {
            VariableElement variableElement = (VariableElement) element;
            //获取类信息
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            //类的完整包名+类名
            String fullClassName = classElement.getQualifiedName().toString();
            ClassFactory proxy = mClassCreatorFactoryMap.get(fullClassName);
            if (proxy == null) {
                proxy = new ClassFactory(mElementUtils, classElement);
                mClassCreatorFactoryMap.put(fullClassName, proxy);
            }
            BindView bindAnnotation = variableElement.getAnnotation(BindView.class);
            //获取 View 的 id
            int id = bindAnnotation.value();
            proxy.putElement(id, variableElement);
        }


        //使用 javapoet 创建java文件
        for (String key : mClassCreatorFactoryMap.keySet()) {
            ClassFactory proxyInfo = mClassCreatorFactoryMap.get(key);
            JavaFile javaFile = JavaFile.builder(proxyInfo.getPackageName(), proxyInfo.generateJavaCodeWithJavapoet()).build();
            try {
                //　生成文件
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    /**
     * @return 指定java版本。
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
    /**
     * 指定该目标的注解对象,指定注解处理器是注册给哪个注解的，返回指定支持的注解类集合。
     *
     * @return Set<String> getCanonicalName即包名.类名，不同的对象获取的值不同，可能为空
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> hashset = new HashSet<>();
        hashset.add(BindView.class.getCanonicalName());
        return hashset;
    }
}
