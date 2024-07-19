package com.omnizia.pubmedservice.mapper;

import com.omnizia.pubmedservice.dto.ErrorDataDto;
import com.omnizia.pubmedservice.entity.ErrorData;

import java.util.List;

public class ErrorDataMapper {

  public static ErrorDataDto mapToErrorDataDto(ErrorData errorData) {
    return ErrorDataDto.builder()
        .jobId(errorData.getJobId())
        .hcpViqId(errorData.getHcpViqId())
        .jobTitle(errorData.getJobTitle())
        .message(errorData.getMessage())
        .timestamp(errorData.getTimestamp())
        .build();
  }

  public static ErrorData mapToErrorData(ErrorDataDto errorDataDto) {
    return ErrorData.builder()
        .jobId(errorDataDto.getJobId())
        .hcpViqId(errorDataDto.getHcpViqId())
        .jobTitle(errorDataDto.getJobTitle())
        .message(errorDataDto.getMessage())
        .timestamp(errorDataDto.getTimestamp())
        .build();
  }

  public static List<ErrorDataDto> mapToErrorDataDto(List<ErrorData> errorDataList) {
    return errorDataList.stream().map(ErrorDataMapper::mapToErrorDataDto).toList();
  }

  public static List<ErrorData> mapToErrorData(List<ErrorDataDto> errorDataDtos) {
    return errorDataDtos.stream().map(ErrorDataMapper::mapToErrorData).toList();
  }
}
