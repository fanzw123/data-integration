package com.chedaojunan.report.utils;

import com.cdja.cloud.data.proto.GpsProto;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author fanzw
 * @description: 序列化类
 */
public class ProtoSerializer implements Serializer<GpsProto.Gps> {
  private String encoding = "UTF8";
  private static final Logger logger = LoggerFactory.getLogger(ProtoSerializer.class);

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
  public byte[] serialize(String topic, GpsProto.Gps data) {
    try {
      if (data == null)
        return null;
      else
        return ProtoFactory.createProtoClass(data).toByteArray();
    } catch (Exception e) {
      logger.error("gpsProto serialize error!!!");
    }
    return null;
  }

  @Override
  public void close() {
  }

}