package ru.practicum.exception.types;

public class ConditionsNotMetException extends RuntimeException {

    public ConditionsNotMetException(String text) {
        super(text);
    }
}
