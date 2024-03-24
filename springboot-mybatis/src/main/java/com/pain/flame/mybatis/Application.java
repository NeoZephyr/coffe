package com.pain.flame.mybatis;

import com.pain.flame.mybatis.entity.Product;
import com.pain.flame.mybatis.mapper.ProductMapper;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@MapperScan("com.pain.flame.mybatis.mapper")
@SpringBootApplication
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private ProductMapper productMapper;

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Override
    public void run(String... args) throws Exception {
        testCURD();
//        generate();
    }

    private void generate() throws IOException, XMLParserException, SQLException, InterruptedException, InvalidConfigurationException {
        List<String> warnings = new ArrayList<>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(
                this.getClass().getResourceAsStream("/generatorConfig.xml"));
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }

    private static final Random random = new Random();

    private void testCURD() {
        Product product = createProduct();
        int count = productMapper.insertProduct(product);

        logger.info("insert product, count: {}, product: {}", count, product);

        product = createProduct();
        count = productMapper.insertProduct(product);

        logger.info("insert product, count: {}, product: {}", count, product);

        product = productMapper.findProductById(product.getId());

        logger.info("find product: {}", product);
    }

    private static Product createProduct() {
        Product product = new Product();
        product.setName("phone" + random.nextInt(1000));
        product.setPrice(Money.of(CurrencyUnit.of("CNY"), random.nextInt(1000)));

        return product;
    }
}
