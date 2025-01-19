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
import static org.testng.Assert.assertFalse;
import static wordpressapi.utils.GenerateData.getGeneratedInt;
import static wordpressapi.utils.GenerateData.getGeneratedString;
import static wordpressdb.steps.TagsSteps.createTag;

@Feature("Tags Tests")
public class TagsTests extends BaseTest {
    private final String getTagByIdSql = "SELECT wp_terms.name, tax.description FROM wp_terms " +
            "JOIN wp_term_taxonomy tax ON wp_terms.term_id = tax.term_id WHERE tax.term_id = %d";
    private int createdTagId;

    @Test
    @Story("Создание тега")
    @Severity(SeverityLevel.MINOR)
    public void createTagTest() throws SQLException {
        String name = getGeneratedString(10);
        String description = getGeneratedString(25);
        String createTagNameSql = String.format("INSERT INTO wp_terms (name) VALUES ('%s')", name);
        String getCreatedTagByNameSql = String.format("SELECT * FROM wp_terms WHERE name = '%s'", name);
        String createTagDescriptionSql = "INSERT INTO wp_term_taxonomy (term_id, taxonomy, description) " +
                "VALUES ('%d', 'post_tag', '%s')";

        // create tag name
        assertEquals(statement.executeUpdate(createTagNameSql), 1);

        // get tag id by name
        ResultSet tagByNameResultSet = statement.executeQuery(getCreatedTagByNameSql);
        tagByNameResultSet.next();
        createdTagId = tagByNameResultSet.getInt("term_id");

        // create tag description
        assertEquals(statement.executeUpdate(String.format(createTagDescriptionSql, createdTagId, description)),
                1);

        // check created tag by id
        ResultSet createdTagResultSet = statement.executeQuery(String.format(getTagByIdSql, createdTagId));
        createdTagResultSet.next();
        assertEquals(createdTagResultSet.getString("name"), name);
        assertEquals(createdTagResultSet.getString("description"), description);
    }

    @Test
    @Story("Получение тега")
    @Severity(SeverityLevel.MINOR)
    public void retrieveTagTest() throws SQLException {
        String name = getGeneratedString(10);
        String description = getGeneratedString(25);

        createdTagId = createTag(name, description);

        ResultSet createdTagResultSet = statement.executeQuery(String.format(getTagByIdSql, createdTagId));
        createdTagResultSet.next();
        assertEquals(createdTagResultSet.getString("name"), name);
        assertEquals(createdTagResultSet.getString("description"), description);
    }

    @Test
    @Story("Изменение описания тега")
    @Severity(SeverityLevel.MINOR)
    public void updateTagDescriptionTest() throws SQLException {
        String name = getGeneratedString(10);
        String description = getGeneratedString(25);
        String  newDescription = getGeneratedString(30);
        String updateTagDescriptionSql = "UPDATE wp_term_taxonomy SET description = '%s' WHERE term_id = '%d'";

        createdTagId = createTag(name, description);

        assertEquals(statement.executeUpdate(String.format(updateTagDescriptionSql, newDescription, createdTagId)),
                1);

        ResultSet createdTagResultSet = statement.executeQuery(String.format(getTagByIdSql, createdTagId));
        createdTagResultSet.next();
        assertEquals(createdTagResultSet.getString("description"), newDescription);
    }

    @Test
    @Story("Удаление тега")
    @Severity(SeverityLevel.MINOR)
    public void deleteTagTest() throws SQLException {
        String name = getGeneratedString(10);
        String description = getGeneratedString(25);
        String deleteTagNameSql = "DELETE FROM wp_terms WHERE term_id = '%d'";
        String deleteTagDescriptionSql = "DELETE FROM wp_term_taxonomy WHERE term_id = '%d'";

        createdTagId = createTag(name, description);

        // delete tag name
        assertEquals(statement.executeUpdate(String.format(deleteTagNameSql, createdTagId)), 1);
        assertEquals(statement.executeUpdate(String.format(deleteTagNameSql, createdTagId)), 0);

        // delete tag description
        assertEquals(statement.executeUpdate(String.format(deleteTagDescriptionSql, createdTagId)), 1);
        assertEquals(statement.executeUpdate(String.format(deleteTagDescriptionSql, createdTagId)), 0);
    }

    @Test
    @Story("Получение списка тегов с указанием страницы и кол-ом элементов на одной странице")
    @Severity(SeverityLevel.MINOR)
    public void getListTagsTest() throws SQLException {
        int numberOfPage = 3;
        int numberOfElementsPerPage = 2;
        List<Integer> createdTagsIds = new ArrayList<>();
        String getListTagsWithLimitAndOffsetSql = String.format("SELECT wp_terms.term_id, wp_terms.name, tax.description " +
                "FROM wp_terms JOIN wp_term_taxonomy tax ON wp_terms.term_id = tax.term_id " +
                "WHERE tax.taxonomy = 'post_tag' LIMIT %d OFFSET %d",
                numberOfElementsPerPage, (numberOfPage - 1) * numberOfElementsPerPage);

        for (int i = 0; i < 5; i++) {
            String name = getGeneratedString(10);
            String description = getGeneratedString(25);
            createdTagsIds.add(createTag(name, description));
        }

        ResultSet lastCreatedTagResultSet = statement.executeQuery(getListTagsWithLimitAndOffsetSql);
        lastCreatedTagResultSet.next();
        assertEquals(lastCreatedTagResultSet.getInt("term_id"), createdTagsIds.get(createdTagsIds.size() - 1));
    }

    @Test
    @Story("Попытка получения тега по некорректному id")
    @Severity(SeverityLevel.MINOR)
    public void attemptToGetTagWithInvalidId() throws SQLException {
        int invalidId = getGeneratedInt(12345, 23452);

        ResultSet resultSet = statement.executeQuery(String.format(getTagByIdSql, invalidId));
        assertFalse(resultSet.next());
    }

    @AfterMethod
    public void cleanUp() throws SQLException {
        statement.executeUpdate("DELETE FROM wp_terms WHERE slug = ''");
        statement.executeUpdate("DELETE FROM wp_term_taxonomy WHERE taxonomy = 'post_tag'");
    }
}
