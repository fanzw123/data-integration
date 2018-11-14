package com.chedaojunan.report.utils;

import com.cdja.cloud.data.proto.GpsProto;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class ProtoSeder implements Serde<GpsProto.Gps> {

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
  public Serializer<GpsProto.Gps> serializer() {
    return serializer;
  }

  @Override
  public Deserializer<GpsProto.Gps> deserializer() {
    return deserializer;
  }

}