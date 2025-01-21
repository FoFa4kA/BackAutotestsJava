package wordpressdb.utils;

import org.testng.annotations.DataProvider;

import static wordpressapi.utils.PropertiesUtil.getProp;

public class UsersDataProvider {

    @DataProvider(name = "My user data provider")
    public Object[][] getMyUserData() {
        return new Object[][] {
                {getProp("username"), getProp("email")},
        };
    }
}
