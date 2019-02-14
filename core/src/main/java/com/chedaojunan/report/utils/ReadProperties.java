package com.chedaojunan.report.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @ClassName: ReadProperties
 * @Description: 获取配置文件信息
 * @date: 2018年3月
 * @version: 1.0.0
 */

public class ReadProperties {

  private final static Logger logger = LoggerFactory.getLogger(ReadProperties.class);

  /**
   * 根据key读取value
   *
   * @param filePath
   * @param keyWord
   * @return String
   */
  public static String getProperties(String filePath, String keyWord){
    Properties prop = new Properties();
    String value = null;
    try {
      String fileP = System.getProperty("user.dir") + File.separator + filePath;
      InputStream inputStream = new FileInputStream(fileP);
      prop.load(inputStream);
      value = prop.getProperty(keyWord);
    } catch (IOException e) {
      logger.error("Exception {} in loading properties file {} ", e, filePath);
    }
    return value;
  }

  /**
   * 读取配置文件
   *
   * @param filePath
   * @return String
   */
  public static Properties getProperties(String filePath){
    Properties prop = new Properties();
    try {
      String fileP = System.getProperty("user.dir") + File.separator + filePath;
      InputStream inputStream = new FileInputStream(fileP);
      prop.load(inputStream);
    } catch (IOException e) {
      logger.error("Exception {} in loading properties file {} ", e, filePath);
    }
    return prop;
  }

}