package ru.tishembitov.reviewsservice.service.impl;

import ru.tishembitov.reviewsservice.config.ContextHolder;
import ru.tishembitov.reviewsservice.exception.ReviewException;
import ru.tishembitov.reviewsservice.dto.OrderDto;
import ru.tishembitov.reviewsservice.dto.ProductAvgRatDto;
import ru.tishembitov.reviewsservice.dto.ReviewCreateDto;
import ru.tishembitov.reviewsservice.dto.ReviewDto;
import ru.tishembitov.reviewsservice.mapper.ReviewMapper;
import ru.tishembitov.reviewsservice.entity.Review;
import ru.tishembitov.reviewsservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tishembitov.reviewsservice.service.OrderServiceClient;
import ru.tishembitov.reviewsservice.service.ReviewService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;
    private final OrderServiceClient orderServiceClient;
    private final ContextHolder context;

    private static boolean isProductPresentInOrder(
            final Long productId,
            final OrderDto order
    ) {
        return order.products()
                .stream()
                .anyMatch(orderProductDto -> orderProductDto.productId()
                        .equals(productId));
    }

    @Override
    @Transactional
    public ReviewDto addOne(final ReviewCreateDto reviewCreateDto) {
        var existsReview = this.reviewRepository.existsByUserIdAndOrderIdAndProductId(
                this.context.getUserId(),
                reviewCreateDto.orderId(),
                reviewCreateDto.productId()
        );

        if (existsReview) {
            throw new ReviewException(ReviewException.REVIEW_ALREADY_EXISTS);
        }

        var order = this.orderServiceClient.getOrder(reviewCreateDto.orderId())
                .orElseThrow(() -> new ReviewException(ReviewException.ORDER_DOES_NOT_EXIST));

        if (!isProductPresentInOrder(reviewCreateDto.productId(), order)) {
            throw new ReviewException(ReviewException.PRODUCT_NOT_PRESENT_IN_ORDER);
        }

        var review = this.reviewMapper.toReview(reviewCreateDto, this.context.getUserId());

        var reviewSaved = this.reviewRepository.save(review);

        return this.reviewMapper.toDto(reviewSaved);
    }

    @Override
    public List<ReviewDto> getAllByUser() {
        var repositoryByUser = this.reviewRepository.findByUserId(this.context.getUserId());
        return this.reviewMapper.toDto(repositoryByUser);
    }

    @Override
    public List<ReviewDto> getAllByProduct(final Long productId) {
        var reviewsByProduct = this.reviewRepository.findByProductId(productId);

        return this.reviewMapper.toDto(reviewsByProduct);
    }

    @Override
    @Transactional
    public boolean deleteOne(final Long reviewId) {
        return this.reviewRepository.deleteOneById(reviewId) == 1;
    }

    @Override
    public List<ProductAvgRatDto> getTopAvgRatedProducts(final int max) {
        return this.reviewRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Review::getProductId,
                        Collectors.averagingDouble(Review::getRating)
                ))
                .entrySet()
                .stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                .limit(max)
                .map(longDoubleEntry ->
                        new ProductAvgRatDto(
                                longDoubleEntry.getKey(),
                                BigDecimal.valueOf(longDoubleEntry.getValue())
                        ))
                .toList();
    }
}