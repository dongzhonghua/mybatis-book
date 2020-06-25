package org.apache.ibatis.executor;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

import static org.apache.ibatis.executor.ExecutionPlaceholder.EXECUTION_PLACEHOLDER;

/**
 * org.apache.ibatis.executor
 * 这个是一级缓存的装饰者模式的实现
 * 把BaseExecutor里面的一级缓存抽离出来用装饰者模式装饰了一层（对，和二级缓存一样）
 * <p>
 * 查询过程:
 * ------------二级缓存相关-------一级缓存相关---------执行器相关
 * SqlSession->CachingExecutor->L1CachingExecutor->BaseExecutor
 *
 * @author intent zzy.main@gmail.com
 * @date 2020/6/25 10:46 上午
 * @since 1.0
 */
public class L1CachingExecutor implements Executor {
    private final Executor delegate;
    // Mybatis一级缓存对象
    protected PerpetualCache localCache;

    public L1CachingExecutor(Executor delegate) {
        this.delegate = delegate;
        this.localCache = new PerpetualCache("LocalCache");
        delegate.setExecutorWrapper(this);
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        return delegate.update(ms, parameter);
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
        // 从缓存中获取结果
        List<E> list = resultHandler == null ? (List<E>) localCache.getObject(key) : null;
        if (list != null) {
//            handleLocallyCachedOutputParameters(ms, key, parameter, boundSql);
            System.out.println("一级缓存命中！！！");
            return list;
        } else {
            localCache.putObject(key, EXECUTION_PLACEHOLDER);
            try {
                list = delegate.query(ms, parameter, rowBounds, resultHandler, key, boundSql);
            } finally {
                localCache.removeObject(key);
            }
            localCache.putObject(key, list);
            return list;
        }
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException {
        BoundSql boundSql = ms.getBoundSql(parameter);
        // 调用createCacheKey（）方法创建缓存Key
        CacheKey key = createCacheKey(ms, parameter, rowBounds, boundSql);
        return query(ms, parameter, rowBounds, resultHandler, key, boundSql);
    }

    @Override
    public <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException {
        return delegate.queryCursor(ms, parameter, rowBounds);
    }

    @Override
    public List<BatchResult> flushStatements() throws SQLException {
        return delegate.flushStatements();
    }

    @Override
    public void commit(boolean required) throws SQLException {
        delegate.commit(required);
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        delegate.rollback(required);
    }

    @Override
    public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
        return delegate.createCacheKey(ms, parameterObject, rowBounds, boundSql);
    }

    @Override
    public boolean isCached(MappedStatement ms, CacheKey key) {
        return delegate.isCached(ms, key);
    }

    @Override
    public void clearLocalCache() {
        if (!delegate.isClosed()) {
            localCache.clear();
        }
        delegate.clearLocalCache();
    }

    @Override
    public void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType) {
        delegate.deferLoad(ms, resultObject, property, key, targetType);
    }

    @Override
    public Transaction getTransaction() {
        return delegate.getTransaction();
    }

    @Override
    public void close(boolean forceRollback) {
        try {
        } finally {
            localCache = null;
            delegate.close(forceRollback);
        }
    }

    @Override
    public boolean isClosed() {
        return delegate.isClosed();
    }

    @Override
    public void setExecutorWrapper(Executor executor) {
    }

//    private void handleLocallyCachedOutputParameters(MappedStatement ms, CacheKey key, Object parameter, BoundSql boundSql) {
//        if (ms.getStatementType() == StatementType.CALLABLE) {
//            final Object cachedParameter = localOutputParameterCache.getObject(key);
//            if (cachedParameter != null && parameter != null) {
//                final MetaObject metaCachedParameter = configuration.newMetaObject(cachedParameter);
//                final MetaObject metaParameter = configuration.newMetaObject(parameter);
//                for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
//                    if (parameterMapping.getMode() != ParameterMode.IN) {
//                        final String parameterName = parameterMapping.getProperty();
//                        final Object cachedValue = metaCachedParameter.getValue(parameterName);
//                        metaParameter.setValue(parameterName, cachedValue);
//                    }
//                }
//            }
//        }
//    }
}
