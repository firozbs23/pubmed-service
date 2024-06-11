package com.omnizia.pubmedservice.batchjob;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.TestDto;
import com.omnizia.pubmedservice.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

@Slf4j
public class MyItemReader implements ItemReader<TestDto> {

  private Iterator<TestDto> dataIterator;
  private final String className;

  public MyItemReader() {
    this.className = this.getClass().getSimpleName();
  }

  @Override
  public TestDto read() {
    String currentTime = TimeUtils.getCurrentTimeUTC();

    if (dataIterator == null) {
      List<TestDto> footprintsDto;
      try {
        footprintsDto = getFromVirtualThread();
      } catch (ExecutionException | InterruptedException e) {
        log.error("{} : Failed to read digital footprints data in {}", className, currentTime);
        throw new RuntimeException(e);
      }

      if (footprintsDto != null) {
        dataIterator = footprintsDto.iterator();
        log.info("{}: Read item size : {}", className, footprintsDto.size());
      } else log.info("{}: Not able to read item.", className);
    }

    if (dataIterator.hasNext()) {
      return dataIterator.next();
    } else {
      return null;
    }
  }

  private List<TestDto> getFromVirtualThread() throws ExecutionException, InterruptedException {
    Callable<List<TestDto>> task =
        () -> {
          try {
            DataSourceContextHolder.setDataSourceType("olam");
            return List.of(
                TestDto.builder().build()); // digitalFootprintsService.getDigitalFootprintsDto();
          } finally {
            DataSourceContextHolder.clearDataSourceType();
          }
        };

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      Future<List<TestDto>> future = executor.submit(task);
      return future.get(); // Wait for the result and return it
    }
  }
}
