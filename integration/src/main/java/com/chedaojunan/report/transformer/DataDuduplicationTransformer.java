package com.chedaojunan.report.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

public class DataDuduplicationTransformer implements Transformer<Windowed<String>, ArrayList<FixedFrequencyGpsData>, KeyValue<String, ArrayList<FixedFrequencyIntegrationData>>> {

  private static final String storeName = "testStateStore";

  private ProcessorContext context;

  private static int schedulePunctuateInMilliSeconds;

  static {
    /*schedulePunctuateInMilliSeconds = Integer.parseInt(
        ReadProperties.getProperties(KafkaConstants.PROPERTIES_FILE_NAME, KafkaConstants.KAFKA_WINDOW_DURATION)
    ) * 1000;*/
    schedulePunctuateInMilliSeconds = 10000; // for test only
  }

  /**
   * Key: event unique identifier - deviceId+local time
   * Value: timestamp (event-time) of the corresponding event when the event ID was seen for the
   * first time
   */
  //private WindowStore<E, Long> eventIdStore;
  // windowStart-windowEnd-devideId as the key
  private KeyValueStore<String, ArrayList<FixedFrequencyGpsData>> rawDataStore;

  /*private final long leftDurationMs;
  private final long rightDurationMs;

  private final KeyValueMapper<K, V, E> idExtractor;*/

  // windowStart-windowEnd as the key
  private HashMap<String, Cancellable> schedulerMap;

  private static AutoGraspApiClient autoGraspApiClient;

  /**
   * //@param maintainDurationPerEventInMs how long to "remember" a known event (or rather, an event
   * ID), during the time of which any incoming duplicates of
   * the event will be dropped, thereby de-duplicating the
   * input.
   * //@param idExtractor extracts a unique identifier from a record by which we de-duplicate input
   * records; if it returns null, the record will not be considered for
   * de-duping but forwarded as-is.
   */
  /*DataDuduplicationTransformer(long maintainDurationPerEventInMs, KeyValueMapper<K, V, E> idExtractor) {
    if (maintainDurationPerEventInMs < 1) {
      throw new IllegalArgumentException("maintain duration per event must be >= 1");
    }
    leftDurationMs = maintainDurationPerEventInMs / 2;
    System.out.println("leftDurationMs: " + leftDurationMs);
    rightDurationMs = maintainDurationPerEventInMs - leftDurationMs;
    System.out.println("rightDurationMs: " + rightDurationMs);
    this.idExtractor = idExtractor;
  }*/
  public DataDuduplicationTransformer() {

  }

  @Override
  @SuppressWarnings("unchecked")
  public void init(final ProcessorContext context) {
    this.context = context;
    //eventIdStore = (WindowStore<E, Long>) context.getStateStore(storeName);
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
    // Note: The store should NOT be closed manually here via `eventIdStore.close()`!
    // The Kafka Streams API will automatically close stores when necessary.
  }

