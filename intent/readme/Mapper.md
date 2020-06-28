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
import org.apache.ibatis.session.SqlSession;
/**
 * {@linkplain org.apache.ibatis.binding.MapperMethod#execute( SqlSession, Object[])}  
 */
public class Test{
}
```

如上面的代码所示，在execute()方法中，首先根据SqlCommand对象获取SQL语句的类型，然后根据SQL语句的类型调用SqlSession对象对应的方法。
例如，当SQL语句类型为INSERT时，通过SqlCommand对象获取Mapper的Id，然后调用SqlSession对象的insert()方法。
MyBatis通过动态代理将Mapper方法的调用转换成通过SqlSession提供的API方法完成数据库的增删改查操作，即旧的iBatis框架调用Mapper的方式。

## SqlSession执行Mapper过程

首先通过DefaultSqlSession的getMapper获取到存储在configuration里面mapperRegistry的Mapper,这个Mapper实际上是一个代理出来的对象，
即MapperProxy，执行用户自定义的Mapper的所有方法都将会调用MapperProxy的invoke方法。

```java
import java.lang.reflect.Method;
/**
 * {@linkplain org.apache.ibatis.binding.MapperProxy#invoke( Object, Method, Object[])}  
 */
public class Test{
}
```

在MapperProxy的invoke方法中会先从缓存中取到MapperMethod，如果取不到就生成一个放入缓存，最后执行MapperMethod的execute方法。

```java
import org.apache.ibatis.session.SqlSession;
/**
 * {@linkplain org.apache.ibatis.binding.MapperMethod#execute( SqlSession, Object[])}   
 */
public class Test{
}
```

MapperMethod的execute判断了当前SQL语句的类型，根据SQL语句不同类型执行不通的操作。
比如SELECT语句返回多条数据就执行executeForMany函数。

```java
import org.apache.ibatis.session.SqlSession;
/**
 * {@linkplain org.apache.ibatis.binding.MapperMethod#executeForMany( SqlSession, Object[])}   
 */
public class Test{
}
```
在executeForMany函数里面调用了SqlSession的selectList方法查询数据。
而SqlSession的实现为DefaultSqlSession，在DefaultSqlSession的selectList()方法中，
首先根据Mapper的Id从Configuration对象中获取对应的MappedStatement对象，
然后以MappedStatement对象作为参数，调用Executor实例的query()方法完成查询操作。

```java
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
/**
 * {@linkplain org.apache.ibatis.session.defaults.DefaultSqlSession#selectList( String, Object, RowBounds)}    
 */
public class Test{
}
```

然后是装饰者模式包装了原来Executor的CachingExecutor，即二级缓存。

```java
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds; 
/**
 * {@linkplain org.apache.ibatis.executor.CachingExecutor#query( MappedStatement, Object, RowBounds, ResultHandler, CacheKey, BoundSql)}     
 */
public class Test{
}
```

在二级缓存类里面先判断了当前是否有缓存，没有就调用装饰了的Executor执行query方法，有二级缓存的的话判断是否需要刷新二级缓存，
从MappedStatement对象对应的二级缓存中获取数据，如果缓存数据不存在，则从数据库中查询数据，如果缓存存在则获取缓存返回数据。

下面是BaseExecutor类对query()方法的实现：

```java
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds; 
/**
 * {@linkplain org.apache.ibatis.executor.BaseExecutor#query( MappedStatement, Object, RowBounds, ResultHandler, CacheKey, BoundSql)}     
 */
public class Test{
}
```

在重载的query()方法中，首先从MyBatis一级缓存中获取查询结果，如果缓存中没有，则调用BaseExecutor类的queryFromDatabase()方法从数据库中查询。

```java
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds; 
/**
 * {@linkplain org.apache.ibatis.executor.BaseExecutor#queryFromDatabase( MappedStatement, Object, RowBounds, ResultHandler, CacheKey, BoundSql)}      
 */
