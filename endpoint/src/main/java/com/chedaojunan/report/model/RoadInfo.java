package com.chedaojunan.report.model;

import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoadInfo {

  public static final String CROSS_POINT = "crosspoint";
  public static final String ROAD_NAME = "roadname";
  public static final String POLYLINE = "ployline";
  public static final String ROAD_LEVEL = "roadlevel";
  public static final String MAX_SPEED = "maxspeed";
  public static final String INTERSECTION = "intersection";
  public static final String INTERSECTION_DISTANCE = "intersectiondistance";

  @JsonProperty(CROSS_POINT)
  private String crosspoint; // 交叉点坐标

  @JsonProperty(ROAD_NAME)
  private String roadname; // 道路名称

  @JsonProperty(POLYLINE)
  private String polyline; // 道路经纬度坐标

  @JsonProperty(ROAD_LEVEL)
  private int roadlevel; // 道路等级

  @JsonProperty(MAX_SPEED)
  private int maxspeed; // 道路最高限速

  @JsonProperty(INTERSECTION)
  private String intersection; // 临近路口

  @JsonProperty(INTERSECTION_DISTANCE)
  private String intersectiondistance; // 距离临近路口距离

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

  public String getPolyline() {
    return polyline;
  }

  public void setPolyline(String polyline) {
    this.polyline = polyline;
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
        .append(crosspoint)
        .append(roadname)
        .append(polyline)
        .append(roadlevel)
        .append(maxspeed)
        .append(intersection)
        .append(intersectiondistance).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof RoadInfo) == false) {
      return false;
    }
    RoadInfo rhs = ((RoadInfo) other);
    return new EqualsBuilder()
        .append(crosspoint, rhs.crosspoint)
        .append(roadname, rhs.roadname)
        .append(roadlevel, rhs.roadlevel)
        .append(polyline, rhs.polyline)
        .append(maxspeed, rhs.maxspeed)
        .append(intersection, rhs.intersection)
        .append(intersectiondistance, rhs.intersectiondistance)
        .isEquals();
  }
}
