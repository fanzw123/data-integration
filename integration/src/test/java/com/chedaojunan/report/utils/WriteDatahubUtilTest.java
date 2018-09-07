package com.chedaojunan.report.utils;

import com.chedaojunan.report.model.DatahubDeviceData;
import org.junit.Before;

import java.io.IOException;
import java.util.ArrayList;

public class WriteDatahubUtilTest {

    private ArrayList<DatahubDeviceData> integrationDataList = null;

    @Before
    public void init() throws IOException {
        DatahubDeviceData integrationData;
        integrationData = new DatahubDeviceData();
        integrationData.setDeviceId("70211191");
        integrationData.setDeviceImei("64691168800");
        integrationData.setTripId("100");
        integrationData.setLocalTime("1521478861000");
        integrationData.setServerTime("");
        integrationData.setLatitude(39.00);
        integrationData.setLongitude(129.01);
        integrationData.setAltitude(30.98);
        integrationData.setDirection(98.00);
        integrationData.setGpsSpeed(98.00);

        integrationData.setRoadApiStatus(1);
        integrationData.setCrosspoint("crosspoint");
        integrationData.setRoadName("roadname");
        integrationData.setRoadLevel(1);
        integrationData.setMaxSpeed(120);
        integrationData.setIntersection("intersection");
        integrationData.setIntersectionDistance("intersectiondistance");
        integrationData.setTrafficRequestTimesamp("1521266461000");
        integrationData.setTrafficRequestId("traffic_request_id");
        integrationData.setTrafficApiStatus(1);

        // 增加 adCode和townCode
        integrationData.setAdCode("101010");
        integrationData.setTownCode("1010101010");

        // json格式
        String congestion_info = "{\"description\":\"北三环路：从安华桥到苏州桥严重拥堵，蓟门桥附近自西向东行驶缓慢；北四环路：学院桥附近自东向西严重拥堵，安慧桥附近自东向西行驶缓慢；京藏高速：北沙滩桥附近出京方向行驶缓慢。\",\"evaluation\":{\"expedite\":\"44.44%\",\"congested\":\"44.44%\",\"blocked\":\"11.11%\",\"unknown\":\"0.01%\",\"status\":\"3\",\"description\":\"中度拥堵\"}}";
        integrationData.setCongestionInfo(congestion_info);

        integrationDataList = new ArrayList();
        integrationDataList.add(integrationData);
    }

//    @Test
//    public void testPutRecords() {
//        WriteDatahubUtil writeDatahubUtil = new WriteDatahubUtil();
//        int failNum = writeDatahubUtil.putRecords(integrationDataList);
//        Assert.assertEquals(0, failNum);
//    }

}
