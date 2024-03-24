package com.pain.green.resource.validation;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class BeanValidationDemo {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:validation-context.xml");

        Validator validator = applicationContext.getBean(Validator.class);
        System.out.println(validator instanceof LocalValidatorFactoryBean);

        StudentProcessor studentProcessor = applicationContext.getBean(StudentProcessor.class);
        studentProcessor.process(new Student());

        applicationContext.close();
    }

    @Component
    @Validated
    static class StudentProcessor {
        void process(@Valid Student student) {
            System.out.println(student);
        }
    }

    static class Student {

        @NotNull
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
