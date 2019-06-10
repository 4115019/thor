package com.thor.java.learn.annotation;

import java.lang.annotation.*;

/**
 * @author huangpin
 * @date 2019-06-10
 */

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodAnnotation {
    String desc() default "methodDefault";
}
