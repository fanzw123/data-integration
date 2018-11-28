package com.chedaojunan.report;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.cdja.cloud.data.proto.GpsProto;
import com.chedaojunan.report.client.RegeoClient;
import com.chedaojunan.report.model.*;
import com.chedaojunan.report.transformer.GpsDataTransformerSupplier;
import com.chedaojunan.report.utils.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chedaojunan.report.client.AutoGraspApiClient;
import com.chedaojunan.report.serdes.ArrayListSerde;
import com.chedaojunan.report.serdes.SerdeFactory;
import com.chedaojunan.report.service.ExternalApiExecutorService;

public class DataEnrich {

    private static final Logger logger = LoggerFactory.getLogger(DataEnrich.class);
    private static final long TIMEOUT_PER_GAODE_API_REQUEST_IN_NANO_SECONDS = 10000000000L;

    private static Properties kafkaProperties = null;

    private static final int kafkaWindowLengthInSeconds;

    private static final Serde<String> stringSerde;

    private static final Serde<FixedFrequencyGpsData> fixedFrequencyAccessDataSerde;

    private static final ArrayListSerde<String> arrayListStringSerde;

    private static RegeoClient regeoClient;

    private static AutoGraspApiClient autoGraspApiClient;

    static {
        kafkaProperties = ReadProperties.getProperties(KafkaConstants.PROPERTIES_FILE_NAME);
        stringSerde = Serdes.String();
        Map<String, Object> serdeProp = new HashMap<>();
        fixedFrequencyAccessDataSerde = SerdeFactory.createSerde(FixedFrequencyGpsData.class, serdeProp);
        arrayListStringSerde = new ArrayListSerde<>(stringSerde);
        kafkaWindowLengthInSeconds = Integer.parseInt(kafkaProperties.getProperty(KafkaConstants.KAFKA_WINDOW_DURATION));
        autoGraspApiClient = AutoGraspApiClient.getInstance();
        regeoClient = RegeoClient.getInstance();
    }

    public static void main(String[] args) {
        String rawDataTopic = kafkaProperties.getProperty(KafkaConstants.KAFKA_RAW_DATA_TOPIC);

        final KafkaStreams sampledRawDataStream = buildDataStream(rawDataTopic);

        sampledRawDataStream.start();

        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(sampledRawDataStream::close));
    }

    private static Properties getStreamConfig() {
        final Properties streamsConfiguration = new Properties();
        String kafkaApplicationName = kafkaProperties.getProperty(KafkaConstants.KAFKA_STREAM_APPLICATION_NAME);
//        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG,
//                String.join(KafkaConstants.HYPHEN, kafkaApplicationName, UUID.randomUUID().toString()));
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaApplicationName);
//        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "test001");
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProperties.getProperty(KafkaConstants.KAFKA_BOOTSTRAP_SERVERS));
//        streamsConfiguration.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG,2);
        //streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, kafkaProperties.getProperty(KafkaConstants.STATE_DIR_CONFIG));
        streamsConfiguration.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4);
        streamsConfiguration.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 60000);

        // RecordTooLargeException
        streamsConfiguration.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 12695150);

//        streamsConfiguration.put(StreamsConfig.producerPrefix(ProducerConfig.COMPRESSION_TYPE_CONFIG), "snappy");
        // Specify default (de)serializers for record keys and for record values.
//        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
//                Serdes.String().getClass().getName());
//        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        // protoc buffer
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, ProtoSeder.class.getName());

        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, FixedFrequencyGpsDataTimestampExtractor.class);
        streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getProperty(KafkaConstants.AUTO_OFFSET_RESET_CONFIG));

        return streamsConfiguration;
    }

    static KafkaStreams buildDataStream(String inputTopic) {
        final Properties streamsConfiguration = getStreamConfig();

        StreamsBuilder builder = new StreamsBuilder();
        StoreBuilder<KeyValueStore<String, ArrayList<FixedFrequencyGpsData>>> rawDataStore = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore("rawDataStoreNew"),
                Serdes.String(),
                new ArrayListSerde(fixedFrequencyAccessDataSerde))
                .withCachingEnabled();


        WriteDatahubUtil writeDatahubUtil = new WriteDatahubUtil();

        builder.addStateStore(rawDataStore);

        KStream<String, GpsProto.Gps> kStream = builder.stream(inputTopic);

        final KStream<String, String> orderedDataStream = kStream
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
                                list.add(SampledDataCleanAndRet.convertTofixedFrequencyGpsData(record).toString());
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
                orderedDataStream.transform(new GpsDataTransformerSupplier(rawDataStore.name()), rawDataStore.name());

        dedupOrderedDataStream
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
                });

        return new KafkaStreams(builder.build(), streamsConfiguration);

    }

}