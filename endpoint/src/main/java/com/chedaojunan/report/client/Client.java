package com.chedaojunan.report.client;

import java.io.IOException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chedaojunan.report.utils.EndpointConfiguration;
import com.chedaojunan.report.utils.ObjectMapperUtils;
import com.chedaojunan.report.utils.UrlUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Client<R> {
  private static final Logger logger = LoggerFactory.getLogger(Client.class);
  private static final ObjectMapper objectMapper = ObjectMapperUtils.getObjectMapper();

  private UrlUtils urlUtils;

  protected String url;
  protected String apiVersion;
  protected String pathSegment;
  protected String apiName;

  public static synchronized <T extends Client> T getInstance(T instance, Class<T> clazz, String apiName){

    logger.info("Configuring client for {}" , clazz.getName());
    try {
      if (instance == null) {
        instance = clazz.newInstance();
        EndpointConfiguration endpointConfiguration = EndpointConfiguration.getConfiguration(clazz);
        instance.setUrl(endpointConfiguration.getBaseUrl());
        instance.setApiVersion(endpointConfiguration.getApiVersion());
        instance.setPathSegment(endpointConfiguration.getPathSegment());
        instance.setUrlUtils(new UrlUtils(endpointConfiguration.getReadTimeout(), endpointConfiguration.getConnectTimeout(),
            endpointConfiguration.getMaxRetries(), endpointConfiguration.getMaxIdleConnection(), endpointConfiguration.getKeepAliveDuration()));
        instance.apiName = apiName;
      }
      return instance;
    } catch (InstantiationException|IllegalAccessException e) {
      logger.error("Error instantiating class {}", clazz, e);
      return null;
    }
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public void setPathSegment(String pathSegment) {
    this.pathSegment = pathSegment;
  }

  R getClientResponseJsonPojo(okhttp3.Request request, Class<R> classType) {
    try {
      long start = Instant.now().toEpochMilli();
      String responseJson = urlUtils.getJsonFromRequest(request, apiName);
      if (responseJson == null)
        return null;
      long end = Instant.now().toEpochMilli();
      logger.info("api query latency: {} for API {} ", (end - start), apiName);
      return objectMapper.readValue(responseJson, classType);
    } catch (IOException e) {
      logger.error("failed to convert to {} from {}", classType, url, e);
      return null;
    }
  }


  String getClientResponseJson(okhttp3.Request request) {
    long start = Instant.now().toEpochMilli();
    String responseJson = urlUtils.getJsonFromRequest(request, apiName);
    if (responseJson == null)
      return null;
    long end = Instant.now().toEpochMilli();
    logger.info("api query latency: {} for API {} ", (end - start), apiName);
    return responseJson;
  }

  public void setUrlUtils(UrlUtils urlUtils) {
    this.urlUtils = urlUtils;
  }
}
