package ru.practicum.exception.types;

public class ConflictPropertyConstraintException extends RuntimeException {
    public ConflictPropertyConstraintException(String message) {
        super(message);
    }
}
