## 一级缓存

```java
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds; 
/**
 * {@linkplain org.apache.ibatis.executor.BaseExecutor#query( MappedStatement, Object, RowBounds, ResultHandler, CacheKey, BoundSql)} }
 */
public class Test{
}
```

MyBatis的缓存分为一级缓存和二级缓存，一级缓存默认是开启的，而且不能关闭。至于一级缓存为什么不能关闭，MyBatis核心开发人员做出了解释：
MyBatis的一些关键特性（例如通过<association>和<collection>建立级联映射、避免循环引用（circular references）、加速重复嵌套查询等）都是基于MyBatis一级缓存实现的，
而且MyBatis结果集映射相关代码重度依赖CacheKey，所以目前MyBatis不支持关闭一级缓存。

MyBatis提供了一个配置参数localCacheScope，用于控制一级缓存的级别，该参数的取值为SESSION、STATEMENT，
当指定localCacheScope参数值为SESSION时，缓存对整个SqlSession有效，只有执行DML语句（更新语句）时，缓存才会被清除。
当localCacheScope值为STATEMENT时，缓存仅对当前执行的语句有效，当语句执行完毕后，缓存就会被清空。

## 二级缓存

设置方法，在主配置中的settings标签内设置cacheEnable = true

- [mybatis-config.xml](../../mybatis-book/mybatis-chapter07/src/main/resources/mybatis-config.xml)

然后在Mapper文件中配置缓存策略、刷新频率、缓存的容量等等

- [UserMapper](../../mybatis-book/mybatis-chapter07/src/main/resources/com/blog4java/mybatis/example/mapper/UserMapper.xml)

### 源码解析

- BlockingCache：阻塞版本的缓存装饰器，能够保证同一时间只有一个线程到缓存中查找指定的Key对应的数据。

- FifoCache：先入先出缓存装饰器，FifoCache内部有一个维护具有长度限制的Key键值链表（LinkedList实例）和一个被装饰的缓存对象，
Key值链表主要是维护Key的FIFO顺序，而缓存存储和获取则交给被装饰的缓存对象来完成。

- LoggingCache：为缓存增加日志输出功能，记录缓存的请求次数和命中次数，通过日志输出缓存命中率。

- LruCache：最近最少使用的缓存装饰器，当缓存容量满了之后，使用LRU算法淘汰最近最少使用的Key和Value。
LruCache中通过重写LinkedHashMap类的removeEldestEntry()方法获取最近最少使用的Key值，
将Key值保存在LruCache类的eldestKey属性中，然后在缓存中添加对象时，淘汰eldestKey对应的Value值。
具体实现细节读者可参考LruCache类的源码。

- ScheduledCache：自动刷新缓存装饰器，当操作缓存对象时，如果当前时间与上次清空缓存的时间间隔大于指定的时间间隔，
则清空缓存。清空缓存的动作由getObject()、putObject()、removeObject()等方法触发。

- SerializedCache：序列化缓存装饰器，向缓存中添加对象时，对添加的对象进行序列化处理，从缓存中取出对象时，进行反序列化处理。

- SoftCache：软引用缓存装饰器，SoftCache内部维护了一个缓存对象的强引用队列和软引用队列，缓存以软引用的方式添加到缓存中，
并将软引用添加到队列中，获取缓存对象时，如果对象已经被回收，则移除Key，如果未被回收，则将对象添加到强引用队列中，
避免被回收，如果强引用队列已经满了，则移除最早入队列的对象的引用。

- SynchronizedCache：线程安全缓存装饰器，SynchronizedCache的实现比较简单，为了保证线程安全，
对操作缓存的方法使用synchronized关键字修饰。

- TransactionalCache：事务缓存装饰器，该缓存与其他缓存的不同之处在于，TransactionalCache增加了两个方法，
即commit()和rollback()。当写入缓存时，只有调用commit()方法后，缓存对象才会真正添加到TransactionalCache对象中，
如果调用了rollback()方法，写入操作将被回滚。

- WeakCache：弱引用缓存装饰器，功能和SoftCache类似，只是使用不同的引用类型。