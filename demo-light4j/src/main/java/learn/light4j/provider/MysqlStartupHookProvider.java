package learn.light4j.provider;

import com.alibaba.druid.pool.DruidDataSource;
import com.networknt.config.Config;
import com.networknt.server.StartupHookProvider;
import learn.light4j.config.DataSourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author: caoyanan
 * @time: 2020/12/7 7:08 下午
 */
@Slf4j
public class MysqlStartupHookProvider implements StartupHookProvider {

    public static final String URL_PROTOCOL_JAR = "jar";
    public static final String JAR_URL_SEPARATOR = "!/";
    public static final String OBLIQUE_LINE = "/";

    public static DataSourceConfig DATASOURCE_CONFIG;

    public static SqlSessionFactory SQL_SESSION_FACTORY;

    @Override
    public void onStartup() {

        loadConfig();
        buildSqlSessionFactory();
    }

    private void loadConfig() {
        String envConfig = System.getProperty("env", "dev");
        log.debug("datasource 加载 {} 环境配置", envConfig);
        String config = String.format("%s-%s", "datasource", envConfig);
        DATASOURCE_CONFIG = (DataSourceConfig) Config.getInstance().getJsonObjectConfig(config, DataSourceConfig.class);
    }


    private void buildSqlSessionFactory() {
        if (Objects.nonNull(SQL_SESSION_FACTORY)) {
            return;
        }
        Configuration configuration = buildConfiguration();

        SQL_SESSION_FACTORY = new DefaultSqlSessionFactory(configuration);
    }

    private Configuration buildConfiguration() {
        Configuration configuration = new Configuration();
        Environment environment = new Environment("env", new JdbcTransactionFactory(), getDataSource() );
        configuration.setEnvironment(environment);

        try {
            loadXmlMapper(configuration);
        } catch (Exception exception) {
            throw new RuntimeException("mybatis xml文件加载失败", exception);
        }

        configuration.setMapUnderscoreToCamelCase(true);
        return configuration;
    }

    private void loadXmlMapper(Configuration configuration) throws Exception {
        URL mapperUrl = this.getClass().getResource("/mapper");
        if (Objects.isNull(mapperUrl)) {
            throw new RuntimeException("未找到mapper文件");
        }
        if (URL_PROTOCOL_JAR.equals(mapperUrl.getProtocol())) {
            loadJarXmlMapper(mapperUrl, configuration);
            return;
        }
        File file = new File(mapperUrl.toURI());
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File xmlFile : files) {
            new XMLMapperBuilder(new FileInputStream(xmlFile),
                    configuration, xmlFile.toString(), configuration.getSqlFragments())
                    .parse();
        }
    }

    private void loadJarXmlMapper(URL jarFileUrl, Configuration configuration) throws Exception {
        JarURLConnection conn = (JarURLConnection) jarFileUrl.openConnection();
        JarFile jarFile = conn.getJarFile();

        String urlFile = jarFileUrl.getFile();
        String rootEntryPath = urlFile.substring(
                urlFile.indexOf(JAR_URL_SEPARATOR)
                        + JAR_URL_SEPARATOR.length()) + OBLIQUE_LINE;

        for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
            JarEntry entry = entries.nextElement();
            String entryPath = entry.getName();
            if (entryPath.startsWith(rootEntryPath)
                    && !entryPath.equals(rootEntryPath)) {
                InputStream xmlInputStream = this.getClass().getClassLoader().getResourceAsStream(entryPath);
                new XMLMapperBuilder(xmlInputStream,
                        configuration, entryPath, configuration.getSqlFragments())
                        .parse();
            }
        }
    }

    private DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(DATASOURCE_CONFIG.getDriverClassName());
        dataSource.setUrl(DATASOURCE_CONFIG.getUrl());
        dataSource.setUsername(DATASOURCE_CONFIG.getUserName());
        dataSource.setPassword(DATASOURCE_CONFIG.getPassword());
        dataSource.setTestOnBorrow(true);
        dataSource.setTestWhileIdle(true);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setValidationQueryTimeout(30);
        return dataSource;
    }
}
