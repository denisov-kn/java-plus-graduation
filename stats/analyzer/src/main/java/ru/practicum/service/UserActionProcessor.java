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
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.UserAction;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class UserActionProcessor {

    @Autowired
    @Qualifier("kafkaConsumerUserAction")
    private KafkaConsumer<String, SpecificRecordBase> consumer;

    @Value("${kafka.topics.user-action}")
    private String userActionTopic;

    @Autowired
    UserActionService userActionService;


    public void start() {

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            log.info("UserActionProcessor subscribed to topic {}", userActionTopic);
            consumer.subscribe(List.of(userActionTopic));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    if(!(record.value() instanceof UserActionAvro userActionAvro)) {
                        log.warn("Unexpected record type: {}", record.value().getClass().getSimpleName());
                        continue;
                    }

                    UserAction userAction = userActionService.processUserAction(userActionAvro);
                    log.info("UserAction: {}", userAction);
                }

                consumer.commitAsync();
            }
        } catch (WakeupException ignore) {

        }
        catch (Exception e) {
            log.error("Ошибка во время обработки сообщений активности пользователей", e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }
}
