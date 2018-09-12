package com.chedaojunan.report.model;

import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FixedFrequencyIntegrationData extends FixedFrequencyAccessGpsData {

  @JsonProperty(value = "road_api_status")
  private int roadApiStatus		    ; // 抓路接口返回结果状态：0表示请求失败；1表示请求成功',

  @JsonProperty(value = "crosspoint")
  private String crosspoint			; // 通过抓路修正的经纬度',

  @JsonProperty(value = "roadname")
  private String roadName				; // 道路名称',

  @JsonProperty(value = "roadlevel")
  private int roadLevel			    ; // 道路等级',

  @JsonProperty(value = "maxspeed")
  private int maxSpeed				; // 道路最高限速',

  @JsonProperty(value = "intersection")
  private String intersection			; // 临近路口',

  @JsonProperty(value = "intersectiondistance")
  private String intersectionDistance	; // 距离临近路口距离',

  @JsonProperty(value = "traffic_request_time")
  private String trafficRequestTimesamp	; // 调用交通态势接口的时间戳',

  @JsonProperty(value = "traffic_request_id")
  private String trafficRequestId	; // 每次调用输入变量id （在调用接口中赋一个唯一值）',

  @JsonProperty(value = "traffic_api_status")
  private int trafficApiStatus	    ; // 交通态势接口返回结果状态：0表示请求失败；1表示请求成功',

  @JsonProperty(value = "congestion_info")
  private String congestionInfo		; // 交通态势，以json串的方式存储',

  public FixedFrequencyIntegrationData(){}

  public FixedFrequencyIntegrationData(FixedFrequencyAccessGpsData gpsData, GaoDeFusionReturn gaoDeFusionReturn) {
    setDeviceImei(gpsData.getDeviceImei());
    setDeviceId(gpsData.getDeviceId());
    setLocalTime(gpsData.getLocalTime());
    setTripId(gpsData.getTripId());
    setServerTime(gpsData.getServerTime());
    setLatitude(gpsData.getLatitude());
    setLongitude(gpsData.getLongitude());
    setAltitude(gpsData.getAltitude());
    setDirection(gpsData.getDirection());
    setGpsSpeed(gpsData.getGpsSpeed());

    setRoadApiStatus(gaoDeFusionReturn.getRoad_api_status());
    setCrosspoint(gaoDeFusionReturn.getCrosspoint());
    setRoadName(gaoDeFusionReturn.getRoadname());
    setRoadLevel(gaoDeFusionReturn.getRoadlevel());
    setMaxSpeed(gaoDeFusionReturn.getMaxspeed());
    setIntersection(gaoDeFusionReturn.getIntersection());
    setIntersectionDistance(gaoDeFusionReturn.getIntersectiondistance());
    setTrafficRequestTimesamp(gaoDeFusionReturn.getTraffic_request_time());
    setTrafficRequestId(gaoDeFusionReturn.getTraffic_request_id());
    setTrafficApiStatus(gaoDeFusionReturn.getTraffic_api_status());
    setCongestionInfo(gaoDeFusionReturn.getCongestion_info());
  }

  public FixedFrequencyIntegrationData(FixedFrequencyAccessGpsData gpsData) {
    setDeviceImei(gpsData.getDeviceImei());
    setDeviceId(gpsData.getDeviceId());
    setLocalTime(gpsData.getLocalTime());
    setTripId(gpsData.getTripId());
    setServerTime(gpsData.getServerTime());
    setLatitude(gpsData.getLatitude());
    setLongitude(gpsData.getLongitude());
    setAltitude(gpsData.getAltitude());
    setDirection(gpsData.getDirection());
    setGpsSpeed(gpsData.getGpsSpeed());
  }

  public int getRoadApiStatus() {
    return roadApiStatus;
  }

  public void setRoadApiStatus(int roadApiStatus) {
    this.roadApiStatus = roadApiStatus;
  }

  public String getCrosspoint() {
    return crosspoint;
  }

  public void setCrosspoint(String crosspoint) {
    this.crosspoint = crosspoint;
  }

  public String getRoadName() {
    return roadName;
  }

  public void setRoadName(String roadName) {
    this.roadName = roadName;
  }

  public int getRoadLevel() {
    return roadLevel;
  }

  public void setRoadLevel(int roadLevel) {
    this.roadLevel = roadLevel;
  }

  public int getMaxSpeed() {
    return maxSpeed;
  }

  public void setMaxSpeed(int maxSpeed) {
    this.maxSpeed = maxSpeed;
  }

  public String getIntersection() {
    return intersection;
  }

  public void setIntersection(String intersection) {
    this.intersection = intersection;
  }

  public String getIntersectionDistance() {
    return intersectionDistance;
  }

  public void setIntersectionDistance(String intersectionDistance) {
    this.intersectionDistance = intersectionDistance;
  }

  public String getTrafficRequestTimesamp() {
    return trafficRequestTimesamp;
  }

  public void setTrafficRequestTimesamp(String trafficRequestTimesamp) {
    this.trafficRequestTimesamp = trafficRequestTimesamp;
  }

  public String getTrafficRequestId() {
    return trafficRequestId;
  }

  public void setTrafficRequestId(String trafficRequestId) {
    this.trafficRequestId = trafficRequestId;
  }

  public int getTrafficApiStatus() {
    return trafficApiStatus;
  }

  public void setTrafficApiStatus(int trafficApiStatus) {
    this.trafficApiStatus = trafficApiStatus;
  }

  public String getCongestionInfo() {
    return congestionInfo;
  }

  public void setCongestionInfo(String congestionInfo) {
    this.congestionInfo = congestionInfo;
  }

  @Override
  public String toString() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(this)
          .replaceAll("\\\\", "")
          .replaceAll("\"\\{", "{")
          .replaceAll("\\}\"", "}");
    } catch (IOException e) {
      return null;
    }
  }

}