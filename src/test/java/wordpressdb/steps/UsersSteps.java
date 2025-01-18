package wordpressdb.steps;

import java.sql.SQLException;
import java.sql.Statement;

import static wordpressdb.utils.JDBCManager.getStatement;


public class UsersSteps {

    public static void createUser(String username, String password, String email, String dateTime) {
        String sql = String.format("INSERT INTO wp_users (user_login, user_pass, user_email, user_registered) " +
                        "VALUES ('%s', '%s', '%s', '%s');", username, password, email, dateTime);

        try(Statement statement = getStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
