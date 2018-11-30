package com.chedaojunan.report.utils;

import com.aliyun.datahub.DatahubClient;
import com.aliyun.datahub.DatahubConfiguration;
import com.aliyun.datahub.auth.AliyunAccount;
import com.aliyun.datahub.common.data.RecordSchema;
import com.aliyun.datahub.exception.InvalidCursorException;
import com.aliyun.datahub.model.GetCursorResult;
import com.aliyun.datahub.model.GetRecordsResult;
import com.aliyun.datahub.model.ListShardResult;
import com.aliyun.datahub.model.RecordEntry;
import com.aliyun.datahub.wrapper.Topic;

import java.io.IOException;
import java.util.List;

public class ReadDatahubTest {

    private static String accessId = "LTAI9cJvmFfwjxCs";
    private static String accessKey = "3R4Ujs6edemUDQ3lnHiPon7BPyPsrr";
    private static String endpoint = "http://dh-cn-beijing.aliyuncs.com";
    private DatahubClient client;
    private static String projectName = "cdja_bigdata_test";
    private static String topicName = "r_busicar_gps_data_di";
    private static String topicShardNum = "10";

    public ReadDatahubTest() {
        DatahubConfiguration conf = new DatahubConfiguration(new AliyunAccount(accessId, accessKey), endpoint);
        client = new DatahubClient(conf);
    }

    // 获取数据到从datahub指定topic
    public void getRecords() throws IOException{
        ListShardResult listShardResult = client.listShard(projectName, topicName);
        for (int i = 0; i < Integer.parseInt(topicShardNum); i++) {
            String shardId = listShardResult.getShards().get(i % Integer.parseInt(topicShardNum)).getShardId();

            // TODO 可以获取指定时间内的第一条数据Cursor
            GetCursorResult cursorRs = client.getCursor(projectName, topicName, shardId,
                    System.currentTimeMillis() - 2 * 60 * 1000);

            Topic topic = Topic.Builder.build(projectName, topicName, client);
            RecordSchema schema = topic.getRecordSchema();

            long begin = System.currentTimeMillis();
            int limit = 100000;
            String cursor = cursorRs.getCursor();
            try {
                GetRecordsResult recordRs = client.getRecords(projectName, topicName, shardId, cursor, limit, schema);
                List<RecordEntry> recordEntries = recordRs.getRecords();
                if (recordEntries.size() > 0) {
                    for (int j = 0; j < recordEntries.size(); j++) {
                        String str = recordEntries.get(j).toJsonNode().get("Data").get(0).toString().replace("\"","");
//                        if (str.equals("04test0001")){
                            System.out.println(recordEntries.get(j).toJsonNode().get("Data").toString());
//                        }
                    }
                }

                // 拿到下一个游标
            } catch (InvalidCursorException ex) {
                long end = System.currentTimeMillis();
                System.out.println(end - begin);
            }
        }
    }

    public static void main(String[] args) throws IOException{
        ReadDatahubTest wirteDatahub = new ReadDatahubTest();
        wirteDatahub.getRecords();
    }
}
