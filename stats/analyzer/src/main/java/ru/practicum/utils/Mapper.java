package ru.practicum.utils;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.EventSimilarity;
import ru.practicum.model.UserAction;

public class Mapper {

    public static EventSimilarity similarityFromAvro(EventSimilarityAvro avro) {
       return EventSimilarity.builder()
                .score(avro.getScore())
                .eventA(avro.getEventA())
                .eventB(avro.getEventB())
                .timestamp(avro.getTimestamp())
                .build();

    }

    public static UserAction actionFromAvro(UserActionAvro avro) {
        return UserAction.builder()
                .actionType(avro.getActionType())
                .userId(avro.getUserId())
                .eventId(avro.getEventId())
                .timestamp(avro.getTimestamp())
                .build();
    }
}
