package ru.tishembitov.reviewsservice.mapper;

import ru.tishembitov.reviewsservice.dto.ReviewCreateDto;
import ru.tishembitov.reviewsservice.dto.ReviewDto;
import ru.tishembitov.reviewsservice.entity.Review;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReviewMapper {

    ReviewDto toDto(Review review);

    List<ReviewDto> toDto(List<Review> review);

    @Mapping(target = "userId",
             expression = "java(userId)")
    Review toReview(
            ReviewCreateDto reviewCreateDto,
            @Context
            Long userId
    );

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Review partialUpdate(
            ReviewDto reviewDto,
            @MappingTarget
            Review review
    );

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Review partialUpdate(
            ReviewCreateDto reviewCreateDto,
            @MappingTarget
            Review review
    );
}


