package ru.practicum.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.utils.CollectorMapper;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    private final KafkaProducer<String, SpecificRecordBase> producer;

    @Value("${kafka.topics.user-actions}")
    private String topicUserActions;

    @Override
    public void kafkaUserAction(UserActionProto userActionProto) {
        log.info("UserActionProto received: {}", userActionProto);
        UserActionAvro avro = CollectorMapper.userActionToAvro(userActionProto);
        log.info("UserActionAvro: {}", avro);
        producer.send(new ProducerRecord<>(topicUserActions, avro));
    }
}
