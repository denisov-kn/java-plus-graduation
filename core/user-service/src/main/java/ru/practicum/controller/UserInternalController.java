package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.service.UserService;
import ru.practicum.api.users.UserInternalApi;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserInternalController implements UserInternalApi {

    private final UserService userService;

    @Override
    public List<UserShortDto> getUsersShort(List<Long> ids, Integer from, Integer size) {

        log.info("In getUsersShort: {ids: {}, from: {}, size: {}", ids, from, size);
        List<UserShortDto> userShortDto = userService.getShortUsers(ids, from, size);
        log.info("Out getUsersShort: {}", userShortDto);
        return userShortDto;

    }

    @Override
    public UserShortDto getUserShortById(Long id) {
        log.info("In getUserShortById: {}", id);
        UserShortDto userShortDto = userService.getUserShortById(id);
        log.info("Out getUserShortById: {}", userShortDto);
        return userShortDto;
    }
}
