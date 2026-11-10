package com.pain.green.resource.binding;

import com.pain.green.ioc.domain.Company;
import com.pain.green.ioc.domain.User;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import java.util.HashMap;
import java.util.Map;

public class DataBinderDemo {
    public static void main(String[] args) {
        User user = new User();
        DataBinder dataBinder = new DataBinder(user);
        Map<String, Object> source = new HashMap<>();
        source.put("id", 1);
        source.put("name", "侯景");

        // BindingResult bindingResult = testAll(source, dataBinder);
        // BindingResult bindingResult = testUnknownFields(source, dataBinder);
        // BindingResult bindingResult = testAutoGrowNestedPaths(source, dataBinder);

        // 包含错误文案 code
        BindingResult bindingResult = testRequiredFields(source, dataBinder);

        System.out.println(user);
        System.out.println(bindingResult);
    }

    private static BindingResult testAll(Map source, DataBinder dataBinder) {
        MutablePropertyValues propertyValues = new MutablePropertyValues(source);
        dataBinder.bind(propertyValues);
        return dataBinder.getBindingResult();
    }

    /**
     * propertyValues 中存在未知属性（默认是忽略的）
     */
    private static BindingResult testUnknownFields(Map source, DataBinder dataBinder) {
        source.put("age", 28);
        MutablePropertyValues propertyValues = new MutablePropertyValues(source);
        dataBinder.setIgnoreUnknownFields(false);
        dataBinder.bind(propertyValues);
        return dataBinder.getBindingResult();
    }

    /**
     * 嵌套属性（默认是支持的）
     */
    private static BindingResult testAutoGrowNestedPaths(Map source, DataBinder dataBinder) {
        source.put("company", new Company());
        source.put("company.name", "google");
        MutablePropertyValues propertyValues = new MutablePropertyValues(source);
        dataBinder.setAutoGrowNestedPaths(false);
        // dataBinder.setIgnoreInvalidFields(true);
        dataBinder.bind(propertyValues);
        return dataBinder.getBindingResult();
    }

    private static BindingResult testRequiredFields(Map source, DataBinder dataBinder) {
        dataBinder.setRequiredFields("id", "name", "city");

        // 配置避免属性被绑定
        // setDisallowedFields
        MutablePropertyValues propertyValues = new MutablePropertyValues(source);
        dataBinder.bind(propertyValues);
        return dataBinder.getBindingResult();
    }
}
