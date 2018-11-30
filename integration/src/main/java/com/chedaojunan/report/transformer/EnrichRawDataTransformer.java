package com.chedaojunan.report.transformer;

import java.util.*;

import com.chedaojunan.report.utils.ReadProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.processor.Cancellable;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import com.chedaojunan.report.client.AutoGraspApiClient;
import com.chedaojunan.report.model.AutoGraspRequest;
import com.chedaojunan.report.model.FixedFrequencyAccessGpsData;
import com.chedaojunan.report.model.FixedFrequencyGpsData;
import com.chedaojunan.report.model.FixedFrequencyIntegrationData;
import com.chedaojunan.report.utils.KafkaConstants;
import com.chedaojunan.report.utils.SampledDataCleanAndRet;

public class EnrichRawDataTransformer implements Transformer<Windowed<String>, ArrayList<FixedFrequencyGpsData>, KeyValue<String, ArrayList<FixedFrequencyIntegrationData>>> {

  private static final String storeName;

  private static Properties kafkaProperties = null;

  private ProcessorContext context;

  private static int schedulePunctuateInMilliSeconds;

  static {
    kafkaProperties = ReadProperties.getProperties(KafkaConstants.PROPERTIES_FILE_NAME);
    schedulePunctuateInMilliSeconds = Integer.parseInt(
            kafkaProperties.getProperty(KafkaConstants.KAFKA_WINDOW_DURATION)
    ) * 60000;
//    schedulePunctuateInMilliSeconds = 60000; // for test only
    storeName = kafkaProperties.getProperty(KafkaConstants.DEDUP_STORE_NAME);
  }

  /**
   * Key: windowStart-windowEnd-devideId
   * Value: list of raw data (FixedFrequencyGpsData) belonging to the same device within the current window
   */

  private KeyValueStore<String, ArrayList<FixedFrequencyGpsData>> rawDataStore;

  // windowStart-windowEnd as the key
  private HashMap<String, Cancellable> schedulerMap;

  private static AutoGraspApiClient autoGraspApiClient;

  public EnrichRawDataTransformer() {

  }

  @Override
  @SuppressWarnings("unchecked")
  public void init(final ProcessorContext context) {
    this.context = context;
    rawDataStore = (KeyValueStore<String, ArrayList<FixedFrequencyGpsData>>) context.getStateStore(storeName);
    schedulerMap = new HashMap<>();
    autoGraspApiClient = AutoGraspApiClient.getInstance();
  }

  @Override
  public KeyValue<String, ArrayList<FixedFrequencyIntegrationData>> punctuate(long timestamp) {
    // Not needed
    return null;
  }

  @Override
  public void close() {
    // Note: The store should NOT be closed manually here via `rawDataStore.close()`!
    // The Kafka Streams API will automatically close stores when necessary.
  }

  public KeyValue<String, ArrayList<FixedFrequencyIntegrationData>> transform(final Windowed<String> windowedKey, final ArrayList<FixedFrequencyGpsData> value) {
    String windowedDeviceId = composeWindowedDeviceId(windowedKey);
    String windowId = getWindowIdString(windowedKey);
    int newSize = value.size();
    if (!schedulerMap.containsKey(windowId)) {
      System.out.println("add to schedulerMap: " + windowedDeviceId + ", " + newSize);
      Cancellable newCs = context.schedule(
          schedulePunctuateInMilliSeconds,
          PunctuationType.WALL_CLOCK_TIME,
          (timestamp) -> checkStateStoreAndTransform(
              windowedKey,
              windowId,
              windowedDeviceId
          )
      );
      schedulerMap.put(windowId, newCs);
    }

    ArrayList<FixedFrequencyGpsData> existList = rawDataStore.get(windowedDeviceId);
    if (CollectionUtils.isEmpty(existList) || newSize > existList.size()) {
      rawDataStore.put(windowedDeviceId, value);
      //System.out.println("rawdatastore put: " + windowedDeviceId + ":" + newSize);
    }

    return null;
  }

