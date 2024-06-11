package com.omnizia.pubmedservice.batchjob;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.TestDto;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

@Slf4j
@Scope("step")
public class MyItemWriter implements ItemWriter<TestDto> {

  private final String className;
  private final String jobId;

  public MyItemWriter(@Value("#{jobParameters['jobId']}") String jobId) {
    this.jobId = jobId;
    className = this.getClass().getSimpleName();
    log.info("{}: Job id inside write: {}", className, jobId);
  }

  @Override
  public void write(@NonNull Chunk<? extends TestDto> chunk) {
    List<TestDto> digitalFootprintDtos = new ArrayList<>(chunk.getItems());
    log.info("{}: Chunk size : {}", className, digitalFootprintDtos.size());

    List<TestDto> updatedFootprints = getHcpStatus(digitalFootprintDtos);

    log.info("{}: Chunk size after status changed : {}", className, updatedFootprints.size());

    updateInVirtualThread(updatedFootprints);

    log.info("{}: Chunk data saved.", className);
  }

  private List<TestDto> getHcpStatus(List<TestDto> digitalFootprintDtos) {
    return List.of(TestDto.builder().build());
  }

  private void updateInVirtualThread(List<TestDto> updatedFootprints) {
    Runnable task =
        () -> {
          try {
            DataSourceContextHolder.setDataSourceType("olam");
            log.info("{}: Job updated successfully. JobId : {}", className, jobId);
          } catch (Exception e) {
            log.error("{} : Error in virtual thread", className, e);
          } finally {
            DataSourceContextHolder.clearDataSourceType();
          }
        };

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      Future<?> future = executor.submit(task);
      future.get(); // Wait for the task to complete
    } catch (ExecutionException | InterruptedException e) {
      log.error("{} : Execution error in virtual thread", className, e);
    }
  }
}
