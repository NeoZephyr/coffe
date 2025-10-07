package com.pain.green.ioc.bean;

import com.pain.green.ioc.annotation.UserGroup;
import com.pain.green.ioc.domain.User;
import com.pain.green.ioc.domain.UserHolder;
import com.pain.green.ioc.domain.UserRepo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Collection;
import java.util.Set;

public class BeanInjectTests implements BeanFactoryAware, ApplicationContextAware {

    public static void main(String[] args) {
        // Setter 方法、Constructor 依赖注入
        // xmlInject();
        // annoInject();
        // apiInject();

        // autowireInject();

        // fieldInject();
        // methodInject();
        // awareInject();

        qualifierInject();

        // lazyInject();

        // builtInInject();
    }

    /**
     * 手动模式：
     * 1. XML 资源配置元信息
     * 2. Java 注解配置元信息
     * 3. API 配置元信息
     */

    public static void xmlInject() {
        // dependency-lookup-context.xml
        // 数组类型注入
        // 集合类型注入
        // 复杂类型注入

        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:dependency-setter-injection.xml");
        UserHolder userHolder = beanFactory.getBean(UserHolder.class);
        System.out.println("xml setter inject, userHolder: " + userHolder);

        beanFactory = new ClassPathXmlApplicationContext("classpath:dependency-constructor-injection.xml");
        userHolder = beanFactory.getBean(UserHolder.class);
        System.out.println("xml constructor inject, userHolder: " + userHolder);
    }

    public static void annoInject() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(BeanInjectTests.class);

        // 还可以用这种方式引入
        // @ImportResource("classpath:dependency-lookup-context.xml")
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        beanDefinitionReader.loadBeanDefinitions("dependency-lookup-context.xml");
        applicationContext.refresh();

        UserHolder userHolder = applicationContext.getBean(UserHolder.class);

        System.out.println("annotation inject, userHolder: " + userHolder);

