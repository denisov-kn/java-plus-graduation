package ru.practicum.api.users;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;


public interface UserInternalApi {

    @GetMapping("/iternal/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    UserShortDto getUserShortById(@PathVariable Long id);

    @GetMapping("/iternal/users")
    @ResponseStatus(HttpStatus.OK)
    List<UserShortDto> getUsersShort(@RequestParam(required = false) List<Long> ids,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size);
}
