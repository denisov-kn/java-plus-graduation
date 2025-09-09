package ru.practicum.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.CommentService;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentControllerPrivate {

    private final CommentService commentService;

    @PostMapping("/users/{userId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable @Positive Long userId,
                                    @RequestParam @Positive Long eventId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {

        log.info("Creating new comment for user {} with id {}", userId, eventId);
        CommentDto comment = commentService.createComment(userId, eventId, newCommentDto);
        log.info("CommentDto created: {}", comment);
        return comment;

    }

    @PatchMapping("/users/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long commentId,
                                    @Valid @RequestBody UpdateCommentDto updateCommentDto) {

        log.info("Updating comment for user {} with id {}", userId, commentId);
        CommentDto comment = commentService.updateComment(userId, commentId, updateCommentDto);
        log.info("CommentDto updated: {}", comment);
        return comment;

    }

    @DeleteMapping("/users/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long userId,
                              @PathVariable @Positive Long commentId) {

        log.info("Deleting comment for user {} with id {}", userId, commentId);
        commentService.deleteCommentByUser(userId, commentId);
        log.info("CommentDto deleted: {}", commentId);

    }

    @GetMapping("/users/{userId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByUserId(@PathVariable @Positive Long userId) {

        log.info("Getting comments by user {}", userId);
        List<CommentDto> comments = commentService.getCommentsByUserId(userId);
        log.info("CommentDto list: {}", comments);
        return comments;

    }






}
