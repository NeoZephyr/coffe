package com.pain.green.bean.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalScope implements Scope {

    public static final String SCOPE_NAME = "thread-local";

    private NamedThreadLocal<Map<String, Object>> threadLocal = new NamedThreadLocal("thread-local-scope") {
        @Override
        protected Object initialValue() {
            return new HashMap<>();
        }
    };

    @NonNull
    private Map getContext() {
        return threadLocal.get();
    }

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Map context = getContext();
        Object bean = context.get(name);

        if (bean == null) {
            bean = objectFactory.getObject();
            context.put(name, bean);
        }

        return bean;
    }

    @Override
    public Object remove(String name) {
        Map context = getContext();
        return context.remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {

    }

    @Override
    public Object resolveContextualObject(String key) {
        Map context = getContext();
        return context.get(key);
    }

    @Override
    public String getConversationId() {
        return String.valueOf(Thread.currentThread().getId());
    }
}
