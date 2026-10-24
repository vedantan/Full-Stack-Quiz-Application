package com.vedant.demo.idempotency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Optional;

/**
 * Makes selected POST endpoints idempotent using the "Idempotency-Key" request header.
 *
 * <pre>
 * no header                      -> request runs normally (opt-in, so old clients keep working)
 * new key                        -> run it, store the 2xx response under the key
 * same key + same request        -> do NOT run again, replay the stored response
 * same key + different request   -> 422 (client bug: key reused for another payload)
 * same key while first is running-> 409 + Retry-After (client should retry shortly)
 * first attempt failed (4xx/5xx) -> key is released, so a retry runs again
 * </pre>
 *
 * Runs after Spring Security, so the authenticated user is known and keys are scoped per user.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class IdempotencyFilter extends OncePerRequestFilter {

    public static final String HEADER = "Idempotency-Key";
    public static final String REPLAY_HEADER = "Idempotency-Replayed";
    private static final int MAX_KEY_LENGTH = 255;

    private final IdempotencyStore store;
    private final String[] protectedPaths;
    private final Duration inProgressTimeout;
    private final boolean keyRequired;

    public IdempotencyFilter(
            IdempotencyStore store,
            @Value("${idempotency.protected-paths:}") String[] protectedPaths,
            @Value("${idempotency.in-progress-timeout-seconds:30}") long inProgressTimeoutSeconds,
            @Value("${idempotency.required:false}") boolean keyRequired) {
        this.store = store;
        this.protectedPaths = protectedPaths;
        this.inProgressTimeout = Duration.ofSeconds(inProgressTimeoutSeconds);
        this.keyRequired = keyRequired;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = stripTrailingSlash(request.getRequestURI());
        return Arrays.stream(protectedPaths)
                .map(String::trim)
                .noneMatch(p -> p.equals(path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String key = request.getHeader(HEADER);

        if (key == null || key.isBlank()) {
            if (keyRequired) {
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Idempotency-Key header is required");
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        key = key.trim();
        if (key.length() > MAX_KEY_LENGTH) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Idempotency-Key is too long");
            return;
        }

        CachedBodyRequest cachedRequest = new CachedBodyRequest(request);
        String requestHash = fingerprint(cachedRequest);
        String owner = currentOwner();

        Optional<IdempotencyRecord> started = store.tryStart(owner, key, requestHash);
        if (started.isPresent()) {
            execute(cachedRequest, response, chain, started.get().getId());
            return;
        }

        // Somebody already used this key.
        Optional<IdempotencyRecord> existing = store.find(owner, key);
        if (existing.isEmpty()) {
            // Record vanished between our insert and our read (released or purged). Ask the client to retry.
            conflict(response, "Request is being processed, retry shortly");
            return;
        }

        IdempotencyRecord record = existing.get();

        if (!record.getRequestHash().equals(requestHash)) {
            writeError(response, 422, "Idempotency-Key was already used with a different request");
            return;
        }

        if (IdempotencyRecord.COMPLETED.equals(record.getStatus())) {
            replay(response, record);
            return;
        }

        // IN_PROGRESS: either another request is running right now, or the first one died.
        if (store.claimIfStale(record.getId(), inProgressTimeout)) {
            execute(cachedRequest, response, chain, record.getId());
            return;
        }
        conflict(response, "Request is being processed, retry shortly");
    }

    /** Run the real endpoint exactly once and remember its result. */
    private void execute(CachedBodyRequest request,
                         HttpServletResponse response,
                         FilterChain chain,
                         Long recordId) throws ServletException, IOException {

        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);

        try {
            chain.doFilter(request, wrapper);
        } catch (IOException | ServletException | RuntimeException e) {
            store.release(recordId); // it blew up: nothing to replay, let the client retry
            throw e;
        }

        int status = wrapper.getStatus();
        if (status >= 200 && status < 300) {
            String body = new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            store.complete(recordId, status, wrapper.getContentType(), body);
        } else {
            store.release(recordId); // failed requests are not cached, the client may fix and retry
        }

        wrapper.copyBodyToResponse(); // ContentCachingResponseWrapper buffers: flush the real response now
    }

    private void replay(HttpServletResponse response, IdempotencyRecord record) throws IOException {
        response.setStatus(record.getResponseStatus());
        if (record.getResponseContentType() != null) {
            response.setContentType(record.getResponseContentType());
        }
        response.setHeader(REPLAY_HEADER, "true");
        byte[] bytes = record.getResponseBody() == null
                ? new byte[0]
                : record.getResponseBody().getBytes(StandardCharsets.UTF_8);
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
    }

    private void conflict(HttpServletResponse response, String message) throws IOException {
        response.setHeader("Retry-After", "1");
        writeError(response, HttpServletResponse.SC_CONFLICT, message);
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":" + status + ",\"error\":\"" + message + "\"}");
    }

    /** Keys are per user, so two users picking the same key never collide or see each other's responses. */
    private String currentOwner() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return auth.getName();
        }
        return "anonymous";
    }

    /** SHA-256 over method, path, query and body: identifies "the same request". */
    private String fingerprint(CachedBodyRequest request) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(request.getMethod().getBytes(StandardCharsets.UTF_8));
            digest.update((byte) '\n');
            digest.update(request.getRequestURI().getBytes(StandardCharsets.UTF_8));
            digest.update((byte) '\n');
            String query = request.getQueryString();
            digest.update((query == null ? "" : query).getBytes(StandardCharsets.UTF_8));
            digest.update((byte) '\n');
            digest.update(request.getBody());
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is always available in the JDK", e);
        }
    }

    private static String stripTrailingSlash(String path) {
        return path.length() > 1 && path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }
}
