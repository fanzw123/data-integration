package com.chedaojunan.report.model;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AutoGraspResponse extends GaoDeApiResponse {

  public static final String COUNT = "count";
  public static final String ROADS = "roads";

  @JsonProperty(COUNT)
  private int count;// 返回结果的数目

  @JsonProperty(ROADS)
  private List<RoadInfo> roadInfoList;// 抓路服务列表

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<RoadInfo> getRoadInfoList() {
    return roadInfoList;
  }

  public void setRoadInfoList(List<RoadInfo> roadInfoList) {
    this.roadInfoList = roadInfoList;
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
        .append(count)
        .append(roadInfoList).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof AutoGraspResponse) == false) {
      return false;
    }
    AutoGraspResponse rhs = ((AutoGraspResponse) other);
    return new EqualsBuilder()
        .append(count, rhs.count)
        .append(roadInfoList, rhs.roadInfoList).isEquals();
  }
}
