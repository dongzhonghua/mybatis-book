# Mapper

Mapper接口用于定义执行SQL语句相关的方法，方法名一般和Mapper XML配置文件中的<select|update|delete|insert>标签的id属性相同，
接口的完全限定名一般对应Mapper XML配置文件的命名空间。

## Mapper接口获取和调用方法测试用例

- [MybatisExample](../../mybatis-book/mybatis-chapter04/src/main/java/com/blog4java/mybatis/example/MybatisExample.java)

## Mapper接口注册过程

```java
public class Test{
    public static void test(){
        // 获取UserMapper代理对象
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        // 执行Mapper方法，获取执行结果
        List<UserEntity> userList = userMapper.listAllUser();
    }
}
```

实际上，getMapper方法返回的是一个动态代理对象。

Mapper通过MapperProxy类实现动态代理。

```java
/**
 * {@linkplain org.apache.ibatis.binding.MapperProxy}
 */
public class Test{
}
```

我们知道，Java语言中比较常用的实现动态代理的方法有两种,即JDK内置的动态代理和CGLIB动态代理。
MapperProxy使用的是JDK内置的动态代理，实现了InvocationHandler接口，invoke()方法中为通用拦截逻辑。

使用JDK内置动态代理，通过MapperProxy类实现InvocationHandler接口，定义方法执行拦截逻辑后，
还需要调用java.lang.reflect.Proxy类的newProxyInstance()方法创建代理对象。
MyBatis对这一过程做了封装，使用MapperProxyFactory创建Mapper动态代理对象。

```java
/**
 * {@linkplain org.apache.ibatis.binding.MapperProxyFactory}
 */
public class Test{
}
```

如上面的代码所示，MapperProxyFactory类的工厂方法newInstance()是非静态的。
也就是说，使用MapperProxyFactory创建Mapper动态代理对象首先需要创建MapperProxyFactory实例。
MapperProxyFactory实例是什么时候创建的呢？Configuration对象中有一个mapperRegistry属性，具体如下

```java
/**
 * {@linkplain org.apache.ibatis.session.Configuration#mapperRegistry}
 */
public class Test{
}
```

MyBatis通过mapperRegistry属性注册Mapper接口与MapperProxyFactory对象之间的对应关系。

```java
import org.apache.ibatis.session.SqlSession; /**
 * {@linkplain org.apache.ibatis.binding.MapperRegistry#getMapper( Class, SqlSession)}
 */
public class Test{
}
```

如上面的代码所示，MapperRegistry类有一个knownMappers属性，用于注册Mapper接口对应的Class对象和MapperProxyFactory对象之间的关系。
另外，MapperRegistry提供了addMapper()方法，用于向knownMappers属性中注册Mapper接口信息。
在addMapper()方法中，为每个Mapper接口对应的Class对象创建一个MapperProxyFactory对象，然后添加到knownMappers属性中。

MapperRegistry还提供了getMapper()方法，能够根据Mapper接口的Class对象获取对应的MapperProxyFactory对象，
然后就可以使用MapperProxyFactory对象创建Mapper动态代理对象了。

MyBatis框架在应用启动时会解析所有的Mapper接口，
然后调用MapperRegistry对象的addMapper()方法将Mapper接口信息和对应的MapperProxyFactory对象注册到MapperRegistry对象中。

## MappedStatement注册过程

Configuration类中有一个mappedStatements属性，该属性用于注册MyBatis中所有的MappedStatement对象。

```java
/**
 * {@linkplain org.apache.ibatis.session.Configuration#mappedStatements}
 */
public class Test{
}
```

mappedStatements属性是一个Map对象，它的Key为Mapper SQL配置的Id，
如果SQL是通过XML配置的，则Id为命名空间加上<select|update|delete|insert>标签的Id，
如果SQL通过Java注解配置，则Id为Mapper接口的完全限定名（包括包名）加上方法名称。

在前面提到过Mapper的解析过程

