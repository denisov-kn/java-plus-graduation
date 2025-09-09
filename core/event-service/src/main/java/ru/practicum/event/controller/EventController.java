package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.events.EventApi;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdatedEventDto;
import ru.practicum.event.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventController implements EventApi {

    private final EventService eventService;

    @Override
    public EventFullDto saveEvent(NewEventDto newEventDto,
                                  Long userId,
                                  HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("Получен запрос на создание события от пользователя с ID {}, IP: {}", userId, ip);
        return eventService.saveEvent(newEventDto, userId, ip);
    }

    @Override
    public EventFullDto updateEventByIdAndUserId(UpdatedEventDto updatedEventDto,
                                                 Long userId,
                                                 Long eventId,
                                                 HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("PATCH /users/{}/events/{} from IP {}", userId, eventId, ip);
        return eventService.updateEvent(updatedEventDto, userId, eventId, ip);
    }

    @Override
    public EventFullDto updateAdminEventByIdAndUserId(UpdatedEventDto updatedEventDto,
                                                      Long eventId,
                                                      HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("PATCH /admin/events/{} from IP {}", eventId, ip);
        return eventService.updateAdminEvent(updatedEventDto, eventId, ip);
    }

    @Override
    public EventFullDto getEventById(Long id, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("GET /events/{} from IP {}", id, ip);
        return eventService.getEventById(id, ip);
    }

    @Override
    public List<EventFullDto> getEvents(String text,
                                        List<Long> categories,
                                        Boolean paid,
                                        Boolean onlyAvailable,
                                        String rangeStart,
                                        String rangeEnd,
                                        String sort,
                                        Integer from,
                                        Integer size,
                                        HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("GET /events from IP {}, params: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, sort={}, from={}, size={}",
                ip, text, categories, paid, rangeStart, rangeEnd, sort, from, size);
        return eventService.getEvents(text, categories, paid,
                rangeStart, rangeEnd,
                onlyAvailable, sort, from, size,
                ip, "user");
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId,
                                                 HttpServletRequest request,
                                                 Integer from,
                                                 Integer size) {
        String ip = request.getRemoteAddr();
        log.info("GET /users/{}/events from IP {}, from={}, size={}", userId, ip, from, size);
        return eventService.getEventsByUserId(userId, from, size, ip);
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId,
                                                   Long eventId,
                                                   HttpServletRequest request,
                                                   Integer from,
                                                   Integer size) {
        String ip = request.getRemoteAddr();
        log.info("GET /users/{}/events/{} from IP {}, from={}, size={}", userId, eventId, ip, from, size);
        return eventService.getEventByUserIdAndEventId(userId, eventId, from, size, ip);
    }

    @Override
    public List<EventFullDto> getAdminEvents(String text,
                                             List<Long> categories,
                                             Boolean paid,
                                             Boolean onlyAvailable,
                                             String rangeStart,
                                             String rangeEnd,
                                             String sort,
                                             Integer from,
                                             Integer size,
                                             HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("GET /admin/events from IP {}, params: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, sort={}, from={}, size={}",
                ip, text, categories, paid, rangeStart, rangeEnd, sort, from, size);
        return eventService.getEvents(text, categories, paid,
                rangeStart, rangeEnd,
                onlyAvailable, sort, from, size,
                ip, "admin");
    }
}