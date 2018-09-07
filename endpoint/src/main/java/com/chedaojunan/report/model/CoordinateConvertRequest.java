package com.chedaojunan.report.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * 坐标转换请求参数实体类
 */

public class CoordinateConvertRequest {

  public static final String KEY = "key";
  public static final String LOCATIONS = "locations";
  public static final String COORDSYS = "coordsys";

  @NotNull
  private String key; // 用户唯一标识

  @NotNull
  @JsonProperty(LOCATIONS)
  private String locations; // 经纬度

  @NotNull
  private CoordsysParamEnum coordsys; // 原坐标系

  private String sig; // 数字签名	可选

  private String output; // 返回数据格式类型	可选

  public CoordinateConvertRequest(String apiKey, String locations, CoordsysParamEnum coordsys) {
    setKey(apiKey);
    setLocations(locations);
    if(coordsys != null)
      setCoordsys(coordsys);
    else
      setCoordsys(CoordsysParamEnum.GPS);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getLocations() {
    return locations;
  }

  public void setLocations(String locations) {
    this.locations = locations;
  }

  public CoordsysParamEnum getCoordsys() {
    return coordsys;
  }

  public void setCoordsys(CoordsysParamEnum coordsys) {
    this.coordsys = coordsys;
  }

  public String getSig() {
    return sig;
  }

  public void setSig(String sig) {
    this.sig = sig;
  }

  public String getOutput() {
    return output;
  }

  public void setOutput(String output) {
    this.output = output;
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
    if ((other instanceof CoordinateConvertRequest) == false) {
      return false;
    }
    CoordinateConvertRequest rhs = ((CoordinateConvertRequest) other);
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


