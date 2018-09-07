package com.chedaojunan.report.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;

public class CoordinateConvertResponse extends GaoDeApiResponse {

  public static final String LOCATIONS = "locations";

  @JsonProperty(LOCATIONS)
  private String locations;// 转换之后的坐标

  public String getLocations() {
    return locations;
  }

  public void setLocations(String locations) {
    this.locations = locations;
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
        .append(locations).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof CoordinateConvertResponse) == false) {
      return false;
    }
    CoordinateConvertResponse rhs = ((CoordinateConvertResponse) other);
    return new EqualsBuilder()
        .append(locations, rhs.locations).isEquals();
  }
}
