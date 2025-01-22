package wordpressdb.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import wordpressdb.utils.UsersDataProvider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static wordpressapi.utils.GenerateData.getGeneratedString;
import static wordpressapi.utils.PropertiesUtil.getProp;
import static wordpressdb.steps.UsersSteps.cleanUsers;
import static wordpressdb.steps.UsersSteps.createUser;
import static wordpressdb.steps.UsersSteps.deleteUserByLogin;
import static wordpressdb.steps.UsersSteps.getListUsersSortedByLogin;
import static wordpressdb.steps.UsersSteps.getUserByLogin;
import static wordpressdb.steps.UsersSteps.getUserIdByLogin;
import static wordpressdb.steps.UsersSteps.getUserNickname;
import static wordpressdb.steps.UsersSteps.getUsersCount;
import static wordpressdb.steps.UsersSteps.updateUserEmail;
import static wordpressdb.steps.UsersSteps.updateUserNickname;
import static wordpressdb.utils.CurrentDateTime.getCurrentDateTimeGmt;

@Epic("DB Tests")
@Feature("Users Tests")
public class UsersTests {

    @Test
    @Story("Создание пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void createUserTest() throws SQLException {
        String login = getGeneratedString(10);
        String password = getGeneratedString(20);
        String email = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        int usersCountBeforeCreatingUser = getUsersCount();

        createUser(login, password, email, currentDateTimeGmt);

        assertEquals(getUsersCount(), usersCountBeforeCreatingUser + 1);
    }

    @Test
    @Story("Получение пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserTest() throws SQLException {
        String login = getGeneratedString(10);
        String password = getGeneratedString(20);
        String email = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();

        createUser(login, password, email, currentDateTimeGmt);

        ResultSet createdUserResultSet = getUserByLogin(login);
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

        createUser(login, password, email, currentDateTimeGmt);
        updateUserEmail(email, newEmail);

        ResultSet updatedUserResultSet = getUserByLogin(login);
        updatedUserResultSet.next();
        assertEquals(updatedUserResultSet.getString("user_email"), newEmail);
    }

    @Test
    @Story("Удаление пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteUserTest() throws SQLException {
        String login = getGeneratedString(10);
        String password = getGeneratedString(20);
        String email = getGeneratedString(15);
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        int usersCountBeforeDeletingUser;

        createUser(login, password, email, currentDateTimeGmt);
        usersCountBeforeDeletingUser = getUsersCount();
        deleteUserByLogin(login);

        assertEquals(getUsersCount(), usersCountBeforeDeletingUser - 1);
    }

    @Test
    @Story("Получение списка пользователей с сортировкой по логину")
    @Severity(SeverityLevel.CRITICAL)
    public void getListUsersTest() throws SQLException {
        List<String> createdUsersLogins = new ArrayList<>();
        int usersCount = 0;

        for (int i = 0; i < 10; i++) {
            String login = getGeneratedString(10);
            String password = getGeneratedString(20);
            String email = getGeneratedString(15);
            String currentDateTimeGmt = getCurrentDateTimeGmt();
            createUser(login, password, email, currentDateTimeGmt);
            createdUsersLogins.add(login);
        }

        createdUsersLogins.sort(String.CASE_INSENSITIVE_ORDER);

        ResultSet usersSortedByLoginResultSet = getListUsersSortedByLogin();
        while (usersSortedByLoginResultSet.next()) {
            assertEquals(usersSortedByLoginResultSet.getString("user_login"),
                    createdUsersLogins.get(usersCount++));
        }
    }

    @Test(dataProvider = "My user data provider", dataProviderClass = UsersDataProvider.class)
    @Story("Получение собственного пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserMeTest(String myLogin, String myEmail) throws SQLException {
        ResultSet myUserResultSet = getUserByLogin(myLogin);
        myUserResultSet.next();
        assertEquals(myUserResultSet.getString("user_login"), myLogin);
        assertEquals(myUserResultSet.getString("user_email"), myEmail);
    }

    @Test(dataProvider = "My user data provider", dataProviderClass = UsersDataProvider.class)
    @Story("Изменение никнейма собственного пользователя")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUserMeNicknameTest(String myLogin, String myEmail) throws SQLException {
        int myUserId = getUserIdByLogin(myLogin);
        String myNewNickname = getGeneratedString(12);

        updateUserNickname(myUserId, myNewNickname);

        ResultSet myUpdatedNicknameResultSet = getUserNickname(myUserId);
        myUpdatedNicknameResultSet.next();
        assertEquals(myUpdatedNicknameResultSet.getString("meta_value"), myNewNickname);
    }

    @Test
    @Story("Попытка создания пользователя с уже занятым логином")
    @Severity(SeverityLevel.CRITICAL)
    public void attemptToCreateUserWithExistingUsernameTest() throws SQLException {
        String login = getGeneratedString(10);
        List<String> createdUsersWithSameLogins = new ArrayList<>();
        List<String> usersWithSameLoginFromDb = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            String password = getGeneratedString(20);
            String email = getGeneratedString(15);
            String currentDateTimeGmt = getCurrentDateTimeGmt();
            createUser(login, password, email, currentDateTimeGmt);
            createdUsersWithSameLogins.add(login);
        }

        ResultSet userByLoginResultSet =  getUserByLogin(login);
        while (userByLoginResultSet.next()) {
            assertEquals(userByLoginResultSet.getString("user_login"), login);
            usersWithSameLoginFromDb.add(userByLoginResultSet.getString("user_login"));
        }
        assertEquals(usersWithSameLoginFromDb.size(), createdUsersWithSameLogins.size());
    }

    @AfterMethod
    public void cleanUp() throws SQLException {
        cleanUsers();
        updateUserNickname(1, getProp("username"));
    }
}
