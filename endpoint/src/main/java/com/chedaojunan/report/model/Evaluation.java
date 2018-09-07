package com.chedaojunan.report.model;

import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Evaluation {

  public static final String EXPEDITE = "expedite";
  public static final String CONGESTED = "congested";
  public static final String BLOCKED = "blocked";
  public static final String UNKNOWN = "unknown";
  public static final String STATUS = "status";
  public static final String DESCRIPTION = "description";

  @JsonProperty(EXPEDITE)
  private String expedite; // 畅通所占百分比

  @JsonProperty(CONGESTED)
  private String congested; // 缓行所占百分比

  @JsonProperty(BLOCKED)
  private String blocked; // 拥堵所占百分比

  @JsonProperty(UNKNOWN)
  private String unknown; // 未知路段所占百分比

  @JsonProperty(STATUS)
  private String status; // 路况

  @JsonProperty(DESCRIPTION)
  private String description; // 道路描述

  public String getExpedite() {
    return expedite;
  }

  public void setExpedite(String expedite) {
    this.expedite = expedite;
  }

  public String getCongested() {
    return congested;
  }

  public void setCongested(String congested) {
    this.congested = congested;
  }

  public String getBlocked() {
    return blocked;
  }

  public void setBlocked(String blocked) {
    this.blocked = blocked;
  }

  public String getUnknown() {
    return unknown;
  }

  public void setUnknown(String unknown) {
    this.unknown = unknown;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writer().writeValueAsString(this);
    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(status)
        .append(expedite)
        .append(congested)
        .append(blocked)
        .append(unknown)
        .append(description).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Evaluation) == false) {
      return false;
    }
    Evaluation rhs = ((Evaluation) other);
    return new EqualsBuilder()
        .append(status, rhs.status)
        .append(expedite, rhs.expedite)
        .append(congested, rhs.congested)
        .append(description, rhs.description)
        .append(blocked, rhs.blocked)
        .append(unknown, rhs.unknown)
        .isEquals();
  }
}
