package com.chedaojunan.report.utils;

import com.chedaojunan.report.model.FrequencyGpsData;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class ProtoSeder implements Serde<FrequencyGpsData.FrequencyGps> {

    private ProtoSerializer serializer = new ProtoSerializer();
    private ProtoDeserializer deserializer = new ProtoDeserializer();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }

    @Override
    public void close() {
        serializer.close();
        deserializer.close();
    }

    @Override
    public Serializer<FrequencyGpsData.FrequencyGps> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<FrequencyGpsData.FrequencyGps> deserializer() {
        return deserializer;
    }

}