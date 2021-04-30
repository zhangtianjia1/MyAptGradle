package com.zhangtj.apt_processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * @ProjectName: MyGradleButter
 * @Package: com.zhangtj.apt_processor
 * @ClassName: ClassFactory
 * @Description: java类作用描述
 * @Author: ztj
 * @CreateDate: 2021/4/30 2:12 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/4/30 2:12 PM
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ClassFactory {

    private String mBindingClassName;
    private String mPackageName;
    private TypeElement mTypeElement;
    private Map<Integer, VariableElement> mVariableElementMap = new HashMap<>();

    ClassFactory(Elements elementUtils, TypeElement classElement) {
        this.mTypeElement = classElement;
        PackageElement packageElement = elementUtils.getPackageOf(mTypeElement);
        String packageName = packageElement.getQualifiedName().toString();
        String className = mTypeElement.getSimpleName().toString();
        this.mPackageName = packageName;
        this.mBindingClassName = className + "_MkViewBinding";
    }

    public void putElement(int id, VariableElement element) {
        mVariableElementMap.put(id, element);
    }

    /**
     * 使用 javapoet 创建 Java 代码
     * javapoet
     *
     * @return TypeSpec
     */
    public TypeSpec generateJavaCodeWithJavapoet() {
        TypeSpec bindingClass = TypeSpec.classBuilder(mBindingClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateMethodsWithJavapoet())
                .build();
        return bindingClass;

    }

    /**
     * 使用 javapoet 创建 Method
     *
     * @return MethodSpec
     */
    private MethodSpec generateMethodsWithJavapoet() {
        ClassName owner = ClassName.bestGuess(mTypeElement.getQualifiedName().toString());
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bindView")// 方法名
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)// 返回类型
                .addParameter(owner, "owner");

        for (int id : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(id);
            // 变量名
            String viewName = element.getSimpleName().toString();
            // 变量父类名称
            String viewType = element.asType().toString();
            String text = "{0}.{1}=({2})({3}.findViewById({4}));";
            methodBuilder.addCode(MessageFormat.format(text, "owner", viewName, viewType, "((android.app.Activity)owner)", String.valueOf(id)));
        }
        return methodBuilder.build();
    }


    public String getPackageName() {
        return mPackageName;
    }

    public String getProxyClassFullName() {
        return mPackageName + "." + mBindingClassName;
    }

    public TypeElement getTypeElement() {
        return mTypeElement;
    }
}
