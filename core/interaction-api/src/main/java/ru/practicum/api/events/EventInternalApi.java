package ru.practicum.api.events;


import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;


public interface EventInternalApi {

    @GetMapping("/internal/events/{id}")
    EventFullDto getInternalEventById(@PathVariable Long id);

    @GetMapping("/internal/events/{id}/by-initiator")
    EventFullDto getEventByInitiatorIdAndEventId(@PathVariable Long id, @RequestParam Long initiatorId);

    @PutMapping("/internal/events/{id}")
    EventFullDto updateEventById(@PathVariable Long id,
                                 @RequestParam Long requests);


}
