package wordpressdb.tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static wordpressapi.utils.GenerateData.getGeneratedString;
import static wordpressapi.utils.PropertiesUtil.getProp;
import static wordpressdb.steps.UsersSteps.createUser;
import static wordpressdb.utils.CurrentDateTime.getCurrentDateTimeGmt;

@Feature("Users Tests")
public class UsersTests extends BaseTest {
    private final String  getUserByIdSql = "SELECT * FROM wp_users WHERE ID = %d";
    private int createdUserId;

    @Test
    @Story("Создание пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void createUserTest() throws SQLException {
        String login = getGeneratedString(10);
        String password = getGeneratedString(20);
        String email = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        String createUserSql = String.format("INSERT INTO wp_users (user_login, user_pass, user_email, user_registered) " +
                "VALUES ('%s', '%s', '%s', '%s');", login, password, email, currentDateTimeGmt);
        String getCreatedUserByLoginSql = String.format("SELECT * FROM wp_users WHERE user_login = '%s'", login);

        // create user
        assertEquals(statement.executeUpdate(createUserSql), 1);

        // get created user id by login
        ResultSet postByTitleResultSet = statement.executeQuery(getCreatedUserByLoginSql);
        postByTitleResultSet.next();
        createdUserId = postByTitleResultSet.getInt("ID");

        // check created user by id
        ResultSet createdUserResultSet = statement.executeQuery(String.format(getUserByIdSql, createdUserId));
        createdUserResultSet.next();
        assertEquals(createdUserResultSet.getString("user_login"), login);
        assertEquals(createdUserResultSet.getString("user_pass"), password);
        assertEquals(createdUserResultSet.getString("user_email"), email);
        assertEquals(createdUserResultSet.getString("user_registered"), currentDateTimeGmt);
    }

    @Test
    @Story("Получение пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserTest() throws SQLException {
        String login = getGeneratedString(10);
        String password = getGeneratedString(20);
        String email = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();

        createdUserId = createUser(login, password, email, currentDateTimeGmt);

        ResultSet createdUserResultSet = statement.executeQuery(String.format(getUserByIdSql, createdUserId));
        createdUserResultSet.next();
        assertEquals(createdUserResultSet.getString("user_login"), login);
        assertEquals(createdUserResultSet.getString("user_pass"), password);
        assertEquals(createdUserResultSet.getString("user_email"), email);
        assertEquals(createdUserResultSet.getString("user_registered"), currentDateTimeGmt);
    }

    @Test
    @Story("Изменение email пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserEmailTest() throws SQLException {
        String login = getGeneratedString(10);
        String password = getGeneratedString(20);
        String email = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        String newEmail = getGeneratedString(13);
        String updateEmailSql = "UPDATE wp_users SET user_email = '%s' WHERE ID = %d";

        createdUserId = createUser(login, password, email, currentDateTimeGmt);

        assertEquals(statement.executeUpdate(String.format(updateEmailSql, newEmail, createdUserId)), 1);

        ResultSet resultSet = statement.executeQuery(String.format(getUserByIdSql, createdUserId));
        resultSet.next();
        assertEquals(resultSet.getString("user_email"), newEmail);
    }

    @Test
    @Story("Удаление пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteUserTest() throws SQLException {
        String login = getGeneratedString(10);
        String password = getGeneratedString(20);
        String email = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        String deleteUserSql = "DELETE FROM wp_users WHERE ID = %d";

        createdUserId = createUser(login, password, email, currentDateTimeGmt);

        assertEquals(statement.executeUpdate(String.format(deleteUserSql, createdUserId)), 1);
        assertEquals(statement.executeUpdate(String.format(deleteUserSql, createdUserId)), 0);
    }

