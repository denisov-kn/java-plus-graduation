package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.events.EventInternalApi;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.event.service.EventService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EventInternalController implements EventInternalApi {
    private final EventService eventService;

    @Override
    public EventFullDto getInternalEventById(Long id) {
        log.info("In getInternalEventById: id={}", id);
        EventFullDto eventFullDto = eventService.getInternalEventById(id);
        log.info("Out getInternalEventById: eventFullDto={}", eventFullDto);
        return eventFullDto;
    }

    @Override
    public EventFullDto getEventByInitiatorIdAndEventId(Long id, Long initiatorId) {
        log.info("In getEventByInitiatorIdAndEventId: id={}, initiatorId={}", id, initiatorId);
        EventFullDto eventFullDto = eventService.getEventByInitiatorIdAndEventId(id, initiatorId);
        log.info("Out getEventByInitiatorIdAndEventId: eventFullDto={}", eventFullDto);
        return eventFullDto;
    }

    @Override
    public EventFullDto updateEventById(Long id, Long requests) {
        log.info("In updateEventById: id={}, requests={}", id, requests);
        EventFullDto eventFullDto = eventService.updateEventById(id, requests);
        log.info("Out updateEventById: eventFullDto={}", eventFullDto);
        return eventFullDto;
    }
}



