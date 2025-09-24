package ru.practicum;


import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.model.EventSimilarity;
import ru.practicum.model.UserAction;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserActionRepository;
import ru.practicum.utils.ActionWeights;
import ru.yandex.practicum.grpc.stats.analyzer.RecommendationsControllerGrpc;
import ru.yandex.practicum.grpc.stats.recommendation.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.stats.recommendation.RecommendedEventProto;
import ru.yandex.practicum.grpc.stats.recommendation.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.stats.recommendation.UserPredictionsRequestProto;

import java.util.*;
import java.util.stream.Collectors;


@GrpcService
@RequiredArgsConstructor
public class RecommendationController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final EventSimilarityRepository eventSimilarityRepository;
    private final UserActionRepository actionRepository;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        Long userId = request.getUserId();
        Long maxResult = request.getMaxResults();

        List<EventSimilarity> newEvents = findNewEventsForUser(userId, maxResult);


        //  Если пользователь ещё не взаимодействовал ни с одним мероприятием,
        //  то рекомендовать нечего — возвращается пустой список.
        if(newEvents.isEmpty()) {
            responseObserver.onCompleted();
            return;
        }

        List<RecommendedEventProto> recommendations = calculateScores(userId, newEvents, maxResult);

        recommendations.forEach(responseObserver::onNext);
        responseObserver.onCompleted();


    }

    //Подбор мероприятий, с которыми пользователь ещё не взаимодействовал.

    private List<EventSimilarity> findNewEventsForUser(Long userId, Long maxresult) {

        //Получить недавно просмотренные. Выгрузить мероприятия, с которыми пользователь уже взаимодействовал.
        // При этом отсортировать их по дате взаимодействия от новых к старым и ограничить N взаимодействиями.


        List<Long> recentEventIds = actionRepository.findAllByUserId(userId).stream()
                .sorted(Comparator.comparing(UserAction::getTimestamp).reversed())
                .limit(maxresult)
                .map(UserAction::getEventId)
                .toList();


        // Все мероприятия пользователя
        List<Long> allUserEventIds = actionRepository.findAllByUserId(userId).stream()
                .map(UserAction::getEventId)
                .toList();

        // Найти похожие новые. Найти мероприятия, похожие на те, что отобрали на предыдущем этапе,
        // но при этом пользователь с ними не взаимодействовал.
        //Выбрать N самых похожих. Отсортировать найденные мероприятия по коэффициенту подобия от большего к меньшему.
        // Выбрать из них первые N мероприятий.

        return eventSimilarityRepository
                .findAllByEventIdsAndExcludedIds(recentEventIds, allUserEventIds)
                .stream()
                .sorted(Comparator.comparingDouble(EventSimilarity::getScore).reversed())
                .limit(maxresult)
                .toList();
    }

    private List<RecommendedEventProto> calculateScores(Long userId, List<EventSimilarity> candidates, Long maxResults) {
        // вытаскиваем все действия пользователя (нужны "оценки" для формулы)
        List<UserAction> userActions = actionRepository.findAllByUserId(userId);

        Map<Long, Double> eventRatings = userActions.stream()
                .collect(Collectors.groupingBy(
                        UserAction::getEventId,
                        Collectors.summingDouble(action ->
                                ActionWeights.WEIGHTS.getOrDefault(action.getActionType(), 0.0))
                ));

        return candidates.stream()
                .map(candidate -> {

                    // найти K соседей (связанные с candidate события, на которые у юзера есть рейтинг)
                    List<EventSimilarity> neighbors = eventSimilarityRepository
                            .findAllByEventA(candidate.getEventA());

                    // берем только те, с которыми юзер взаимодействовал
                    List<EventSimilarity> ratedNeighbors = neighbors.stream()
                            .filter(n -> eventRatings.containsKey(
                                    Objects.equals(n.getEventA(), candidate.getEventB()) ? n.getEventB() : n.getEventA()
                            ))
                            .toList();

                    double weightedSum = 0.0;
                    double similaritySum = 0.0;

                    for (EventSimilarity neighbor : ratedNeighbors) {
                        Long neighborId = Objects.equals(neighbor.getEventA(), candidate.getEventB())
                                ? neighbor.getEventB()
                                : neighbor.getEventA();

                        double rating = eventRatings.getOrDefault(neighborId, 0.0);
                        double sim = neighbor.getScore();

                        weightedSum += rating * sim;
                        similaritySum += sim;
                    }

                    double finalScore = similaritySum > 0 ? weightedSum / similaritySum : 0.0;

                    return RecommendedEventProto.newBuilder()
                            .setEventId(candidate.getEventB()) // новое событие
                            .setScore(finalScore)
                            .build();
                })
                .sorted(Comparator.comparingDouble(RecommendedEventProto::getScore).reversed())
                .limit(maxResults)
                .toList();
    }



    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {

        Long userId = request.getUserId();
        Long maxResult = request.getMaxResults();
        Long eventId = request.getEventId();


        List<UserAction> userActions = actionRepository.findAllByUserId(userId);
        List<Long> userEventIds = userActions.stream()
                .map(UserAction::getEventId)
                .toList();

        eventSimilarityRepository.findAllByEventIdAndEventIds(eventId, userEventIds).stream()
                .sorted(Comparator.comparingDouble(EventSimilarity::getScore).reversed())
                .limit(maxResult)
                .forEach(similarity -> {
                    RecommendedEventProto proto = RecommendedEventProto.newBuilder()
                            .setEventId(similarity.getEventA())
                            .setScore(similarity.getScore())
                            .build();
                    responseObserver.onNext(proto);
                });

        responseObserver.onCompleted();

    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        List<Long> eventIds = request.getEventIdList();
        actionRepository.findAllByEventIdIn(eventIds).stream()
                .collect(Collectors.groupingBy(
                        UserAction::getEventId,
                        Collectors.summingDouble(action ->
                                ActionWeights.WEIGHTS.getOrDefault(action.getActionType(), 0.0)
                        )
                ))
                .entrySet().stream()
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build()
                )
                .forEach(responseObserver::onNext);

        responseObserver.onCompleted();

    }
}