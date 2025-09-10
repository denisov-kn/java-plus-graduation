package ru.practicum;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @GetMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        log.info("In getUserRequests: userid {}", userId);
        List<ParticipationRequestDto>  participationRequestDto = requestService.getUserRequests(userId);
        log.info("Out getUserRequests: participationRequestDto {}", participationRequestDto);
        return participationRequestDto;
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addUserRequest(@PathVariable Long userId,
                                                  @RequestParam Long eventId) {
        log.info("In addUserRequest: userid {}, eventId {}", userId, eventId);
        ParticipationRequestDto participationRequestDto = requestService.addUserRequest(userId, eventId);
        log.info("Out addUserRequest: participationRequestDto {}", participationRequestDto);
        return participationRequestDto;
    }


    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelUserRequest(@PathVariable Long userId,
                                  @PathVariable Long requestId) {
        log.info("In cancelUserRequest: userid {}, requestId {}", userId, requestId);
        ParticipationRequestDto participationRequestDto = requestService.cancelUserRequest(userId, requestId);
        log.info("Out cancelUserRequest: participationRequestDto {}", participationRequestDto);
        return participationRequestDto;
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @Valid @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("In changeRequestStatus: userid {}, eventId {}", userId, eventId);
        EventRequestStatusUpdateResult updateResult = requestService.changeRequestStatus(userId, eventId, request);
        log.info("Out changeRequestStatus: updateResult {}", updateResult);
        return updateResult;
    }


    @GetMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestByInitiator(@PathVariable Long userId,
                                                         @PathVariable Long eventId) {
        log.info("In getRequestByInitiator: userid {}, eventId {}", userId, eventId);
        List<ParticipationRequestDto>  participationRequestDto = requestService.getRequestByInitiator(userId, eventId);
        log.info("Out getRequestByInitiator: participationRequestDto {}", participationRequestDto);
        return participationRequestDto;
    }


}
