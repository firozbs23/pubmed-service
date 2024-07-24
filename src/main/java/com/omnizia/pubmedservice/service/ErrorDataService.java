package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dto.ErrorDataDto;
import com.omnizia.pubmedservice.entity.ErrorData;
import com.omnizia.pubmedservice.mapper.ErrorDataMapper;
import com.omnizia.pubmedservice.repository.ErrorDataRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorDataService {

  private final ErrorDataRepository repository;

  public ErrorDataDto getErrorData(UUID jobId) {
    ErrorData errorData = repository.findById(jobId).orElse(null);
    if (errorData == null) return null;
    return ErrorDataMapper.mapToErrorDataDto(errorData);
  }

  public ErrorDataDto createErrorData(ErrorData errorData) {
    ErrorData savedErrorData = repository.save(errorData);
    return ErrorDataMapper.mapToErrorDataDto(savedErrorData);
  }

  public ErrorDataDto createErrorData(ErrorDataDto errorDataDto) {
    ErrorData errorData = ErrorDataMapper.mapToErrorData(errorDataDto);
    ErrorData savedErrorData = repository.save(errorData);
    return ErrorDataMapper.mapToErrorDataDto(savedErrorData);
  }

  public List<ErrorDataDto> getPubmedJobErrorList(UUID jobId) {
    List<ErrorData> errorDataList = repository.findByJobId(jobId);
    return ErrorDataMapper.mapToErrorDataDto(errorDataList);
  }

  public void saveErrors(List<ErrorData> errorDataList) {
    repository.saveAll(errorDataList);
  }
}
