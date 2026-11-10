package com.pain.green.resource.conversion.type;

import com.pain.green.ioc.domain.User;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Stream;

public class GenericApiDemo {
    public static void main(String[] args) throws NoSuchMethodException {
        // test1();

        // test2();

        // test3();

        test4();
    }

    public static void test1() {
        // 原生类型 primitive types
        Class intClazz = int.class;

        // 数组类型 array types
        Class arrayClazz = Object[].class;

        // 原始类型 raw types
        Class stringClazz = String.class;

        // 泛型参数类型 parameterized types
        ParameterizedType parameterizedType = (ParameterizedType) ArrayList.class.getGenericSuperclass();
        Type[] types = parameterizedType.getActualTypeArguments();

        System.out.println("ArrayList genericSuperclass = " + parameterizedType);
        System.out.println("ArrayList genericSuperclass type args = " + Arrays.asList(types));

        Stream.of(types).map(TypeVariable.class::cast).forEach(System.out::println);

        parameterizedType = (ParameterizedType) MyHashMap.class.getGenericSuperclass();
        types = parameterizedType.getActualTypeArguments();

        System.out.println("ArrayList genericSuperclass = " + parameterizedType);
        System.out.println("ArrayList genericSuperclass type args = " + Arrays.asList(types));
    }

    public static void test2() throws NoSuchMethodException {
        Method findByIdMethod = Repository.class.getMethod("findById", Long.class);
        Method findAllMethod = Repository.class.getMethod("findAll");
        Method saveMethod = Repository.class.getMethod("save", Object.class);

        // 解析返回类型
        Class<?> findByIdReturnType = GenericTypeResolver.resolveReturnType(findByIdMethod, Repository.class);
        Class<?> findAllReturnType = GenericTypeResolver.resolveReturnType(findAllMethod, Repository.class);
        Class<?> saveReturnType = GenericTypeResolver.resolveReturnType(saveMethod, Repository.class);

        System.out.println("Repository#findById 返回类型: " + findByIdReturnType);
        System.out.println("Repository#findAll 返回类型: " + findAllReturnType);
        System.out.println("Repository#save 返回类型: " + saveReturnType);

        findByIdReturnType = GenericTypeResolver.resolveReturnType(findByIdMethod, UserRepository.class);
        findAllReturnType = GenericTypeResolver.resolveReturnType(findAllMethod, UserRepository.class);
        saveReturnType = GenericTypeResolver.resolveReturnType(saveMethod, UserRepository.class);

        System.out.println("UserRepository#findById 返回类型: " + findByIdReturnType);
        System.out.println("UserRepository#findAll 返回类型: " + findAllReturnType);
        System.out.println("UserRepository#save 返回类型: " + saveReturnType);

        findAllMethod = UserRepository.class.getMethod("findAll");
        Class<?> clazz = GenericTypeResolver.resolveReturnTypeArgument(findAllMethod, List.class);
        System.out.println("UserRepository#findAll 返回类型: " + clazz);
    }

    public static void test3() {
        // 类型变量映射表，将类型参数（如 T、K、V）映射到具体的类型
        // Map<TypeVariable, Type> typeVariableMap = GenericTypeResolver.getTypeVariableMap(UserRepository.class);
        Map<TypeVariable, Type> typeVariableMap = GenericTypeResolver.getTypeVariableMap(MyHashMap.class);

        for (Map.Entry<TypeVariable, Type> entry : typeVariableMap.entrySet()) {
            // 类型变量：类型变量名，声明位置，边界类型
            TypeVariable typeVariable = entry.getKey();
            Type actualType = entry.getValue();

            System.out.println("声明位置：" + typeVariable.getGenericDeclaration() + "，类型变量: " + typeVariable.getName() + " -> 实际类型: " + actualType.getTypeName());
        }
    }

    public static void test4() {
        // forClass
        // forMethodParameter
        // forMethodReturnType
        ResolvableType resolvableType = ResolvableType.forClass(MyHashMap.class);

        System.out.println(resolvableType.getSuperType());
        System.out.println(resolvableType.getSuperType().getSuperType());

        System.out.println(resolvableType.asMap());

        // 获取 rawType
        System.out.println(resolvableType.asMap().resolve());

        // 获取泛型参数类型
        System.out.println(resolvableType.asMap().resolveGeneric(0));
        System.out.println(resolvableType.asMap().resolveGeneric(1));
    }

    /**
     * 获取接口的泛型参数类型
     */
    public static Class<?>[] getInterfaceTypeArguments(Class<?> clazz, Class<?> interfaceClass) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericInterface;

                if (pt.getRawType().equals(interfaceClass)) {
                    return Arrays.stream(pt.getActualTypeArguments())
                            .map(type -> (Class<?>) type)
                            .toArray(Class<?>[]::new);
                }
            }
        }
        return new Class<?>[0];
    }

    public Object createBean(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Class<?> rawType = (Class<?>) pt.getRawType();
            Type[] typeArgs = pt.getActualTypeArguments();

            // 根据泛型信息创建合适的 Bean
            if (rawType == Repository.class) {
                // return createRepository(typeArgs[0]); // 获取 T 的类型
                return null;
            }
        }
        return null;
    }

    interface Repository<T> {
        T findById(Long id);
        List<T> findAll();
        T save(T entity);
    }

    static class UserRepository implements Repository<User> {
        @Override
        public User findById(Long id) {
            return null;
        }

        @Override
        public List<User> findAll() {
            return null;
        }

        @Override
        public User save(User entity) {
            return null;
        }
    }

    static class MyHashMap extends HashMap<String, Long> {}
}