    @Test
    @Story("Получение списка пользователей с сортировкой по дате регистрации")
    @Severity(SeverityLevel.CRITICAL)
    public void getListUsersTest() throws SQLException {
        List<Integer> createdUserIdsAddedByTimeOfCreation = new ArrayList<>();
        List<Integer> userIdsSortedByRegistrationDateInDb = new ArrayList<>();
        String getListUsersSortedByRegistrationDateSql = String.format("SELECT * FROM wp_users " +
                "WHERE NOT user_login = '%s' ORDER BY user_registered ASC", getProp("username"));

        for (int i = 0; i < 4; i++) {
            String login = getGeneratedString(10);
            String password = getGeneratedString(20);
            String email = getGeneratedString(15);
            String currentDateTimeGmt = getCurrentDateTimeGmt();
            createdUserId = createUser(login, password, email, currentDateTimeGmt);
            createdUserIdsAddedByTimeOfCreation.add(createdUserId);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ResultSet resultSet = statement.executeQuery(getListUsersSortedByRegistrationDateSql);
        while (resultSet.next()) {
            userIdsSortedByRegistrationDateInDb.add(resultSet.getInt("ID"));
        }
        assertEquals(userIdsSortedByRegistrationDateInDb, createdUserIdsAddedByTimeOfCreation);
    }

    @Test
    @Story("Получение собственного пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserMeTest() throws SQLException {
        String myLogin = getGeneratedString(10);
        String myPassword = getGeneratedString(20);
        String myEmail = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        String getMyUserSql = String.format("SELECT * FROM wp_users WHERE user_login = '%s' AND user_pass = '%s'",
                myLogin, myPassword);

        createdUserId = createUser(myLogin, myPassword, myEmail, currentDateTimeGmt);

        ResultSet myUserResultSet = statement.executeQuery(getMyUserSql);
        myUserResultSet.next();
        assertEquals(myUserResultSet.getInt("ID"), createdUserId);
        assertEquals(myUserResultSet.getString("user_login"), myLogin);
        assertEquals(myUserResultSet.getString("user_pass"), myPassword);
        assertEquals(myUserResultSet.getString("user_email"), myEmail);
        assertEquals(myUserResultSet.getString("user_registered"), currentDateTimeGmt);
    }

    @Test
    @Story("Изменение никнейма собственного пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserMeNicknameTest() throws SQLException {
        String myLogin = getGeneratedString(10);
        String myPassword = getGeneratedString(20);
        String myEmail = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        String createNicknameSql = "INSERT INTO wp_usermeta (user_id, meta_key, meta_value) " +
                "VALUES (%d, 'nickname', '%s')";
        String myNewNickname = getGeneratedString(12);
        String updateNicknameSql = "UPDATE wp_usermeta SET meta_value = '%s' WHERE user_id = %d " +
                "AND meta_key = 'nickname'";
        String myUserUpdatedNicknameFromDb = "";
        String getUserNickname = "SELECT * FROM wp_usermeta WHERE user_id = %d AND meta_key = 'nickname'";

        // create user with default nickname identical to login
        createdUserId = createUser(myLogin, myPassword, myEmail, currentDateTimeGmt);
        String createNickname = String.format(createNicknameSql, createdUserId, myLogin);
        statement.executeUpdate(createNickname);

        // update nickname
        assertEquals(statement.executeUpdate(String.format(updateNicknameSql, myNewNickname, createdUserId)), 1);

        // check updated nickname
        ResultSet userNicknameResultSet = statement.executeQuery(String.format(getUserNickname, createdUserId));
        userNicknameResultSet.next();
        myUserUpdatedNicknameFromDb = userNicknameResultSet.getString("meta_value");
        assertEquals(myUserUpdatedNicknameFromDb, myNewNickname);
    }

    @Test
    @Story("Попытка создания пользователя с уже занятым логином")
    @Severity(SeverityLevel.CRITICAL)
    public void attemptToCreateUserWithExistingUsernameTest() throws SQLException {
        String login = getGeneratedString(10);
        String password_1 = getGeneratedString(20);
        String email_1 = getGeneratedString(15);
        String currentDateTimeGmt_1 = getCurrentDateTimeGmt();
        String password_2 = getGeneratedString(18);
        String email_2 = getGeneratedString(12);
        String currentDateTimeGmt_2 = getCurrentDateTimeGmt();
        List<String> createdUsersLogins = new ArrayList<>();
        String getUserByLoginSql = String.format("SELECT * FROM wp_users WHERE user_login = '%s'", login);

        // create first user
        createUser(login, password_1, email_1, currentDateTimeGmt_1);

        // create second user with the same login as the first user
        createUser(login, password_2, email_2, currentDateTimeGmt_2);

        // check number of users with the same login
        ResultSet userByLoginResultSet =  statement.executeQuery(getUserByLoginSql);
        while (userByLoginResultSet.next()) {
            assertEquals(userByLoginResultSet.getString("user_login"), login);
            createdUsersLogins.add(userByLoginResultSet.getString("user_login"));
        }
        assertEquals(createdUsersLogins.size(), 2);
    }

    @AfterMethod
    public void cleanUp() throws SQLException {
        statement.execute(String.format("DELETE FROM wp_users WHERE NOT user_login = '%s'", getProp("username")));
        statement.execute("DELETE FROM wp_usermeta WHERE NOT user_id = 1");
    }
}
