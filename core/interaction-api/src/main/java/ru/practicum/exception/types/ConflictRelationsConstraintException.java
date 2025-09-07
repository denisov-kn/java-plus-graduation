package ru.practicum.exception.types;

public class ConflictRelationsConstraintException extends RuntimeException {
    public ConflictRelationsConstraintException(String message) {
        super(message);
    }
}
