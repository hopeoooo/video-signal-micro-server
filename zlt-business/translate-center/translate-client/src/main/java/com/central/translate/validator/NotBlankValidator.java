package com.central.translate.validator;

import cn.hutool.core.util.StrUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 自定义校验
 *
 * @author lance
 * @since 2022 -02-18 12:58:58
 */
public class NotBlankValidator implements ConstraintValidator<INotBlank, String> {

    @Override
    public void initialize(INotBlank constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String o, ConstraintValidatorContext constraintValidatorContext) {
        return StrUtil.isNotBlank(o);
    }
}
