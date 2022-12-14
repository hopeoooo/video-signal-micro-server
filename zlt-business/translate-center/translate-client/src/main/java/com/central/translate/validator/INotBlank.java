package com.central.translate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NotBlankValidator.class})
public @interface INotBlank {

    // 默认错误消息
    String message() default "{javax.validation.constraints.NotBlank.message}";

    // 分组
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
