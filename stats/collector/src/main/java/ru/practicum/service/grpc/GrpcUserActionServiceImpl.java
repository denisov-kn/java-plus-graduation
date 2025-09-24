package ru.practicum.service.grpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.service.kafka.KafkaService;
import ru.yandex.practicum.grpc.stats.action.UserActionProto;

@Service
@Slf4j
@RequiredArgsConstructor
public class GrpcUserActionServiceImpl implements GrpcUserActionService {

    private final KafkaService kafkaService;

    @Override
    public void processUserAction(UserActionProto userActionProto) {

        log.info("Processing UserActionProto: {}", userActionProto);
        kafkaService.kafkaUserAction(userActionProto);
    }
}
