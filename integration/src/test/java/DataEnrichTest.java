import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cdja.cloud.data.proto.GpsProto;
import com.chedaojunan.report.client.AutoGraspApiClient;
import com.chedaojunan.report.client.RegeoClient;
import com.chedaojunan.report.model.FixedFrequencyGpsData;
import com.chedaojunan.report.model.FixedFrequencyIntegrationData;
import com.chedaojunan.report.serdes.ArrayListSerde;
import com.chedaojunan.report.serdes.SerdeFactory;
import com.chedaojunan.report.utils.FixedFrequencyGpsDataTimestampExtractor;
import com.chedaojunan.report.utils.KafkaConstants;
import com.chedaojunan.report.utils.ProtoSeder;
import com.chedaojunan.report.utils.ReadProperties;
import com.chedaojunan.report.utils.SampledDataCleanAndRet;
import com.chedaojunan.report.utils.WriteDatahubUtil;

import com.chedaojunan.report.transformer.DataDuduplicationTransformer;

public class DataEnrichTest {

  private static final Logger logger = LoggerFactory.getLogger(DataEnrichTest.class);
  private static final long TIMEOUT_PER_GAODE_API_REQUEST_IN_NANO_SECONDS = 10000000000L;

  private static Properties kafkaProperties = null;

  private static final String dedupStoreName = "testStateStore";

  private static final int kafkaWindowLengthInSeconds;

  private static final Serde<String> stringSerde;

  private static final Serde<FixedFrequencyGpsData> fixedFrequencyGpsDataSerde;

  private static final Serde<FixedFrequencyIntegrationData> fixedFrequencyIntegrationSerde;

  private static final ArrayListSerde<String> arrayListStringSerde;

  private static final ArrayListSerde<FixedFrequencyGpsData> arrayListFixedFrequencyGpsSerde;

  private static final ArrayListSerde<FixedFrequencyIntegrationData> arrayListFixedFrequencyIntegrationSerde;

  private static RegeoClient regeoClient;

  private static AutoGraspApiClient autoGraspApiClient;

  static {
    kafkaProperties = ReadProperties.getProperties(KafkaConstants.PROPERTIES_FILE_NAME);
    stringSerde = Serdes.String();
    Map<String, Object> serdeProp = new HashMap<>();
    fixedFrequencyGpsDataSerde = SerdeFactory.createSerde(FixedFrequencyGpsData.class, serdeProp);
    fixedFrequencyIntegrationSerde = SerdeFactory.createSerde(FixedFrequencyIntegrationData.class, serdeProp);
    arrayListStringSerde = new ArrayListSerde<>(stringSerde);
    arrayListFixedFrequencyGpsSerde = new ArrayListSerde<>(fixedFrequencyGpsDataSerde);
    arrayListFixedFrequencyIntegrationSerde = new ArrayListSerde<>(fixedFrequencyIntegrationSerde);
    kafkaWindowLengthInSeconds = Integer.parseInt(kafkaProperties.getProperty(KafkaConstants.KAFKA_WINDOW_DURATION));
    autoGraspApiClient = AutoGraspApiClient.getInstance();
    regeoClient = RegeoClient.getInstance();
  }

  public static void main(String[] args) {
    String rawDataTopic = kafkaProperties.getProperty(KafkaConstants.KAFKA_RAW_DATA_TOPIC);
    String outputTopic = kafkaProperties.getProperty(KafkaConstants.KAFKA_OUTPUT_TOPIC);

    /*
     * 1. sample data within the window
     * 2. make gaode API calls
     * 3. enrich raw data within the window and output to the output topic
     */

    final KafkaStreams sampledRawDataStream = buildDataStream(rawDataTopic, outputTopic);

    sampledRawDataStream.start();

    //final KafkaStreams testWriteStream = buildDataStream1(outputTopic);

    //testWriteStream.start();

    // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
    Runtime.getRuntime().addShutdownHook(new Thread(sampledRawDataStream::close));
    //Runtime.getRuntime().addShutdownHook(new Thread(testWriteStream::close));
  }

