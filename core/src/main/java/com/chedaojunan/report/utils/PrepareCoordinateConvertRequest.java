package com.chedaojunan.report.utils;

import com.chedaojunan.report.common.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class PrepareCoordinateConvertRequest {

  public static String convertLocationsToRequestString(List<Pair<Double, Double>> locations) {
    if(CollectionUtils.isNotEmpty(locations))
      return StringUtils.join(locations.stream().map(Pair::toString).collect(Collectors.toList()), Constants.PIPE);
    else
      return null;
  }
}
