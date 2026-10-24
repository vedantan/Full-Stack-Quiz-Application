package com.vedant.demo.idempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

/**
 * One row per (owner, Idempotency-Key). The UNIQUE constraint is what makes the
 * whole mechanism safe under concurrency: the database lets exactly one request
 * insert the row, every other concurrent request with the same key fails the insert.
 */
@Entity
@Table(
        name = "idempotency_record",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_idempotency_owner_key",
                columnNames = {"owner_scope", "idempotency_key"}))
public class IdempotencyRecord {

    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String COMPLETED = "COMPLETED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Who sent the request (username from the gateway, or "anonymous"), so two users can reuse the same key. */
    @Column(name = "owner_scope", nullable = false, length = 255)
    private String ownerScope;

    @Column(name = "idempotency_key", nullable = false, length = 255)
    private String idempotencyKey;

    /** SHA-256 of method + path + query + body, used to detect "same key, different request". */
    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;

    @Column(nullable = false, length = 20)
    private String status;

    private Integer responseStatus;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    private String responseContentType;

    @Column(nullable = false)
    private Instant createdAt;

    public IdempotencyRecord() {
    }

    public IdempotencyRecord(String ownerScope, String idempotencyKey, String requestHash) {
        this.ownerScope = ownerScope;
        this.idempotencyKey = idempotencyKey;
        this.requestHash = requestHash;
        this.status = IN_PROGRESS;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getOwnerScope() {
        return ownerScope;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
