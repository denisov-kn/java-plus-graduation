package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.service.EventSimilarityProcessor;
import ru.practicum.service.UserActionProcessor;

@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {
    final EventSimilarityProcessor eventSimilarityProcessor;
    final UserActionProcessor userActionProcessor;


    @Override
    public void run(String... args) throws Exception {

        Thread eventSimilarityProcessorThread = new Thread(eventSimilarityProcessor);
        eventSimilarityProcessorThread.setName("event-similarity-processor");
        eventSimilarityProcessorThread.start();

        userActionProcessor.start();

    }
}
