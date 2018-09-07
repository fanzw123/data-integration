package com.chedaojunan.report.utils;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Pair<A, B> implements Serializable {
  private static final long serialVersionUID = -6297271665077029913L;
  public final A first;
  public final B second;

  public Pair(A first, B second) {
    this.first = first;
    this.second = second;
  }

  public String toString() {
    return this.first.toString() + "," + this.second.toString();
  }

  private static boolean equals(Object x, Object y) {
    return x == null && y == null || x != null && x.equals(y);
  }

  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (this == obj) {
      return true;
    } else if (!Pair.class.isAssignableFrom(obj.getClass())) {
      return false;
    } else {
      Pair other = (Pair) obj;
      return (new EqualsBuilder()).append(this.first, other.first).append(this.second, other.second).isEquals();
    }
  }

  public int hashCode() {
    return (new HashCodeBuilder(17, 31)).append(this.first).append(this.second).hashCode();
  }

  public static <A, B> Pair<A, B> of(A a, B b) {
    return new Pair(a, b);
  }

}