public class Test{
}
```

如上面的代码所示，在queryFromDatabase()方法中，调用doQuery()方法进行查询，然后将查询结果进行缓存，
doQuery()是一个模板方法，由BaseExecutor子类实现。
在学习MyBatis核心组件时，我们了解到Executor有几个不同的实现，分别为BatchExecutor、SimpleExecutor和ReuseExecutor。
接下来我们了解一下SimpleExecutor对doQuery()方法的实现，代码如下：

```java
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds; 
/**
 * {@linkplain org.apache.ibatis.executor.SimpleExecutor#doQuery( MappedStatement, Object, RowBounds, ResultHandler, BoundSql)}       
 */
public class Test{
}
```

如上面的代码所示，在SimpleExecutor类的doQuery()方法中，首先调用Configuration对象的newStatementHandler()方法创建StatementHandler对象。
newStatementHandler()方法返回的是RoutingStatementHandler的实例。
在RoutingStatementHandler类中，会根据配置Mapper时statementType属性指定的StatementHandler类型创建对应的StatementHandler实例进行处理，
例如statementType属性值为SIMPLE时，则创建SimpleStatementHandler实例。

StatementHandler对象创建完毕后，接着调用SimpleExecutor类的prepareStatement()方法创建JDBC中的Statement对象，
然后为Statement对象设置参数操作。Statement对象初始化工作完成后，再调用StatementHandler的query()方法执行查询操作。
我们先来看一下SimpleExecutor类中prepareStatement()方法的具体内容，代码如下：

```java
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds; 
/**
 * {@linkplain org.apache.ibatis.executor.SimpleExecutor#prepareStatement( StatementHandler, Log)}        
 */
public class Test{
}
```

在SimpleExecutor类的prepareStatement()方法中，首先获取JDBC中的Connection对象，然后调用StatementHandler对象的prepare()方法创建Statement对象，
接着调用StatementHandler对象的parameterize()方法（parameterize()方法中会使用ParameterHandler为Statement对象设置参数）。
具体逻辑读者可以参考MyBatis对应的源代码。

MyBatis的StatementHandler接口有几个不同的实现类，分别为SimpleStatementHandler、PreparedStatementHandler和CallableStatementHandler。
MyBatis默认情况下会使用PreparedStatementHandler与数据库交互。接下来我们了解一下PreparedStatementHandler的query()方法的实现，代码如下：

```java
import org.apache.ibatis.session.ResultHandler;
import java.sql.Statement; 
/**
 * {@linkplain org.apache.ibatis.executor.statement.PreparedStatementHandler#query( Statement, ResultHandler)}         
 */
public class Test{
}
```

如上面的代码所示，在PreparedStatementHandler的query()方法中，首先调用PreparedStatement对象的execute()方法执行SQL语句，
然后调用ResultSetHandler的handleResultSets()方法处理结果集。
ResultSetHandler只有一个默认的实现，即DefaultResultSetHandler类，DefaultResultSetHandler处理结果集的逻辑在第4章介绍MyBatis核心组件时已经介绍过了。
这里我们简单回顾一下，下面是DefaultResultSetHandler类handleResultSets()方法的关键代码:

```java
import java.sql.Statement;
/**
 * {@linkplain org.apache.ibatis.executor.resultset.DefaultResultSetHandler#handleResultSets( Statement)}          
 */
public class Test{
}
```

如上面的代码所示，DefaultResultSetHandler类的handleResultSets()方法具体逻辑如下：

（1）首先从Statement对象中获取ResultSet对象，然后将ResultSet包装为ResultSetWrapper对象，
通过ResultSetWrapper对象能够更方便地获取数据库字段名称以及字段对应的TypeHandler信息。

（2）获取Mapper SQL配置中通过resultMap属性指定的ResultMap信息，一条SQL Mapper配置一般只对应一个ResultMap。

（3）调用handleResultSet()方法对ResultSetWrapper对象进行处理，将结果集转换为Java实体对象，然后将生成的实体对象存放在multipleResults列表中。

（4）调用collapseSingleResultList()方法对multipleResults进行处理，如果只有一个结果集，就返回结果集中的元素，否则返回多个结果集。
具体细节，读者可参考该方法的源码。