package wordpressapi.utils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateDeserializer extends JsonDeserializer<LocalDateTime> {
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        return LocalDateTime.parse(jsonParser.getText(), formatter);
    }
}
