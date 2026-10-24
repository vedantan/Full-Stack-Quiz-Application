package com.vedant.demo.idempotency;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Thin persistence layer for idempotency records. Kept as a separate bean (not inside the filter)
 * so every method runs through the Spring proxy and gets its own short transaction.
 */
@Service
public class IdempotencyStore {

    private final IdempotencyRecordRepository repository;

    public IdempotencyStore(IdempotencyRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * Try to become the one request that executes the operation.
     *
     * @return the new IN_PROGRESS record if we won; empty if a record for this key already exists.
     */
    public Optional<IdempotencyRecord> tryStart(String ownerScope, String key, String requestHash) {
        try {
            // saveAndFlush => the INSERT hits the DB (and its UNIQUE constraint) right now, in its own transaction.
            return Optional.of(repository.saveAndFlush(new IdempotencyRecord(ownerScope, key, requestHash)));
        } catch (DataIntegrityViolationException duplicateKey) {
            return Optional.empty();
        }
    }

    public Optional<IdempotencyRecord> find(String ownerScope, String key) {
        return repository.findByOwnerScopeAndIdempotencyKey(ownerScope, key);
    }

    /** @return true if this caller took over an abandoned IN_PROGRESS record and may now execute. */
    public boolean claimIfStale(Long id, Duration timeout) {
        Instant now = Instant.now();
        return repository.claimIfStale(id, now, now.minus(timeout)) == 1;
    }

    @Transactional
    public void complete(Long id, int responseStatus, String contentType, String body) {
        repository.findById(id).ifPresent(record -> {
            record.setStatus(IdempotencyRecord.COMPLETED);
            record.setResponseStatus(responseStatus);
            record.setResponseContentType(contentType);
            record.setResponseBody(body);
        });
    }

    /** Forget a key so the client may retry (used when the attempt failed and produced no result). */
    public void release(Long id) {
        repository.deleteById(id);
    }

    public int purgeOlderThan(Duration ttl) {
        return repository.deleteOlderThan(Instant.now().minus(ttl));
    }
}
