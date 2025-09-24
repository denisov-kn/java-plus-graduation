package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarity;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.utils.Mapper;

@Service
@RequiredArgsConstructor
public class EventSimilarityService {
    private final EventSimilarityRepository eventSimilarityRepository;

    public EventSimilarity processEventSimilarity(EventSimilarityAvro eventSimilarityAvro) {

        EventSimilarity eventSimilarity = Mapper.similarityFromAvro(eventSimilarityAvro);
        eventSimilarityRepository.save(eventSimilarity);
        return Mapper.similarityFromAvro(eventSimilarityAvro);
    }


}
