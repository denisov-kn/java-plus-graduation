package ru.practicum.service.grpc;

import ru.yandex.practicum.grpc.stats.action.UserActionProto;

public interface GrpcUserActionService {
    void processUserAction(UserActionProto userActionProto);
}
