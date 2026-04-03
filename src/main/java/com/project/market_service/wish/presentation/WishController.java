package com.project.market_service.wish.presentation;

import com.project.market_service.common.dto.ApiResult;
import com.project.market_service.common.dto.PageResponse;
import com.project.market_service.common.security.jwt.JwtUserInfo;
import com.project.market_service.wish.application.service.WishService;
import com.project.market_service.wish.presentation.dto.ToggleWishResponse;
import com.project.market_service.wish.presentation.dto.WishResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "WISH", description = "찜 토글 및 목록")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WishController {

    private final WishService wishService;

    @Operation(description = "찜 토글")
    @ApiResponse(responseCode = "200", description = "찜 토글 변경")
    @PostMapping("/products/{id}/wish")
    public ResponseEntity<ApiResult<ToggleWishResponse>> toggleWished(
            @PathVariable Long id,
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        return ResponseEntity.ok().body(
                ApiResult.success(wishService.toggleWish(id, userInfo.userId()))
        );
    }

    @Operation(description = "찜 목록 조회")
    @ApiResponse(responseCode = "200", description = "찜 목록 조회 성공")
    @GetMapping("/users/{id}/wishes")
    public ResponseEntity<ApiResult<PageResponse<WishResponse>>> getWishProducts(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        return ResponseEntity.ok().body(
                ApiResult.success(PageResponse.from(wishService.getWishProducts(userInfo.userId(), pageable))));
    }
}
