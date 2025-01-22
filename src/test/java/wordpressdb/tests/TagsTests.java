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
import static org.testng.Assert.assertFalse;
import static wordpressapi.utils.GenerateData.getGeneratedInt;
import static wordpressapi.utils.GenerateData.getGeneratedString;
import static wordpressdb.steps.TagsSteps.cleanTagDescriptions;
import static wordpressdb.steps.TagsSteps.cleanTagsNames;
import static wordpressdb.steps.TagsSteps.createTagDescription;
import static wordpressdb.steps.TagsSteps.createTagName;
import static wordpressdb.steps.TagsSteps.deleteTagDescriptionById;
import static wordpressdb.steps.TagsSteps.deleteTagNameById;
import static wordpressdb.steps.TagsSteps.getListTags;
import static wordpressdb.steps.TagsSteps.getTagById;
import static wordpressdb.steps.TagsSteps.getTagNameId;
import static wordpressdb.steps.TagsSteps.getTagsDescriptionsCount;
import static wordpressdb.steps.TagsSteps.getTagsNamesCount;
import static wordpressdb.steps.TagsSteps.updateTagDescription;

@Epic("DB Tests")
@Feature("Tags Tests")
public class TagsTests {

    @Test
    @Story("Создание тега")
    @Severity(SeverityLevel.MINOR)
    public void createTagTest() throws SQLException {
        String name = getGeneratedString(10);
        String description = getGeneratedString(25);
        int tagNameId;
        int tagsNamesCountBeforeCreatingTag = getTagsNamesCount();
        int tagsDescriptionsCountBeforeCreatingTag = getTagsDescriptionsCount();

        createTagName(name);
        tagNameId = getTagNameId(name);
        createTagDescription(tagNameId, description);

        assertEquals(getTagsNamesCount(), tagsNamesCountBeforeCreatingTag + 1);
        assertEquals(getTagsDescriptionsCount(), tagsDescriptionsCountBeforeCreatingTag + 1);
    }

    @Test
    @Story("Получение тега")
    @Severity(SeverityLevel.MINOR)
    public void retrieveTagTest() throws SQLException {
        String name = getGeneratedString(10);
        String description = getGeneratedString(25);
        int tagId;

        createTagName(name);
        tagId = getTagNameId(name);
        createTagDescription(tagId, description);

        ResultSet createdTagResultSet = getTagById(tagId);
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
        String newDescription = getGeneratedString(30);
        int tagId;

        createTagName(name);
        tagId = getTagNameId(name);
        createTagDescription(tagId, description);
        updateTagDescription(tagId, newDescription);

        ResultSet updatedTagResultSet = getTagById(tagId);
        updatedTagResultSet.next();
        assertEquals(updatedTagResultSet.getString("description"), newDescription);
    }

    @Test
    @Story("Удаление тега")
    @Severity(SeverityLevel.MINOR)
    public void deleteTagTest() throws SQLException {
        String name = getGeneratedString(10);
        String description = getGeneratedString(25);
        int tagId;
        int tagsNamesCountBeforeDeletingTag;
        int tagsDescriptionsCountBeforeDeletingTag;

        createTagName(name);
        tagId = getTagNameId(name);
        createTagDescription(tagId, description);
        tagsNamesCountBeforeDeletingTag = getTagsNamesCount();
        tagsDescriptionsCountBeforeDeletingTag = getTagsDescriptionsCount();
        deleteTagNameById(tagId);
        deleteTagDescriptionById(tagId);

        assertEquals(getTagsNamesCount(), tagsNamesCountBeforeDeletingTag - 1);
        assertEquals(getTagsDescriptionsCount(), tagsDescriptionsCountBeforeDeletingTag - 1);
    }

    @Test
    @Story("Получение списка тегов с указанием страницы и кол-ом элементов на одной странице")
    @Severity(SeverityLevel.MINOR)
    public void getListTagsTest() throws SQLException {
        int numberOfPage = 3;
        int numberOfElementsPerPage = 2;
        List<Integer> createdTagsIds = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String name = getGeneratedString(10);
            String description = getGeneratedString(25);
            int tagId;

            createTagName(name);
            tagId = getTagNameId(name);
            createTagDescription(tagId, description);
            createdTagsIds.add(tagId);
        }

        ResultSet lastCreatedTagResultSet = getListTags(numberOfPage, numberOfElementsPerPage);
        lastCreatedTagResultSet.next();
        assertEquals(lastCreatedTagResultSet.getInt("term_id"), createdTagsIds.get(createdTagsIds.size() - 1));
    }

    @Test
    @Story("Попытка получения тега по некорректному id")
    @Severity(SeverityLevel.MINOR)
    public void attemptToGetTagWithInvalidId() throws SQLException {
        int invalidId = getGeneratedInt(12345, 23452);

        ResultSet tagByInvalidIdResultSet = getTagById(invalidId);
        assertFalse(tagByInvalidIdResultSet.next());
    }

    @AfterMethod
    public void cleanUp() throws SQLException {
        cleanTagsNames();
        cleanTagDescriptions();
    }
}
