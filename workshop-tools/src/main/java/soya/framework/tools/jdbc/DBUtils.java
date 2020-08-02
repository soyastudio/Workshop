package soya.framework.tools.jdbc;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class DBUtils {
    private static Logger logger = LoggerFactory.getLogger(DBUtils.class);

    public static JsonArray executeQuery(String sql, DataSource dataSource) {
        logger.info(sql);

        JsonArray results = null;
        Statement stmt = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            results = resultSetToJson(rs);

            logger.info("results: {}", results.size());

        } catch (SQLException e) {
            logger.error(e.getMessage());

        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return results;
    }

    public static JsonArray resultSetToJson(ResultSet rs) throws SQLException {
        JsonArray array = new JsonArray();
        ResultSetMetaData metadata = rs.getMetaData();
        int count = metadata.getColumnCount();

        while (rs.next()) {
            JsonObject row = new JsonObject();
            for (int i = 0; i < count; i++) {
                int index = i + 1;
                String colName = metadata.getColumnName(index);
                Object value = rs.getString(index);
                if (value != null) {
                    if (value instanceof Number) {
                        row.addProperty(colName.toLowerCase(), (Number) value);
                    } else {
                        row.addProperty(colName.toLowerCase(), value.toString().trim());
                    }
                }
            }

            array.add(row);
        }

        return array;
    }

    public static List<DynaBean> toDynaBean(ResultSet rs) throws SQLException {
        RowSetDynaClass rowSetDynaClass = new RowSetDynaClass(rs);
        return ImmutableList.copyOf(rowSetDynaClass.getRows());
    }
}
