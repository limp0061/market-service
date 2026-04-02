package com.project.market_service.product.presentation;

import com.project.market_service.common.dto.ApiResult;
import com.project.market_service.common.dto.PageResponse;
import com.project.market_service.common.security.jwt.JwtUserInfo;
import com.project.market_service.product.application.service.ProductService;
import com.project.market_service.product.presentation.dto.ProductDetailResponse;
import com.project.market_service.product.presentation.dto.ProductResponse;
import com.project.market_service.product.presentation.dto.ProductSaveRequest;
import com.project.market_service.product.presentation.dto.ProductSaveResponse;
import com.project.market_service.product.presentation.dto.ProductSearchRequest;
import com.project.market_service.product.presentation.dto.ProductUpdateRequest;
import com.project.market_service.product.presentation.dto.UpdateProductStatusRequest;
import com.project.market_service.product.presentation.dto.UpdateProductStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Product", description = "상품 목록 조회 및 관리")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 등록")
    @ApiResponse(responseCode = "201", description = "상품 등록 성공")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<ProductSaveResponse>> saveProduct(
            @Valid @RequestPart("request") ProductSaveRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResult.success(productService.saveProduct(request, images, userInfo.userId()))
        );
    }

    @Operation(summary = "상품 수정")
    @ApiResponse(responseCode = "200", description = "상품 수정 성공")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResult<ProductSaveResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestPart("request") ProductUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        return ResponseEntity.ok().body(
                ApiResult.success(productService.updateProduct(id, request, images, userInfo.userId()))
        );
    }

    @Operation(summary = "상품 삭제")
    @ApiResponse(responseCode = "200", description = "상품 삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResult<Void>> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        productService.deleteProduct(id, userInfo.userId());
        return ResponseEntity.ok().body(
                ApiResult.success(null)
        );
    }

    @Operation(summary = "상품 상태 변경")
    @ApiResponse(responseCode = "200", description = "상품 상태 변경 성공")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResult<UpdateProductStatusResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductStatusRequest request,
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        return ResponseEntity.ok().body(
                ApiResult.success(productService.updateProductStatus(id, request, userInfo.userId()))
        );
    }

    @Operation(summary = "상품 목록 조회(카테고리, 가격, 키워드)")
    @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공")
    @GetMapping
    public ResponseEntity<ApiResult<PageResponse<ProductResponse>>> getProducts(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
            @Valid @ModelAttribute ProductSearchRequest request
    ) {
        Page<ProductResponse> products = productService.getProducts(request, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResult.success(PageResponse.from(products))
        );
    }

    @Operation(summary = "상품 상세 조회")
    @ApiResponse(responseCode = "200", description = "상품 상세 조회 성공")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<ProductDetailResponse>> getProduct(
            @PathVariable Long id,
            @RequestParam("curLat") Double curLat,
            @RequestParam("curLng") Double curLng
    ) {
        return ResponseEntity.ok().body(
                ApiResult.success(productService.getProductDetail(id, curLat, curLng))
        );
    }
}
