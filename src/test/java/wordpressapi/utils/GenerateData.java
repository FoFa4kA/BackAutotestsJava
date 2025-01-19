package wordpressapi.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class GenerateData {

    public static String getGeneratedString(int length) {
        return RandomStringUtils.random(length, true, true);
    }

    public static int getGeneratedInt(int minValue, int maxValue) {
        return minValue + (int) (Math.random() * (maxValue - minValue + 1));
    }
}
