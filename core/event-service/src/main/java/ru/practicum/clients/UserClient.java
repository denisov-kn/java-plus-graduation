package ru.practicum.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.api.users.UserInternalApi;
import ru.practicum.dto.user.UserShortDto;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient extends UserInternalApi {

    @GetMapping("/iternal/users/{id}")
    UserShortDto getUserShortById(@PathVariable Long id);

    @GetMapping("/iternal/users")
    List<UserShortDto> getUsersShort(@RequestParam(required = false) List<Long> ids,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size);
}
