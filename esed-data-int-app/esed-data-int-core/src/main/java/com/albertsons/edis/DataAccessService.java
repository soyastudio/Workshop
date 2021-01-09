package com.albertsons.edis;

import com.google.gson.JsonArray;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.sql.SQLException;

public interface DataAccessService {

    SelectResult search(SqlBuilder builder) throws ServiceException;

    void update(SqlBuilder builder) throws ServiceException;

    void executeBatch(String[] queries) throws ServiceException;

    void executeBatch(BatchBuilder builder) throws ServiceException;

    DataSource getDataSource();

    SqlBuilder sqlBuilder(String sql);

    BatchBuilder batchBuilder(String sql);

    static interface SqlBuilder {
        SqlBuilder set(int index, Object value, Class<?> type) throws SQLException;

        SqlBuilder set(int index, Object value, Object defaultValue) throws SQLException;

        SqlBuilder set(String paramName, Object paramValue, Class<?> type) throws SQLException;

        SqlBuilder set(String paramName, Object paramValue, Object defaultValue) throws SQLException;

        SelectResult executeQuery() throws SQLException;

        void executeUpdate() throws SQLException;
    }

    static interface BatchBuilder {

        BatchBuilder flushSize(int flushSize);

        BatchBuilder statement(String sql);

        BatchBuilder parameterTypes(Class<?>[] types);

        BatchBuilder parameterTypes(Class<?>[] types, Object[] defaultValues);

        BatchBuilder addParameters(Object[] parameters);

        void execute() throws SQLException;
    }

    static interface SelectResult {
        PropertyDescriptor[] getProperties();

        JsonArray getData();
    }
}
