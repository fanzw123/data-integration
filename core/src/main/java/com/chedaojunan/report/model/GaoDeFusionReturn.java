package com.chedaojunan.report.model;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GaoDeFusionReturn {

  private int road_api_status		    ; // 抓路接口返回结果状态：0表示请求失败；1表示请求成功',
  private String crosspoint			; // 通过抓路修正的经纬度',
  private String roadname				; // 道路名称',
  private int roadlevel			    ; // 道路等级',
  private int maxspeed				; // 道路最高限速',
  private String intersection			; // 临近路口',
  private String intersectiondistance	; // 距离临近路口距离',
  private String traffic_request_time	; // 调用交通态势接口的时间戳',
  private String traffic_request_id	; // 每次调用输入变量id （在调用接口中赋一个唯一值）',
  private int traffic_api_status	    ; // 交通态势接口返回结果状态：0表示请求失败；1表示请求成功',
  private String congestion_info		; // 交通态势，以json串的方式存储',

  public GaoDeFusionReturn() {
  }

  public int getRoad_api_status() {
    return road_api_status;
  }

  public void setRoad_api_status(int road_api_status) {
    this.road_api_status = road_api_status;
  }

  public String getCrosspoint() {
    return crosspoint;
  }

  public void setCrosspoint(String crosspoint) {
    this.crosspoint = crosspoint;
  }

  public String getRoadname() {
    return roadname;
  }

  public void setRoadname(String roadname) {
    this.roadname = roadname;
  }

  public int getRoadlevel() {
    return roadlevel;
  }

  public void setRoadlevel(int roadlevel) {
    this.roadlevel = roadlevel;
  }

  public int getMaxspeed() {
    return maxspeed;
  }

  public void setMaxspeed(int maxspeed) {
    this.maxspeed = maxspeed;
  }

  public String getIntersection() {
    return intersection;
  }

  public void setIntersection(String intersection) {
    this.intersection = intersection;
  }

  public String getIntersectiondistance() {
    return intersectiondistance;
  }

  public void setIntersectiondistance(String intersectiondistance) {
    this.intersectiondistance = intersectiondistance;
  }

  public String getTraffic_request_time() {
    return traffic_request_time;
  }

  public void setTraffic_request_time(String traffic_request_time) {
    this.traffic_request_time = traffic_request_time;
  }

  public String getTraffic_request_id() {
    return traffic_request_id;
  }

  public void setTraffic_request_id(String traffic_request_id) {
    this.traffic_request_id = traffic_request_id;
  }

  public int getTraffic_api_status() {
    return traffic_api_status;
  }

  public void setTraffic_api_status(int traffic_api_status) {
    this.traffic_api_status = traffic_api_status;
  }

  public String getCongestion_info() {
    return congestion_info;
  }

  public void setCongestion_info(String congestion_info) {
    this.congestion_info = congestion_info;
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