  private static Properties getStreamConfig() {
    final Properties streamsConfiguration = new Properties();
    String kafkaApplicationName = kafkaProperties.getProperty(KafkaConstants.KAFKA_STREAM_APPLICATION_NAME);
    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaApplicationName);
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaProperties.getProperty(KafkaConstants.KAFKA_BOOTSTRAP_SERVERS));
    //streamsConfiguration.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4);
    streamsConfiguration.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 60000);

    // protoc buffer
    streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, ProtoSeder.class.getName());
    streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

    streamsConfiguration.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, FixedFrequencyGpsDataTimestampExtractor.class);
    streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getProperty(KafkaConstants.AUTO_OFFSET_RESET_CONFIG));
    // disable cache
    streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
    // Use a temporary directory for storing state, which will be automatically removed after the test.
    streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "/Users/qianz/Documents/Misc/Work-beijing/");

    return streamsConfiguration;
  }

  static KafkaStreams buildDataStream(String inputTopic, String outputTopic) {
    final Properties streamsConfiguration = getStreamConfig();

    StreamsBuilder builder = new StreamsBuilder();
    StoreBuilder<KeyValueStore<String, ArrayList<FixedFrequencyGpsData>>> dedupStoreBuilder = Stores.keyValueStoreBuilder(
        Stores.persistentKeyValueStore(dedupStoreName),
        Serdes.String(),
        arrayListFixedFrequencyGpsSerde);

    builder.addStateStore(dedupStoreBuilder);

    WriteDatahubUtil writeDatahubUtil = new WriteDatahubUtil();
    FixedFrequencyGpsData fixedFrequencyGpsData = new FixedFrequencyGpsData();


    KStream<String, GpsProto.Gps> inputStream = builder.stream(inputTopic);

    KStream<Windowed<String>, ArrayList<FixedFrequencyGpsData>> windowedRawData = inputStream
        .groupByKey()
        .windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(kafkaWindowLengthInSeconds)).until(TimeUnit.SECONDS.toMillis(kafkaWindowLengthInSeconds)))
        .aggregate(
            () -> new ArrayList<>(),
            (windowedCarId, record, list) -> {
              if (!list.contains(record)) {
                SampledDataCleanAndRet.convertTofixedFrequencyGpsData(fixedFrequencyGpsData, record);
                list.add(fixedFrequencyGpsData);
              }
              return list;
            },
            Materialized.with(stringSerde, arrayListFixedFrequencyGpsSerde)
        )
        .toStream();

    //windowedRawData.print(Printed.toSysOut());

    windowedRawData
        .transform(
            () -> new DataDuduplicationTransformer(),
            dedupStoreName
        ).print(Printed.toSysOut());
        //.to(outputTopic, Produced.with(stringSerde, arrayListFixedFrequencyIntegrationSerde));


    /*final KStream<String, String> orderedDataStream = kStream
        .map(
            (key, frequencyGps) ->
                new KeyValue<>(frequencyGps.getDeviceId(), frequencyGps)
        )
        .groupByKey()
        .windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(kafkaWindowLengthInSeconds)).until(TimeUnit.SECONDS.toMillis(kafkaWindowLengthInSeconds)))
        .aggregate(
            () -> new ArrayList<>(),
            (windowedCarId, record, list) -> {
              if (!list.contains(record))
                SampledDataCleanAndRet.convertTofixedFrequencyGpsData(fixedFrequencyGpsData,record);
              list.add(fixedFrequencyGpsData.toString());
              return list;
            },
            Materialized.with(stringSerde, arrayListStringSerde)
        )
        .toStream()
        .map((windowedString, accessDataList) -> {
          long windowStartTime = windowedString.window().start();
          long windowEndTime = windowedString.window().end();
          String dataKey = String.join("-", String.valueOf(windowStartTime), String.valueOf(windowEndTime));
          return new KeyValue<>(dataKey, accessDataList);
        })
        .flatMapValues(accessDataList -> accessDataList.stream().collect(Collectors.toList()));

    orderedDataStream.print();
    KStream<String, ArrayList<ArrayList<FixedFrequencyGpsData>>> dedupOrderedDataStream =
        orderedDataStream.transform(new GpsDataTransformerSupplier(rawDataStore.name()), rawDataStore.name());*/

    /*dedupOrderedDataStream
        .flatMapValues(accessDataLists -> {
          ArrayList<DatahubDeviceData> enrichedDataList = new ArrayList<>();
          List<Future<?>> futures = accessDataLists
              .stream()
              .map(
                  accessDataList -> ExternalApiExecutorService.getExecutorService().submit(() -> {
                    // 坐标转化接口调用
                    List<FixedFrequencyAccessGpsData> coordinateConvertResponseList;
                    coordinateConvertResponseList = SampledDataCleanAndRet.getCoordinateConvertResponseList(accessDataList);
//                                coordinateConvertResponseList.sort(SampledDataCleanAndRet.sortingByServerTime);
                    ArrayList<FixedFrequencyAccessGpsData> sampledDataList = SampledDataCleanAndRet.sampleKafkaData(new ArrayList<>(coordinateConvertResponseList));
                    AutoGraspRequest autoGraspRequest = SampledDataCleanAndRet.autoGraspRequestRet(sampledDataList);
//                                System.out.println("apiQuest: " + autoGraspRequest);
                    logger.info("apiQuest:" + autoGraspRequest);
                    List<FixedFrequencyIntegrationData> gaodeApiResponseList = new ArrayList<>();
                    if (autoGraspRequest != null)
                      gaodeApiResponseList = autoGraspApiClient.getTrafficInfoFromAutoGraspResponse(autoGraspRequest);
                    ArrayList<FixedFrequencyIntegrationData> enrichedData = SampledDataCleanAndRet.dataIntegration(coordinateConvertResponseList, sampledDataList, gaodeApiResponseList);

                    ArrayList<DatahubDeviceData> enrichedDataOver = null;
                    if (enrichedData != null) {
                      enrichedDataOver = regeoClient.getRegeoFromResponse(enrichedData);
                    }
                    if (enrichedDataOver != null) {
                      try {
                        // 整合数据入库datahub
                        if (CollectionUtils.isNotEmpty(enrichedDataOver)) {
//                                            System.out.println("write to DataHub: " + Instant.now().toString() + ", enrichedDataOver.size(): " + enrichedDataOver.size());
                          logger.info("write to DataHub: " + Instant.now().toString() + ", enrichedDataOver.size(): " + enrichedDataOver.size());
                          writeDatahubUtil.putRecords(enrichedDataOver);
                        }
                      } catch (Exception e) {
                        e.printStackTrace();
                      }

//                                    enrichedDataOver
//                                            .stream()
//                                            .forEach(data -> enrichedDataList.add(data));
                    }
                  })
              ).collect(Collectors.toList());
          ExternalApiExecutorService.getFuturesWithTimeout(futures, TIMEOUT_PER_GAODE_API_REQUEST_IN_NANO_SECONDS, "calling Gaode API");

          return enrichedDataList;
        });*/

    return new KafkaStreams(builder.build(), streamsConfiguration);

  }

}