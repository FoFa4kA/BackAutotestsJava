package wordpressdb.steps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static wordpressdb.utils.JDBCManager.getStatement;


public class UsersSteps {

    public static int createUser(String login, String password, String email, String dateTime) {
        String createUserSql = String.format("INSERT INTO wp_users (user_login, user_pass, user_email, user_registered) " +
                "VALUES ('%s', '%s', '%s', '%s');", login, password, email, dateTime);
        String getCreatedUserByLoginSql = String.format("SELECT * FROM wp_users WHERE user_login = '%s'", login);
        int createdUserId;

        try(Statement statement = getStatement()) {
            // create user
            statement.executeUpdate(createUserSql);

            // get created user id by user login
            ResultSet postByTitleResultSet = statement.executeQuery(getCreatedUserByLoginSql);
            postByTitleResultSet.next();
            createdUserId = postByTitleResultSet.getInt("ID");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return createdUserId;
    }
}
