package com.chedaojunan.report.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;

public class ExternalApiExecutorService {

  private static final Logger LOG = LoggerFactory.getLogger(ExternalApiExecutorService.class);
  private static final int TIMEOUT_TO_CLOSE_EXECUTOR_SERVICE = 30;
  private static final String TIMEOUT_COUNTER_NAME = "timeout";

  private static ExecutorService executorService = Executors.newWorkStealingPool(8);

  private ExternalApiExecutorService() {}

  public static void closeExecutorService() {
    executorService.shutdown();
    try {
      executorService.awaitTermination(TIMEOUT_TO_CLOSE_EXECUTOR_SERVICE, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      LOG.error("Failed to close Executor Service. Forcing to close...");
      executorService.shutdownNow();
    }
  }

  public static void getFuturesWithTimeout(List<Future<?>> futureList, long timeout, String jobName) {
    long numTimeouts = 0;
    for (Future<?> future : futureList) {
      long startTime = Instant.now().toEpochMilli();
      try{
        future.get(timeout, TimeUnit.NANOSECONDS);
      } catch (TimeoutException te) {
        numTimeouts++;
        future.cancel(true);
      } catch (Exception e) {
        LOG.error("Error occurred while " + jobName, e);
      } finally {
        long endTime = Instant.now().toEpochMilli();
        long timeDuration = endTime - startTime;
        timeout = Math.max(timeout - timeDuration, 0);
      }
    }
    if (numTimeouts > 0){
      LOG.debug("{} timeouts occurred while " + jobName, numTimeouts);
    }
  }

  public static ExecutorService getExecutorService() {
    return executorService;
  }


}
