package com.chedaojunan.report;

import com.cdja.cloud.data.proto.GpsProto;
import com.chedaojunan.report.client.RegeoClient;
import com.chedaojunan.report.model.DatahubDeviceData;
import com.chedaojunan.report.model.FixedFrequencyGpsData;
import com.chedaojunan.report.model.FixedFrequencyIntegrationData;
import com.chedaojunan.report.serdes.ArrayListSerde;
import com.chedaojunan.report.serdes.SerdeFactory;
import com.chedaojunan.report.transformer.EnrichRawDataTransformer;
import com.chedaojunan.report.utils.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class DataEnrich {

  private static final Logger logger = LoggerFactory.getLogger(DataEnrich.class);

  private static Properties kafkaProperties = null;

  private static final String dedupStoreName;

  private static final int kafkaWindowLengthInSeconds;

  private static final Serde<String> stringSerde;

  private static final Serde<FixedFrequencyGpsData> fixedFrequencyGpsDataSerde;

  private static final Serde<FixedFrequencyIntegrationData> fixedFrequencyIntegrationSerde;

  private static final ArrayListSerde<FixedFrequencyGpsData> arrayListFixedFrequencyGpsSerde;

  private static final ArrayListSerde<FixedFrequencyIntegrationData> arrayListFixedFrequencyIntegrationSerde;

  private static RegeoClient regeoClient;

  static {
    kafkaProperties = ReadProperties.getProperties(KafkaConstants.PROPERTIES_FILE_NAME);
    stringSerde = Serdes.String();
    Map<String, Object> serdeProp = new HashMap<>();
    fixedFrequencyGpsDataSerde = SerdeFactory.createSerde(FixedFrequencyGpsData.class, serdeProp);
    fixedFrequencyIntegrationSerde = SerdeFactory.createSerde(FixedFrequencyIntegrationData.class, serdeProp);
    arrayListFixedFrequencyGpsSerde = new ArrayListSerde<>(fixedFrequencyGpsDataSerde);
    arrayListFixedFrequencyIntegrationSerde = new ArrayListSerde<>(fixedFrequencyIntegrationSerde);
    kafkaWindowLengthInSeconds = Integer.parseInt(kafkaProperties.getProperty(KafkaConstants.KAFKA_WINDOW_DURATION));
    regeoClient = RegeoClient.getInstance();
    dedupStoreName = kafkaProperties.getProperty(KafkaConstants.DEDUP_STORE_NAME);
  }

  public static void main(String[] args) {
    String rawDataTopic = kafkaProperties.getProperty(KafkaConstants.KAFKA_RAW_DATA_TOPIC);
    String outputTopic = kafkaProperties.getProperty(KafkaConstants.KAFKA_OUTPUT_TOPIC);

    /*
     * 1. sample data within the window
     * 2. make gaode API calls
     * 3. enrich raw data within the window and output to the output topic
     */

    final KafkaStreams rawDataToEnrichedStream = buildRawDataToEnrichedStream(rawDataTopic, outputTopic);

    rawDataToEnrichedStream.start();

    final KafkaStreams writeToDatahubStream = buildWriteToDatahubStream(outputTopic);

    writeToDatahubStream.start();

    // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
    Runtime.getRuntime().addShutdownHook(new Thread(rawDataToEnrichedStream::close));
    Runtime.getRuntime().addShutdownHook(new Thread(writeToDatahubStream::close));
  }

  private static Properties getStreamConfigRawDataToEnriched() {
    final Properties streamsConfiguration = new Properties();
    String kafkaApplicationName = kafkaProperties.getProperty(KafkaConstants.RAWDATA_STREAM_APPLICATION_NAME);
    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaApplicationName);
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
            kafkaProperties.getProperty(KafkaConstants.KAFKA_BOOTSTRAP_SERVERS));
    //streamsConfiguration.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4);
    streamsConfiguration.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG,
            kafkaProperties.getProperty(KafkaConstants.RAWDATA_ENRICHED_REQUEST_TIMEOUT_MS_CONFIG));
    streamsConfiguration.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "90000");
    // 一次从kafka中poll出来的数据条数
    // max.poll.records条数据需要在在session.timeout.ms这个时间内处理完
    streamsConfiguration.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");
    // server发送到消费端的最小数据，若是不满足这个数值则会等待直到满足指定大小。默认为1表示立即接收。
    streamsConfiguration.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "100");
    // 若是不满足fetch.min.bytes时，等待消费端请求的最长等待时间
    streamsConfiguration.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "1000");

    // RecordTooLargeException
    streamsConfiguration.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,
            kafkaProperties.getProperty(KafkaConstants.MAX_REQUEST_SIZE_CONFIG));

    // protoc buffer
    streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, ProtoSeder.class.getName());
    streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

    streamsConfiguration.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, FixedFrequencyGpsDataTimestampExtractor.class);
    streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getProperty(KafkaConstants.AUTO_OFFSET_RESET_CONFIG));
    // disable cache
    streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
    // Use a temporary directory for storing state, which will be automatically removed after the test.
    streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, kafkaProperties.getProperty(KafkaConstants.RAWDATA_STATE_DIR_CONFIG));

    return streamsConfiguration;
  }

  static KafkaStreams buildRawDataToEnrichedStream(String inputTopic, String outputTopic) {
    final Properties streamsConfiguration = getStreamConfigRawDataToEnriched();

    StreamsBuilder builder = new StreamsBuilder();
    StoreBuilder<KeyValueStore<String, ArrayList<FixedFrequencyGpsData>>> dedupStoreBuilder = Stores.keyValueStoreBuilder(
        Stores.persistentKeyValueStore(dedupStoreName),
        Serdes.String(),
        arrayListFixedFrequencyGpsSerde);

    builder.addStateStore(dedupStoreBuilder);

    KStream<String, GpsProto.Gps> inputStream = builder.stream(inputTopic);

    KStream<Windowed<String>, ArrayList<FixedFrequencyGpsData>> windowedRawData = inputStream
        .map(
                (key, frequencyGps) ->
                        new KeyValue<>(frequencyGps.getDeviceId(), frequencyGps)
        )
        .groupByKey()
        .windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(kafkaWindowLengthInSeconds)).until(TimeUnit.SECONDS.toMillis(kafkaWindowLengthInSeconds)))
        .aggregate(
            () -> new ArrayList<>(),
            (windowedCarId, record, list) -> {
              FixedFrequencyGpsData fixedFrequencyGpsData = SampledDataCleanAndRet.convertTofixedFrequencyGpsData(record);
              if (!list.contains(fixedFrequencyGpsData)) {
                list.add(fixedFrequencyGpsData);
              }
              return list;
            },
            Materialized.with(stringSerde, arrayListFixedFrequencyGpsSerde)
        )
        .toStream();

    windowedRawData
        .transform(
            () -> new EnrichRawDataTransformer(),
            dedupStoreName
        )//.print(Printed.toSysOut());
        .to(outputTopic, Produced.with(stringSerde, arrayListFixedFrequencyIntegrationSerde));

    return new KafkaStreams(builder.build(), streamsConfiguration);
  }

  private static Properties getStreamConfigWriteToDatahub() {
    final Properties streamsConfiguration = new Properties();
    String kafkaApplicationName = kafkaProperties.getProperty(KafkaConstants.WRITE_DATAHUB_STREAM_APPLICATION_NAME);
    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaApplicationName);
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaProperties.getProperty(KafkaConstants.KAFKA_BOOTSTRAP_SERVERS));
    streamsConfiguration.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG,
            kafkaProperties.getProperty(KafkaConstants.WRITE_DATAHUB_REQUEST_TIMEOUT_MS_CONFIG));
    // RecordTooLargeException
    streamsConfiguration.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,
            kafkaProperties.getProperty(KafkaConstants.MAX_REQUEST_SIZE_CONFIG));

    streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getProperty(KafkaConstants.AUTO_OFFSET_RESET_CONFIG));

    long seconds = Long.parseLong(kafkaProperties.getProperty(KafkaConstants.WRITE_DATAHUB_COMMIT_INTERVAL_MS_CONFIG));
    streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, TimeUnit.SECONDS.toMillis(seconds));

    streamsConfiguration.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "90000");
    // 一次从kafka中poll出来的数据条数
    // max.poll.records条数据需要在在session.timeout.ms这个时间内处理完
    streamsConfiguration.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");
    // server发送到消费端的最小数据，若是不满足这个数值则会等待直到满足指定大小。默认为1表示立即接收。
    streamsConfiguration.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "100");
    // 若是不满足fetch.min.bytes时，等待消费端请求的最长等待时间
    streamsConfiguration.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "1000");

    // disable cache
    streamsConfiguration.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
    // Use a temporary directory for storing state, which will be automatically removed after the test.
    streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, kafkaProperties.getProperty(KafkaConstants.WRITE_DATAHUB_STATE_DIR_CONFIG));

    return streamsConfiguration;
  }

  static KafkaStreams buildWriteToDatahubStream(String inputTopic) {
    final Properties streamsConfiguration = getStreamConfigWriteToDatahub();

    StreamsBuilder builder = new StreamsBuilder();

    WriteDatahubUtil writeDatahubUtil = new WriteDatahubUtil();

    KStream<String, ArrayList<FixedFrequencyIntegrationData>> writeToDataHubStream =
        builder.stream(inputTopic, Consumed.with(stringSerde, arrayListFixedFrequencyIntegrationSerde));
//    writeToDataHubStream.print(Printed.toSysOut());

    writeToDataHubStream.foreach((windowedDeviceId, enrichedDataList) -> {
      ArrayList<DatahubDeviceData> enrichedDataOver = null;
      if (enrichedDataList != null) {
        try {
          enrichedDataOver = regeoClient.getRegeoFromResponse(enrichedDataList);
        } catch (Exception e) {
          logger.error("getRegeoFromResponse is error:" + e);
        }
      }
      if (enrichedDataOver != null) {
        try {
          // 整合数据入库datahub
          if (CollectionUtils.isNotEmpty(enrichedDataOver)) {
  //          System.out.println("write to DataHub: " + Instant.now().toString() + " enrichedDataOver.size(): " + enrichedDataOver.size());
            logger.info("write to DataHub: " + Instant.now().toString() + " enrichedDataOver.size(): " + enrichedDataOver.size());
            writeDatahubUtil.putRecords(enrichedDataOver);
          }
        } catch (Exception e) {
          logger.error("enrichedData write to datahub is error:" + e);
        }
      }
    });

    return new KafkaStreams(builder.build(), streamsConfiguration);
  }

}