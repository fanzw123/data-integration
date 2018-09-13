package com.chedaojunan.report.utils;

import com.chedaojunan.report.model.FrequencyGpsData;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * @author fanzw
 * @description: 反序列化类
 */
public class ProtoDeserializer implements Deserializer {
  private String encoding = "UTF8";

  @Override
  public void configure(Map configs, boolean isKey) {
    String propertyName = isKey ? "key.deserializer.encoding" : "value.deserializer.encoding";
    Object encodingValue = configs.get(propertyName);
    if (encodingValue == null)
      encodingValue = configs.get("deserializer.encoding");
    if (encodingValue != null && encodingValue instanceof String)
      encoding = (String) encodingValue;
  }

  @Override
  public Object deserialize(String topic, byte[] data) {
    FrequencyGpsData.FrequencyGps frequencyGps = ProtoFactory.getFrequencyGps(data);
    try {
      if (data == null)
        return null;
      else if (frequencyGps != null)
        return frequencyGps;
      else
        return null;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void close() {
  }

}