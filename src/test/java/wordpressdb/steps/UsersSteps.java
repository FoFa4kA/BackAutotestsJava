package wordpressdb.steps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static wordpressapi.utils.PropertiesUtil.getProp;
import static wordpressdb.utils.JDBCManager.getStatement;


public class UsersSteps {
    private static final Statement STATEMENT = getStatement();

    public static void createUser(String login, String password, String email, String dateTime) throws SQLException {
        String createUserSql = String.format("INSERT INTO wp_users (user_login, user_pass, user_email, user_registered) " +
                "VALUES ('%s', '%s', '%s', '%s');", login, password, email, dateTime);

        STATEMENT.executeUpdate(createUserSql);
    }

    public static int getUsersCount() throws SQLException {
        String countUsersSql = String.format("SELECT COUNT(ID) FROM wp_users;");

        ResultSet resultSet = STATEMENT.executeQuery(countUsersSql);
        resultSet.next();
        return resultSet.getInt(1);
    }

    public static ResultSet getUserByLogin(String login) throws SQLException {
        String getUserByLoginSql = String.format("SELECT * FROM wp_users WHERE user_login = '%s'", login);

        return STATEMENT.executeQuery(getUserByLoginSql);
    }

    public static void updateUserEmail(String currentEmail, String newEmail) throws SQLException {
        String updateEmailSql = String.format("UPDATE wp_users SET user_email = '%s' WHERE user_email = '%s'",
                newEmail, currentEmail);

        STATEMENT.executeUpdate(updateEmailSql);
    }

    public static void deleteUserByLogin(String login) throws SQLException {
        String deleteUserByLoginSql = String.format("DELETE FROM wp_users WHERE user_login = '%s'", login);

        STATEMENT.executeUpdate(deleteUserByLoginSql);
    }

    public static ResultSet getListUsersSortedByLogin() throws SQLException {
        String getListUsersSortedByLoginSql = String.format("SELECT * FROM wp_users " +
                "WHERE NOT user_login = '%s' ORDER BY user_login", getProp("username"));

        return STATEMENT.executeQuery(getListUsersSortedByLoginSql);
    }

    public static int getUserIdByLogin(String login) throws SQLException {
        ResultSet userByLogin = getUserByLogin(login);
        userByLogin.next();
        return userByLogin.getInt("ID");
    }

    public static void updateUserNickname(int userId, String newNickname) throws SQLException {
        String updateUserNicknameSql = String.format("UPDATE wp_usermeta SET meta_value = '%s' " +
                "WHERE user_id = %d AND meta_key = 'nickname'", newNickname, userId);

        STATEMENT.executeUpdate(updateUserNicknameSql);
    }

    public static ResultSet getUserNickname(int userId) throws SQLException {
        String getUserNicknameSql = String.format("SELECT * FROM wp_usermeta WHERE user_id = %d AND meta_key = 'nickname'",
                userId);

        return STATEMENT.executeQuery(getUserNicknameSql);
    }

    public static void cleanUsers() throws SQLException {
        STATEMENT.execute(String.format("DELETE FROM wp_users WHERE NOT user_login = '%s'", getProp("username")));
    }
}
