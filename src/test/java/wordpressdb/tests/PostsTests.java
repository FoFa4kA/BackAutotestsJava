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
    private final String getPostByIdSql = "SELECT * FROM wp_posts WHERE ID = %d";
    private int createdPostId;

    @Test
    @Story("Создание поста с заголовком и содержанием")
    @Severity(SeverityLevel.NORMAL)
    public void createPostTest() throws SQLException {
        String title = getGeneratedString(10);
        String content = getGeneratedString(25);
        String currentDateTime = getCurrentDateTime();
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        String createPostSql = String.format("INSERT INTO wp_posts " +
                        "(post_title, post_content, post_date, post_date_gmt, post_modified, post_modified_gmt, " +
                        "post_excerpt, post_status, to_ping, pinged, post_content_filtered) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '', 'draft', '', '', '');",
                title, content, currentDateTime, currentDateTimeGmt, currentDateTime, currentDateTimeGmt);
        String getCreatedPostByTitleSql = String.format("SELECT * FROM wp_posts WHERE post_title = '%s'", title);

        // create post
        assertEquals(statement.executeUpdate(createPostSql), 1);

        // get created post id by title
        ResultSet postByTitleResultSet = statement.executeQuery(getCreatedPostByTitleSql);
        postByTitleResultSet.next();
        createdPostId = postByTitleResultSet.getInt("ID");

        // check created post by id
        ResultSet createdPostResultSet = statement.executeQuery(String.format(getPostByIdSql, createdPostId));
        createdPostResultSet.next();
        assertEquals(createdPostResultSet.getString("post_title"), title);
        assertEquals(createdPostResultSet.getString("post_content"), content);
        assertEquals(createdPostResultSet.getString("post_date"), currentDateTime);
        assertEquals(createdPostResultSet.getString("post_date_gmt"), currentDateTimeGmt);
    }

    @Test
    @Story("Получение поста")
    @Severity(SeverityLevel.NORMAL)
    public void getPostTest() throws SQLException {
        String title = getGeneratedString(10);
        String content = getGeneratedString(25);
        String currentDateTime = getCurrentDateTime();
        String currentDateTimeGmt = getCurrentDateTimeGmt();

        createdPostId = createPost(title, content, currentDateTime, currentDateTimeGmt);

        ResultSet createdPostResultSet = statement.executeQuery(String.format(getPostByIdSql, createdPostId));
        createdPostResultSet.next();
        assertEquals(createdPostResultSet.getString("post_title"), title);
        assertEquals(createdPostResultSet.getString("post_content"), content);
        assertEquals(createdPostResultSet.getString("post_date"), currentDateTime);
        assertEquals(createdPostResultSet.getString("post_date_gmt"), currentDateTimeGmt);
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
        String updateTitleSql = "UPDATE wp_posts SET post_title = '%s' WHERE ID = %d";

        createdPostId = createPost(title, content, currentDateTime, currentDateTimeGmt);

        assertEquals(statement.executeUpdate(String.format(updateTitleSql, newTitle, createdPostId)), 1);

        ResultSet createdPostResultSet = statement.executeQuery(String.format(getPostByIdSql, createdPostId));
        createdPostResultSet.next();
        assertEquals(createdPostResultSet.getString("post_title"), newTitle);
    }

    @Test
    @Story("Удаление поста")
    @Severity(SeverityLevel.NORMAL)
    public void deletePostTest() throws SQLException {
        String title = getGeneratedString(10);
        String content = getGeneratedString(25);
        String currentDateTime = getCurrentDateTime();
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        String deletePostSql = "DELETE FROM wp_posts WHERE ID = %d";

        createdPostId = createPost(title, content, currentDateTime, currentDateTimeGmt);

        assertEquals(statement.executeUpdate(String.format(deletePostSql, createdPostId)), 1);
        assertEquals(statement.executeUpdate(String.format(deletePostSql, createdPostId)), 0);
    }

    @Test
    @Story("Получение списка постов с поиском по заголовку")
    @Severity(SeverityLevel.NORMAL)
    public void getListPostsTest() throws SQLException {
        String searchTitle = "";
        String title;
        String content;
        String currentDateTime;
        String currentDateTimeGmt;
        String findPostsByTitleSql = "SELECT * FROM wp_posts WHERE post_title = '%s'";
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

        ResultSet foundPostsresultSet = statement.executeQuery(String.format(findPostsByTitleSql, searchTitle));
        while (foundPostsresultSet.next()) {
            assertEquals(foundPostsresultSet.getString("post_title"), searchTitle);
            foundPosts.add(foundPostsresultSet.getString("post_title"));
        }
        assertEquals(foundPosts.size(), 1);
    }

    @Test
    @Story("Попытка удаления поста с некорректным id")
    @Severity(SeverityLevel.NORMAL)
    public void attemptToDeletePostWithInvalidId() throws SQLException {
        int invalidId = getGeneratedInt(1234, 32341);
        String sql = String.format("DELETE FROM wp_posts WHERE ID = %d", invalidId);

        assertEquals(statement.executeUpdate(sql), 0);
    }

    @AfterMethod
    public void cleanUp() throws SQLException {
        statement.execute("DELETE FROM wp_posts WHERE post_status = 'draft'");
    }
}
