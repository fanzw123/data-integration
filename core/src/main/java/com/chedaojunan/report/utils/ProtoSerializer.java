package com.chedaojunan.report.utils;

import com.chedaojunan.report.model.FrequencyGpsData;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * @author fanzw
 * @description: 序列化类
 */
public class ProtoSerializer implements Serializer<FrequencyGpsData.FrequencyGps> {
  private String encoding = "UTF8";

  @Override
  public void configure(Map configs, boolean isKey) {
    String propertyName = isKey ? "key.serializer.encoding" : "value.serializer.encoding";
    Object encodingValue = configs.get(propertyName);
    if (encodingValue == null)
      encodingValue = configs.get("serializer.encoding");
    if (encodingValue != null && encodingValue instanceof String)
      encoding = (String) encodingValue;
  }

  @Override
  public byte[] serialize(String topic, FrequencyGpsData.FrequencyGps data) {
    try {
      if (data == null)
        return null;
      else
        return ProtoFactory.createProtoClass(data).toByteArray();
    } catch (Exception e) {
      //throw new SerializationException("Error when serializing string to byte[] due to unsupported encoding " + encoding);
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void close() {
  }

}