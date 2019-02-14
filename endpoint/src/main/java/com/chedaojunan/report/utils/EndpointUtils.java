package com.chedaojunan.report.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.System.exit;

public class EndpointUtils {
  private static final Logger logger = LoggerFactory.getLogger(EndpointUtils.class);

  private static Properties endpointProperties = null;

  static {
    try {
      String filePath = System.getProperty("user.dir") + File.separator + EndpointConstants.PROPERTIES_FILE_NAME;
      InputStream inputStream = new FileInputStream(filePath);
      endpointProperties = new Properties();
      endpointProperties.load(inputStream);
      inputStream.close();
    } catch (IOException e) {
      logger.error("Error occurred while reading properties file. ", e);
      exit(1);
    }
  }

  public static Properties getEndpointProperties() {
    return endpointProperties;
  }

}