package learn.base.test.business.mybatisshard;


import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;

/**
 * 分表拦截器
 *
 * @author Zephyr
 * @date 2021/5/26.
 */
@Intercepts({
        @Signature(
                type = StatementHandler.class,
                method = "prepare",
                args = {Connection.class, Integer.class}
        )
})
public class MybatisShardInterceptor implements Interceptor {
    private static final ReflectorFactory defaultReflectorFactory = new DefaultReflectorFactory();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        // MetaObject是mybatis里面提供的一个工具类，类似反射的效果
        MetaObject metaObject = getMetaObject(invocation);MapperRegistry mapperRegistry = ((Configuration) metaObject.getValue("delegate.configuration")).getMapperRegistry();
        MappedStatement mappedStatement = (MappedStatement)
                metaObject.getValue("delegate.mappedStatement");

        //获取Mapper执行方法
        Method method = invocation.getMethod();

        //获取分表注解
        TableShard tableShard = getTableShard(method, mappedStatement);

        // 如果method与class都没有TableShard注解或执行方法不存在，执行下一个插件逻辑
        if (tableShard == null) {
            return invocation.proceed();
        }

        //获取值
        String value = tableShard.value();
        //value是否字段名，如果是，需要解析请求参数字段名的值
        boolean fieldFlag = tableShard.fieldFlag();

        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        if (fieldFlag) {
            //获取请求参数
            Object parameterObject = boundSql.getParameterObject();

            if (parameterObject instanceof MapperMethod.ParamMap) { //ParamMap类型逻辑处理

                MapperMethod.ParamMap parameterMap = (MapperMethod.ParamMap) parameterObject;
                //根据字段名获取参数值
                Object valueObject = parameterMap.get(value);
                if (valueObject == null) {
                    throw new RuntimeException(String.format("入参字段%s无匹配", value));
                }
                //替换sql
                replaceSql(tableShard, valueObject, metaObject, boundSql);

            } else { //单参数逻辑

                //如果是基础类型抛出异常
                if (isBaseType(parameterObject)) {
                    throw new RuntimeException("单参数非法，请使用@Param注解");
                }

                if (parameterObject instanceof Map){
                    Map<String,Object> parameterMap =  (Map<String,Object>)parameterObject;
                    Object valueObject = parameterMap.get(value);
                    //替换sql
                    replaceSql(tableShard, valueObject, metaObject, boundSql);
                } else {
                    //非基础类型对象
                    Class<?> parameterObjectClass = parameterObject.getClass();
                    Field declaredField = parameterObjectClass.getDeclaredField(value);
                    declaredField.setAccessible(true);
                    Object valueObject = declaredField.get(parameterObject);
                    //替换sql
                    replaceSql(tableShard, valueObject, metaObject, boundSql);
                }
            }

        } else {//无需处理parameterField
            //替换sql
            replaceSql(tableShard, value, metaObject, boundSql);
        }
        //执行下一个插件逻辑
        return invocation.proceed();
    }


    @Override
    public Object plugin(Object target) {
        // 当目标类是StatementHandler类型时，才包装目标类，否者直接返回目标本身, 减少目标被代理的次数
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }


    /**
     * @param object
     * @methodName: isBaseType
     * @author: 程序员阿星
     * @description: 基本数据类型验证，true是，false否
     * @date: 2021/5/9
     * @return: boolean
     */
    private boolean isBaseType(Object object) {
        if (object.getClass().isPrimitive()
                || object instanceof String
                || object instanceof Integer
                || object instanceof Double
                || object instanceof Float
                || object instanceof Long
                || object instanceof Boolean
                || object instanceof Byte
                || object instanceof Short) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param tableShard 分表注解
     * @param value      值
     * @param metaObject mybatis反射对象
     * @param boundSql   sql信息对象
     * @author: 程序猿阿星
     * @description: 替换sql
     * @date: 2021/5/9
     * @return: void
     */
    private void replaceSql(TableShard tableShard, Object value, MetaObject metaObject, BoundSql boundSql) {
        String tableNamePrefix = tableShard.tableNamePrefix();
        //获取策略class
        Class<? extends ITableShardStrategy> strategyClazz = tableShard.shardStrategy();
        //从spring ioc容器获取策略类
        // ITableShardStrategy tableShardStrategy = SpringUtil.getBean(strategyClazz);
        ITableShardStrategy tableShardStrategy = null;
        try {
            tableShardStrategy = strategyClazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        //生成分表名
        String shardTableName = tableShardStrategy.generateTableName(tableNamePrefix, value);
        // 获取sql
        String sql = boundSql.getSql();
        // 完成表名替换
        metaObject.setValue("delegate.boundSql.sql", sql.replaceAll(tableNamePrefix, shardTableName));
    }

    /**
     * @param invocation
     * @author: 程序猿阿星
     * @description: 获取MetaObject对象-mybatis里面提供的一个工具类，类似反射的效果
     * @date: 2021/5/9
     * @return: org.apache.ibatis.reflection.MetaObject
     */
    private MetaObject getMetaObject(Invocation invocation) {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        // MetaObject是mybatis里面提供的一个工具类，类似反射的效果
        MetaObject metaObject = MetaObject.forObject(statementHandler,
                SystemMetaObject.DEFAULT_OBJECT_FACTORY,
                SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
                defaultReflectorFactory
        );

        return metaObject;
    }

    /**
     * @author: 程序猿阿星
     * @description: 获取分表注解
     * @param method
     * @param mappedStatement
     * @date: 2021/5/9
     * @return: com.xing.shard.interceptor.TableShard
     */
    private TableShard getTableShard(Method method, MappedStatement mappedStatement) throws ClassNotFoundException, NoSuchMethodException {
        String id = mappedStatement.getId();
        //获取Class
        int splitEndIndex = id.lastIndexOf(".");
        final String className = id.substring(0, splitEndIndex);
        final String methodName = id.substring(splitEndIndex + 1);
        //分表注解
        TableShard tableShard = null;
        //获取Mapper执行方法的TableShard注解
        tableShard = Class.forName(className).getMethod(methodName, mappedStatement.getParameterMap().getType()).getAnnotation(TableShard.class);
        //如果方法没有设置注解，从Mapper接口上面获取TableShard注解
        if (tableShard == null) {
            // 获取TableShard注解
            tableShard = Class.forName(className).getAnnotation(TableShard.class);
        }
        return tableShard;
    }

}
