# 日志

MyBatis针对不同的日志框架提供对Log接口对应的实现，Log接口的实现类如图8-5所示。
从实现类可以看出，MyBatis支持7种不同的日志实现，具体如下。

- Apache Commons Logging：使用JCL输出日志。

- Log4j 2：使用Log4j 2框架输入日志。

- Java Util Logging：使用JDK内置的日志模块输出日志。

- Log4j：使用Log4j框架输出日志。

- No Logging：不输出任何日志。

- SLF4J：使用SLF4J日志门面输出日志。

- Stdout：将日志输出到标准输出设备（例如控制台）。

- [实现](../../mybatis-3/src/main/java/org/apache/ibatis/logging)