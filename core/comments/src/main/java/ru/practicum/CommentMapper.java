package ru.practicum;

import ru.practicum.dto.comment.CommentDto;

public class CommentMapper {

    public static CommentDto commentToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(comment.getUser().getId())
                .event(comment.getEvent().getId())
                .build();
    }
}
