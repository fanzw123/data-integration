package com.chedaojunan.report.transformer;

import com.chedaojunan.report.model.FixedFrequencyGpsData;
import com.chedaojunan.report.utils.DateUtils;
import com.chedaojunan.report.utils.KafkaConstants;
import com.chedaojunan.report.utils.ReadProperties;
import com.chedaojunan.report.utils.SampledDataCleanAndRet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class GpsDataTransformerSupplier
    implements TransformerSupplier<String, String, KeyValue<String, ArrayList<ArrayList<FixedFrequencyGpsData>>>> {

  private static int schedulePunctuateInMilliSeconds;

  static {
    schedulePunctuateInMilliSeconds = Integer.parseInt(
        ReadProperties.getProperties(KafkaConstants.PROPERTIES_FILE_NAME, KafkaConstants.KAFKA_WINDOW_DURATION)
    ) * 1000;
    //schedulePunctuateInMilliSeconds = 30000; // for test only
  }

  final private String stateStoreName;

  public GpsDataTransformerSupplier(String stateStoreName) {
    this.stateStoreName = stateStoreName;
  }

  @Override
  public Transformer<String, String, KeyValue<String, ArrayList<ArrayList<FixedFrequencyGpsData>>>> get() {
    return new Transformer<String, String, KeyValue<String, ArrayList<ArrayList<FixedFrequencyGpsData>>>>() {

      private KeyValueStore<String, ArrayList<FixedFrequencyGpsData>> stateStore;

      private ProcessorContext context;

      @SuppressWarnings("unchecked")
      @Override
      public void init(ProcessorContext context) {
        stateStore = (KeyValueStore<String, ArrayList<FixedFrequencyGpsData>>) context.getStateStore(stateStoreName);

        this.context = context;
        this.context.schedule(schedulePunctuateInMilliSeconds, PunctuationType.WALL_CLOCK_TIME, (timestamp) -> {
          //LocalDateTime dateTime =
          //    Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
          //System.out.println("timestamp: " + dateTime.toString());
          ArrayList<ArrayList<FixedFrequencyGpsData>> allDeviceAccessDataList = outputToDownstream();

          if (allDeviceAccessDataList.size() > 0 && allDeviceAccessDataList.get(0).size() > 0) {
            context.forward(timestamp, allDeviceAccessDataList);

          }

          context.commit();
        });
      }

      public ArrayList<ArrayList<FixedFrequencyGpsData>> outputToDownstream() {
        KeyValueIterator<String, ArrayList<FixedFrequencyGpsData>> iter = this.stateStore.all();
        ArrayList<String> currentEventTimeWindowList = new ArrayList<>();
        ArrayList<ArrayList<FixedFrequencyGpsData>> allDeviceAccessDataList = new ArrayList<>();
        while (iter.hasNext()) {
          KeyValue<String, ArrayList<FixedFrequencyGpsData>>  entry = iter.next();
          if (!entry.key.endsWith("previous")) {
            String previousWindowKey = String.join(KafkaConstants.HYPHEN, entry.key, "previous");
            ArrayList<FixedFrequencyGpsData> multipleWindowAccessDataList = entry.value;
            ArrayList<ArrayList<FixedFrequencyGpsData>> windowedAccessDataLists = new ArrayList<>(
                multipleWindowAccessDataList
                    .stream()
                    .collect(
                        Collectors.groupingBy(accessData ->
                                DateUtils.roundMilliSecondToNextMinute(accessData.getServerTime()),
                            Collectors.toCollection(ArrayList::new)
                        )
                    )
                    .values());

            allDeviceAccessDataList.addAll(windowedAccessDataLists);
            windowedAccessDataLists
                .stream()
                .forEach(dataList -> currentEventTimeWindowList.add(DateUtils.roundMilliSecondToNextMinute(dataList.get(0).getServerTime())));

            currentEventTimeWindowList.sort(Comparator.naturalOrder());
            int listSize = currentEventTimeWindowList.size();
            int index = listSize > 1 ? listSize - 1 : 0;
            String latestEventTimeWindow = currentEventTimeWindowList.get(index);
            //System.out.println("latestEventTimeWindow: " + latestEventTimeWindow);
//            ArrayList<FixedFrequencyAccessData> backToStateStoreDataList = windowedAccessDataLists
//                .stream()
//                .filter(accessDataList -> DateUtils.roundMilliSecondToNextMinute(accessDataList.get(0).getServerTime()).equals(latestEventTimeWindow))
//                .collect(Collectors.toCollection(ArrayList::new))
//                .get(0);


            ArrayList<ArrayList<FixedFrequencyGpsData>> lists = windowedAccessDataLists
                    .stream()
                    .filter(accessDataList -> DateUtils.roundMilliSecondToNextMinute(accessDataList.get(0).getServerTime()).equals(latestEventTimeWindow))
                    .collect(Collectors.toCollection(ArrayList::new));

            ArrayList<FixedFrequencyGpsData> backToStateStoreDataList = null;
            if (lists.size()!=0) {
                backToStateStoreDataList = lists.get(0);
            }
            //System.out.println("backToStateStoreDataList");
            //backToStateStoreDataList.stream().map(FixedFrequencyAccessData::getTripId).forEach(System.out::print);
            //System.out.println();
            stateStore.delete(entry.key);
            stateStore.put(previousWindowKey, backToStateStoreDataList);
          }
        }
        iter.close();
        return allDeviceAccessDataList;

      }

      @Override
      public KeyValue<String, ArrayList<ArrayList<FixedFrequencyGpsData>>> transform(String key, String value) {
        //Optional<ArrayList<String>> eventList = Optional.ofNullable(stateStore.get(value));
        FixedFrequencyGpsData accessData = SampledDataCleanAndRet.convertToFixedGpsDataPojo(value);
        //String tempKey = key+"-"+accessData.getDeviceId()+"-"+accessData.getTripId();
        //System.out.println("key: " + tempKey);
        //String dataKey = String.join(KafkaConstants.HYPHEN, key, accessData.getDeviceId());
        String stateStoreKey = accessData.getDeviceId();
        //String currentWindowKey = key;
        String previousWindowKey = String.join(KafkaConstants.HYPHEN, stateStoreKey, "previous");

        ArrayList<FixedFrequencyGpsData> currentEventList = stateStore.get(stateStoreKey);
        ArrayList<FixedFrequencyGpsData> previousEventList = stateStore.get(previousWindowKey);

        if (CollectionUtils.isEmpty(previousEventList) ||
            (CollectionUtils.isNotEmpty(previousEventList) && (!previousEventList.contains(accessData)))){
          if (CollectionUtils.isEmpty(currentEventList))
            stateStore.put(stateStoreKey, new ArrayList<>(Arrays.asList(accessData)));
          else if (!currentEventList.contains(accessData)){
            currentEventList.add(accessData);
            stateStore.put(stateStoreKey, currentEventList);
          }
        }

        return null;
      }

      @Override
      public KeyValue<String, ArrayList<ArrayList<FixedFrequencyGpsData>>> punctuate(long timestamp) {
        // Not needed
        return null;
      }

      @Override
      public void close() {
        // Note: The store should NOT be closed manually here via `stateStore.close()`!
        // The Kafka Streams API will automatically close stores when necessary.
        stateStore.close();
      }
    };
  }
}
