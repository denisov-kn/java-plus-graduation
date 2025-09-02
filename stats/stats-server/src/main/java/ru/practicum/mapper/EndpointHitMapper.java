package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    EndpointHit toEntity(EndpointHitDto endpointHitDto);

    EndpointHitDto toDto(EndpointHit endpointHit);

}
