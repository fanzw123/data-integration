package com.chedaojunan.report.model;

import java.io.IOException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 固定频率接入数据（GPS位置和速度数据）实体类
 *
 */

public class FixedFrequencyGpsData {

  @JsonProperty(value = "deviceImei")
  private String deviceImei			; // 国际移动设备身份识别码

  @JsonProperty(value = "deviceId")
  private String deviceId			    ; // 厂商设备编码,当设备厂商无设备编码时该字段值为IMEI值

  @JsonProperty(value = "localTime")
  private String localTime			; // 设备端数据采集的时间戳

  @JsonProperty(value = "serverTime")
  private String serverTime			; // 服务端时间戳

  @JsonProperty(value = "tripId")
  private String tripId				; // 行程ID

  @JsonProperty(value = "lat")
  private double latitude				; // 纬度

  @JsonProperty(value = "longi")
  private double longitude			    ; // 经度

  @JsonProperty(value = "alt")
  private double altitude				; // 海拔

  @JsonProperty(value = "dir")
  private double direction             ; // 方向角，[0~360), 正北为0， 顺时针

  @JsonProperty(value = "gpsSpeed")
  private double gpsSpeed	    ; // GPS速度

  public FixedFrequencyGpsData(){}

  public String getDeviceImei() {
    return deviceImei;
  }

  public void setDeviceImei(String deviceImei) {
    this.deviceImei = deviceImei;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getLocalTime() {
    return localTime;
  }

  public void setLocalTime(String localTime) {
    this.localTime = localTime;
  }

  public String getServerTime() {
    return serverTime;
  }

  public void setServerTime(String serverTime) {
    this.serverTime = serverTime;
  }

  public String getTripId() {
    return tripId;
  }

  public void setTripId(String tripId) {
    this.tripId = tripId;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public double getAltitude() {
    return altitude;
  }

  public void setAltitude(double altitude) {
    this.altitude = altitude;
  }

  public double getGpsSpeed() {
    return gpsSpeed;
  }

  public void setGpsSpeed(double gpsSpeed) {
    this.gpsSpeed = gpsSpeed;
  }

  public double getDirection() {
    return direction;
  }

  public void setDirection(double direction) {
    this.direction = direction;
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

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(deviceId)
        .append(deviceImei)
        .append(localTime)
        .append(serverTime)
        .append(tripId)
        .append(latitude)
        .append(longitude)
        .append(altitude)
        .append(direction)
        .append(gpsSpeed)
        .toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof FixedFrequencyGpsData) == false) {
      return false;
    }
    FixedFrequencyGpsData rhs = ((FixedFrequencyGpsData) other);
    return new EqualsBuilder()
        .append(deviceId, rhs.deviceId)
        .append(deviceImei, rhs.deviceImei)
        .append(localTime, rhs.localTime)
        .append(serverTime, rhs.serverTime)
        .append(tripId, rhs.tripId)
        .append(latitude, rhs.latitude)
        .append(longitude, rhs.longitude)
        .append(altitude, rhs.altitude)
        .append(direction, rhs.direction)
        .append(gpsSpeed, rhs.gpsSpeed)
        .isEquals();
  }

}