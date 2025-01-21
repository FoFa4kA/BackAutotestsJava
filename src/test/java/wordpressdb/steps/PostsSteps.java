package wordpressdb.steps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static wordpressdb.utils.JDBCManager.getStatement;

public class PostsSteps {
    private static final Statement STATEMENT = getStatement();

    public static void createPost(String title, String content, String dateTime, String dateTimeGmt) throws SQLException {
        String createPostSql = String.format("INSERT INTO wp_posts " +
                        "(post_title, post_content, post_date, post_date_gmt, post_modified, post_modified_gmt, " +
                        "post_excerpt, post_status, to_ping, pinged, post_content_filtered) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '', 'draft', '', '', '');",
                title, content, dateTime, dateTimeGmt, dateTime, dateTimeGmt);

        STATEMENT.executeUpdate(createPostSql);
    }

    public static int getPostsCount() throws SQLException {
        String countPostsSql = "SELECT COUNT(ID) FROM wp_posts";

        ResultSet resultSet = STATEMENT.executeQuery(countPostsSql);
        resultSet.next();
        return resultSet.getInt(1);
    }

    public static ResultSet getPostsByTittle(String title) throws SQLException {
        String getPostByTitleSql = String.format("SELECT * FROM wp_posts WHERE post_title = '%s'", title);

        return STATEMENT.executeQuery(getPostByTitleSql);
    }

    public static void updatePostTitle(String currentTitle, String newTitle) throws SQLException {
        String updatePostTitleSql = String.format("UPDATE wp_posts SET post_title = '%s' WHERE post_title = '%s'",
                newTitle, currentTitle);

        STATEMENT.executeUpdate(updatePostTitleSql);
    }

    public static void deletePostByTitle(String title) throws SQLException {
        String deletePostByTitleSql = String.format("DELETE FROM wp_posts WHERE post_title = '%s'", title);

        STATEMENT.executeUpdate(deletePostByTitleSql);
    }

    public static void deletePostById(int id) throws SQLException {
        String deletePostByIdSql = String.format("DELETE FROM wp_posts WHERE ID = %d", id);

        STATEMENT.executeUpdate(deletePostByIdSql);
    }

    public static void cleanPosts() throws SQLException {
        STATEMENT.execute("DELETE FROM wp_posts WHERE post_status = 'draft'");
    }
}
