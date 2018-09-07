package com.chedaojunan.report.model;

import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrafficInfo {

  public static final String DESCRIPTION = "description";
  public static final String EVALUATION = "evaluation";

  @JsonProperty(DESCRIPTION)
  private String description; // 路况综述

  @JsonProperty(EVALUATION)
  private Evaluation evaluation; // 路况评价

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Evaluation getEvaluation() {
    return evaluation;
  }

  public void setEvaluation(Evaluation evaluation) {
    this.evaluation = evaluation;
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
        .append(description)
        .append(evaluation).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof TrafficInfo) == false) {
      return false;
    }
    TrafficInfo rhs = ((TrafficInfo) other);
    return new EqualsBuilder()
        .append(description, rhs.description)
        .append(evaluation, rhs.evaluation).isEquals();
  }
}
