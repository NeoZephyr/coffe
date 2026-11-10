package com.pain.green.resource.conversion;

import com.pain.green.ioc.domain.User;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

import java.util.Properties;

/**
 * 扩展 PropertyEditor
 */
public class CustomizedPropertyEditorRegistrar implements PropertyEditorRegistrar {

    /**
     * 这里没有被调用到
     *
     * 1. 需要提前注册才会被应用，通过 BeanFactoryPostProcessor#postProcessBeanFactory 里面 beanFactory#addPropertyEditorRegistrar 实现
     *
     * <bean id="customEditorConfigurer" class="org.springframework.beans.factory.config.CustomEditorConfigurer">
     *         <property name="propertyEditorRegistrars">
     *             <list>
     *                 <bean class="com.pain.green.resource.conversion.CustomizedPropertyEditorRegistrar"/>
     *             </list>
     *         </property>
     * </bean>
     *
     * 2. requiredType 的 含义 – the type of the property
     * 应该不是 User.class 而应该是属性类型 Properties.class
     *
     */
    @Override
    public void registerCustomEditors(PropertyEditorRegistry registry) {
        // 1. 通用类型转换
        // 2. java bean 属性类型转换

        registry.registerCustomEditor(User.class, "context", new StringToPropertiesPropertyEditor());
    }
}