  //assume key is windowedId + deviceId (Windowed<String>)
  public KeyValue<String, ArrayList<FixedFrequencyIntegrationData>> transform(final Windowed<String> windowedKey, final ArrayList<FixedFrequencyGpsData> value) {
    // probably this key should be windowstart + end
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

  // keyStoreKey: windowId + deviceId
  // pass windowId directly?
  private void checkStateStoreAndTransform(
      Windowed<String> windowedKey,
      String windowedDeviceId) {
    String windowId = getWindowIdString(windowedKey);
    System.out.println("inside checkStateStoreAndTransform: " + windowedDeviceId);
    //if(shouldCreateList(windowEndTime)) {
    System.out.println("cancelling schedulerMap: " + windowId);
    if (schedulerMap.containsKey(windowId))
      schedulerMap.get(windowId).cancel();

    ArrayList<FixedFrequencyGpsData> rawDataList = rawDataStore.get(windowedDeviceId);
    //ArrayList<String> testList = ((ArrayList<String>) rawDataList);
    if(CollectionUtils.isNotEmpty(rawDataList))
      context.forward(windowedDeviceId, operateOnWindowRawData(rawDataList));
    //operateOnWindowRawData(testList).stream().forEach(System.out::println);
    //System.out.println("======");

    //System.out.println("count: " + rawDataStore.approximateNumEntries());
    long nextWindowEnd = windowedKey.window().end() + TimeUnit.SECONDS.toMillis(schedulePunctuateInMilliSeconds);
    //System.out.println("nextWindowEnd: " + nextWindowEnd);
    //Windowed<K> nextWindow = new Windowed<>();
    String nextWindowKey = String.join("-", windowId.substring(0, windowId.indexOf("-")), String.valueOf(nextWindowEnd));
    KeyValueIterator<String, ArrayList<FixedFrequencyGpsData>> iterator = rawDataStore.range(windowId, nextWindowKey);
    while (iterator.hasNext()) {
      KeyValue<String, ArrayList<FixedFrequencyGpsData>> entry = iterator.next();
      if (!windowedDeviceId.equals(entry.key)) {
        System.out.println("extra data: " + entry.key);
        rawDataList = rawDataStore.get(entry.key);
        if(CollectionUtils.isNotEmpty(rawDataList))
          context.forward(windowedDeviceId, operateOnWindowRawData(rawDataList));
        //operateOnWindowRawData(testList).stream().forEach(System.out::println);
        //System.out.println("======");
      }
    }
    iterator.close();
    context.commit();
    //}
  }


  private String composeWindowedDeviceId(Windowed<String> windowedKey) {
    return String.join(KafkaConstants.HYPHEN, String.valueOf(windowedKey.window().start()),
        String.valueOf(windowedKey.window().end()), windowedKey.key());
  }

  // store closed exception?
  /*private boolean isDuplicate(final E eventId) {
    long eventTime = context.timestamp();
    WindowStoreIterator<Long> timeIterator = eventIdStore.fetch(
        eventId,
        eventTime - leftDurationMs,
        eventTime + rightDurationMs);
    boolean isDuplicate = timeIterator.hasNext();
    timeIterator.close();
    return isDuplicate;
  }

  private void updateTimestampOfExistingEventToPreventExpiry(final E eventId, long newTimestamp) {
    eventIdStore.put(eventId, newTimestamp, newTimestamp);
  }

  private void rememberNewEvent(final E eventId, long timestamp) {
    eventIdStore.put(eventId, timestamp, timestamp);
  }*/

  private String getWindowIdString(Windowed<String> windowedKey) {
    String windowStart = String.valueOf(windowedKey.window().start());
    String windowEnd = String.valueOf(windowedKey.window().end());
    return String.join(KafkaConstants.HYPHEN, windowStart, windowEnd);
  }

  /*
   * 1. convert rawDataString to FixedFrequencyAccessData
   * 2. convert gps
   * 3. sample
   * 4. API call
   * 5. enrich
   * */

  private List<FixedFrequencyIntegrationData> operateOnWindowRawData(ArrayList<FixedFrequencyGpsData> rawDataList) {
    // need to order the raw data

    // 坐标转化接口调用
    List<FixedFrequencyAccessGpsData> coordinateConvertResponseList;
    coordinateConvertResponseList = SampledDataCleanAndRet.getCoordinateConvertResponseList(rawDataList);
    ArrayList<FixedFrequencyAccessGpsData> sampledDataList = SampledDataCleanAndRet.sampleKafkaData(new ArrayList<>(coordinateConvertResponseList));
    AutoGraspRequest autoGraspRequest = SampledDataCleanAndRet.autoGraspRequestRet(sampledDataList);
    List<FixedFrequencyIntegrationData> gaodeApiResponseList = new ArrayList<>();
    if (autoGraspRequest != null)
      gaodeApiResponseList = autoGraspApiClient.getTrafficInfoFromAutoGraspResponse(autoGraspRequest);
    /*List<String> enrichedDataList = SampledDataCleanAndRet.dataIntegration(coordinateConvertResponseList, sampledDataList, gaodeApiResponseList)
        .stream()
        .map(fixedFrequencyIntegrationData -> fixedFrequencyIntegrationData.toString())
        .collect(Collectors.toList());*/
    ArrayList<FixedFrequencyIntegrationData> enrichedDataList = SampledDataCleanAndRet.dataIntegration(coordinateConvertResponseList, sampledDataList, gaodeApiResponseList);
    return enrichedDataList;
  }

}

