package com.pain.apple.lab;

import com.pain.apple.lab.config.FooConfig;
import com.pain.apple.lab.config.JdbcConfig;
import com.pain.apple.lab.config.ReadBodyFilter;
import com.pain.apple.lab.domain.Student;
import com.pain.apple.lab.service.UserService;
import com.pain.apple.lab.service.WorkerService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;
import java.util.List;

@Import({JdbcConfig.class})
@PropertySource("classpath:jdbc.yml")
@MapperScan("com.pain.apple.lab.mapper")
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ServletComponentScan
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Value("${phone.color}")
    String color;

    @Autowired
    FooConfig fooConfig;

    @Bean
    public String serviceName() {
        return "hello";
    }

    @Bean
    public String appName() {
        return "hello";
    }

    @Bean
    public Student student() {
        return new Student("jack");
    }

    @Bean
    public Student senior() {
        return new Student("pain");
    }

    @Bean
    public Student junior() {
        return new Student("hoop");
    }

    @Bean
    public List<Student> students() {
        Student a = new Student("a");
        Student b = new Student("b");
        return Arrays.asList(a, b);
    }

    @Autowired
    UserService userService;

    @Autowired
    WorkerService workerService;

    @Override
    public void run(String... args) throws Exception {
        // workerService.save("jack");
        workerService.testSave();
    }

    @Bean
    public FilterRegistrationBean<ReadBodyFilter> filterFilterRegistrationBean(){
        FilterRegistrationBean<ReadBodyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ReadBodyFilter());
        registrationBean.addUrlPatterns("/user");
        return registrationBean;
    }
}
