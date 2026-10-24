package com.vedant.demo.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, Long> {

    Optional<IdempotencyRecord> findByOwnerScopeAndIdempotencyKey(String ownerScope, String idempotencyKey);

    /**
     * Atomically take over a record whose first request seems to have died (server crashed mid-request).
     * The WHERE clause makes this a compare-and-set: only one of several competing retries gets "1" back.
     */
    @Modifying
    @Transactional
    @Query("update IdempotencyRecord r set r.createdAt = :now "
            + "where r.id = :id and r.status = 'IN_PROGRESS' and r.createdAt < :staleBefore")
    int claimIfStale(@Param("id") Long id,
                     @Param("now") Instant now,
                     @Param("staleBefore") Instant staleBefore);

    @Modifying
    @Transactional
    @Query("delete from IdempotencyRecord r where r.createdAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") Instant cutoff);
}
