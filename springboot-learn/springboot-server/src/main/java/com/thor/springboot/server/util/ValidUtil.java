package com.thor.springboot.server.util;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author huangpin
 * @date 2018-12-10
 */
public class ValidUtil {
    private static Validator validator = Validation
            .buildDefaultValidatorFactory().getValidator();

    public static <T> void validate(T t) {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);
        if (constraintViolations.size() > 0) {
            for (ConstraintViolation<T> constraintViolation : constraintViolations) {
                String msg = constraintViolation.getMessage();
                if (StringUtils.isNotEmpty(msg)) {
                    throw new IllegalArgumentException(msg);
                }
            }
        }
    }
}
