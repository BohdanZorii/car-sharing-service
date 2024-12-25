package com.zorii.carsharing.mapper;

import com.zorii.carsharing.dto.CarRequestDto;
import com.zorii.carsharing.dto.CarResponseDto;
import com.zorii.carsharing.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarMapper {

  @Mapping(target = "type", expression = "java(Car.Type.valueOf(dto.type()))") // Convert String to Enum
  Car toEntity(CarRequestDto dto);

  @Mapping(target = "type", expression = "java(car.getType().name())") // Convert Enum to String
  CarResponseDto toResponseDto(Car car);
}
