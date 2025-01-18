package wordpressdb.tests;

import io.qameta.allure.Epic;

import java.sql.Statement;

import static wordpressdb.utils.JDBCManager.getStatement;

@Epic("DB Tests")
public abstract class BaseTest {
    protected Statement statement = getStatement();
}
