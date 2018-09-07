package com.chedaojunan.report.model;

import com.chedaojunan.report.utils.Pair;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.List;

/**
 * 坐标转换请求参数实体类
 */

public class CoordinateConvertRequestParam {

  public static final String KEY = "key";
  public static final String LOCATIONS = "locations";
  public static final String COORDSYS = "coordsys";

  @NotNull
  private String key; // 用户唯一标识

  @NotNull
  @Size(min = 1, max = 40)
  private List<Pair<Double, Double>> locations; // 经纬度

  @NotNull
  private String coordsys; // 原坐标系

  public CoordinateConvertRequestParam(String apiKey, List<Pair<Double, Double>> locations, String apiCoordsys) {
    setKey(apiKey);
    setLocations(locations);
    setCoordsys(apiCoordsys);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public List<Pair<Double, Double>> getLocations() {
    return locations;
  }

  public void setLocations(List<Pair<Double, Double>> locations) {
    this.locations = locations;
  }

  public String getCoordsys() {
    return coordsys;
  }

  public void setCoordsys(String coordsys) {
    this.coordsys = coordsys;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(key)
        .append(locations)
        .append(coordsys).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof CoordinateConvertRequestParam) == false) {
      return false;
    }
    CoordinateConvertRequestParam rhs = ((CoordinateConvertRequestParam) other);
    return new EqualsBuilder()
        .append(key, rhs.key)
        .append(locations, rhs.locations)
        .append(coordsys, rhs.coordsys).isEquals();
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