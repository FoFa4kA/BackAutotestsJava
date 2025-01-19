package wordpressdb.steps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static wordpressdb.utils.JDBCManager.getStatement;

public class TagsSteps {

    public static int createTag(String name, String description) {
        String createTagNameSql = String.format("INSERT INTO wp_terms (name) VALUES ('%s')", name);
        String getCreatedTagByNameSql = String.format("SELECT * FROM wp_terms WHERE name = '%s'", name);
        int createdTagNameId = 0;
        String createTagDescriptionSql = "INSERT INTO wp_term_taxonomy (term_id, taxonomy, description) " +
                "VALUES ('%d', 'post_tag', '%s')";

        try(Statement statement = getStatement()) {
            // create tag name
            statement.executeUpdate(createTagNameSql);

            // get tag id by name
            ResultSet tagByNameResultSet = statement.executeQuery(getCreatedTagByNameSql);
            tagByNameResultSet.next();
            createdTagNameId = tagByNameResultSet.getInt("term_id");

            // create tag description
            statement.executeUpdate(String.format(createTagDescriptionSql, createdTagNameId, description));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return createdTagNameId;
    }
}
