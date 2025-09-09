package ru.practicum.clients;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.users.UserInternalApi;

@FeignClient (name = "user-service")
public interface UserClient extends UserInternalApi {
}
