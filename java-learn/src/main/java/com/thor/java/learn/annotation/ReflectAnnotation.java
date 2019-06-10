package com.thor.java.learn.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author huangpin
 * @date 2019-06-10
 */

@FieldTypeAnnotation(type = "class", hobby = {"smoke"})
public class ReflectAnnotation {

    @FieldTypeAnnotation(hobby = {"sleep", "play"})
    private String maomao;

    @FieldTypeAnnotation(hobby = {"phone", "buy"}, age = 27, type = "normal")
    private String zhangwenping;

    @MethodAnnotation()
    public void methodDefault() {
    }

    @MethodAnnotation(desc = "method1")
    public void method1() {
    }

    public static void main(String[] args) {

        Class<ReflectAnnotation> clz = ReflectAnnotation.class;
        boolean clzHasAnno = clz.isAnnotationPresent(FieldTypeAnnotation.class);
        if (clzHasAnno) {
            // 获取类上的注解
            FieldTypeAnnotation annotation = clz.getAnnotation(FieldTypeAnnotation.class);
            // 输出注解上的属性
            int age = annotation.age();
            String[] hobby = annotation.hobby();
            String type = annotation.type();
            System.out.println(clz.getName() + " age = " + age + ", hobby = " + Arrays.asList(hobby).toString() + " type = " + type);
        }

        Field[] fields = clz.getDeclaredFields();
        for(Field field : fields){
            boolean fieldHasAnno = field.isAnnotationPresent(FieldTypeAnnotation.class);
            if(fieldHasAnno){
                FieldTypeAnnotation fieldAnno = field.getAnnotation(FieldTypeAnnotation.class);
                //输出注解属性
                int age = fieldAnno.age();
                String[] hobby = fieldAnno.hobby();
                String type = fieldAnno.type();
                System.out.println(field.getName() + " age = " + age + ", hobby = " + Arrays.asList(hobby).toString() + " type = " + type);
            }
        }

        Method[] methods = clz.getDeclaredMethods();
        for(Method method : methods){
            boolean methodHasAnno = method.isAnnotationPresent(MethodAnnotation.class);
            if(methodHasAnno){
                //得到注解
                MethodAnnotation methodAnno = method.getAnnotation(MethodAnnotation.class);
                //输出注解属性
                String desc = methodAnno.desc();
                System.out.println(method.getName() + " desc = " + desc);
            }
        }
    }
}
