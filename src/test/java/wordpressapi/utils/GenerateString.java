package wordpressapi.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class GenerateString {

    public static String getGeneratedString(int length) {
        return RandomStringUtils.random(length, true, true);
    }
}
