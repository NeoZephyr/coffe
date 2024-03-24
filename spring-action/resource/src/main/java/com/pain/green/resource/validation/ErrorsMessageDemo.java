package com.pain.green.resource.validation;

import com.pain.green.ioc.domain.User;
import org.springframework.context.MessageSource;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Locale;

public class ErrorsMessageDemo {
    public static void main(String[] args) {
        User user = new User();
        user.setName("阿甘左");

        Errors errors = new BeanPropertyBindingResult(user, "user");

        errors.reject("user.properties.not.null");
        errors.rejectValue("name", "name.required");

        List<FieldError> fieldErrors = errors.getFieldErrors();
        List<ObjectError> globalErrors = errors.getGlobalErrors();
        List<ObjectError> allErrors = errors.getAllErrors();

        MessageSource messageSource = createMessageSource();

        for (ObjectError objectError : allErrors) {
            String message = messageSource.getMessage(objectError.getCode(), objectError.getArguments(), Locale.getDefault());
            System.out.println(message);
        }
    }

    static MessageSource createMessageSource() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("user.properties.not.null", Locale.getDefault(), "user 所有属性不能为空");
        messageSource.addMessage("name.required", Locale.getDefault(), "the name of User must not be null");
        messageSource.addMessage("id.required", Locale.getDefault(), "the id of User must not be null");

        return messageSource;
    }
}
