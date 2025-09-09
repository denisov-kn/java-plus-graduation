package ru.practicum.clients;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.api.events.EventInternalApi;

@FeignClient(name = "events")
public interface EventClient extends EventInternalApi {
}
