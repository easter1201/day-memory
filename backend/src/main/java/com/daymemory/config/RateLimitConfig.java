package com.daymemory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class RateLimitConfig {

    /**
     * Rate Limit 저장소 (메모리 기반)
     * 실제 프로덕션에서는 Redis 사용 권장
     */
    @Bean
    public Map<String, RateLimitEntry> rateLimitStore() {
        ConcurrentHashMap<String, RateLimitEntry> store = new ConcurrentHashMap<>();

        // 매 분마다 만료된 엔트리 정리
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            store.entrySet().removeIf(entry ->
                now - entry.getValue().getTimestamp() > 60000 // 1분 경과
            );
        }, 1, 1, TimeUnit.MINUTES);

        return store;
    }

    /**
     * Rate Limit 엔트리
     */
    public static class RateLimitEntry {
        private int count;
        private long timestamp;

        public RateLimitEntry() {
            this.count = 1;
            this.timestamp = System.currentTimeMillis();
        }

        public void increment() {
            this.count++;
            this.timestamp = System.currentTimeMillis();
        }

        public int getCount() {
            return count;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > 60000; // 1분
        }
    }
}
