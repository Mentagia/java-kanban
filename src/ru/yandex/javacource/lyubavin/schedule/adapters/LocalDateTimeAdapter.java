package ru.yandex.javacource.lyubavin.schedule.adapters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;


public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter dtf =  DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDate) throws IOException {
        if (localDate != null) {
            jsonWriter.value(localDate.format(dtf));
        } else {
            jsonWriter.nullValue();
        }

    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), dtf);
    }
}