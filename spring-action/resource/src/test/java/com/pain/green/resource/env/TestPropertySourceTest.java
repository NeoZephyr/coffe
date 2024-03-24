package com.pain.green.resource.env;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestPropertySourceTest.class)
@TestPropertySource(
        properties = "user.name = 王世充",
        locations = "classpath:user.properties"
)
public class TestPropertySourceTest {

    @Value("${user.name}")
    String userName;

    @Autowired
    private ConfigurableEnvironment environment;

    @Test
    public void test() {
        assertEquals("王世充", userName);

        MutablePropertySources propertySources = environment.getPropertySources();

        for (PropertySource propertySource : propertySources) {
            System.out.printf("name: %s, value: %s\n", propertySource.getName(), propertySource.getProperty("user.name"));
        }
    }
}
