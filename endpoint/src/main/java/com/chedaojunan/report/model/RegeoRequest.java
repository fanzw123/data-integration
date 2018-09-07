package com.chedaojunan.report.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * 逆地理编码请求参数实体类
 */
public class RegeoRequest {

  public static final String KEY = "key";
  public static final String LOCATION = "location";
  public static final String POITYPE = "poitype";
  public static final String RADIUS = "radius";
  public static final String EXTENSIONS = "extensions";
  public static final String BATCH = "batch";
  public static final String ROADLEVEL = "roadlevel";
  public static final String SIG = "sig";
  public static final String OUTPUT = "output";
  public static final String CALLBACK = "callback";
  public static final String HOMEORCORP = "homeorcorp";

  @NotNull
  private String key; // 用户唯一标识
  @NotNull
  @JsonProperty(LOCATION)
  private String location; // 经纬度
  private String poitype; // 返回附近POI类型
  private String radius; // 搜索半径
  private String extensions; // 返回结果控制
  private String batch; // 批量查询控制
  private String roadlevel; // 道路等级
  private String sig; // 数字签名
  private String output; // 返回数据格式类型
  private String callback; // 回调函数
  private String homeorcorp; // 是否优化POI返回顺序

  public String getPoitype() {
    return poitype;
  }

  public void setPoitype(String poitype) {
    this.poitype = poitype;
  }

  public String getRadius() {
    return radius;
  }

  public void setRadius(String radius) {
    this.radius = radius;
  }

  public String getExtensions() {
    return extensions;
  }

  public void setExtensions(String extensions) {
    this.extensions = extensions;
  }

  public String getBatch() {
    return batch;
  }

  public void setBatch(String batch) {
    this.batch = batch;
  }

  public String getRoadlevel() {
    return roadlevel;
  }

  public void setRoadlevel(String roadlevel) {
    this.roadlevel = roadlevel;
  }

  public String getCallback() {
    return callback;
  }

  public void setCallback(String callback) {
    this.callback = callback;
  }

  public String getHomeorcorp() {
    return homeorcorp;
  }

  public void setHomeorcorp(String homeorcorp) {
    this.homeorcorp = homeorcorp;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
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

  public RegeoRequest(String apiKey, String locations, String extensions, String batch, String roadlevel) {
    setKey(apiKey);
    setLocation(locations);
    if (null!=radius) {
      setRadius(radius);
    } else {
      setRadius("1000");
    }
    if (null!=extensions) {
      setExtensions(extensions);
    } else {
      setExtensions("base");
    }
    if (null!=batch) {
      setBatch(batch);
    } else {
      setBatch("true");
    }
    if (null!=roadlevel) {
      setRoadlevel(roadlevel);
    } else {
      setRoadlevel("1");
    }
    if (null!=output) {
      setOutput(output);
    } else {
      setOutput("JSON");
    }
    if (null!=homeorcorp) {
      setHomeorcorp(homeorcorp);
    } else {
      setHomeorcorp("0");
    }

  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(key)
        .append(location)
        .append(poitype)
        .append(radius)
        .append(extensions)
        .append(batch)
        .append(roadlevel)
        .append(sig)
        .append(output)
        .append(callback)
        .append(homeorcorp).toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof RegeoRequest) == false) {
      return false;
    }
    RegeoRequest rhs = ((RegeoRequest) other);
    return new EqualsBuilder()
        .append(key, rhs.key)
        .append(location, rhs.location)
        .append(poitype, rhs.poitype)
        .append(radius, rhs.radius)
        .append(extensions, rhs.extensions)
        .append(batch, rhs.batch)
        .append(roadlevel, rhs.roadlevel)
        .append(sig, rhs.sig)
        .append(output, rhs.output)
        .append(callback, rhs.callback)
        .append(homeorcorp, rhs.homeorcorp).isEquals();
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