```java
import org.apache.ibatis.parsing.XNode; /**
 * --2.6：解析mappers {@link org.apache.ibatis.builder.xml.XMLConfigBuilder#mapperElement( XNode)}
 * ----2.6.1：根据mapper类型逐个解析，如果是package，直接加入configuration
 * ----------{@link org.apache.ibatis.builder.BaseBuilder#configuration}
 * ----2.6.2：如果是resource或url，则先打开文件，再调用
 * ----------{@link org.apache.ibatis.builder.xml.XMLMapperBuilder#parse()} 解析属性
 * ------2.6.2.1：解析SQL元素后添加Statement到Mapper
 * ----------{@link org.apache.ibatis.builder.MapperBuilderAssistant#addMappedStatement()} 
 * ----2.6.3：如果是class，则直接加入configuration
 * ----------{@link org.apache.ibatis.builder.BaseBuilder#configuration}
 */
public class Test{
}
```

重点关注2.6.2的XMLMapperBuilder#parse()方法，这个方法解析了Mapper的所有标签，我们重点关注<select|update|delete|insert>标签，
调用了方法buildStatementFromContext(context.evalNodes("select|insert|update|delete"));来解析的。

最后调用

```java
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
/**
 * {@linkplain XMLStatementBuilder#parseStatementNode()} }
 */
public class Test{
}
```

XMLStatementBuilder类的parseStatementNode()方法的内容相对较多，但是逻辑非常清晰，主要做了以下几件事情：

1. 获取<select|insert|delete|update>标签的所有属性信息。

2. 将<include>标签引用的SQL片段替换为对应的<sql>标签中定义的内容。

3. 获取lang属性指定的LanguageDriver，通过LanguageDriver创建SqlSource。MyBatis中的SqlSource表示一个SQL资源，后面章节中会对SqlSource做更详细的介绍。

4. 获取KeyGenerator对象。KeyGenerator的不同实例代表不同的主键生成策略。

5. 所有解析工作完成后，使用MapperBuilderAssistant对象的addMappedStatement()方法创建MappedStatement对象。
创建完成后，调用Configuration对象的addMappedStatement()方法将MappedStatement对象注册到Configuration对象中。

需要注意的是，MyBatis中的MapperBuilderAssistant是一个辅助工具类，用于构建Mapper相关的对象，例如Cache、ParameterMap、ResultMap等。

## Mapper方法调用过程

MyBatis中的MapperProxy实现了InvocationHandler接口，用于实现动态代理相关逻辑。
熟悉JDK动态代理机制都知道，当我们调用动态代理对象方法的时候，会执行MapperProxy类的invoke()方法。

```java
import java.lang.reflect.Method;
/**
 * {@linkplain org.apache.ibatis.binding.MapperProxy#invoke( Object, Method, Object[])}
 */
public class Test{
}
```

如上面的代码所示，在MapperProxy类的invoke()方法中，对从Object类继承的方法不做任何处理，
对Mapper接口中定义的方法，调用cachedMapperMethod()方法获取一个MapperMethod对象。

```java
import java.lang.reflect.Method;
/**
 * {@linkplain org.apache.ibatis.binding.MapperProxy#cachedMapperMethod( Method)} 
 */
public class Test{
}
```

如上面的代码所示，cachedMapperMethod()方法中对MapperMethod对象做了缓存，首先从缓存中获取，
如果获取不到，则创建MapperMethod对象，然后添加到缓存中，这是享元思想的应用，避免频繁创建和回收对象。

```java
import org.apache.ibatis.session.Configuration;
import java.lang.reflect.Method; 
/**
 * {@linkplain org.apache.ibatis.binding.MapperMethod#MapperMethod( Class, Method, Configuration)} 
 */
public class Test{
}
```

在MapperMethod构造方法中创建了一个SqlCommand对象和一个MethodSignature对象：
SqlCommand对象用于获取SQL语句的类型、Mapper的Id等信息；
MethodSignature对象用于获取方法的签名信息，例如Mapper方法的参数名、参数注解等信息。
创建完MapperMethod后返回，最后调用MapperMethod的execute()方法

```java
import org.apache.ibatis.session.SqlSession; /**
 * {@linkplain org.apache.ibatis.binding.MapperMethod#execute( SqlSession, Object[])}  
 */
public class Test{
}
```

如上面的代码所示，在execute()方法中，首先根据SqlCommand对象获取SQL语句的类型，然后根据SQL语句的类型调用SqlSession对象对应的方法。
例如，当SQL语句类型为INSERT时，通过SqlCommand对象获取Mapper的Id，然后调用SqlSession对象的insert()方法。
MyBatis通过动态代理将Mapper方法的调用转换成通过SqlSession提供的API方法完成数据库的增删改查操作，即旧的iBatis框架调用Mapper的方式。

## SqlSession执行Mapper过程

