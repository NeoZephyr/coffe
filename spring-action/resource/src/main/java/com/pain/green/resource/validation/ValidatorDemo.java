package com.pain.green.resource.validation;

import com.pain.green.ioc.domain.User;
import org.springframework.context.MessageSource;
import org.springframework.validation.*;

import java.util.Locale;

import static com.pain.green.resource.validation.ErrorsMessageDemo.createMessageSource;

public class ValidatorDemo {
    public static void main(String[] args) {
        User user = new User();
        user.setId((long) 10);
        UserValidator validator = new UserValidator();
        Errors errors = new BeanPropertyBindingResult(user, "user");
        validator.validate(user, errors);

        MessageSource messageSource = createMessageSource();

        for (ObjectError error : errors.getAllErrors()) {
            String message = messageSource.getMessage(error.getCode(), error.getArguments(), Locale.getDefault());
            System.out.println(message);
        }
    }

    static class UserValidator implements Validator {
        @Override
        public boolean supports(Class<?> clazz) {
            return clazz.isAssignableFrom(User.class);
        }

        @Override
        public void validate(Object target, Errors errors) {
            User user = (User) target;
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "id", "id.required");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.required");
        }
    }
}
