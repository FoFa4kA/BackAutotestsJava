package wordpressapi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    public static String getProp(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        try(InputStream inputStream = PropertiesUtil.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
