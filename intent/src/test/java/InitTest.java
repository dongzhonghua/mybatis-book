import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * xyz.intent.mybatis.init
 * MyBatis初始化的步骤
 *
 * @author intent zzy.main@gmail.com
 * @date 2020/6/5 9:59 上午
 * @since 1.0
 */
public class InitTest {
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
    @Test
    public void testInit() throws FileNotFoundException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = new FileInputStream(resource);
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);
        Configuration configuration = sqlSessionFactory.getConfiguration();
        System.out.println(configuration);
    }
}
