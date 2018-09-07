package com.chedaojunan.report.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ExtensionParamEnum {
  BASE("base"),
  ALL("all");

  private static Map<String, ExtensionParamEnum> constants = new HashMap<>();

  static {
    for (ExtensionParamEnum c : values()) {
      constants.put(c.value, c);
    }
  }

  private String value;

  ExtensionParamEnum(String value) {
    this.value = value;
  }

  @JsonCreator
  public static ExtensionParamEnum fromValue(String value) {
    ExtensionParamEnum constant = constants.get(value);
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