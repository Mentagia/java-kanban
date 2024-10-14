package ru.yandex.javacource.lyubavin.schedule.exceptions;

public class TaskValidationException extends RuntimeException {
    public TaskValidationException() {
    }

    public TaskValidationException(String message) {
        super(message);
    }

    public TaskValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskValidationException(Throwable cause) {
        super(cause);
    }
}