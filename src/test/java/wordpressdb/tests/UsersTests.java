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
import static wordpressdb.steps.UsersSteps.createUser;
import static wordpressdb.utils.CurrentDateTime.getCurrentDateTimeGmt;

@Feature("Users Tests")
public class UsersTests extends BaseTest {

    @Test
    @Story("Создание пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void createUserTest() throws SQLException {
        String username = getGeneratedString(10);
        String password = getGeneratedString(20);
        String email = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();

        String sql = String.format("insert into wp_users (user_login, user_pass, user_email, user_registered) " +
                "values ('%s', '%s', '%s', '%s');"
                , username, password, email, currentDateTimeGmt);
        assertEquals(statement.executeUpdate(sql), 1);

        ResultSet resultSet = statement.executeQuery(String.format("select * from wp_users where user_login = '%s'",
                username));
        while (resultSet.next()) {
            assertEquals(resultSet.getString("user_login"), username);
            assertEquals(resultSet.getString("user_pass"), password);
            assertEquals(resultSet.getString("user_email"), email);
            assertEquals(resultSet.getString("user_registered"), currentDateTimeGmt);
        }
    }

    @Test
    @Story("Получение пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserTest() throws SQLException {
        String username = getGeneratedString(10);
        String password = getGeneratedString(20);
        String email = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();

        createUser(username, password, email, currentDateTimeGmt);

        ResultSet resultSet = statement.executeQuery(String.format("select * from wp_users where user_login = '%s'",
                username));
        while (resultSet.next()) {
            assertEquals(resultSet.getString("user_login"), username);
            assertEquals(resultSet.getString("user_pass"), password);
            assertEquals(resultSet.getString("user_email"), email);
            assertEquals(resultSet.getString("user_registered"), currentDateTimeGmt);
        }
    }

    @Test
    @Story("Изменение email пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserEmailTest() throws SQLException {
        String username = getGeneratedString(10);
        String password = getGeneratedString(20);
        String email = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        String newEmail = getGeneratedString(13);

        createUser(username, password, email, currentDateTimeGmt);

        String sql = String.format("update wp_users set user_email = '%s' where user_login = '%s'",
                newEmail, username);
        assertEquals(statement.executeUpdate(sql), 1);

        ResultSet resultSet = statement.executeQuery(String.format("select * from wp_users where user_login = '%s'",
                username));
        while (resultSet.next()) {
            assertEquals(resultSet.getString("user_email"), newEmail);
        }
    }

    @Test
    @Story("Удаление пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteUserTest() throws SQLException {
        String username = getGeneratedString(10);
        String password = getGeneratedString(20);
        String email = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();

        createUser(username, password, email, currentDateTimeGmt);

        String sql = String.format("delete from wp_users where user_login = '%s'", username);
        assertEquals(statement.executeUpdate(sql), 1);
        assertEquals(statement.executeUpdate(sql), 0);
    }

    @Test
    @Story("Получение списка пользователей с сортировкой по дате регистрации")
    @Severity(SeverityLevel.CRITICAL)
    public void getListUsersTest() throws SQLException {
        List<String> createdUsernames = new ArrayList<>();
        List<String> userNamesFromDb = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            String username = getGeneratedString(10);
            String password = getGeneratedString(20);
            String email = getGeneratedString(15);
            String currentDateTimeGmt = getCurrentDateTimeGmt();
            createUser(username, password, email, currentDateTimeGmt);
            createdUsernames.add(username);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ResultSet resultSet = statement.executeQuery("select * from wp_users " +
                "where not user_login = 'Vladimir.Askerov' order by user_registered asc");
        while (resultSet.next()) {
            userNamesFromDb.add(resultSet.getString("user_login"));
        }

        assertEquals(userNamesFromDb, createdUsernames);
    }

    @Test
    @Story("Получение собственного пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserMeTest() throws SQLException {
        String myUsername = getGeneratedString(10);
        String myPassword = getGeneratedString(20);
        String myEmail = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();

        createUser(myUsername, myPassword, myEmail, currentDateTimeGmt);

        ResultSet resultSet = statement.executeQuery(String.format("select * from wp_users where user_login = '%s' " +
                        "and user_pass = '%s'", myUsername, myPassword));
        while (resultSet.next()) {
            assertEquals(resultSet.getString("user_login"), myUsername);
            assertEquals(resultSet.getString("user_pass"), myPassword);
            assertEquals(resultSet.getString("user_email"), myEmail);
            assertEquals(resultSet.getString("user_registered"), currentDateTimeGmt);
        }
    }

    @Test
    @Story("Изменение никнейма собственного пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserMeNicknameTest() throws SQLException {
        String myUsername = getGeneratedString(10);
        String myPassword = getGeneratedString(20);
        String myEmail = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        int myUserIdFromDb = 0;
        String myNewNickname = getGeneratedString(12);
        String myUserUpdatedNicknameFromDb = "";

        // create user with default nickname identical to username (user_login)
        createUser(myUsername, myPassword, myEmail, currentDateTimeGmt);
        ResultSet userResultSet = statement.executeQuery(String.format("select * from wp_users where user_login = '%s' " +
                "and user_pass = '%s'", myUsername, myPassword));
        while (userResultSet.next()) {
            myUserIdFromDb = userResultSet.getInt("ID");
        }
        String createNickname = String.format("insert into wp_usermeta (user_id, meta_key, meta_value) " +
                        "values (%d, 'nickname', '%s');", myUserIdFromDb, myUsername);
        statement.executeUpdate(createNickname);

        // update nickname
        String updateNickname = String.format("update wp_usermeta set meta_value = '%s' where user_id = %d " +
                "and meta_key = 'nickname'", myNewNickname, myUserIdFromDb);
        assertEquals(statement.executeUpdate(updateNickname), 1);

        // check updated nickname
        ResultSet userMetaResultSet = statement.executeQuery(String.format("select * from wp_usermeta " +
                "where user_id = %d and meta_key = 'nickname'", myUserIdFromDb));
        while (userMetaResultSet.next()) {
            myUserUpdatedNicknameFromDb = userMetaResultSet.getString("meta_value");
        }
        assertEquals(myUserUpdatedNicknameFromDb, myNewNickname);
    }

    @Test
    @Story("Попытка создания пользователя с уже занятым никнеймом")
    @Severity(SeverityLevel.CRITICAL)
    public void attemptToCreateUserWithExistingUsernameTest() throws SQLException {
        String username = getGeneratedString(10);
        String password = getGeneratedString(20);
        String email = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        createUser(username, password, email, currentDateTimeGmt);
        List<String> createdUsersFromDb = new ArrayList<>();

        String createUserWithSameUsername = String.format("insert into " +
                        "wp_users (user_login, user_pass, user_email, user_registered) values ('%s', '%s', '%s', '%s');",
                username, getGeneratedString(18), getGeneratedString(12), getCurrentDateTimeGmt());
        statement.executeUpdate(createUserWithSameUsername);

        ResultSet resultSet =  statement.executeQuery(String.format("select * from wp_users where user_login = '%s'",
                username));
        while (resultSet.next()) {
            createdUsersFromDb.add(resultSet.getString("user_login"));
        }

        assertEquals(createdUsersFromDb.size(), 2);
    }

    @AfterMethod
    public void cleanUp() throws SQLException {
        statement.execute("delete from wp_users where not user_login = 'Vladimir.Askerov'");
        statement.execute("delete from wp_usermeta where not user_id = 1");
    }
}
