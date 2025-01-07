package com.zorii.carsharing.mapper;

import com.zorii.carsharing.dto.car.CarRequestDto;
import com.zorii.carsharing.dto.car.CarResponseDto;
import com.zorii.carsharing.model.Car;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CarMapper {

  @Mapping(target = "type", expression = "java(Car.Type.valueOf(dto.type()))")
  Car toEntity(CarRequestDto dto);

  CarResponseDto toResponseDto(Car car);

  List<CarResponseDto> toResponseDtoList(List<Car> cars);
}
