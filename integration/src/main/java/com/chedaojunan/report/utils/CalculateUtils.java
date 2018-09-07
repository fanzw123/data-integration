package com.chedaojunan.report.utils;

import java.math.BigDecimal;
import java.util.Random;

public class CalculateUtils {

  // 获取随机数
  public double randomReturn(int numRange, double decimalDigits){
    Random random = new Random();// 定义随机类
    BigDecimal firstParm = new BigDecimal(Double.valueOf(random.nextInt(numRange)));
    BigDecimal secondParm = new BigDecimal(Double.valueOf(decimalDigits));
    return firstParm.multiply(secondParm).doubleValue();
  }

  // 加法(double数据)
  public double add(double... v) {
    BigDecimal b;
    BigDecimal first;
    if (v.length > 0) {
      first = new BigDecimal(Double.toString(v[0]));
      for (int i = 1; i < v.length; i++) {
        b = new BigDecimal(Double.toString(v[i]));
        first = first.add(b);
      }
      return first.doubleValue();
    } else {return 0.0;}
  }

}
