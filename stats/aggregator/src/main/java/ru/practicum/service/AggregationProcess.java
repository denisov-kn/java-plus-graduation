package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.utils.ActionWeights;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class AggregationProcess {

    // Map<Event, Map<User, Weight>> - матрица весов действий пользователей c мероприятиями/
    private final Map<Long, Map<Long, Double>> eventUserWeight = new HashMap<>();

    // Map<Event, Map<Event, Sum_weight> - суммы весов каждого из мероприятия
    private final Map<Long, Double> eventSumWeight = new HashMap<>();

    //Map<Event, Map<Event, S_min>> - сумма минимальных весов для каждой пары мероприятий
    private final Map<Long, Map<Long, Double>> eventSumMinWeight = new HashMap<>();


    public List<EventSimilarityAvro> updateSimilarity(UserActionAvro userActionAvro) {

        Long userId = userActionAvro.getUserId();
        Long eventId = userActionAvro.getEventId();
        ActionTypeAvro actionType = userActionAvro.getActionType();
        Double weight = ActionWeights.WEIGHTS.get(actionType);
        Instant timestamp = userActionAvro.getTimestamp();

        // Создаем новый перечень оценок для eventId (если не существует)
        Map<Long, Double> userWeight = eventUserWeight.computeIfAbsent(eventId, k -> new HashMap<>());

        // Подтягиваем текущую оценку
        Double currentWeight = userWeight.get(userId);

        // Обновляем только если текущая оценка меньше новой или её еще не было
        if (currentWeight == null || currentWeight < weight) {
            log.debug("updateSimilarity: current weight is {}, weight is {}", currentWeight, weight);


            userWeight.put(userId, weight); // обновляем новый вес
            eventSumWeightUpdate(eventId); // обновляем сумму весов для события
            // расчет сходства и подготовка сообщений
            List<EventSimilarityAvro> eventSimilarityAvroList = new ArrayList<>();
            eventSimilarityAvroList = eventSumMinWeightUpdate(eventId, userId, weight, currentWeight, timestamp);
            return eventSimilarityAvroList;


        } else {
            log.debug("updateSimilarity: No action - current weight is {} more than weight from - {}", currentWeight, weight);
            return List.of();
        }

    }

    // Метод по расчету сходства

    private List<EventSimilarityAvro> eventSumMinWeightUpdate(Long eventId,
                                                              Long userId,
                                                              Double newWeight,
                                                              Double oldWeight,
                                                              Instant timestamp) {

        List<EventSimilarityAvro> eventSimilarityAvroList = new ArrayList<>();

        for (Long otherEventId : eventUserWeight.keySet()) { // проходим по всем Event и находим все id
            if (otherEventId.equals(eventId)) continue;  // Исключаем id текущего  Event
            /*  Упорядочивание идентификаторов Event для исключения дублирования
                S_min(A, B) = S_min(B, A)
             */
            long minId = Math.min(otherEventId, eventId);
            long maxId = Math.max(otherEventId, eventId);
            Map<Long, Double> usersOther = eventUserWeight.get(otherEventId); // Оценки пользователей для второго Event
            // если внутри нет оценок для текущего пользователя - прерываем цикл для текущего Event
            if (!usersOther.containsKey(userId)) continue;

            // считаем только дельту весов, что-бы не пересчитывать всю сумму.
            double otherWeight = usersOther.get(userId); // вес для второго Event для текущего User
            // cnfhjt значение веса - до изменения. Обрабатываем кейс если старого веса нет (null) - для новых оценок
            double oldW = (oldWeight != null) ? oldWeight : 0.0;
            double oldMin = Math.min(oldW, otherWeight);  // минимум для старого веса
            double newMin = Math.min(newWeight, otherWeight); // минимум для нового веса
            double deltaMin = newMin - oldMin; // Считаем дельту - если оценка новая то будет - deltaMin = newMin - 0;


            if (deltaMin != 0) { //пересчитываем только если дельта = 0
                eventSumMinWeight
                        .computeIfAbsent(minId, k -> new HashMap<>())
                        .merge(maxId, deltaMin, Double::sum);
            }

            double sumMin = eventSumMinWeight.get(minId).get(maxId);
            double sumA = eventSumWeight.get(minId);
            double sumB = eventSumWeight.get(maxId);
            double score = sumMin / Math.sqrt(sumA * sumB);

            EventSimilarityAvro.Builder builder = EventSimilarityAvro.newBuilder()
                    .setEventA(minId)
                    .setEventB(maxId)
                    .setScore(score)
                    .setTimestamp(timestamp);

            EventSimilarityAvro eventSimilarityAvro = builder.build();
            eventSimilarityAvroList.add(eventSimilarityAvro);

            log.debug("eventSumMinWeightUpdate: Avro {}", eventSimilarityAvro);
        }

        return eventSimilarityAvroList;
    }

    // Метод по обновлению суммы весов для события

    private void eventSumWeightUpdate(Long eventId) {
        double sumWeight = eventUserWeight.get(eventId).values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();


        eventSumWeight.put(eventId, sumWeight);

        log.debug("eventSumWeightUpdate: eventId {}, sumWeight is {}", eventId, sumWeight);
    }

}
