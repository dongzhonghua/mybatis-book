# MyBatis中的设计模式

## 创建型

### 工厂方法
```java
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;import java.sql.Connection;
/**
 * {@linkplain DefaultSqlSessionFactory#openSession()} 
 * {@linkplain org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory#newTransaction( Connection)}  
 * {@linkplain org.apache.ibatis.transaction.managed.ManagedTransactionFactory#newTransaction( Connection)}  
 */
public class Test{
}
```
![SqlSessionFactory](./static/FactoryMethod1.png)
![TransactionFactory](./static/FactoryMethod2.png)

### Builder

```java
import org.apache.ibatis.session.Configuration;
/**
 * {@linkplain org.apache.ibatis.session.SqlSessionFactoryBuilder#build( Configuration)}
 */
public class Test{
}
```

## 

### 装饰器

```java
import org.apache.ibatis.executor.Executor;
/**
 * {@linkplain org.apache.ibatis.executor.CachingExecutor#CachingExecutor( Executor)}
 */
public class Test{
}
```