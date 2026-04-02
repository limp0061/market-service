package com.project.market_service.product.presentation.dto;

import com.project.market_service.product.domain.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ProductDetailResponse {
    @Schema(description = "상품 고유 식별 번호", example = "1")
    private Long productId;
    @Schema(description = "판매자 고유 식별 번호", example = "1")
    private Long sellerId;
    @Schema(description = "판매자 명", example = "홍길동")
    private String sellerName;
    @Schema(description = "카테고리 고유 식별 번호", example = "1")
    private Long categoryId;
    @Schema(description = "카테고리 명", example = "가전제품")
    private String categoryName;
    @Schema(description = "상품 명", example = "냉장고")
    private String productName;
    @Schema(description = "상품 설명", example = "싸게 팝니다")
    private String description;
    @Schema(description = "상품 가격", example = "12000000")
    private BigDecimal price;
    @Schema(description = "상품 이미지", example = "[\"https://s3/image1.png\", \"https://s3/image2.png\"]")
    private List<String> imageUrls;
    @Schema(description = "상품 상태", example = "SELLING")
    private ProductStatus productStatus;
    @Schema(description = "조회 수", example = "120")
    private int viewCount;
    @Schema(description = "찜하기 수", example = "15")
    private int wishCount;
    @Schema(description = "장소", example = "풍무동")
    private String address;
    @Schema(description = "거리", example = "1.5")
    private Double distance;
    @Schema(description = "상품 등록 일자", example = "2026-04-02T13:49:00")
    private LocalDateTime createdAt;
}
