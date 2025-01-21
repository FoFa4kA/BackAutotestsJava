package wordpressdb.steps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static wordpressdb.utils.JDBCManager.getStatement;

public class TagsSteps {
    private static final Statement STATEMENT = getStatement();

    public static void createTagName(String name) throws SQLException {
        String createTagNameSql = String.format("INSERT INTO wp_terms (name) VALUES ('%s')", name);

        STATEMENT.executeUpdate(createTagNameSql);
    }

    public static int getTagNameId(String name) throws SQLException {
        String getTagIdByNameSql = String.format("SELECT * FROM wp_terms WHERE name = '%s'", name);

        ResultSet resultSet = STATEMENT.executeQuery(getTagIdByNameSql);
        resultSet.next();
        return resultSet.getInt("term_id");
    }

    public static void createTagDescription(int tagId, String description) throws SQLException {
        String createTagDescriptionSql = String.format("INSERT INTO wp_term_taxonomy (term_id, taxonomy, description) " +
                "VALUES ('%d', 'post_tag', '%s')", tagId, description);

        STATEMENT.executeUpdate(createTagDescriptionSql);
    }

    public static int getTagsNamesCount() throws SQLException {
        String getTagsNamesCountSql = "SELECT COUNT(term_id) FROM wp_terms";

        ResultSet resultSet = STATEMENT.executeQuery(getTagsNamesCountSql);
        resultSet.next();
        return resultSet.getInt(1);
    }

    public static int getTagsDescriptionsCount() throws SQLException {
        String getTagsDescriptionsCountSql = "SELECT COUNT(term_taxonomy_id) FROM wp_term_taxonomy";

        ResultSet resultSet = STATEMENT.executeQuery(getTagsDescriptionsCountSql);
        resultSet.next();
        return resultSet.getInt(1);
    }

    public static ResultSet getTagById(int tagId) throws SQLException {
        String getTagByIdSql = String.format("SELECT wp_terms.name, tax.description FROM wp_terms " +
                "JOIN wp_term_taxonomy tax ON wp_terms.term_id = tax.term_id WHERE tax.term_id = %d", tagId);

        return STATEMENT.executeQuery(getTagByIdSql);
    }

    public static void updateTagDescription(int tagId, String newDescription) throws SQLException {
        String updateTagDescriptionByIdSql = String.format("UPDATE wp_term_taxonomy SET description = '%s' " +
                "WHERE term_id = '%d'", newDescription, tagId);

        STATEMENT.executeUpdate(updateTagDescriptionByIdSql);
    }

    public static void deleteTagNameById(int tagId) throws SQLException {
        String deleteTagNameByIdSql = String.format("DELETE FROM wp_terms WHERE term_id = %d", tagId);

        STATEMENT.executeUpdate(deleteTagNameByIdSql);
    }

    public static void deleteTagDescriptionById(int tagId) throws SQLException {
        String deleteTagDescriptionByIdSql = String.format("DELETE FROM wp_term_taxonomy WHERE term_id = %d", tagId);

        STATEMENT.executeUpdate(deleteTagDescriptionByIdSql);
    }

    public static ResultSet getListTags(int numberOfPage, int numberOfElementsPerPage) throws SQLException {
        String getListTagsWithLimitAndOffsetSql = String.format("SELECT wp_terms.term_id, wp_terms.name, tax.description " +
                        "FROM wp_terms JOIN wp_term_taxonomy tax ON wp_terms.term_id = tax.term_id " +
                        "WHERE tax.taxonomy = 'post_tag' LIMIT %d OFFSET %d",
                numberOfElementsPerPage, (numberOfPage - 1) * numberOfElementsPerPage);

        return STATEMENT.executeQuery(getListTagsWithLimitAndOffsetSql);
    }

    public static void cleanTagsNames() throws SQLException {
        STATEMENT.executeUpdate("DELETE FROM wp_terms WHERE slug = ''");
    }

    public static void cleanTagDescriptions() throws SQLException {
        STATEMENT.executeUpdate("DELETE FROM wp_term_taxonomy WHERE taxonomy = 'post_tag'");
    }
}
