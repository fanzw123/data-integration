package com.chedaojunan.report.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.util.List;

public class RegeoResponse extends GaoDeApiResponse {

  public static final String REGEOCODES = "regeocodes";

  @JsonProperty(REGEOCODES)
  private List<Regeocodes> regeocodes;

  public List<Regeocodes> getRegeocodes() {
    return regeocodes;
  }

  public void setRegeocodes(List<Regeocodes> regeocodes) {
    this.regeocodes = regeocodes;
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
        .append(regeocodes).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof RegeoResponse) == false) {
      return false;
    }
    RegeoResponse rhs = ((RegeoResponse) other);
    return new EqualsBuilder()
        .append(regeocodes, rhs.regeocodes).isEquals();
  }

}