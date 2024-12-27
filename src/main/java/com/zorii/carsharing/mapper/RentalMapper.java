package com.zorii.carsharing.mapper;

import com.zorii.carsharing.dto.rental.RentalRequestDto;
import com.zorii.carsharing.dto.rental.RentalResponseDto;
import com.zorii.carsharing.model.Car;
import com.zorii.carsharing.model.Rental;
import com.zorii.carsharing.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CarMapper.class})
public interface RentalMapper {

    RentalResponseDto toResponseDto(Rental rental);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actualReturnDate", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "car", source = "car")
    Rental toEntity(RentalRequestDto dto, User user, Car car);
}
