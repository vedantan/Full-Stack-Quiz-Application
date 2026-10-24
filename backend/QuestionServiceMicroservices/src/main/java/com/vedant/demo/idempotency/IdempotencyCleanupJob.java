package com.vedant.demo.idempotency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;

/**
 * Idempotency keys only need to live as long as a client might realistically retry.
 * Without this job the table would grow forever.
 */
@Configuration
@EnableScheduling
public class IdempotencyCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyCleanupJob.class);

    private final IdempotencyStore store;
    private final Duration ttl;

    public IdempotencyCleanupJob(IdempotencyStore store,
                                 @Value("${idempotency.ttl-hours:24}") long ttlHours) {
        this.store = store;
        this.ttl = Duration.ofHours(ttlHours);
    }

    @Scheduled(fixedDelayString = "${idempotency.cleanup-interval-ms:3600000}")
    public void purgeExpiredKeys() {
        int removed = store.purgeOlderThan(ttl);
        if (removed > 0) {
            log.info("Removed {} expired idempotency records", removed);
        }
    }
}
