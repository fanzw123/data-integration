package com.chedaojunan.report.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RectangleTrafficInfoRequest {

  public static final String KEY = "key";
  public static final String RECTANGLE = "rectangle";
  public static final String EXTENSIONS = "extensions";

  @NotNull
  private String key; // 请求服务权限标识	必填

  @NotNull
  private String rectangle; // 代表此为矩形区域查询	必填

  @NotNull
  private String trafficRequestId; // unique

  @NotNull
  private String trafficRequestTime;

  private String level; // 道路等级	可选

  private ExtensionParamEnum extensions; // 返回结果控制	可选

  private String sig; // 数字签名	可选

  private String output; // 返回数据格式类型	可选

  private String callback; // 回调函数	可选

  public RectangleTrafficInfoRequest(String key, String rectangle, String trafficRequestId,
                                     String trafficRequestTime, ExtensionParamEnum extensions) {
    setKey(key);
    setRectangle(rectangle);
    setTrafficRequestId(trafficRequestId);
    setTrafficRequestTime(trafficRequestTime);
    if(extensions != null)
      setExtensions(extensions);
    else
      setExtensions(ExtensionParamEnum.BASE);
  }


  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public ExtensionParamEnum getExtensions() {
    return extensions;
  }

  public void setExtensions(ExtensionParamEnum extensions) {
    this.extensions = extensions;
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

  public String getCallback() {
    return callback;
  }

  public void setCallback(String callback) {
    this.callback = callback;
  }

  public String getRectangle() {
    return rectangle;
  }

  public void setRectangle(String rectangle) {
    this.rectangle = rectangle;
  }

  public String getTrafficRequestId() {
    return trafficRequestId;
  }

  public void setTrafficRequestId(String trafficRequestId) {
    this.trafficRequestId = trafficRequestId;
  }

  public String getTrafficRequestTime() {
    return trafficRequestTime;
  }

  public void setTrafficRequestTime(String trafficRequestTime) {
    this.trafficRequestTime = trafficRequestTime;
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
