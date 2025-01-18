package wordpressdb.steps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static wordpressdb.utils.JDBCManager.getStatement;

public class TagsSteps {

    public static int createTag(String name, String description) {
        int createdTagNameIdFromDb = 0;

        try(Statement statement = getStatement()) {
            // create tag name
            statement.executeUpdate(String.format("insert into wp_terms (name) values ('%s')", name));

            // create tag description
            ResultSet tagNameResultSet = statement.executeQuery(String.format("select * from wp_terms where name = '%s'",
                    name));
            while (tagNameResultSet.next()) {
                createdTagNameIdFromDb = tagNameResultSet.getInt("term_id");
            }
            statement.executeUpdate(String.format("insert into wp_term_taxonomy (term_id, taxonomy, description) " +
                    "values ('%d', 'post_tag', '%s')", createdTagNameIdFromDb, description));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return createdTagNameIdFromDb;
    }
}
