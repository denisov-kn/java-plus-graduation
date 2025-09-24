package ru.practicum.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_actions", schema = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    Long eventId;

    @NotNull
    Long userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    ActionTypeAvro actionType;

    @NotNull
    Instant timestamp;
}
