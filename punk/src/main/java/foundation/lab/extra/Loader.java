package foundation.lab.extra;

import sun.misc.Launcher;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;

public class Loader {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        // loadClass();
        // printClassLoaderTest();
    }

    static class HelloClassLoader extends ClassLoader {

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] clazz = loadClazzData();
            return defineClass(name, clazz, 0, clazz.length);
        }

        private byte[] loadClazzData() {
            byte[] data = null;

            try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                 FileInputStream input = new FileInputStream("input/HelloByteCode.class")
            ) {
                byte[] buf = new byte[1024];
                int len = 0;
                while ((len = input.read(buf)) != -1) {
                    output.write(buf, 0, len);
                }

                data = output.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

        public byte[] decode(String clazzText) {
            return Base64.getDecoder().decode(clazzText);
        }
    }

    static class Singleton {
        private static Singleton instance;

        static {
            System.out.println("loading class Singleton");
        }

        public static Singleton getInstance() {
            if (instance == null) {
                synchronized (Singleton.class) {
                    if (instance == null) {
                        instance = new Singleton();
                    }
                }
            }

            return instance;
        }

        public static Singleton getAnotherInstance() {
            return Holder.instance;
        }

        static class Holder {
            static {
                System.out.println("loading class Holder");
            }

            private static Singleton instance = new Singleton();
        }
    }

    private static void testInstance() {
        Singleton.getInstance();
        new Singleton();
        Singleton.getAnotherInstance();
    }

    private static void loadClassTest() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        HelloClassLoader classLoader = new HelloClassLoader();
        Class<?> clazz = classLoader.findClass("com.pain.white.coffee.bytecode.HelloByteCode");
        Object o = clazz.newInstance();
        Method hello = o.getClass().getMethod("hello");
        System.out.println(hello.invoke(o));;
        System.out.println(o.getClass().getClassLoader());
        System.out.println(o.getClass().getClassLoader().getParent());
    }

    public static void printClassLoaderTest() {
        URL[] urls = Launcher.getBootstrapClassPath().getURLs();
        System.out.println("bootstrap classloader");
        for (int i = 0; i < urls.length; ++i) {
            System.out.printf("%d ==> %s\n", i, urls[i].toExternalForm());
        }

        Object ucp = insightField(Loader.class.getClassLoader().getParent(), "ucp");
        Object path = insightField(ucp, "path");
        ArrayList ps = (ArrayList) path;

        System.out.println("ext classloader");

        for (int i = 0; i < Objects.requireNonNull(ps).size(); ++i) {
            System.out.printf("%d ==> %s\n", i, ps.get(i).toString());
        }

        ucp = insightField(Loader.class.getClassLoader(), "ucp");
        path = insightField(ucp, "path");
        ps = (ArrayList) path;

        System.out.println("app classloader");

        for (int i = 0; i < Objects.requireNonNull(ps).size(); ++i) {
            System.out.printf("%d ==> %s\n", i, ps.get(i).toString());
        }
    }

    private static Object insightField(Object obj, String fName) {
        try {
            Field f = null;
            if (obj instanceof URLClassLoader) {
                f = URLClassLoader.class.getDeclaredField(fName);
            } else {
                f = obj.getClass().getDeclaredField(fName);
            }

            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
