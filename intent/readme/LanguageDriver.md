## LanguageDriver

我们了解到MyBatis通过SqlSource描述XML文件或者Java注解中配置的SQL资源，那么SQL配置信息是如何转换为SqlSource对象的呢？
实际上，SQL配置信息到SqlSource对象的转换是由LanguageDriver组件来完成的。下面来看一下LanguageDriver接口的定义

```java
/**
 * {@linkplain org.apache.ibatis.scripting.LanguageDriver}
 */
public class Test{
}
```

LanguageDriver接口中一共有3个方法，其中createParameterHandler()方法用于创建ParameterHandler对象，
另外还有两个重载的createSqlSource()方法，这两个重载的方法用于创建SqlSource对象。

MyBatis中为LanguageDriver接口提供了两个实现类，分别为XMLLanguageDriver和RawLanguageDriver。

XMLLanguageDriver为XML语言驱动，为MyBatis提供了通过XML标签（我们常用的<if>、<where>等标签）结合OGNL表达式语法实现动态SQL的功能。

而RawLanguageDriver表示仅支持静态SQL配置，不支持动态SQL功能。

```java
/**
 * {@linkplain org.apache.ibatis.scripting.xmltags.XMLLanguageDriver}
 * {@linkplain org.apache.ibatis.scripting.defaults.RawLanguageDriver}
 */
public class Test{
}
```

重点看下XMLLanguageDriver，XMLLanguageDriver类实现了LanguageDriver接口中两个重载的createSqlSource()方法，
分别用于处理XML文件和Java注解中配置的SQL信息，将SQL配置转换为SqlSource对象。

第一个重载的createSqlSource()方法用于处理XML文件中配置的SQL信息，该方法中创建了一个XMLScriptBuilder对象，
然后调用XMLScriptBuilder对象的parseScriptNode()方法将SQL资源转换为SqlSource对象。

第二个重载的createSqlSource()方法用于处理Java注解中配置的SQL信息，该方法中首先判断SQL配置是否以<script>标签开头，
如果是，则以XML方式处理Java注解中配置的SQL信息，否则简单处理，替换SQL中的全局参数。如果SQL中仍然包含${}参数占位符，
则SQL语句仍然需要根据传递的参数动态生成，所以使用DynamicSqlSource对象描述SQL资源，否则说明SQL语句不需要根据参数动态生成，
使用RawSqlSource对象描述SQL资源。

从XMLLanguageDriver类的createSqlSource()方法的实现来看，我们除了可以通过XML配置文件结合OGNL表达式配置动态SQL外，还可以通过Java注解的方式配置，
只需要注解中的内容加上<script>标签。下面是使用Java注解配置动态SQL的案例代码：

- [UserMapper](../../mybatis-book/mybatis-chapter09/src/main/java/com/blog4java/mybatis/example/mapper/UserMapper.java)

