package com.chedaojunan.report.utils;

import java.util.HashMap;
import java.util.Map;

import com.chedaojunan.report.client.CoordinateConvertClient;
import com.chedaojunan.report.client.RegeoClient;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chedaojunan.report.client.AutoGraspApiClient;
import com.chedaojunan.report.client.RectangleTrafficInfoClient;

public class EndpointConfiguration {

  private final static Logger logger = LoggerFactory.getLogger(EndpointConfiguration.class);

  private String baseUrl;
  private String apiVersion;
  private String pathSegment;

  private int readTimeout;
  private int connectTimeout;
  private int maxRetries;
  private int maxIdleConnection;
  private int keepAliveDuration;

  private EndpointConfiguration(String baseUrl, String apiVersion, String pathSegment, int readTimeout, int connectTimeout, int maxRetries, int maxIdleConnection, int keepAliveDuration) {
    this.baseUrl = baseUrl;
    this.apiVersion = apiVersion;
    this.pathSegment = pathSegment;
    this.readTimeout = readTimeout;
    this.connectTimeout = connectTimeout;
    this.maxRetries = maxRetries;
    this.maxIdleConnection = maxIdleConnection;
    this.keepAliveDuration = keepAliveDuration;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public String getPathSegment() {
    return pathSegment;
  }

  public int getReadTimeout() {
    return readTimeout;
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public int getMaxRetries() {
    return maxRetries;
  }

  public int getMaxIdleConnection() {
    return maxIdleConnection;
  }

  public int getKeepAliveDuration() {
    return keepAliveDuration;
  }

  private static Map<Class, EndpointConfiguration> settingsMap = new HashMap<>();

  public static EndpointConfiguration getConfiguration(Class clazz) {
    if (MapUtils.isEmpty(settingsMap)) {
      settingsMap.put(AutoGraspApiClient.class, new EndpointConfiguration(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_AUTOGRASP_API_URL),
          EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_AUTOGRASP_API_VERSION),
          EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_AUTOGRASP_API_PATH_SEGMENT),
          Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_AUTOGRASP_API_READ_TIMEOUT)),
          Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_AUTOGRASP_API_CONNECT_TIMEOUT)),
          Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_AUTOGRASP_API_MAX_CONNECT_RETRY)),
          Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_API_POOL_MAX_IDLE_CONNECTIONS)),
          Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_API_POOL_KEEP_ALIVE_DURATION))));
      settingsMap.put(RectangleTrafficInfoClient.class, new EndpointConfiguration(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_RECTANGLE_API_URL),
          EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_RECTANGLE_API_VERSION),
          EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_RECTANGLE_API_PATH_SEGMENT),
          Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_RECTANGLE_API_READ_TIMEOUT)),
          Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_RECTANGLE_API_CONNECT_TIMEOUT)),
          Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_RECTANGLE_API_MAX_CONNECT_RETRY)),
          Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_API_POOL_MAX_IDLE_CONNECTIONS)),
          Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_API_POOL_KEEP_ALIVE_DURATION))));
      settingsMap.put(CoordinateConvertClient.class, new EndpointConfiguration(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_COORDINATE_CONVERT_API_URL),
              EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_COORDINATE_CONVERT_API_VERSION),
              EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_COORDINATE_CONVERT_API_PATH_SEGMENT),
              Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_COORDINATE_CONVERT_API_READ_TIMEOUT)),
              Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_COORDINATE_CONVERT_API_CONNECT_TIMEOUT)),
              Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_COORDINATE_CONVERT_API_MAX_CONNECT_RETRY)),
              Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_API_POOL_MAX_IDLE_CONNECTIONS)),
              Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_API_POOL_KEEP_ALIVE_DURATION))));
      settingsMap.put(RegeoClient.class, new EndpointConfiguration(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_REGEO_API_URL),
              EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_REGEO_API_VERSION),
              EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_REGEO_API_PATH_SEGMENT),
              Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_REGEO_API_READ_TIMEOUT)),
              Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_REGEO_API_CONNECT_TIMEOUT)),
              Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_REGEO_API_MAX_CONNECT_RETRY)),
              Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_API_POOL_MAX_IDLE_CONNECTIONS)),
              Integer.parseInt(EndpointUtils.getEndpointProperties().getProperty(EndpointConstants.GAODE_API_POOL_KEEP_ALIVE_DURATION))));
    }

    if (settingsMap.containsKey(clazz))
      return settingsMap.get(clazz);

    return null;
  }

}