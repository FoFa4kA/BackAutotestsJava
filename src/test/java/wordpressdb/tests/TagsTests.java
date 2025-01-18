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
    private final String tagResultSetSql = "select wp_terms.name, tax.description from wp_terms " +
            "join wp_term_taxonomy tax on wp_terms.term_id = tax.term_id where tax.term_id = '%d'";

    @Test
    @Story("Создание тега")
    @Severity(SeverityLevel.MINOR)
    public void createTagTest() throws SQLException {
        String name = getGeneratedString(10);
        String description = getGeneratedString(25);
        int createdTagNameIdFromDb = 0;

        // create tag name
        String createTagName = String.format("insert into wp_terms (name) values ('%s')", name);
        assertEquals(statement.executeUpdate(createTagName), 1);

        // create tag description
        ResultSet tagNameResultSet = statement.executeQuery(String.format("select * from wp_terms where name = '%s'",
                name));
        while (tagNameResultSet.next()) {
            createdTagNameIdFromDb = tagNameResultSet.getInt("term_id");
        }
        String createTagDescription = String.format("insert into wp_term_taxonomy (term_id, taxonomy, description) " +
                "values ('%d', 'post_tag', '%s')", createdTagNameIdFromDb, description);
        assertEquals(statement.executeUpdate(createTagDescription), 1);

        // check created tag with description
        ResultSet createdTagResultSet = statement.executeQuery(String.format(tagResultSetSql, createdTagNameIdFromDb));
        while (createdTagResultSet.next()) {
            assertEquals(createdTagResultSet.getString("name"), name);
            assertEquals(createdTagResultSet.getString("description"), description);
        }
    }

    @Test
    @Story("Получение тега")
    @Severity(SeverityLevel.MINOR)
    public void retrieveTagTest() throws SQLException {
        String name = getGeneratedString(10);
        String description = getGeneratedString(25);
        int createdTagNameIdFromDb;

        createdTagNameIdFromDb = createTag(name, description);

        ResultSet resultSet = statement.executeQuery(String.format(tagResultSetSql, createdTagNameIdFromDb));
        while (resultSet.next()) {
            assertEquals(resultSet.getString("name"), name);
            assertEquals(resultSet.getString("description"), description);
        }
    }

    @Test
    @Story("Изменение описания тега")
    @Severity(SeverityLevel.MINOR)
    public void updateTagDescriptionTest() throws SQLException {
        String name = getGeneratedString(10);
        String description = getGeneratedString(25);
        int createdTagNameIdFromDb;
        String  newDescription = getGeneratedString(30);

        createdTagNameIdFromDb = createTag(name, description);

        String sql = String.format("update wp_term_taxonomy set description = '%s' where term_id = '%d'",
                newDescription, createdTagNameIdFromDb);
        assertEquals(statement.executeUpdate(sql), 1);

        ResultSet resultSet = statement.executeQuery(String.format(tagResultSetSql, createdTagNameIdFromDb));
        while (resultSet.next()) {
            assertEquals(resultSet.getString("description"), newDescription);
        }
    }

    @Test
    @Story("Удаление тега")
    @Severity(SeverityLevel.MINOR)
    public void deleteTagTest() throws SQLException {
        String name = getGeneratedString(10);
        String description = getGeneratedString(25);
        int createdTagNameIdFromDb;

        createdTagNameIdFromDb = createTag(name, description);

        // delete tag name
        String deleteTagNameSql = String.format("delete from wp_terms where term_id = '%d'", createdTagNameIdFromDb);
        assertEquals(statement.executeUpdate(deleteTagNameSql), 1);
        assertEquals(statement.executeUpdate(deleteTagNameSql), 0);

        // delete tag description
        String deleteTagDescriptionSql = String.format("delete from wp_term_taxonomy where term_id = '%d'",
                createdTagNameIdFromDb);
        assertEquals(statement.executeUpdate(deleteTagDescriptionSql), 1);
        assertEquals(statement.executeUpdate(deleteTagDescriptionSql), 0);
    }

    @Test
    @Story("Получение списка тегов с указанием страницы и кол-ом элементов на одной странице")
    @Severity(SeverityLevel.MINOR)
    public void getListTagsTest() throws SQLException {
        int numberOfPage = 3;
        int numberOfElementsPerPage = 2;
        List<Integer> createdTagIds = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String name = getGeneratedString(10);
            String description = getGeneratedString(25);
            createdTagIds.add(createTag(name, description));
        }

        String sql = String.format("select wp_terms.term_id, wp_terms.name, tax.description from wp_terms join wp_term_taxonomy tax " +
                "on wp_terms.term_id = tax.term_id where tax.taxonomy = 'post_tag' limit %d offset %d",
                numberOfElementsPerPage, (numberOfPage - 1) * numberOfElementsPerPage);

        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            assertEquals(resultSet.getInt("term_id"), createdTagIds.get(createdTagIds.size() - 1));
        }
    }

    @Test
    @Story("Попытка получения тега по некорректному id")
    @Severity(SeverityLevel.MINOR)
    public void attemptToGetTagWithInvalidId() throws SQLException {
        int invalidId = getGeneratedInt(3, 23452);

        ResultSet resultSet = statement.executeQuery(String.format(tagResultSetSql, invalidId));
        assertFalse(resultSet.next());
    }

    @AfterMethod
    public void cleanUp() throws SQLException {
        statement.executeUpdate("delete from wp_terms where slug = ''");
        statement.executeUpdate("delete from wp_term_taxonomy where taxonomy = 'post_tag'");
    }
}
