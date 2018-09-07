package com.chedaojunan.report.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ObjectMapperUtils {
  private static ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
    SimpleModule cardDeserializerModule = new SimpleModule();
    //cardDeserializerModule.addDeserializer(ActivityCard.class, new ActivityCardDeserializer<>(ActivityCard.class));
    //cardDeserializerModule.addDeserializer(EventCommon.class, new EventDeserializer<>(EventCommon.class));
    //objectMapper.registerModule(cardDeserializerModule);
    //objectMapper.registerModule(new JodaModule());
  }

  public static ObjectMapper getObjectMapper(){
    return objectMapper;
  }
}