  private void checkStateStoreAndTransform(
      Windowed<String> windowedKey,
      String windowId,
      String windowedDeviceId) {
//    System.out.println("inside checkStateStoreAndTransform: " + windowedDeviceId);
//    System.out.println("cancelling schedulerMap: " + windowId);
    if (schedulerMap.containsKey(windowId))
      schedulerMap.get(windowId).cancel();

    ArrayList<FixedFrequencyGpsData> rawDataList = rawDataStore.get(windowedDeviceId);
    if(CollectionUtils.isNotEmpty(rawDataList)) {
      context.forward(windowedDeviceId, operateOnWindowRawData(rawDataList));
      rawDataStore.delete(windowedDeviceId);
    }
    //operateOnWindowRawData(testList).stream().forEach(System.out::println);
    //System.out.println("======");

    /*long nextWindowEnd = windowedKey.window().end() + TimeUnit.SECONDS.toMillis(schedulePunctuateInMilliSeconds);
    String nextWindowKey = String.join("-", windowId.substring(0, windowId.indexOf("-")), String.valueOf(nextWindowEnd));
    KeyValueIterator<String, ArrayList<FixedFrequencyGpsData>> iterator = rawDataStore.range(windowId, nextWindowKey);*/

    KeyValueIterator<String, ArrayList<FixedFrequencyGpsData>> iterator = rawDataStore.all();
    while (iterator.hasNext()) {
      KeyValue<String, ArrayList<FixedFrequencyGpsData>> entry = iterator.next();
      if (!windowedDeviceId.equals(entry.key)) {
        System.out.println("extra data: " + entry.key);
        rawDataList = rawDataStore.get(entry.key);
        if(CollectionUtils.isNotEmpty(rawDataList)){
          context.forward(windowedDeviceId, operateOnWindowRawData(rawDataList));
          rawDataStore.delete(entry.key);
        }
        //operateOnWindowRawData(testList).stream().forEach(System.out::println);
        //System.out.println("======");
      }
    }
    iterator.close();
    context.commit();
  }


  private String composeWindowedDeviceId(Windowed<String> windowedKey) {
    return String.join(KafkaConstants.HYPHEN, String.valueOf(windowedKey.window().start()),
        String.valueOf(windowedKey.window().end()), windowedKey.key());
  }

  private String getWindowIdString(Windowed<String> windowedKey) {
    String windowStart = String.valueOf(windowedKey.window().start());
    String windowEnd = String.valueOf(windowedKey.window().end());
    return String.join(KafkaConstants.HYPHEN, windowStart, windowEnd);
  }

  /*
   * 1. order based on local time
   * 2. convert gps
   * 3. sample
   * 4. API call
   * 5. enrich
   * */

  private List<FixedFrequencyIntegrationData> operateOnWindowRawData(ArrayList<FixedFrequencyGpsData> rawDataList) {
    // need to order the raw data
    Collections.sort(rawDataList, SampledDataCleanAndRet.sortingByLocalTime);

    // 坐标转化接口调用
    List<FixedFrequencyAccessGpsData> coordinateConvertResponseList;
    coordinateConvertResponseList = SampledDataCleanAndRet.getCoordinateConvertResponseList(rawDataList);
    ArrayList<FixedFrequencyAccessGpsData> sampledDataList = SampledDataCleanAndRet.sampleKafkaData(new ArrayList<>(coordinateConvertResponseList));
    AutoGraspRequest autoGraspRequest = SampledDataCleanAndRet.autoGraspRequestRet(sampledDataList);
    List<FixedFrequencyIntegrationData> gaodeApiResponseList = new ArrayList<>();
    if (autoGraspRequest != null)
      gaodeApiResponseList = autoGraspApiClient.getTrafficInfoFromAutoGraspResponse(autoGraspRequest);
    ArrayList<FixedFrequencyIntegrationData> enrichedDataList = SampledDataCleanAndRet.dataIntegration(coordinateConvertResponseList, sampledDataList, gaodeApiResponseList);
    return enrichedDataList;
  }

}

