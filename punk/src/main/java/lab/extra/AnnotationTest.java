package lab.extra;

import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.*;

public class AnnotationTest {

    public static void main(String[] args) throws NoSuchMethodException {
        Foo foo = new Foo();
        Author fooAuthor = foo.getClass().getAnnotation(Author.class);
        System.out.println(fooAuthor.value());
        Author fooTarAuthor = foo.getClass().getMethod("tar").getAnnotation(Author.class);
        System.out.println(fooTarAuthor.value());

        Bar bar = new Bar();
        Author barAuthor = bar.getClass().getAnnotation(Author.class);
        System.out.println(barAuthor.value());
        Author barTarAuthor = bar.getClass().getMethod("tar").getAnnotation(Author.class);
        System.out.println(barTarAuthor.value());
        barAuthor = AnnotatedElementUtils.findMergedAnnotation(bar.getClass(), Author.class);
        barTarAuthor = AnnotatedElementUtils.findMergedAnnotation(bar.getClass().getMethod("tar"), Author.class);

        System.out.println(barAuthor.value());
        System.out.println(barTarAuthor.value());
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface Author {
        String value();
    }

    @Author("pain")
    static class Foo {
        @Author("pain")
        public void tar() {}
    }

    static class Bar extends Foo {
        @Override
        public void tar() {}
    }
}
