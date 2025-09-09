package ru.practicum.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.RequestMapper;
import ru.practicum.RequestRepository;
import ru.practicum.clients.EventClient;
import ru.practicum.clients.UserClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.enums.State;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.enums.Status;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.exception.types.ConflictPropertyConstraintException;
import ru.practicum.exception.types.ConflictRelationsConstraintException;
import ru.practicum.exception.types.NotFoundException;
import ru.practicum.model.Request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserClient userClient;
    private final EventClient eventClient;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        findUser(userId);
        return requestRepository.findByRequesterId(userId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addUserRequest(Long userId, Long eventId) {
        UserShortDto userShortDto = findUser(userId);
        EventFullDto event = findEvent(eventId);
        requestRepository.findByRequesterIdAndEventId(userId, eventId).ifPresent(
                request -> {
                    throw new ConflictPropertyConstraintException("Нельзя добавить повторный запрос");
                }
        );

        if (event.getInitiator().getId().equals(
                userShortDto.getId())
        ) {
            throw new ConflictRelationsConstraintException(
                    "Инициатор события не может добавить запрос на участие в своём событии"
            );
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictRelationsConstraintException("Нельзя участвовать в неопубликованном событии");
        }

        if (event.getConfirmedRequests() == event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ConflictPropertyConstraintException("Достигнут лимит запросов на участие");
        }

        Status status = Status.PENDING;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            status = Status.CONFIRMED;
            addConfirmedRequestToEvent(event, 1);
        }

        Request request = Request.builder()
                .requesterId(userId)
                .status(status)
                .eventId(event.getId())
                .created(LocalDateTime.now().withNano(
                        (LocalDateTime.now().getNano() / 1_000_000) * 1_000_000
                        ))
                .build();

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }


    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        findUser(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос с id " + requestId + " не найден")
        );

        request.setStatus(Status.CANCELED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long initiatorId, Long eventId, EventRequestStatusUpdateRequest httpRequest) {
        findUser(initiatorId);
        EventFullDto event = findEvent(eventId);
        checkInitiatorEvent(initiatorId, eventId);

        List<Request> requests = requestRepository.findAllByIdIn(httpRequest.getRequestIds());

        if (event.getConfirmedRequests() == event.getParticipantLimit() && event.getParticipantLimit() != 0)
            throw new ConflictPropertyConstraintException("Достигнут лимит запросов на участие");

        List<Long> invalidRequestByEventIds = requests.stream()
                .filter(request -> !request.getEventId().equals(eventId))
                .map(Request::getId)
                .toList();

        if (!invalidRequestByEventIds.isEmpty())
            throw new ConflictPropertyConstraintException(
                    "Событие с id " + eventId + " не принадлежит запросам с id " + invalidRequestByEventIds
            );

        List<Long> invalidRequestByStatusIds = requests.stream()
                .filter(request -> request.getStatus() != Status.PENDING)
                .map(Request::getId)
                .toList();
        if (!invalidRequestByStatusIds.isEmpty())
            throw new ConflictPropertyConstraintException("Неверный статус у запросов c id " + invalidRequestByStatusIds);

        if (httpRequest.getStatus().equals(Status.REJECTED)) {
            List<Request> cancelRequests = requests.stream()
                    .peek(request -> request.setStatus(Status.REJECTED))
                    .toList();
            requestRepository.saveAll(cancelRequests);

            List<ParticipationRequestDto> requestDTOs = requests.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .toList();

            return EventRequestStatusUpdateResult.builder()
                    .confirmedRequests(List.of())
                    .rejectedRequests(requestDTOs)
                    .build();
        }

        long participantLimit = event.getParticipantLimit();
        long confirmedRequests = event.getConfirmedRequests();
        long requestsSize = requests.size();

        if (participantLimit - confirmedRequests < requestsSize)
            throw new ConflictPropertyConstraintException("Достигнут лимит запросов на участие");


        List<Request> confirmedRequestsList = requests.stream()
                .peek(request -> request.setStatus(Status.CONFIRMED))
                .toList();
        requestRepository.saveAll(confirmedRequestsList);

        addConfirmedRequestToEvent(event, requestsSize);


        List<ParticipationRequestDto> confirmedRequestDTOs = confirmedRequestsList.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();

        List<ParticipationRequestDto> canceledRequestDTOs = List.of();

        if (participantLimit - confirmedRequests == requestsSize) {
            List<Request> pendingRequests = requestRepository.findByEventIdAndStatus(eventId, Status.PENDING);
            List<Request> cancelRequests = pendingRequests.stream()
                    .peek(request -> request.setStatus(Status.REJECTED))
                    .toList();
            requestRepository.saveAll(cancelRequests);

            canceledRequestDTOs = cancelRequests.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .toList();
        }

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequestDTOs)
                .rejectedRequests(canceledRequestDTOs)
                .build();
    }

    @Override
    public List<ParticipationRequestDto> getRequestByInitiator(Long userId, Long eventId) {
        checkInitiatorEvent(userId, eventId);
        return requestRepository.findByEventId(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    private UserShortDto findUser(Long userId) {
            return userClient.getUserShortById(userId);
    }

    private EventFullDto findEvent(Long eventId) {
        return eventClient.getInternalEventById(eventId);
    }

    private void addConfirmedRequestToEvent(EventFullDto event, long add) {
        eventClient.updateEventById(event.getId(),event.getConfirmedRequests() + add);
    }

    private void checkInitiatorEvent(Long initiatorId, Long eventId) {
        eventClient.getEventByInitiatorIdAndEventId(eventId, initiatorId);
    }
}
