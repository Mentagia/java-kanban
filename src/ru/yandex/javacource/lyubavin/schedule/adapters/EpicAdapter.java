package ru.yandex.javacource.lyubavin.schedule.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;
import ru.yandex.javacource.lyubavin.schedule.task.Epic;


import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EpicAdapter extends TypeAdapter<Epic> {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter out, Epic value) throws IOException {
        out.beginObject();
        out.name("id").value(value.getId());
        out.name("taskName").value(value.getTaskName());
        out.name("taskDiscr").value(value.getTaskDiscr());
        out.name("taskStatus").value(value.getTaskStatus().toString());
        out.name("duration").value(value.getDuration().toMinutes());

        if (value.getStartTime() == null) {
            out.name("startTime").value("null");
        } else {
            out.name("startTime").value(value.getStartTime().format(dtf));
        }

        if (value.getEndTime() == null) {
            out.name("endTime").value("null");
        } else {
            out.name("endTime").value(value.getEndTime().format(dtf));
        }

        out.name("subtaskIds").beginArray();

        for (Integer id : value.getSubtaskIds()) {
            out.value(id);
        }

        out.endArray();
        out.endObject();
    }

    @Override
    public Epic read(JsonReader in) throws IOException {
        Integer id = null;
        String name = null;
        String description = null;
        TaskStatus status = null;
        LocalDateTime startTime = null;
        Duration duration = null;

        in.beginObject();
        while (in.hasNext()) {
            String field = in.nextName();

            switch (field) {
                case "id" -> {
                    try {
                        id = in.nextInt();
                    } catch (Exception ex) {
                        id = null;
                    }
                }
                case "taskName" -> name = in.nextString();
                case "taskDiscr" -> description = in.nextString();
                case "taskStatus" -> status = switch (in.nextString()) {
                    case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
                    case "DONE" -> TaskStatus.DONE;
                    default -> TaskStatus.NEW;
                };
                case "duration" -> {
                    try {
                        duration = Duration.ofMinutes(Integer.parseInt(in.nextString()));
                    } catch (DateTimeParseException ex) {
                        duration = Duration.ZERO;
                    }
                }
                case "startTime" -> {
                    try {
                        startTime = LocalDateTime.parse(in.nextString(), dtf);
                    } catch (DateTimeParseException ex) {
                        startTime = null;
                    }
                }
                default -> in.skipValue();
            }
        }
        in.endObject();

        if (id == null) {
            return new Epic(name, description, status, startTime, duration);
        } else {
            return new Epic(id, name, description, status, startTime, duration);
        }
    }
}