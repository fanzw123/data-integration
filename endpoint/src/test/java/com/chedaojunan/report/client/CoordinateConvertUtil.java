package com.chedaojunan.report.client;

import com.chedaojunan.report.common.Constants;

public class CoordinateConvertUtil {
    private static  double PI     = Math.PI;
    private static  double AXIS   = 6378245.0;  //
    private static  double OFFSET = 0.00669342162296594323;  //(a^2 - b^2) / a^2
    private static  double X_PI   = PI * 3000.0 / 180.0;

    public static void main(String[] args) {
        // 116.39806876799298,39.90830142538959
        // 116.397826,39.90894374525333
        // 116.3978262189183,39.90894374525333
        String lon = "116.391910";
        String lat = "39.906050";
        lon = "";
        lat = "";
//        double [] arr2 = bd092GCJ(39.9151, 116.4039595 );
//        System.out.println(arr2[0]+ "," + arr2[1]);
//        double [] arr = bd092WGS(39.9151, 116.4039595 );
//        System.out.println(arr[0]+ "," + arr[1]);

        // 39.9175257000,116.4952822100
//        39.9174057000,116.4952822100
//        39.9172457000,116.4952562100
//        116.53215789794922,39.84053421020508|116.53213500976562,39.840518951416016|116.53211975097656,39.84050369262695
        double [] arr = wgs2GCJ(39.84050369262695,116.53211975097656);
        System.out.println(arr[1]+ "," + arr[0]);
    }

    //GCJ-02=>BD09 火星坐标系=>百度坐标系
    public static double[] gcj2BD09(double glat, double glon){
        double x = glon;
        double y = glat;
        double[] latlon = new double[2];
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * X_PI);
        latlon[0] = z * Math.sin(theta) + 0.006;
        latlon[1] = z * Math.cos(theta) + 0.0065;
        return latlon;
    }

    //BD09=>GCJ-02 百度坐标系=>火星坐标系
    public static double[] bd092GCJ(double glat, double glon){
        double x = glon - 0.0065;
        double y = glat - 0.006;
        double[] latlon = new double[2];
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        latlon[0] = z * Math.sin(theta);
        latlon[1] = z * Math.cos(theta);
        return latlon;
    }
    //BD09=>WGS84 百度坐标系=>地球坐标系
    public static double[] bd092WGS(double glat, double glon){
        double[] latlon = bd092GCJ(glat,glon);
        return gcj2WGS(latlon[0],latlon[1]);
    }
    // WGS84=》BD09   地球坐标系=>百度坐标系
    public static double[] wgs2BD09(double wgLat, double wgLon) {
        double[] latlon = wgs2GCJ(wgLat,wgLon);
        return gcj2BD09(latlon[0],latlon[1]);
    }

    // WGS84=》GCJ02   地球坐标系=>火星坐标系
    public static double[] wgs2GCJ(double wgLat, double wgLon) {
        double[] latlon  = new double[2];
        if (outOfChina(wgLat, wgLon)){
            latlon[0] = wgLat;
            latlon[1] = wgLon;
            return latlon;
        }
        double[] deltaD =  delta(wgLat,wgLon);
        latlon[0] = wgLat + deltaD[0];
        latlon[1] = wgLon + deltaD[1];
        return latlon;
    }
    //GCJ02=>WGS84   火星坐标系=>地球坐标系(粗略)
    public static double[] gcj2WGS(double glat,double glon){
        double[] latlon  = new double[2];
        if (outOfChina(glat, glon)){
            latlon[0] = glat;
            latlon[1] = glon;
            return latlon;
        }
        double[] deltaD =  delta(glat,glon);
        latlon[0] = glat - deltaD[0];
        latlon[1] = glon - deltaD[1];
        return latlon;
    }
    //GCJ02=>WGS84   火星坐标系=>地球坐标系（精确）
    public static double[] gcj2WGSExactly(double gcjLat,double gcjLon){
        double initDelta = 0.01;
        double threshold = 0.000000001;
        double dLat = initDelta, dLon = initDelta;
        double mLat = gcjLat - dLat, mLon = gcjLon - dLon;
        double pLat = gcjLat + dLat, pLon = gcjLon + dLon;
        double wgsLat, wgsLon, i = 0;
        while (true) {
            wgsLat = (mLat + pLat) / 2;
            wgsLon = (mLon + pLon) / 2;
            double[] tmp = wgs2GCJ(wgsLat, wgsLon);
            dLat = tmp[0] - gcjLat;
            dLon = tmp[1] - gcjLon;
            if ((Math.abs(dLat) < threshold) && (Math.abs(dLon) < threshold))
                break;

            if (dLat > 0) pLat = wgsLat; else mLat = wgsLat;
            if (dLon > 0) pLon = wgsLon; else mLon = wgsLon;

            if (++i > 10000) break;
        }
        double[] latlon = new double[2];
        latlon[0] = wgsLat;
        latlon[1] = wgsLon;
        return latlon;
    }

    //两点距离
    public static double distance(double latA, double logA, double latB,double  logB){
        int earthR = 6371000;
        double x = Math.cos(latA*Math.PI/180) * Math.cos(latB*Math.PI/180) * Math.cos((logA-logB)*Math.PI/180);
        double y = Math.sin(latA*Math.PI/180) * Math.sin(latB*Math.PI/180);
        double s = x + y;
        if (s > 1)
            s = 1;
        if (s < -1)
            s = -1;
        double alpha = Math.acos(s);
        double distance = alpha * earthR;
        return distance;
    }

    public static double[] delta(double wgLat, double wgLon){
        double[] latlng  = new double[2];
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - OFFSET * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((AXIS * (1 - OFFSET)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (AXIS / sqrtMagic * Math.cos(radLat) * PI);
        latlng[0] =dLat;
        latlng[1] =dLon;
        return latlng;
    }

    public static boolean outOfChina(double lat, double lon){
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    public static double transformLat(double x, double y){
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    public static double transformLon(double x, double y){
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    // WGS84=》GCJ02 地球坐标系=>火星坐标系(一次多个gps转化)
    public static String retLocations(String latLonStr) {
        if (latLonStr.length()>0){
            String [] latLonStrs = latLonStr.split(Constants.PIPE);
            for (int i = 0; i < latLonStrs.length; i++) {
                double lat = Double.parseDouble(latLonStrs[i].split(Constants.COMMA)[0]);
                double lon = Double.parseDouble(latLonStrs[i].split(Constants.COMMA)[1]);
                double [] arr = wgs2GCJ(lat,lon);
            }
        }

        return null;
    }

}