package com.chedaojunan.report.utils;

import com.chedaojunan.report.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SampledDataCleanAndRetTest {

    List<FixedFrequencyAccessGpsData> batchList;
    ArrayList<FixedFrequencyAccessGpsData> listSample = null;
    FixedFrequencyAccessGpsData gpsData;

    ArrayList<FixedFrequencyAccessGpsData> batchList02;
    ArrayList<FixedFrequencyAccessGpsData> listSample02 = null;
    FixedFrequencyAccessGpsData gpsData02;

    ArrayList<FixedFrequencyAccessGpsData> batchList03;
    ArrayList<FixedFrequencyAccessGpsData> listSample03 = null;
    FixedFrequencyAccessGpsData gpsData03;
    List<FixedFrequencyIntegrationData> gaodeApiResponseList = null;

    SampledDataCleanAndRet sampledDataCleanAndRet = new SampledDataCleanAndRet();

    @Before
    public void init() throws IOException {
        batchList = new ArrayList<>();
        // accessData数据设置
        for (int i = 0; i < 6; i++) {
            gpsData = new FixedFrequencyAccessGpsData();
            gpsData.setDeviceId("70211191");
            gpsData.setDeviceImei("64691168800");
            gpsData.setTripId(i + 100 + "");
            gpsData.setLocalTime("1521478861000");
            gpsData.setServerTime(1521478866000L + i + "");
            gpsData.setLatitude(39.00);
            gpsData.setLongitude(129.01);
            gpsData.setAltitude(30.98);
            gpsData.setDirection(98.00);
            gpsData.setGpsSpeed(98.00);
            batchList.add(gpsData);
        }

        batchList02 = new ArrayList<>();
        // accessData02数据设置
        for (int i = 0; i < 6; i++) {
            gpsData02 = new FixedFrequencyAccessGpsData();
            gpsData02.setDeviceId("70211191");
            gpsData02.setDeviceImei("64691168800");
            gpsData02.setTripId(i + 100 + "");
            gpsData02.setLocalTime("1521478861000");
            gpsData02.setServerTime(1521478866000L + i + "");
            gpsData02.setLatitude(39.00 + i);
            gpsData02.setLongitude(129.01 + i);
            gpsData02.setAltitude(30.98);
            gpsData02.setDirection(98.00);
            gpsData02.setGpsSpeed(98.00);
            batchList02.add(gpsData02);
        }

        batchList03 = new ArrayList<>();
        listSample03 = new ArrayList<>();

        GaoDeFusionReturn gaoDeFusionReturn;
        FixedFrequencyIntegrationData integrationData;
        gaodeApiResponseList = new ArrayList<>();
        // accessData03数据设置
        for (int i = 0; i < 6; i++) {
            gpsData03 = new FixedFrequencyAccessGpsData();
            gpsData03.setDeviceId("70211191");
            gpsData03.setDeviceImei("64691168800");
            gpsData03.setTripId(i + 100 + "");
            gpsData03.setLocalTime("1521478861000");
            gpsData03.setServerTime(1521478866000L + i + "");
            gpsData03.setLatitude(39.917066 + i);
            gpsData03.setLongitude(116.496167 + i);
            gpsData03.setAltitude(30.98);
            gpsData03.setDirection(98.00);
            gpsData03.setGpsSpeed(98.00);
            if ( i == 0 || i == 2 || i == 4) {
                listSample03.add(gpsData03);

                gaoDeFusionReturn = new GaoDeFusionReturn();
                gaoDeFusionReturn.setRoad_api_status(1);
                gaoDeFusionReturn.setCrosspoint("crosspoint");
                gaoDeFusionReturn.setRoadname("roadname");
                gaoDeFusionReturn.setRoadlevel(1);
                gaoDeFusionReturn.setMaxspeed(120);
                gaoDeFusionReturn.setIntersection("intersection");
                gaoDeFusionReturn.setIntersectiondistance("intersectiondistance");
                gaoDeFusionReturn.setTraffic_request_time("1521266461000");
                gaoDeFusionReturn.setTraffic_request_id("traffic_request_id");
                gaoDeFusionReturn.setTraffic_api_status(1);
                // json格式
                String congestion_info = "{\"description\":\"北三环路：从安华桥到苏州桥严重拥堵，蓟门桥附近自西向东行驶缓慢；北四环路：学院桥附近自东向西严重拥堵，安慧桥附近自东向西行驶缓慢；京藏高速：北沙滩桥附近出京方向行驶缓慢。\",\"evaluation\":{\"expedite\":\"44.44%\",\"congested\":\"44.44%\",\"blocked\":\"11.11%\",\"unknown\":\"0.01%\",\"status\":\"3\",\"description\":\"中度拥堵\"}}";
                gaoDeFusionReturn.setCongestion_info(congestion_info);


                integrationData = new FixedFrequencyIntegrationData(gpsData03, gaoDeFusionReturn);
                gaodeApiResponseList.add(integrationData);
            }
            batchList03.add(gpsData03);
        }
    }

    @Test
    public void testSampleKafkaDataGpsSame() {
        listSample = sampledDataCleanAndRet.sampleKafkaData(batchList);
        Assert.assertEquals(3, listSample.size());
    }

    @Test
    public void testSampleKafkaDataGpsDiff() {
        listSample02 = sampledDataCleanAndRet.sampleKafkaData(batchList02);
        Assert.assertEquals(3, listSample02.size());
    }

    @Test
    public void testAutoGraspRequestRet() {
        AutoGraspRequest autoGraspRequest = sampledDataCleanAndRet.autoGraspRequestRet(sampledDataCleanAndRet.sampleKafkaData(batchList));
        Assert.assertNotNull(autoGraspRequest);
    }

    @Test
    public void testDataIntegrationGaoDeNoResponseData() throws IOException{
        // TODO test
        List<FixedFrequencyIntegrationData> gaodeApiResponseList = new ArrayList<>();
//        listSample = sampledDataCleanAndRet.sampleKafkaData(batchList);
        List integrationDataList = sampledDataCleanAndRet.dataIntegration(batchList03, listSample03, gaodeApiResponseList);
        Assert.assertNotNull(integrationDataList);
        Assert.assertEquals(6, integrationDataList.size());
    }

    @Test
    public void testDataIntegrationGaoDeWithResponseData() throws IOException{
        // TODO test
//        List<FixedFrequencyIntegrationData> gaodeApiResponseList = new ArrayList<>();
//        listSample = sampledDataCleanAndRet.sampleKafkaData(batchList);
        List integrationDataList = sampledDataCleanAndRet.dataIntegration(batchList03, listSample03, gaodeApiResponseList);
        Assert.assertNotNull(integrationDataList);
        Assert.assertEquals(6, integrationDataList.size());
    }

}