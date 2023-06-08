package learn.springboot.extradatasource.plugin;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * @author hanlipeng
 * @since 2018-11-15
 */
@Intercepts({@Signature(
    type = StatementHandler.class,
    method = "query",
    args = {Statement.class, ResultHandler.class}
), @Signature(
    type = StatementHandler.class,
    method = "update",
    args = {Statement.class}
)})
public class LoggingInterceptor implements Interceptor {


    private static Logger logger = LoggerFactory.getLogger("base-mybatis");
    private final static String SQL_FORMAT = "\nsource-sql:#{source-sql}\nparam:#{param}\ncount:#{count}\nrunning-time:#{running-time}ms";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        BoundSql boundSql = statementHandler.getBoundSql();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        Configuration configuration = mappedStatement.getConfiguration();
        String sql = boundSql.getSql();
        List<Object> params = getParams(boundSql, configuration);
        Object proceed;
        try {
            proceed = invocation.proceed();
        } catch (Exception e) {
            logger.error(buildSqlDetail(sql, params, 0, 0));
            throw e;
        }
        long endTime = System.currentTimeMillis();
        int count;
        if (proceed instanceof Collection) {
            count = ((Collection) proceed).size();
        } else {
            count = 1;
        }
        logger.debug(buildSqlDetail(sql, params, count, endTime - startTime));
        return proceed;
    }

    private List<Object> getParams(BoundSql boundSql, Configuration configuration) {
        List<Object> params = new LinkedList<>();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();
        if (parameterMappings.isEmpty() || parameterObject == null) {
            return params;
        }
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            params.add(parameterObject);
        } else {
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                if (metaObject.hasGetter(propertyName)) {
                    Object obj = metaObject.getValue(propertyName);
                    params.add(obj);
                } else if (boundSql.hasAdditionalParameter(propertyName)) {
                    Object obj = boundSql.getAdditionalParameter(propertyName);
                    params.add(obj);
                }
            }
        }
        return params;
    }

    private String buildSqlDetail(String sql, List<Object> params, long count, long runTime) {
        return SQL_FORMAT.replace("#{count}", Long.toString(count))
            .replace("#{running-time}", String.valueOf(runTime))
            .replace("#{param}", params.toString())
            .replace("#{source-sql}", sql.replaceAll("[\\s]+", " "));

    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }


    @Override
    public void setProperties(Properties properties) {

    }
}
