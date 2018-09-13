package com.chedaojunan.report.utils;

import com.chedaojunan.report.model.FrequencyGpsData;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author fanzw
 * @description: protoc工厂类
 */
public class ProtoFactory {
  public static FrequencyGpsData.FrequencyGps createProtoClass(FrequencyGpsData.FrequencyGps gpsData) {
    FrequencyGpsData.FrequencyGps.Builder builder = FrequencyGpsData.FrequencyGps.newBuilder()
            .setDeviceId(gpsData.getDeviceId())
            .setDeviceImei(gpsData.getDeviceImei())
            .setLocalTime(gpsData.getLocalTime())
            .setTripId(gpsData.getTripId())
            .setServerTime(gpsData.getServerTime())
            .setLat(gpsData.getLat())
            .setLongi(gpsData.getLongi())
            .setAlt(gpsData.getAlt())
            .setDir(gpsData.getDir())
            .setGpsSpeed(gpsData.getGpsSpeed());

    return builder.build();
  }

  public static FrequencyGpsData.FrequencyGps getFrequencyGps(byte[] bytes) {
    try {
      return FrequencyGpsData.FrequencyGps.parseFrom(bytes);
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
    }
    return null;
  }

}