package com.thor.springboot.server.util.argument.resolver;

import java.lang.annotation.*;

/**
 * @author huangpin
 * @date 2018-12-10
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JSONParam {
}
