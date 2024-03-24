自动生成 mapper 有以下方式

1. 命令行
```
java -jar mybatis-generator-core-x.x.x.jar -configfile generatorConfig.xml
```

2. 通过插件 mybatis-generator-maven-plugin
```
mvn mybatis-generator:generate
```

3. 代码执行