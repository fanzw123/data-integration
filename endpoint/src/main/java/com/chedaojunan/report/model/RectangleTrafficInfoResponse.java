package com.chedaojunan.report.model;

import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RectangleTrafficInfoResponse extends GaoDeApiResponse {

  public static final String TRAFFIC_INFO = "trafficinfo";

  @JsonProperty(TRAFFIC_INFO)
  private TrafficInfo trafficInfo;

  public TrafficInfo getTrafficInfo() {
    return trafficInfo;
  }

  public void setTrafficInfo(TrafficInfo trafficInfo) {
    this.trafficInfo = trafficInfo;
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
        .append(trafficInfo).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof RectangleTrafficInfoResponse) == false) {
      return false;
    }
    RectangleTrafficInfoResponse rhs = ((RectangleTrafficInfoResponse) other);
    return new EqualsBuilder()
        .append(trafficInfo, rhs.trafficInfo).isEquals();
  }
}
