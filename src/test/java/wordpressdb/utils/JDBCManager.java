package wordpressdb.utils;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static wordpressapi.utils.PropertiesUtil.getProp;

public class JDBCManager {

    public static Statement getStatement() {
        try {
            return DriverManager.getConnection(
                    getProp("db_url"),
                    getProp("db_user"),
                    getProp("db_password")
            ).createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
