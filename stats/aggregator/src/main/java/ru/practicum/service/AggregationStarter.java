package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final KafkaConsumer<String, SpecificRecordBase> consumer;

    private final AggregationProcess process;

    @Value("${kafka.topics.user-actions}")
    private String topicUserActions;

    @Value("${kafka.topics.events-similarity}")
    private String topicEventsSimilarity;

    public void start() {

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(topicUserActions));
            log.info("Aggregator subscribed to topic: {}", topicUserActions);

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {

                    if (!(record.value() instanceof UserActionAvro userActionAvro)) {
                        log.warn("Unexpected record type: {}", record.value().getClass().getSimpleName());
                        continue;
                    }

                    log.info("User action: {}", userActionAvro);

                    List<EventSimilarityAvro> eventSimilarity = process.updateSimilarity(userActionAvro);
                    eventSimilarity.forEach( similarity -> {
                                try {
                                    producer.send(new ProducerRecord<>(topicEventsSimilarity, similarity));
                                    log.info("Updated  similarity {} for event: {}", similarity, similarity.getEventA());
                                } catch (Exception e) {
                                    log.error("Failed to send snapshot to Kafka", e);
                                }
                            }
                    );

                    if (eventSimilarity.isEmpty()) {
                        log.info("No similarity to send");
                    }

                    consumer.commitAsync();

                }
            }
        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Failed to start aggregator", e);
        }
        finally {

            try {
                producer.flush();
                consumer.close();
            } finally {
                log.info("Consumer stopped");
                consumer.close();
                log.info("Producer closed");
                producer.close();
            }

        }

    }
}
