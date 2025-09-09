package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.CommentService;
import ru.practicum.dto.comment.DeleteCommentsDto;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentControllerAdmin {

    private final CommentService commentService;

    @DeleteMapping("/admin/comments")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentsByAdmin(@Valid @RequestBody DeleteCommentsDto deleteCommentsDto) {
        log.info("In deleteCommentsByAdmin deleteCommentsDto:{}", deleteCommentsDto);
        commentService.deleteCommentsByAdmin(deleteCommentsDto);
        log.info("Out deleteCommentsByAdmin is successes");
    }
}
