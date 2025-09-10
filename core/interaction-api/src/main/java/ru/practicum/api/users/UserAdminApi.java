package ru.practicum.api.users;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserAdminApi {

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    UserDto postUser(@RequestBody
                     @Valid
                     @NotNull
                     UserCreateDto userCreateDto);

    @GetMapping("/admin/users")
    @ResponseStatus(HttpStatus.OK)
    List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                           @RequestParam(defaultValue = "0") Integer from,
                           @RequestParam(defaultValue = "10") Integer size);

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUserById(@PathVariable Long userId);
}

