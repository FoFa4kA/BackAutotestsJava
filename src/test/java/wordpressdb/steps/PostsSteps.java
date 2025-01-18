package wordpressdb.steps;

import java.sql.SQLException;
import java.sql.Statement;

import static wordpressdb.utils.JDBCManager.getStatement;

public class PostsSteps {

    public static void createPost(String title, String content, String dateTime, String dateTimeGmt) {
        String sql = String.format("insert into wp_posts " +
                        "(post_title, post_content, post_date, post_date_gmt, post_modified, post_modified_gmt, " +
                        "post_excerpt, post_status, to_ping, pinged, post_content_filtered) " +
                        "values ('%s', '%s', '%s', '%s', '%s', '%s', '', 'draft', '', '', '');",
                title, content, dateTime, dateTimeGmt, dateTime, dateTimeGmt);

        try(Statement statement = getStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
