package foundation;

import biz.ingest.JsonUtil;
import org.springframework.core.ResolvableType;

public class ClazzExplore {

    public static void main(String[] args) throws ClassNotFoundException {
        Event<String> event = new CloseEvent();
        System.out.println(event.getClass().getName());
        Class<?> clazz = Class.forName(event.getClass().getName());

        // return ClassUtils.findTypeParameter(eventClass, NotificationEvent.class);
        Class<?> typeParameter = findTypeParameter(clazz, Event.class);
        System.out.println(typeParameter.getName());

        System.out.println(JsonUtil.objToStr(event));

    }

    public static Class<?> findTypeParameter(Class<?> subclazz, Class<?> superclazz, int ... index ) {
        if (index.length > 1) {
            throw new IllegalArgumentException("index参数只能有一个");
        }
        if (index.length == 0) {
            index = new int[]{0};
        }
        ResolvableType type = ResolvableType.forClass(subclazz);

        type = type.getSuperType();
        // find super type repeatedly until find the type is NotificationEvent
        while (!type.equals(ResolvableType.NONE) && !type.getRawClass().equals(superclazz)) {
            type = type.getSuperType();
        }
        if (type.getRawClass() != null &&
                type.getRawClass().equals(superclazz)) {
            ResolvableType genericType = type.getGeneric(index);
            if (genericType != null) {
                @SuppressWarnings("unchecked")
                Class<?> enclosedType = genericType.resolve();
                return enclosedType;
            }
        }
        return null;
    }
}
