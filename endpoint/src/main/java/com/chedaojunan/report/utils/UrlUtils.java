package com.chedaojunan.report.utils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UrlUtils {

  private static final Logger logger = LoggerFactory.getLogger(UrlUtils.class);

  private static final int SC_OK = 200;
  private static final int SC_PARTIAL_CONTENT = 206;

  private OkHttpClient httpClient;

  public UrlUtils(int readTimeout, int connectTimeout, int maxRetry, int maxIdleConnection, int keepAliveDuration) {
    ConnectionPool connectionPool = new ConnectionPool(maxIdleConnection, keepAliveDuration, TimeUnit.SECONDS);
    httpClient = new OkHttpClient.Builder()
        .connectionPool(connectionPool)
        .readTimeout(readTimeout, TimeUnit.SECONDS)
        .connectTimeout(connectTimeout, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .addInterceptor(new RetryInterceptor(maxRetry))
        //.cache(new Cache(cacheDir, cacheSize))
        .build();
  }

  private Response doRequest(Interceptor.Chain chain, Request request) {
    Response response = null;
    try {
      response = chain.proceed(request);
    } catch (IOException e) {
      logger.error("Exception {} for making the request {}", e, request);
    }
    return response;
  }

  public String getJsonFromRequest(Request request, String apiName) {
    String url = request.url().toString();
    try (Response response = httpClient.newCall(request).execute()) {
      if (response == null || response.body() == null || !response.isSuccessful()
          || (response.code() != SC_OK && response.code() != SC_PARTIAL_CONTENT)) {
        logger.error("invalid response {}", url);
        return null;
      }
      return response.body().string();
    } catch (SocketTimeoutException ste) {
      logger.error("Socket Timeout Exception occurred while getting {} to API {} ", url, apiName);
      return null;
    } catch (IOException e) {
      logger.error("Exception {} occurred while getting {} from API {}", e, url, apiName);
      return null;
    }
  }

  public String getJsonFromUrl(String url, String apiName) {
    Request request = new Request.Builder().url(url).build();
    return getJsonFromRequest(request, apiName);
  }

  class RetryInterceptor implements Interceptor{
    private int maxRetry;
    public RetryInterceptor(int maxRetry){
      setMaxRetry(maxRetry);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
      Request request = chain.request();

      Response response = doRequest(chain, request);
      int tryCount = 0;
      while (response == null && tryCount <= maxRetry) {
        tryCount++;
        // retry the request
        response = doRequest(chain, request);
      }
      if (response == null) {
        throw new IOException();
      }
      return response;
    }

    public int getMaxRetry() {
      return maxRetry;
    }

    public void setMaxRetry(int maxRetry) {
      this.maxRetry = maxRetry;
    }
  }

}