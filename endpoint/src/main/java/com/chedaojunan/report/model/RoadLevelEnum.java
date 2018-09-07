package com.chedaojunan.report.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoadLevelEnum {
  //PRICE("price"),
  //STATUS("status");
  GAO_SU_GONG_LU("高速公路"),//41000
  GUO_DAO("国道"), //42000
  CHENG_SHI_KUAI_SU_LU("主要大街、城市快速路"), //43000
  SHENG_DAO("省道"), //51000
  ZHU_YAO_DAO_LU("主要道路"), //44000
  CI_YAO_DAO_LU("次要道路"), //45000
  XIANG_GONG_LU("乡公路"), //52000
  XIAN_XIANG_CUN_NEI_BU_DAO_LU("县乡村内部道路"), //53000,54000
  PU_TONG_DAO_LU("普通道路"), //47000
  FEI_DAO_HANG_DAO_LU("非导航道路"); //49

  private static Map<String, RoadLevelEnum> constants = new HashMap<>();
  private static Map<String, RoadLevelEnum> roadLevelMap = new HashMap<>();

  static {
    for (RoadLevelEnum c : values()) {
      constants.put(c.value, c);
    }


    roadLevelMap.put("41000", RoadLevelEnum.GAO_SU_GONG_LU);
    roadLevelMap.put("42000", RoadLevelEnum.GUO_DAO);
    roadLevelMap.put("43000", RoadLevelEnum.CHENG_SHI_KUAI_SU_LU);
    roadLevelMap.put("51000", RoadLevelEnum.SHENG_DAO);
    roadLevelMap.put("44000", RoadLevelEnum.ZHU_YAO_DAO_LU);
    roadLevelMap.put("45000", RoadLevelEnum.CI_YAO_DAO_LU);
    roadLevelMap.put("52000", RoadLevelEnum.XIANG_GONG_LU);
    roadLevelMap.put("53000", RoadLevelEnum.XIAN_XIANG_CUN_NEI_BU_DAO_LU);
    roadLevelMap.put("54000", RoadLevelEnum.XIAN_XIANG_CUN_NEI_BU_DAO_LU);
    roadLevelMap.put("47000", RoadLevelEnum.PU_TONG_DAO_LU);
    roadLevelMap.put("49", RoadLevelEnum.FEI_DAO_HANG_DAO_LU);
  }

  private String value;

  RoadLevelEnum(String value) {
    this.value = value;
  }

  @JsonCreator
  public static RoadLevelEnum fromValue(String value) {
    RoadLevelEnum constant = constants.get(value);
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

  public static Map<String, RoadLevelEnum> getRoadLevelMap() {
    return roadLevelMap;
  }
}
