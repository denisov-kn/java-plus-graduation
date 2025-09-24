package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.service.AggregationStarter;

@Component
@RequiredArgsConstructor
public class AggregatorRunner implements CommandLineRunner {

    final AggregationStarter aggregationStarter;

    @Override
    public void run(String... args) throws Exception {
        aggregationStarter.start();
    }
}
