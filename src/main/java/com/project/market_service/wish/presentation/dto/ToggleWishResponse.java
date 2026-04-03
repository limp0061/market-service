package com.project.market_service.wish.presentation.dto;

public record ToggleWishResponse(
        boolean isWished,
        Long wishCount
) {
    public static ToggleWishResponse of(boolean isWished, long wishCount) {
        return new ToggleWishResponse(isWished, wishCount);
    }
}
