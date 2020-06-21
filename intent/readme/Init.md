# MyBatis创建SqlSession的过程

## XPath

JDK提供了3种解析XML的方式，DOM、SAX和XPath。
其中API最易用的是XPath，MyBatis种也是使用的XPath来解析XML文件中的配置信息。

- [XPath的解析测试用例](../../mybatis-book/mybatis-chapter05/src/main/java/com/blog4java/mybatis/xpath)

## Configuration实例创建过程

Configuration作用主要有三个

1. 用于描述MyBatis配置信息，例如<settings>标签配置的参数信息

2. 作为容器注册MyBatis其他组件，例如TypeHandler、MappedStatement等

3. 提供工厂犯法，创建ResultSetHandler、StatementHandler、Executor、ParamenterHandler等组件实例

MyBatis通过XMLConfigBuilder类完成Configuration对象的创建工作。

- [创建Configuration测试用例](../../mybatis-book/mybatis-chapter05/src/main/java/com/blog4java/mybatis/configuration/ConfigurationExample.java)

```java
/**
 * 初始化步骤
 * 1：构造会话工厂，抽象工厂设计模式
 * {@link SqlSessionFactoryBuilder#build(InputStream, String, Properties)}
 * 2：解析config {@link org.apache.ibatis.builder.xml.XMLConfigBuilder#parseConfiguration(XNode)}
 * --2.1：解析属性 {@link org.apache.ibatis.builder.xml.XMLConfigBuilder#propertiesElement(XNode)}
 * --2.2：解析设置 {@link org.apache.ibatis.builder.xml.XMLConfigBuilder#settingsAsProperties(XNode)}
 * --2.3：解析别名 {@link org.apache.ibatis.builder.xml.XMLConfigBuilder#typeAliasesElement(XNode)}
 * --2.4：解析插件 {@link org.apache.ibatis.builder.xml.XMLConfigBuilder#pluginElement(XNode)}
 * --2.5：解析typeHandler {@link org.apache.ibatis.builder.xml.XMLConfigBuilder#typeHandlerElement(XNode)}
 * --2.6：解析mappers {@link org.apache.ibatis.builder.xml.XMLConfigBuilder#mapperElement(XNode)}
 * ----2.6.1：根据mapper类型逐个解析，如果是package，直接加入configuration
 * ----------{@link org.apache.ibatis.builder.BaseBuilder#configuration}
 * ----2.6.2：如果是resource或url，则先打开文件，再调用
 * ----------{@link XMLMapperBuilder#parse()} 解析属性
 * ------2.6.2.1：解析SQL元素后添加Statement到Mapper
 * ----------{@link org.apache.ibatis.builder.MapperBuilderAssistant#addMappedStatement(String, SqlSource, StatementType, SqlCommandType, Integer, Integer, String, Class, String, Class, ResultSetType, boolean, boolean, boolean, KeyGenerator, String, String, String, LanguageDriver, String)}
 * ----2.6.3：如果是class，则直接加入configuration
 * ----------{@link org.apache.ibatis.builder.BaseBuilder#configuration}
 * 3：返回SqlSessionFactory
 * {@link SqlSessionFactoryBuilder#build(Configuration)}
 */
public class Test{
}
```

## SqlSession的创建过程

从上面的初始化步骤可以看出，MyBatis中的SqlSession实例使用工厂模式创建，
所以在创建SqlSession实例之前需要先创建SqlSessionFactory工厂对象，然后调用SqlSessionFactory对象的openSession()方法，
SqlSessionFactory只有一个实现类DefaultSqlSessionFactory，DefaultSqlSessionFactory的openSession()方法实际上是openSessionFromDataSource方法，
然后根据配置的事务管理器类型创建对应的事务管理器工厂，MyBatis提供两种事务管理器，分别是JdbcTransaction和ManagedTransaction，
创建完成后调用Configuration对象的newExecutor()方法，根据配置文件中指定的Executor类型创建Executor对象，
最后以Configuration和Executor为参数创建DefaultSqlSession实例。
