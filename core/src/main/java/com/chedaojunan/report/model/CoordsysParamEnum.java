package com.chedaojunan.report.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum CoordsysParamEnum {
  GPS("gps"),
  MAPBAR("mapbar"),
  BAIDU("baidu"),
  AUTONAVI("autonavi"); // 不进行转换

  private static Map<String, CoordsysParamEnum> constants = new HashMap<>();

  static {
    for (CoordsysParamEnum c : values()) {
      constants.put(c.value, c);
    }
  }

  private String value;

  CoordsysParamEnum(String value) {
    this.value = value;
  }

  @JsonCreator
  public static CoordsysParamEnum fromValue(String value) {
    CoordsysParamEnum constant = constants.get(value);
    if (constant == null) {
      throw new IllegalArgumentException(value);
    } else {
      return constant;
    }
  }

  public static boolean isValid(String value) {
    return constants.get(value) != null;
  }

  @JsonValue
  @Override
  public String toString() {
    return this.value;
  }

}