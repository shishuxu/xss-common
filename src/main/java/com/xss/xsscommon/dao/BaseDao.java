package com.xss.xsscommon.dao;


import com.xss.xsscommon.db.AppConstant;
import com.xss.xsscommon.db.MessageUtil;
import com.xss.xsscommon.db.SessionContext;
import com.xss.xsscommon.db.SessionFactory;
import com.xss.xsscommon.util.GingersoftException;
import com.xss.xsscommon.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import rx.exceptions.Exceptions;

import java.util.Objects;
import java.util.function.Function;


@Slf4j
public abstract class BaseDao {
    @Autowired
    protected SessionFactory sessionFactory;

    protected <T, R> R executeRead(Class<T> clazz, Function<T, R> function) {
        SessionContext context = null;
        try {
            context = sessionFactory.getSessionContext(AppConstant.DB_MASTER);
            T mapper = context.getMapper(clazz);
            return function.apply(mapper);
        } catch (Exception e) {
            log.error("", e);
            throw Exceptions.propagate(new GingersoftException(MessageUtil.CODE_DB_ERROR));
        } finally {
            SessionContext.closeSilently(context);
        }
    }

    protected <T, R> R executeWrite(Class<T> clazz, Function<T, R> function) {
        SessionContext context = null;
        try {
            context = sessionFactory.getSessionContext(AppConstant.DB_MASTER);
            T mapper = context.getMapper(clazz);
            R t = function.apply(mapper);
            context.commit();
            return t;
        } catch (Exception e) {
            log.error("", e);
            throw Exceptions.propagate(new GingersoftException(MessageUtil.CODE_DB_ERROR));
        } finally {
            SessionContext.closeSilently(context);
        }
    }


    protected <T, R> R executeFromRead(Class<T> clazz, Function<T, R> function) {
        SessionContext context = null;
        try {
            context = sessionFactory.getSessionContext(AppConstant.DB_SLAVE);
            T mapper = context.getMapper(clazz);
            return function.apply(mapper);
        } catch (Exception e) {
            log.error("", e);
            throw Exceptions.propagate(new GingersoftException(MessageUtil.CODE_DB_ERROR));
        } finally {
            SessionContext.closeSilently(context);
        }
    }

    protected <T, R> R executeWriteCache(Class<T> clazz, Function<T, R> function) {
        SessionContext context = null;
        try {
            context = sessionFactory.getSessionContext(AppConstant.DB_MASTER);
            T mapper = context.getMapper(clazz);
            R t = function.apply(mapper);
            Long size = ObjectUtil.toLong(t);
            if (Objects.isNull(size) || size == 0) {
                log.error("{}", t);
                throw Exceptions.propagate(new GingersoftException(MessageUtil.CODE_DB_ERROR));
            }
            context.commit();
            return t;
        } catch (Exception e) {
            log.error("", e);
            throw Exceptions.propagate(new GingersoftException(MessageUtil.CODE_DB_ERROR));
        } finally {
            SessionContext.closeSilently(context);
        }
    }

    /**
     * @author LYC
     * @version 1.0
     * @date 2021/5/22 15:40
     * 业务库的执行
     */
    protected <T, R> R executeRead(Class<T> clazz, String db, Function<T, R> function) {
        SessionContext context = null;
        try {
            context = sessionFactory.getSessionContext(db);
            T mapper = context.getMapper(clazz);
            return function.apply(mapper);
        } catch (Exception e) {
            log.error("", e);
            throw Exceptions.propagate(new GingersoftException(MessageUtil.CODE_DB_ERROR));
        } finally {
            SessionContext.closeSilently(context);
        }
    }

    protected <T, R> R executeWrite(Class<T> clazz, String db, Function<T, R> function) {
        SessionContext context = null;
        try {
            context = sessionFactory.getSessionContext(db);
            T mapper = context.getMapper(clazz);
            R t = function.apply(mapper);
            context.commit();
            return t;
        } catch (Exception e) {
            log.error("", e);
            throw Exceptions.propagate(new GingersoftException(MessageUtil.CODE_DB_ERROR));
        } finally {
            SessionContext.closeSilently(context);
        }
    }
}
