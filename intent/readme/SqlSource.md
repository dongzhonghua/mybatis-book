## SqlSource

MyBatis中的SqlSource用于描述SQL资源，通过前面章节的介绍，我们知道MyBatis可以通过两种方式配置SQL信息，
一种是通过@Selelect、@Insert、@Delete、@Update或者@SelectProvider、@InsertProvider、@DeleteProvider、
@UpdateProvider等注解；

另一种是通过XML配置文件。SqlSource就代表Java注解或者XML文件配置的SQL资源。下面是SqlSource接口的定义：

```java
/**
 * {@linkplain org.apache.ibatis.mapping.SqlSource}
 */
public class Test{
}
```

SqlSource接口的定义非常简单，只有一个getBoundSql()方法，该方法返回一个BoundSql实例。
BoundSql是对SQL语句及参数信息的封装，它是SqlSource解析后的结果。

SqlSource的4个实现类：

```java
/**
 * {@linkplain org.apache.ibatis.builder.annotation.ProviderSqlSource}
 * {@linkplain org.apache.ibatis.builder.StaticSqlSource}
 * {@linkplain org.apache.ibatis.scripting.defaults.RawSqlSource}
 * {@linkplain org.apache.ibatis.scripting.xmltags.DynamicSqlSource}
 */
public class Test{
}
```

这4种SqlSource实现类的作用如下。

ProviderSqlSource：用于描述通过@Select、@SelectProvider等注解配置的SQL资源信息。

DynamicSqlSource：用于描述Mapper XML文件中配置的SQL资源信息，这些SQL通常包含动态SQL配置或者${}参数占位符，需要在Mapper调用时才能确定具体的SQL语句。

RawSqlSource：用于描述Mapper XML文件中配置的SQL资源信息，与DynamicSqlSource不同的是，这些SQL语句在解析XML配置的时候就能确定，即不包含动态SQL相关配置。

StaticSqlSource：用于描述ProviderSqlSource、DynamicSqlSource及RawSqlSource解析后得到的静态SQL资源。

无论是Java注解还是XML文件配置的SQL信息，在Mapper调用时都会根据用户传入的参数将Mapper配置转换为StaticSqlSource类。我们不妨了解一下StaticSqlSource类的实现,
StaticSqlSource类的内容比较简单，只封装了Mapper解析后的SQL内容和Mapper参数映射信息。
我们知道Executor组件与数据库交互，除了需要参数映射信息外，还需要参数信息。
因此，Executor组件并不是直接通过StaticSqlSource对象完成数据库操作的，而是与BoundSql交互。
BoundSql是对Executor组件执行SQL信息的封装，具体实现代码如下：

```java
/**
 * {@linkplain org.apache.ibatis.mapping.BoundSql}
 */
public class Test{
}
```

BoundSql除了封装了Mapper解析后的SQL语句和参数映射信息外，还封装了Mapper调用时传入的参数对象。
另外，MyBatis任意一个Mapper都有两个内置的参数，即_parameter和_databaseId。_parameter代表整个参数，
包括<bind>标签绑定的参数信息，这些参数存放在BoundSql对象的additionalParameters属性中。
_databaseId为Mapper配置中通过databaseId属性指定的数据库类型。