package ru.practicum;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;


public class RequestMapper {


    public static  ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequesterId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }

}
