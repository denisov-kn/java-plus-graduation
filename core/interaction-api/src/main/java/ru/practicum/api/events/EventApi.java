package ru.practicum.api.events;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdatedEventDto;

import java.util.List;

public interface EventApi {
    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto saveEvent(@Valid @RequestBody NewEventDto newEventDto,
                                  @PathVariable(name = "userId") Long userId,
                                  HttpServletRequest request);

    @PatchMapping("/users/{userId}/events/{eventId}")
    EventFullDto updateEventByIdAndUserId(@RequestBody @Valid UpdatedEventDto updatedEventDto,
                                                 @PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 HttpServletRequest request);

    @PatchMapping("/admin/events/{eventId}")
    EventFullDto updateAdminEventByIdAndUserId(@RequestBody @Valid UpdatedEventDto updatedEventDto,
                                                      @PathVariable Long eventId,
                                                      HttpServletRequest request);

    @GetMapping("/events/{id}")
    EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request);

    @GetMapping("/events")
    List<EventFullDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request
    );

    @GetMapping("/users/{userId}/events")
    List<EventShortDto> getEventsByUserId(@PathVariable Long userId, HttpServletRequest request,
                                                 @RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size);

    @GetMapping("/users/{userId}/events/{eventId}")
    EventFullDto getEventByUserIdAndEventId(@PathVariable Long userId,
                                                   @PathVariable Long eventId,
                                                   HttpServletRequest request,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size);

    @GetMapping("/admin/events")
    List<EventFullDto> getAdminEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest request
    );
}
