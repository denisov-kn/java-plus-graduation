package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.UserServiceImpl;
import ru.practicum.api.users.UserAdminApi;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserAdminController implements UserAdminApi {
    private final UserServiceImpl userService;

    @Override
    public UserDto postUser(UserCreateDto userCreateDto) {
        return userService.createUser(userCreateDto);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        return userService.getUsers(ids, from, size);
    }

    @Override
    public void deleteUserById(Long userId) {
        userService.deleteUserById(userId);
    }
}
