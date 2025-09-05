package ru.practicum.event;


import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.Location;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.user.UserShortDto;

public class EventDtoMapper {

    public static Event mapToModel(NewEventDto dto, long userId) {
        return Event.builder()
                .initiatorId(userId)
                .annotation(dto.getAnnotation())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .locationLat(dto.getLocation().getLat())
                .locationLon(dto.getLocation().getLon())
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .build();
    }

    public static EventShortDto mapToShortDto(Event event, CategoryDto categoryDto, UserShortDto userShortDto) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests((int) event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id((int) event.getId())
                .initiator(userShortDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventFullDto mapToFullDto(Event event, CategoryDto categoryDto, UserShortDto userShortDto) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests((int) event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(userShortDto)
                .location(Location.builder()
                        .lat(event.getLocationLat())
                        .lon(event.getLocationLon())
                        .build())
                .paid(event.getPaid())
                .participantLimit((int) event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}
