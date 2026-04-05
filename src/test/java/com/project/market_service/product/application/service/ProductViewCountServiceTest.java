package com.project.market_service.product.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.project.market_service.common.redis.RedisManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductViewCountServiceTest {

    @Mock
    private RedisManager redisManager;

    @InjectMocks
    private ProductViewCountService productViewCountService;

    @Test
    void increaseViewCount_first_view() {
        // given
        Long productId = 1L;
        Long userId = 1L;
        given(redisManager.setIfAbsent(anyString(), any(), anyLong(), any()))
                .willReturn(true);
        given(redisManager.increment(anyString())).willReturn(11L);

        // when
        Long result = productViewCountService.increaseViewCount(productId, userId);

        // then
        assertThat(result).isEqualTo(11L);
        then(redisManager).should(times(1)).increment(anyString());

    }

    @Test
    void noIncreaseViewCount() {
        Long productId = 1L;
        Long userId = 1L;
        given(redisManager.setIfAbsent(anyString(), any(), anyLong(), any()))
                .willReturn(false);
        given(redisManager.get(anyString(), eq(Long.class))).willReturn(10L);

        // when
        Long result = productViewCountService.increaseViewCount(productId, userId);

        // then
        assertThat(result).isEqualTo(10L);
        then(redisManager).should(never()).increment(anyString());
    }
}