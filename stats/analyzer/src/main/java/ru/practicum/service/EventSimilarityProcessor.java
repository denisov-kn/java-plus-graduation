package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarity;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class EventSimilarityProcessor implements Runnable {

    @Autowired
    @Qualifier("kafkaConsumerEventSimilarity")
    private KafkaConsumer<String, SpecificRecordBase> consumer;


    @Autowired
    private EventSimilarityService eventSimilarityService;

    @Value("${kafka.topics.event-similarity}")
    private String eventSimilarityTopic;


    @Override
    public void run() {

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            log.info("EventSimilarityProcessor subscribed to topic {}", eventSimilarityTopic);
            consumer.subscribe(List.of(eventSimilarityTopic));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(100));

                for(ConsumerRecord<String, SpecificRecordBase> record : records) {

                    if(!(record.value() instanceof EventSimilarityAvro eventSimilarityAvro)) {
                        log.warn("Unexpected record type: {}", record.value().getClass().getSimpleName());
                        continue;
                    }

                    EventSimilarity eventSimilarity = eventSimilarityService.processEventSimilarity(eventSimilarityAvro);
                    log.info("EventSimilarity save: {}", eventSimilarity);
                }

                consumer.commitSync();
            }
        } catch (WakeupException ignore) {

        } catch (Exception e) {
            log.error("Ошибка во время обработки сообщений схожести событий", e);
        }
        finally {

            try {
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }

    }
}
