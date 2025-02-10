package com.pain.green;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPart;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WebServerApplicationContextDemo {

    public static void main(String[] args) throws Exception {
        // argumentResolver();
        // returnHandlerValue();
        inspectArgumentResolver();
    }

    private static void overview() throws Exception {
        AnnotationConfigServletWebServerApplicationContext context =
                new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);

        // 解析 @RequestMapping 及其派生注解，生成路径与控制器方法的映射关系，在初始化时就生成
        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);

        // key 为路径信息，value 为控制器方法
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();

        System.out.println(">>> 控制器方法");

        handlerMethods.forEach((k, v) -> {
            System.out.println(k + " = " + v);
        });

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/gun");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter("name", "汉密尔顿");
        HandlerExecutionChain chain = handlerMapping.getHandler(request);

        System.out.println(chain);

        RequestMappingHandlerAdapter handlerAdapter = context.getBean(RequestMappingHandlerAdapter.class);

        Method method = RequestMappingHandlerAdapter.class.getDeclaredMethod(
                "invokeHandlerMethod",
                HttpServletRequest.class, HttpServletResponse.class, HandlerMethod.class);
        method.setAccessible(true);
        HandlerMethod handlerMethod = (HandlerMethod) chain.getHandler();
        method.invoke(handlerAdapter, request, response, handlerMethod);

        // 参数解析器
        List<HandlerMethodArgumentResolver> argumentResolvers = handlerAdapter.getArgumentResolvers();

        System.out.println(">>> 参数解析器");

        for (HandlerMethodArgumentResolver resolver : argumentResolvers) {
            System.out.println(resolver);
        }

        System.out.println(">>> 返回值解析器");

        List<HandlerMethodReturnValueHandler> returnValueHandlers = handlerAdapter.getReturnValueHandlers();

        for (HandlerMethodReturnValueHandler handler : returnValueHandlers) {
            System.out.println(handler);
        }
    }

    private static void argumentResolver() throws Exception {
        AnnotationConfigServletWebServerApplicationContext context =
                new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/hat");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("token", "token 123456");
        HandlerExecutionChain chain = handlerMapping.getHandler(request);
        RequestMappingHandlerAdapter handlerAdapter = context.getBean(RequestMappingHandlerAdapter.class);

        Method method = RequestMappingHandlerAdapter.class.getDeclaredMethod(
                "invokeHandlerMethod",
                HttpServletRequest.class, HttpServletResponse.class, HandlerMethod.class);
        method.setAccessible(true);
        HandlerMethod handlerMethod = (HandlerMethod) chain.getHandler();

        // 调用 handlerAdapter 的 invokeHandlerMethod 方法
        method.invoke(handlerAdapter, request, response, handlerMethod);
    }

    private static void returnHandlerValue() throws Exception {
        AnnotationConfigServletWebServerApplicationContext context =
                new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/kim");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecutionChain chain = handlerMapping.getHandler(request);
        RequestMappingHandlerAdapter handlerAdapter = context.getBean(RequestMappingHandlerAdapter.class);

        Method method = RequestMappingHandlerAdapter.class.getDeclaredMethod(
                "invokeHandlerMethod",
                HttpServletRequest.class, HttpServletResponse.class, HandlerMethod.class);
        method.setAccessible(true);
        HandlerMethod handlerMethod = (HandlerMethod) chain.getHandler();

        method.invoke(handlerAdapter, request, response, handlerMethod);
        byte[] content = response.getContentAsByteArray();
        System.out.println(new String(content, StandardCharsets.UTF_8));
    }

    private static void inspectArgumentResolver() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();

        HttpServletRequest request = mockRequest();
        HandlerMethod handlerMethod = new HandlerMethod(
                new TestController(),
                TestController.class.getMethod("test", String.class, int.class, String.class, MultipartFile.class, int.class, String.class, String.class, String.class, HttpServletRequest.class, User.class, User.class, User.class));

        // 请求参数与 java 数据对象绑定、类型转换
        // DefaultDataBinderFactory dataBinderFactory = new DefaultDataBinderFactory(null);
        ServletRequestDataBinderFactory dataBinderFactory = new ServletRequestDataBinderFactory(null, null);

        ModelAndViewContainer container = new ModelAndViewContainer();

        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();

        for (MethodParameter methodParameter : methodParameters) {
            // useDefaultResolution false 表示必须有 @RequestParam 注解
            // beanFactory 可以读取变量
            RequestParamMethodArgumentResolver resolver = new RequestParamMethodArgumentResolver(beanFactory, false);

            // 组合设计模式，组合多个解析器
            HandlerMethodArgumentResolverComposite composite = new HandlerMethodArgumentResolverComposite();
            composite.addResolvers(
                    resolver,
                    new PathVariableMethodArgumentResolver(),
                    new RequestHeaderMethodArgumentResolver(beanFactory),
                    new ServletCookieValueMethodArgumentResolver(beanFactory),
                    new ExpressionValueMethodArgumentResolver(beanFactory),
                    new ServletRequestMethodArgumentResolver(),
                    new ServletModelAttributeMethodProcessor(false), // false 表示需要 @ModelAttribute 注解
                    new RequestResponseBodyMethodProcessor(Collections.singletonList(new MappingJackson2HttpMessageConverter())), // 顺序重要，避免使用了没有 @ModelAttribute 注解参数对应的对象
                    new ServletModelAttributeMethodProcessor(true)
            );

            String annotation = Arrays.stream(methodParameter.getParameterAnnotations())
                    .map(anno -> anno.annotationType().getSimpleName())
                    .collect(Collectors.joining());

            // javac -parameters 编译之后，会在字节码中保留参数名 反射可以获取到参数名
            // javac -g 编译之后，会在字节码中保留参数名 反射可以获取到参数名，但是 asm 技术可以
            // 参数名解析
            methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());

            if (composite.supportsParameter(methodParameter)) {
                Object v = composite.resolveArgument(methodParameter, container, new ServletWebRequest(request), dataBinderFactory);
                System.out.printf("[%d] %s %s %s -> %s\n",
                        methodParameter.getParameterIndex(),
                        annotation.isEmpty() ? "@_" : "@" + annotation,
                        methodParameter.getParameterType().getSimpleName(),
                        methodParameter.getParameterName(), v);
            } else {
                System.out.printf("[%d] %s %s %s\n",
                        methodParameter.getParameterIndex(),
                        annotation.isEmpty() ? "@_" : "@" + annotation,
                        methodParameter.getParameterType().getSimpleName(),
                        methodParameter.getParameterName());
            }
        }

        System.out.println("模型数据：" + container.getModel());
    }

    private static HttpServletRequest mockRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("name", "宝宝巴士");
        request.setParameter("age", "33");
        request.addPart(new MockPart("file", "a.txt", "点击去看了一下这个问题下的高赞回答".getBytes(StandardCharsets.UTF_8)));

        // 放入 request 作用域
        Map<String, String> uriTemplateVariables = new AntPathMatcher().extractUriTemplateVariables("/test/{id}", "/test/123");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVariables);
        request.setContentType("application/json");
        request.setCookies(new Cookie("token", "@xyz"));
        request.setContent("{\"name\":\"贝乐虎\",\"age\":32}".getBytes(StandardCharsets.UTF_8));
        return new StandardServletMultipartResolver().resolveMultipart(request);
    }


    @Configuration
    @ComponentScan // 组件扫描，默认扫描配置类所在的包及子包
    // @PropertySource("classpath:application.yaml")
    @PropertySource(value = "classpath:application.properties")

    // properties bean 中的属性绑定配置文件中的键值
    // 以 spring.mvc, server 开头的键
    @EnableConfigurationProperties({WebMvcProperties.class, ServerProperties.class})
    static class WebConfig {

        @Bean
        public TomcatServletWebServerFactory tomcatServletWebServerFactory(ServerProperties serverProperties) {
            int port = serverProperties.getPort();

            // 内嵌 web 容器工厂
            return new TomcatServletWebServerFactory(port);
        }

        @Bean
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }

        @Bean
        public DispatcherServletRegistrationBean dispatcherServletRegistrationBean(DispatcherServlet dispatcherServlet, WebMvcProperties properties) {
            DispatcherServletRegistrationBean registrationBean = new DispatcherServletRegistrationBean(dispatcherServlet, "/");

            // Servlet 默认的初始化是第一次访问的时候，从 Servlet 的 init 方法开始，到 onRefresh 方法
            // onRefresh 调用 initStrategies，然后分别调用

            // initMultipartResolver 初始化文件上传解析器
            // initLocaleResolver 本地化解析器
            // initThemeResolver
            // initHandlerMappings 路径映射
            // initHandlerAdapters 适配器，适配不同形式的控制器方法
            // initHandlerExceptionResolvers 异常解析
            // initRequestToViewNameTranslator
            // initViewResolvers
            // initFlashMapManager

            // 这些组件在 DispatcherServlet.properties 文件里面有默认配置
            // 以 initHandlerMappings 为列，先会尝试从父子容器中获取 handlerMapping，如果都没有，就使用配置文件中的默认配置

            // 可以配置 load-on-startup 参数，让 Servlet 一开始就初始化好
            int loadOnStartup = properties.getServlet().getLoadOnStartup();
            registrationBean.setLoadOnStartup(loadOnStartup);
            return registrationBean;
        }

        @Bean
        public RequestMappingHandlerMapping requestMappingHandlerMapping() {
            return new RequestMappingHandlerMapping();
        }

        @Bean
        public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
            RequestMappingHandlerAdapter handlerAdapter = new RequestMappingHandlerAdapter();
            TokenArgumentResolver argumentResolver = new TokenArgumentResolver();
            TextReturnValueHandler returnValueHandler = new TextReturnValueHandler();
            handlerAdapter.setCustomArgumentResolvers(Collections.singletonList(argumentResolver));
            handlerAdapter.setCustomReturnValueHandlers(Collections.singletonList(returnValueHandler));
            return handlerAdapter;
        }
    }

    @Controller
    static class TestController {

        @GetMapping("/foo")
        public ModelAndView foo() {
            System.out.println(">>> foo 方法调用");
            return null;
        }

        @GetMapping("/gun")
        public ModelAndView gun(@RequestParam("name") String name) {
            System.out.println(">>> gun 方法调用，name = " + name);
            return null;
        }

        @PostMapping("/bar")
        public ModelAndView bar() {
            System.out.println(">>> bar 方法调用");
            return null;
        }

        @GetMapping("/hat")
        public ModelAndView hat(@Token String token) {
            System.out.println(">>> hat 方法调用，token = " + token);
            return null;
        }

        @Text
        @GetMapping("/kim")
        public User kim() {
            System.out.println(">>> kim 方法调用");
            return new User("kim", 30);
        }

        public void test(@RequestParam("name") String name,
                         @RequestParam("age") int age,
                         @RequestParam(name = "home", defaultValue = "${USER}") String username1, // spring 获取数据
                         @RequestParam("file") MultipartFile file,
                         @PathVariable("id") int id,
                         @RequestHeader("Content-Type") String contentType,
                         @CookieValue("token") String token,
                         @Value("${USER}") String username2,
                         HttpServletRequest request,
                         @ModelAttribute("abc") User user1,
                         User user2,
                         @RequestBody User user3) {}
    }

    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Token {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Text {}

    static class TokenArgumentResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            Token token = parameter.getParameterAnnotation(Token.class);
            return token != null;
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            return webRequest.getHeader("token");
        }
    }

    static class TextReturnValueHandler implements HandlerMethodReturnValueHandler {
        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            Text annotation = returnType.getMethodAnnotation(Text.class);
            return annotation != null;
        }

        @Override
        public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
            String text = returnValue.toString();
            HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
            response.setContentType("text/plain;charset=utf-8");
            response.getWriter().print("TEXT!!" + text);

            // 设置请求已经处理完毕
            mavContainer.setRequestHandled(true);
        }
    }

    static class User {
        public String name;
        public int age;

        public User() {}

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
