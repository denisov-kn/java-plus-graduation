package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.UserAction;
import ru.practicum.repository.UserActionRepository;
import ru.practicum.utils.ActionWeights;
import ru.practicum.utils.Mapper;

@Service
@RequiredArgsConstructor
public class UserActionService {
    private final UserActionRepository userActionRepository;

    public UserAction processUserAction(UserActionAvro userAction) {

        UserAction userActionFromBD = findUserActionByEventIdAndUserId(userAction.getEventId(), userAction.getUserId());
        UserAction userActionFromAvro = Mapper.actionFromAvro(userAction);

        if (userActionFromBD != null) {
            if (ActionWeights.WEIGHTS.get(userAction.getActionType()) >=
                    ActionWeights.WEIGHTS.get(userActionFromBD.getActionType())) {

                userActionFromBD.setActionType(userAction.getActionType());
                userActionFromBD.setTimestamp(userAction.getTimestamp());
                userActionFromBD = userActionRepository.save(userActionFromBD);

            }
        } else
            userActionFromBD = userActionRepository.save(userActionFromAvro);

        return userActionFromBD;
    }

    UserAction findUserActionByEventIdAndUserId(Long eventId,  Long userId) {
        return userActionRepository.findByEventIdAndUserId(eventId, userId);
    }



}
