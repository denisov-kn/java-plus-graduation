package ru.practicum.kafka.serialazer;

import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GeneralSerializer implements Serializer<SpecificRecordBase> {


    @Override
    public byte[] serialize(String s, SpecificRecordBase specificRecordBase) {
        if (specificRecordBase == null) {
            return null;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(specificRecordBase.getSchema());
            Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            writer.write(specificRecordBase, encoder);
            encoder.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error serializing Avro data", e);
        }

    }

}
