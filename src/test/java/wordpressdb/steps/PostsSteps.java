package wordpressdb.steps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static wordpressdb.utils.JDBCManager.getStatement;

public class PostsSteps {

    public static int createPost(String title, String content, String dateTime, String dateTimeGmt) {
        String createPostSql = String.format("INSERT INTO wp_posts " +
                        "(post_title, post_content, post_date, post_date_gmt, post_modified, post_modified_gmt, " +
                        "post_excerpt, post_status, to_ping, pinged, post_content_filtered) " +
                        "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '', 'draft', '', '', '');",
                title, content, dateTime, dateTimeGmt, dateTime, dateTimeGmt);
        String getCreatedPostByTitleSql = String.format("SELECT * FROM wp_posts WHERE post_title = '%s'", title);
        int createdPostId;

        try(Statement statement = getStatement()) {
            // create post
            statement.executeUpdate(createPostSql);

            // get created post id by title
            ResultSet postByTitleResultSet = statement.executeQuery(getCreatedPostByTitleSql);
            postByTitleResultSet.next();
            createdPostId = postByTitleResultSet.getInt("ID");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return createdPostId;
    }
}
