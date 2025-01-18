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

import static org.testng.Assert.assertEquals;
import static wordpressapi.utils.GenerateData.getGeneratedInt;
import static wordpressapi.utils.GenerateData.getGeneratedString;
import static wordpressdb.steps.PostsSteps.createPost;
import static wordpressdb.utils.CurrentDateTime.getCurrentDateTime;
import static wordpressdb.utils.CurrentDateTime.getCurrentDateTimeGmt;

@Feature("Posts Tests")
public class PostsTests extends BaseTest {

    @Test
    @Story("Создание поста с заголовком и содержанием")
    @Severity(SeverityLevel.NORMAL)
    public void createPostTest() throws SQLException {
        String title = getGeneratedString(10);
        String content = getGeneratedString(25);
        String currentDateTime = getCurrentDateTime();
        String currentDateTimeGmt = getCurrentDateTimeGmt();

        String sql = String.format("insert into wp_posts " +
                        "(post_title, post_content, post_date, post_date_gmt, post_modified, post_modified_gmt, " +
                        "post_excerpt, post_status, to_ping, pinged, post_content_filtered) " +
                        "values ('%s', '%s', '%s', '%s', '%s', '%s', '', 'draft', '', '', '');",
                title, content, currentDateTime, currentDateTimeGmt, currentDateTime, currentDateTimeGmt);
        assertEquals(statement.executeUpdate(sql), 1);

        ResultSet resultSet = statement.executeQuery(String.format("select * from wp_posts where post_title = '%s'",
                title));
        while (resultSet.next()) {
            assertEquals(resultSet.getString("post_title"), title);
            assertEquals(resultSet.getString("post_content"), content);
            assertEquals(resultSet.getString("post_date"), currentDateTime);
            assertEquals(resultSet.getString("post_date_gmt"), currentDateTimeGmt);
        }
    }

    @Test
    @Story("Получение поста")
    @Severity(SeverityLevel.NORMAL)
    public void getPostTest() throws SQLException {
        String title = getGeneratedString(10);
        String content = getGeneratedString(25);
        String currentDateTime = getCurrentDateTime();
        String currentDateTimeGmt = getCurrentDateTimeGmt();

        createPost(title, content, currentDateTime, currentDateTimeGmt);

        ResultSet resultSet = statement.executeQuery(String.format("select * from wp_posts where post_title = '%s'",
                title));
        while (resultSet.next()) {
            assertEquals(resultSet.getString("post_title"), title);
            assertEquals(resultSet.getString("post_content"), content);
            assertEquals(resultSet.getString("post_date"), currentDateTime);
            assertEquals(resultSet.getString("post_date_gmt"), currentDateTimeGmt);
        }
    }

    @Test
    @Story("Изменение заголовка поста")
    @Severity(SeverityLevel.NORMAL)
    public void updatePostTitleTest() throws SQLException {
        String title = getGeneratedString(10);
        String content = getGeneratedString(25);
        String currentDateTime = getCurrentDateTime();
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        String newTitle = getGeneratedString(14);

        createPost(title, content, currentDateTime, currentDateTimeGmt);

        String sql = String.format("update wp_posts set post_title = '%s' where post_content = '%s'",
                newTitle, content);
        assertEquals(statement.executeUpdate(sql), 1);

        ResultSet resultSet = statement.executeQuery(String.format("select * from wp_posts where post_content = '%s'",
                content));
        while (resultSet.next()) {
            assertEquals(resultSet.getString("post_title"), newTitle);
        }
    }

    @Test
    @Story("Удаление поста")
    @Severity(SeverityLevel.NORMAL)
    public void deletePostTest() throws SQLException {
        String title = getGeneratedString(10);
        String content = getGeneratedString(25);
        String currentDateTime = getCurrentDateTime();
        String currentDateTimeGmt = getCurrentDateTimeGmt();

        createPost(title, content, currentDateTime, currentDateTimeGmt);

        String sql = String.format("delete from wp_posts where post_title = '%s'", title);
        assertEquals(statement.executeUpdate(sql), 1);
        assertEquals(statement.executeUpdate(sql), 0);
    }

    @Test
    @Story("Получение списка постов с поиском по тексту")
    @Severity(SeverityLevel.NORMAL)
    public void getListPostsTest() throws SQLException {
        String searchTitle = "";
        String title;
        String content;
        String currentDateTime;
        String currentDateTimeGmt;
        List<String> foundPosts = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            if (i == 2) {
                title = getGeneratedString(10);
                content = getGeneratedString(25);
                currentDateTime = getCurrentDateTime();
                currentDateTimeGmt = getCurrentDateTimeGmt();
                searchTitle = title;
            } else {
                title = getGeneratedString(15);
                content = getGeneratedString(30);
                currentDateTime = getCurrentDateTime();
                currentDateTimeGmt = getCurrentDateTimeGmt();
            }
            createPost(title, content, currentDateTime, currentDateTimeGmt);
        }

        String sql = String.format("select * from wp_posts where post_title = '%s'", searchTitle);
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            assertEquals(resultSet.getString("post_title"), searchTitle);
            foundPosts.add(resultSet.getString("post_title"));
        }
        assertEquals(foundPosts.size(), 1);
    }

    @Test
    @Story("Попытка удаления поста с некорректным id")
    @Severity(SeverityLevel.NORMAL)
    public void attemptToDeletePostWithInvalidId() throws SQLException {
        int invalidId = getGeneratedInt(13, 23452);

        String sql = String.format("delete from wp_posts where ID = '%s'", invalidId);
        assertEquals(statement.executeUpdate(sql), 0);
    }

    @AfterMethod
    public void cleanUp() throws SQLException {
        statement.execute("delete from wp_posts where post_status = 'draft'");
    }
}