        applicationContext.close();
    }

    public static void apiInject() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.loadBeanDefinitions("dependency-lookup-context.xml");

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(UserHolder.class);

        // setter 方式注入
        // beanDefinitionBuilder.addPropertyReference("user", "superUser");

        // constructor 方式注入
        beanDefinitionBuilder.addConstructorArgReference("superUser");

        beanFactory.registerBeanDefinition("userHolder", beanDefinitionBuilder.getBeanDefinition());

        UserHolder userHolder = beanFactory.getBean(UserHolder.class);

        System.out.println("api inject, userHolder: " + userHolder);
    }

    // @Resource
    @Autowired
    private UserHolder userHolder1;

    private UserHolder userHolder2;

    public static void fieldInject() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(BeanInjectTests.class);

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        beanDefinitionReader.loadBeanDefinitions("dependency-lookup-context.xml");
        applicationContext.refresh();

        BeanInjectTests demo = applicationContext.getBean(BeanInjectTests.class);
        UserHolder userHolder = demo.userHolder1;

        System.out.println("field inject userHolder: " + userHolder);

        applicationContext.close();
    }

    public static void methodInject() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(BeanInjectTests.class);

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        beanDefinitionReader.loadBeanDefinitions("dependency-lookup-context.xml");
        applicationContext.refresh();

        BeanInjectTests demo = applicationContext.getBean(BeanInjectTests.class);
        UserHolder userHolder = demo.userHolder2;

        System.out.println("method inject userHolder: " + userHolder);

        applicationContext.close();
    }

    private static BeanFactory beanFactory;

    private static ApplicationContext applicationContext;

    public static void awareInject() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(BeanInjectTests.class);

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(context);
        beanDefinitionReader.loadBeanDefinitions("dependency-lookup-context.xml");
        context.refresh();

        // 还有很多 aware 接口
        System.out.println("aware inject beanFactory: " + beanFactory);
        System.out.println("aware inject applicationContext: " + applicationContext);
        context.close();
    }

    // @Resource
    @Autowired
    public void inject(UserHolder userHolder) {
        this.userHolder2 = userHolder;
    }

    /**
     * 自动模式：Autowiring（自动绑定）
     * 1. byName      根据被注入属性的名称作为 Bean 名称进行依赖查找，并将对象设置到该属性
     * 2. byType      根据被注入属性的类型作为依赖类型进行查找，并将对象设置到该属性
     * 3. no          默认值，未激活 Autowiring，需要手动指定依赖注入对象
     * 4. constructor 特殊 byType 类型，用于构造器参数
     *
     */

    public static void autowireInject() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.loadBeanDefinitions("dependency-autowire-setter-injection.xml");

        // autowire 满足注入的有两个，但是 SuperUser 标注了 Primary
        UserHolder userHolder = beanFactory.getBean(UserHolder.class);

        // 有 byType 跟 byName 两种
        System.out.println("autowire setter inject, userHolder: " + userHolder);

        beanFactory = new DefaultListableBeanFactory();
        beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.loadBeanDefinitions("dependency-autowire-constructor-injection.xml");
        userHolder = beanFactory.getBean(UserHolder.class);

        System.out.println("autowire constructor inject, userHolder: " + userHolder);
    }

    // 没有指定，有多个 User，取 primary
    // 依赖查找
    // DependencyDescriptor
    //  required: 必须
    //  eager: 实时注入
    //  类型：User.class
    //  字段名称：user1
    //  primary: 是否首要
    @Autowired
    private User user1;

    // 指定 beanName 为 user 的 Bean
    @Autowired
    @Qualifier("user")
    private User user2;

    // 不包括 @Qualifier 分组的 bean
    // 不包含当前类的 Bean，参考 DefaultListableBeanFactory#findAutowireCandidates
    // 可能是同时用 xml 跟 config 类导致的
    @Autowired
    private Collection<User> users;

    @Autowired
    @Qualifier
    private Collection<User> qualifiedUsers;

    @Autowired
    @UserGroup
    private Collection<User> animalUsers;

    public static void qualifierInject() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(BeanInjectTests.class);

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        beanDefinitionReader.loadBeanDefinitions("dependency-lookup-context.xml");
        applicationContext.refresh();

        BeanInjectTests tests = applicationContext.getBean(BeanInjectTests.class);

        System.out.println("inject primary user: " + tests.user1);
        System.out.println("inject qualifier user: " + tests.user2);

        System.out.println("===== inject users =====");
        for (User user : tests.users) {
            System.out.println(user);
        }
        System.out.println("===== inject users =====");

        System.out.println("===== inject qualifier users =====");
        for (User user : tests.qualifiedUsers) {
            System.out.println(user);
        }
        System.out.println("===== inject qualifier users =====");

        System.out.println("===== inject animal users =====");
        for (User user : tests.animalUsers) {
            System.out.println(user);
        }
        System.out.println("===== inject animal users =====");

        applicationContext.close();
    }

    @Autowired
    private ObjectFactory<Set<User>> objectFactory;

    /**
     * 延迟注入
     */
    @Autowired
    private ObjectProvider<User> objectProvider;

    public static void lazyInject() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(BeanInjectTests.class);

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(applicationContext);
        beanDefinitionReader.loadBeanDefinitions("dependency-lookup-context.xml");
        applicationContext.refresh();

        BeanInjectTests tests = applicationContext.getBean(BeanInjectTests.class);

        System.out.println("lazy inject objectFactory: " + tests.objectFactory.getObject());
        System.out.println("lazy inject objectProvider: " + tests.objectProvider.getObject());

        System.out.println("===== inject lazy users =====");
        tests.objectProvider.forEach(System.out::println);
        System.out.println("===== inject lazy users =====");

        applicationContext.close();
    }

    private static void builtInInject() {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:dependency-autowire-setter-injection.xml");

        // 依赖来源一：自定义 bean
        // // 自动注入了 users objectFactory
        UserRepo userRepo = beanFactory.getBean(UserRepo.class);

        // 依赖来源二：依赖注入，内建依赖（非 Bean）
        // ClassPathXmlApplicationContext -> AbstractXmlApplicationContext -> AbstractRefreshableConfigApplicationContext
        //  -> AbstractRefreshableApplicationContext -> AbstractApplicationContext -> ConfigurableApplicationContext
        //  -> ApplicationContext -> ListableBeanFactory -> BeanFactory
        //
        // AbstractRefreshableApplicationContext 虽然 implement 了 BeanFactory，但是是通过内部又持有了一个 BeanFactory 对象来实现的
        // AbstractRefreshableApplicationContext#getBeanFactory

        // 真正的 IoC 的底层实现就是 BeanFactory 的实现类，ApplicationContext 委托 DefaultListableBeanFactory 来操作 getBean 等方法

        // 为什么 UserRepo 的 beanFactory 字段注入的是 DefaultListableBeanFactory，而不是 ClassPathXmlApplicationContext
        // 既然他们两者都实现了 BeanFactory 接口，按 type 注入，为什么不是 ClassPathXmlApplicationContext？

        // AbstractApplicationContext#prepareBeanFactory 方法中，beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
        // 明确地指定了 BeanFactory 类型的对象是 ApplicationContext#getBeanFactory() 方法的内容，而非它自身
        BeanFactory injectBeanFactory = userRepo.getBeanFactory();
        ApplicationContext context = userRepo.getObjectFactory().getObject();

        System.out.println("builtin inject BeanFactory: " + injectBeanFactory);
        System.out.println("inject ApplicationContext: " + context);
        System.out.println("beanFactory == injectBeanFactory, " + (beanFactory == injectBeanFactory));
        System.out.println("beanFactory == context, " + (beanFactory == context));

        // ApplicationContext 实现 BeanFactory 接口，同时包含一个 BeanFactory 对象
        // Not exists
        // System.out.println(beanFactory.getBean(BeanFactory.class));

        // 依赖来源三：内建 bean
        Environment environment = beanFactory.getBean(Environment.class);
        System.out.println("environment: " + environment);
    }

    @Bean
    public UserHolder userHolder(User user) {
        UserHolder userHolder = new UserHolder();
        userHolder.setUser(user);
        return userHolder;

        // constructor 方式注入
        // return new UserHolder(user);
    }

    @Bean
    public User user2() {
        return new User(2L, "🍉");
    }

    @Bean
    @Qualifier // 进行逻辑分组
    public User user3() {
        return new User(3L, "🍅");
    }

    @Bean
    @UserGroup
    public User user4() {
        return new User(4L, "🐶");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
