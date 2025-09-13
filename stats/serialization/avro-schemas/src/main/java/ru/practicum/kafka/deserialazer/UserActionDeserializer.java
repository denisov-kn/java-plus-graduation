package ru.practicum.kafka.deserialazer;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ru.practicum.ewm.stats.avro.UserActionAvro;

public class UserActionDeserializer extends BaseAvroDeserializer<UserActionAvro> {
    public UserActionDeserializer() {
        super(UserActionAvro.getClassSchema());
    }
}
