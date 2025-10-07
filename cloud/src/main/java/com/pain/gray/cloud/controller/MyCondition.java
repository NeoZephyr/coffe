package com.pain.gray.cloud.controller;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

public class MyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String[] props = (String[]) metadata.getAnnotationAttributes("com.pain.cloud.MyConditional").get("value");

        for (String prop : props) {
            if (!StringUtils.hasText(context.getEnvironment().getProperty(prop))) {
                return false;
            }
        }
        return true;
    }
}
