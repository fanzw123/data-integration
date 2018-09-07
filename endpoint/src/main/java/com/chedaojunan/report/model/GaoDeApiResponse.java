package com.chedaojunan.report.model;

import java.io.IOException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GaoDeApiResponse {

  public static final String STATUS = "status";
  public static final String INFO = "info";
  public static final String INFO_CODE = "infocode";


  @JsonProperty(STATUS)
  private int status;// 结果状态0,表示失败,1:表示成功

  @JsonProperty(INFO)
  private String info;// 返回状态说明

  @JsonProperty(INFO_CODE)
  private String infoCode; // 返回信息码


  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public String getInfoCode() {
    return infoCode;
  }

  public void setInfoCode(String infoCode) {
    this.infoCode = infoCode;
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
        .append(status)
        .append(info)
        .append(infoCode).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof GaoDeApiResponse) == false) {
      return false;
    }
    GaoDeApiResponse rhs = ((GaoDeApiResponse) other);
    return new EqualsBuilder()
        .append(status, rhs.status)
        .append(info, rhs.info)
        .append(infoCode, rhs.infoCode).isEquals();
  }
}
