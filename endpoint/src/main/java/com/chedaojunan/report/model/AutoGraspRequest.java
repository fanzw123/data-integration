package com.chedaojunan.report.model;

import java.io.IOException;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AutoGraspRequest {

  public static final String KEY = "key";
  public static final String CAR_ID = "carid";
  public static final String LOCATIONS = "locations";
  public static final String TIME = "time";
  public static final String DIRECTION = "direction";
  public static final String SPEED = "speed";

  @NotNull
  @JsonProperty(KEY)
  private String key; // 用户唯一标识

  @NotNull
  @JsonProperty(CAR_ID)
  private String carId; // 车辆唯一标识

  @NotNull
  @JsonProperty(LOCATIONS)
  private String locations; // 经纬度

  @NotNull
  @JsonProperty(TIME)
  private String time; // gps时间 (UTC format)

  @NotNull
  @JsonProperty(DIRECTION)
  private String direction; // 行驶方向

  @NotNull
  @JsonProperty(SPEED)
  private String speed; // 行驶速度

  public AutoGraspRequest() {}

  public AutoGraspRequest(String key, String carId, String locations, String time, String direction, String speed){
    setCarId(carId);
    setDirection(direction);
    setKey(key);
    setLocations(locations);
    setSpeed(speed);
    setTime(time);
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

  public String getLocations() {
    return locations;
  }

  public void setLocations(String locations) {
    this.locations = locations;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getDirection() {
    return direction;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public String getSpeed() {
    return speed;
  }

  public void setSpeed(String speed) {
    this.speed = speed;
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
    if ((other instanceof AutoGraspRequest) == false) {
      return false;
    }
    AutoGraspRequest rhs = ((AutoGraspRequest) other);
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
