package com.chedaojunan.report.utils;

public class AzimuthFromLogLatUtil {

  private static final double EARTH_RADIUS = 6378137; // 赤道半径(单位m)
  private static final double EARTH_POLAR = 6356725; // 极半径(单位m)
  private double m_LoDeg, m_LoMin, m_LoSec;
  private double m_LaDeg, m_LaMin, m_LaSec;
  private double m_Longitude, m_Latitude;
  private double m_RadLo, m_RadLa;
  private double Ec;
  private double Ed;

  public AzimuthFromLogLatUtil() {
  }

  private static double rad(double d) {
    return d * Math.PI / 180.0;
  }

  /**
   * 获取某个点的经纬度
   *
   * @param longitude 经度
   * @param latitude 纬度
   */
  public AzimuthFromLogLatUtil(double longitude, double latitude) {
    m_LoDeg = (int) longitude;
    m_LoMin = (int) ((longitude - m_LoDeg) * 60);
    m_LoSec = (longitude - m_LoDeg - m_LoMin / 60.) * 3600;

    m_LaDeg = (int) latitude;
    m_LaMin = (int) ((latitude - m_LaDeg) * 60);
    m_LaSec = (latitude - m_LaDeg - m_LaMin / 60.) * 3600;

    m_Longitude = longitude;
    m_Latitude = latitude;
    m_RadLo = longitude * Math.PI / 180.;
    m_RadLa = latitude * Math.PI / 180.;
    Ec = EARTH_POLAR + (EARTH_RADIUS - EARTH_POLAR) * (90. - m_Latitude) / 90.;
    Ed = Ec * Math.cos(m_RadLa);
  }

  /**
   * 获取AB连线与正北方向的角度
   *
   * @param A A点的经纬度
   * @param B B点的经纬度
   * @return AB连线与正北方向的角度（0~360）
   */
  public double getAngle(AzimuthFromLogLatUtil A, AzimuthFromLogLatUtil B) {
    double dx = (B.m_RadLo - A.m_RadLo) * A.Ed;
    double dy = (B.m_RadLa - A.m_RadLa) * A.Ec;
    double angle = 0.0;
    angle = Math.atan(Math.abs(dx / dy)) * 180. / Math.PI;
    double dLo = B.m_Longitude - A.m_Longitude;
    double dLa = B.m_Latitude - A.m_Latitude;
    if (dLo > 0 && dLa <= 0) {
      angle = (90. - angle) + 90;
    } else if (dLo <= 0 && dLa < 0) {
      angle = angle + 180.;
    } else if (dLo < 0 && dLa >= 0) {
      angle = (90. - angle) + 270;
    }
    return angle;
  }

  public double getAzimuth(AzimuthFromLogLatUtil A, AzimuthFromLogLatUtil B) {
    String double_str = String.format("%.1f", getAngle(A, B));
    double direction = Double.valueOf(double_str);
    return direction;
  }

  /**
   * 获取两个gps的经纬度距离(m)
   *
   * @param longitude1 经度
   * @param latitude1 纬度
   * @param longitude2 经度
   * @param latitude2 纬度
   */
  public double getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
    double a, b, d, sa2, sb2;
    latitude1 = rad(latitude1);
    latitude2 = rad(latitude2);
    a = latitude1 - latitude2;
    b = rad(longitude1 - longitude2);

    sa2 = Math.sin(a / 2.0);
    sb2 = Math.sin(b / 2.0);
    d = 2 * EARTH_RADIUS
        * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(latitude1)
        * Math.cos(latitude2) * sb2 * sb2));

    return Math.ceil(d);
  }

}
