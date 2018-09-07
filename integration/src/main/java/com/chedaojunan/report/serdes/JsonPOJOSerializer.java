package com.chedaojunan.report.serdes;

import java.util.Map;

import org.apache.commons.lang3.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonPOJOSerializer<T> implements Serializer<T> {

  /*private final ObjectMapper objectMapper = new ObjectMapper();

  public com.chedaojunan.report.serdes.JsonPOJOSerializer() {
  }

  @Override
  public void configure(Map<String, ?> props, boolean isKey) {

  }

  @Override
  public byte[] serialize(String topic, T data) {
    if (data == null)
      return null;

    try {
      return objectMapper.writeValueAsBytes(data);
    } catch (Exception e) {
      throw new SerializationException("Error serializing JSON message", e);
    }
  }

  @Override
  public void close() {
  }*/

  private final ObjectMapper objectMapper = new ObjectMapper();

  private Class<T> tClass;

  public JsonPOJOSerializer() {

  }

  @SuppressWarnings("unchecked")
  @Override
  public void configure(Map<String, ?> props, boolean isKey) {
    tClass = (Class<T>) props.get("JsonPOJOClass");
  }

  @Override
  public byte[] serialize(String topic, T data) {
    if (data == null)
      return null;

    try {
      return objectMapper.writeValueAsBytes(data);
    } catch (Exception e) {
      throw new SerializationException("Error serializing JSON message", e);
    }
  }

  @Override
  public void close() {
  }

}
