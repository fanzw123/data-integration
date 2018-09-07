package com.chedaojunan.report.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.chedaojunan.report.common.Constants;

public class PrepareAutoGraspRequest {

  public static String convertLocationsToRequestString(List<Pair<Double, Double>> locations) {
    if(CollectionUtils.isNotEmpty(locations))
      return StringUtils.join(locations.stream().map(Pair::toString).collect(Collectors.toList()), Constants.PIPE);
    else
      return null;
  }

  public static String convertDirectionToRequestString(List<Double> direction){
    if(CollectionUtils.isNotEmpty(direction))
      return StringUtils.join(direction, Constants.COMMA);
    else
      return null;
  }

  public static String convertSpeedToRequestString(List<Double> speed) {
    if(CollectionUtils.isNotEmpty(speed))
      return StringUtils.join(speed, Constants.COMMA);
    else
      return null;
  }

  public static String convertTimeToRequstString(List<Long> time) {
    if(CollectionUtils.isNotEmpty(time))
      return StringUtils.join(time, Constants.COMMA);
    else
      return null;
  }
}
