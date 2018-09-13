//package com.chedaojunan.report.serdes;
//
//import org.apache.kafka.common.serialization.Deserializer;
//import org.apache.kafka.common.serialization.Serde;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.common.serialization.Serializer;
//
//import java.util.ArrayList;
//import java.util.Map;
//
//public class ArrayListGps<T> implements Serde<ArrayList<T>> {
//
//
//  public ArrayListGps(Serde<T> serde) {
//    inner =
//        Serdes.serdeFrom(
//            new ArrayListSerializer<>(serde.serializer()),
//            new ArrayListDeserializer<>(serde.deserializer()));
//  }
//
//  @Override
//  public Serializer<ArrayList<T>> serializer() {
//    return inner.serializer();
//  }
//
//  @Override
//  public Deserializer<ArrayList<T>> deserializer() {
//    return inner.deserializer();
//  }
//
//  @Override
//  public void configure(Map<String, ?> configs, boolean isKey) {
//    inner.serializer().configure(configs, isKey);
//    inner.deserializer().configure(configs, isKey);
//  }
//
//  @Override
//  public void close() {
//    inner.serializer().close();
//    inner.deserializer().close();
//  }
//}
