package com.chedaojunan.report.model;

import java.io.IOException;
import java.util.List;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.chedaojunan.report.utils.Pair;
import javax.validation.constraints.NotNull;

/**
 * 抓路服务请求参数实体类
 */

public class AutoGraspRequestParam {

  public static final String KEY = "key";
  public static final String CAR_ID = "carid";
  public static final String LOCATIONS = "locations";
  public static final String TIME = "time";
  public static final String DIRECTION = "direction";
  public static final String SPEED = "speed";
  public static final String EXTENSIONS = "extensions";

  @NotNull
  private String key; // 用户唯一标识

  @NotNull
  private String carId; // 车辆唯一标识

  @NotNull
  @Size(min = 3, max = 200)
  private List<Pair<Double, Double>> locations; // 经纬度

  @NotNull
  @Size(min = 3, max = 200)
  private List<Long> time; // gps时间 (UTC format)

  @NotNull
  @Size(min = 3, max = 200)
  private List<Double> direction; // 行驶方向

  @NotNull
  @Size(min = 3, max = 200)

  private List<Double> speed; // 行驶速度

  private ExtensionParamEnum extensionParamEnum; // base or all

  public AutoGraspRequestParam(String apiKey, String carId, List<Pair<Double, Double>> locations, List<Long> time, List<Double> direction, List<Double> speed, ExtensionParamEnum extensionParamEnum) {
    setKey(apiKey);
    setCarId(carId);
    setLocations(locations);
    setTime(time);
    setDirection(direction);
    setSpeed(speed);
    if(extensionParamEnum != null)
      setExtensionParamEnum(extensionParamEnum);
    else
      setExtensionParamEnum(ExtensionParamEnum.BASE);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getCarId() {
    return carId;
  }

  public void setCarId(String carId) {
    this.carId = carId;
  }

  public List<Pair<Double, Double>> getLocations() {
    return locations;
  }

  public void setLocations(List<Pair<Double, Double>> locations) {
    this.locations = locations;
  }

  public List<Long> getTime() {
    return time;
  }

  public void setTime(List<Long> time) {
    this.time = time;
  }

  public List<Double> getDirection() {
    return direction;
  }

  public void setDirection(List<Double> direction) {
    this.direction = direction;
  }

  public List<Double> getSpeed() {
    return speed;
  }

  public void setSpeed(List<Double> speed) {
    this.speed = speed;
  }

  public ExtensionParamEnum getExtensionParamEnum() {
    return extensionParamEnum;
  }

  public void setExtensionParamEnum(ExtensionParamEnum extensionParamEnum) {
    this.extensionParamEnum = extensionParamEnum;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(key)
        .append(carId)
        .append(locations)
        .append(time)
        .append(direction)
        .append(speed).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof AutoGraspRequestParam) == false) {
      return false;
    }
    AutoGraspRequestParam rhs = ((AutoGraspRequestParam) other);
    return new EqualsBuilder()
        .append(key, rhs.key)
        .append(carId, rhs.carId)
        .append(locations, rhs.locations)
        .append(time, rhs.time)
        .append(direction, rhs.direction)
        .append(speed, rhs.speed).isEquals();
  }

  @Override
  public String toString() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(this);
    } catch (IOException e) {
      return null;
    }
  }

}