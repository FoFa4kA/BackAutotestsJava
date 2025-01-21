package wordpressdb.tests;

import io.qameta.allure.Epic;
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
import static wordpressdb.steps.PostsSteps.cleanPosts;
import static wordpressdb.steps.PostsSteps.createPost;
import static wordpressdb.steps.PostsSteps.deletePostById;
import static wordpressdb.steps.PostsSteps.deletePostByTitle;
import static wordpressdb.steps.PostsSteps.getPostsCount;
import static wordpressdb.steps.PostsSteps.getPostsByTittle;
import static wordpressdb.steps.PostsSteps.updatePostTitle;
import static wordpressdb.utils.CurrentDateTime.getCurrentDateTime;
import static wordpressdb.utils.CurrentDateTime.getCurrentDateTimeGmt;

@Epic("DB Tests")
@Feature("Posts Tests")
public class PostsTests {

    @Test
    @Story("Создание поста с заголовком и содержанием")
    @Severity(SeverityLevel.NORMAL)
    public void createPostTest() throws SQLException {
        String title = getGeneratedString(10);
        String content = getGeneratedString(25);
        String currentDateTime = getCurrentDateTime();
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        int postsCountBeforeCreatingPost = getPostsCount();

        createPost(title, content, currentDateTime, currentDateTimeGmt);
        assertEquals(getPostsCount(), postsCountBeforeCreatingPost + 1);
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

        ResultSet createdPostResultSet = getPostsByTittle(title);
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

        createPost(title, content, currentDateTime, currentDateTimeGmt);
        updatePostTitle(title, newTitle);

        ResultSet updatedPostResultSet = getPostsByTittle(newTitle);
        updatedPostResultSet.next();
        assertEquals(updatedPostResultSet.getString("post_title"), newTitle);
        assertEquals(updatedPostResultSet.getString("post_content"), content);
    }

    @Test
    @Story("Удаление поста")
    @Severity(SeverityLevel.NORMAL)
    public void deletePostTest() throws SQLException {
        String title = getGeneratedString(10);
        String content = getGeneratedString(25);
        String currentDateTime = getCurrentDateTime();
        String currentDateTimeGmt = getCurrentDateTimeGmt();
        int postsCountBeforeDeletingPost;

        createPost(title, content, currentDateTime, currentDateTimeGmt);
        postsCountBeforeDeletingPost = getPostsCount();
        deletePostByTitle(title);

        assertEquals(getPostsCount(), postsCountBeforeDeletingPost - 1);
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

        ResultSet foundPostsresultSet = getPostsByTittle(searchTitle);
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
        int postsCountBeforeDeletingPost = getPostsCount();

        deletePostById(invalidId);
        assertEquals(getPostsCount(), postsCountBeforeDeletingPost);
    }

    @AfterMethod
    public void cleanUp() throws SQLException {
        cleanPosts();
    }
}
