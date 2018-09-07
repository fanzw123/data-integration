import java.util.Comparator;
import java.util.PriorityQueue;

import com.chedaojunan.report.utils.DateUtils;

public class HongyanDataAccumulator {

  /*final Comparator<String> comparator =
      (o1, o2) -> {
        HongyanRawData rawData1 = KafkaStreamTest.convertToHongYanPojo(o1);
        HongyanRawData rawData2 = KafkaStreamTest.convertToHongYanPojo(o2);
        return (int)(KafkaStreamTest.convertTimeStringToEpochSecond(rawData2.getGpsTime()) -
            KafkaStreamTest.convertTimeStringToEpochSecond(rawData1.getGpsTime()));};*/
  final Comparator<HongyanRawData> comparator =
      (o1, o2) -> (int)(DateUtils.convertTimeStringToEpochSecond(o2.getGpsTime()) -
          DateUtils.convertTimeStringToEpochSecond(o1.getGpsTime()));

  PriorityQueue queue = new PriorityQueue<>(comparator);

  public HongyanDataAccumulator add(HongyanRawData data) {
    queue.add(data);
    return this;
  }

  public HongyanDataAccumulator remove(HongyanRawData data) {
    queue.remove(data);
    return this;
  }
}
