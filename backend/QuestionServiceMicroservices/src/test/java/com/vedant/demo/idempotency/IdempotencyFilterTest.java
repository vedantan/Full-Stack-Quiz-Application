package com.vedant.demo.idempotency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Runs the real filter + real JPA store (in-memory H2) in front of a tiny fake controller.
 * Transactions are switched off so inserts are committed and visible to other threads, like in production.
 */
@DataJpaTest
@Import(IdempotencyStore.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class IdempotencyFilterTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = IdempotencyRecord.class)
    @EnableJpaRepositories(basePackageClasses = IdempotencyRecordRepository.class)
    static class TestConfig {
    }

    @RestController
    static class DemoController {
        final AtomicInteger calls = new AtomicInteger();
        volatile boolean fail = false;
        volatile CountDownLatch entered = new CountDownLatch(1);
        volatile CountDownLatch proceed = new CountDownLatch(0);

        @PostMapping("/api/demo/create")
        ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) throws InterruptedException {
            int n = calls.incrementAndGet();
            entered.countDown();
            proceed.await(5, TimeUnit.SECONDS);
            if (fail) {
                return ResponseEntity.status(500).body(Map.of("error", "boom"));
            }
            return ResponseEntity.status(201).body(Map.of("id", n, "name", body.get("name")));
        }

        @PostMapping("/api/demo/other")
        ResponseEntity<String> other() {
            calls.incrementAndGet();
            return ResponseEntity.ok("ok");
        }
    }

    @Autowired
    IdempotencyStore store;

    @Autowired
    IdempotencyRecordRepository repository;

    DemoController controller;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        controller = new DemoController();
        IdempotencyFilter filter = new IdempotencyFilter(store, new String[]{"/api/demo/create"}, 30, false);
        mvc = MockMvcBuilders.standaloneSetup(controller).addFilters(filter).build();
    }

    private MvcResult send(String key, String json) throws Exception {
        var request = post("/api/demo/create").contentType(MediaType.APPLICATION_JSON).content(json);
        if (key != null) {
            request.header(IdempotencyFilter.HEADER, key);
        }
        return mvc.perform(request).andReturn();
    }

    @Test
    void sameKeyAndBody_executesOnce_andReplaysStoredResponse() throws Exception {
        MvcResult first = send("k1", "{\"name\":\"java\"}");
        MvcResult second = send("k1", "{\"name\":\"java\"}");

        assertEquals(1, controller.calls.get());
        assertEquals(201, first.getResponse().getStatus());
        assertEquals(201, second.getResponse().getStatus());
        assertEquals(first.getResponse().getContentAsString(), second.getResponse().getContentAsString());
        assertNull(first.getResponse().getHeader(IdempotencyFilter.REPLAY_HEADER));
        assertEquals("true", second.getResponse().getHeader(IdempotencyFilter.REPLAY_HEADER));
    }

    @Test
    void sameKeyDifferentBody_isRejectedWith422() throws Exception {
        send("k2", "{\"name\":\"java\"}");
        MvcResult second = send("k2", "{\"name\":\"python\"}");

        assertEquals(422, second.getResponse().getStatus());
        assertEquals(1, controller.calls.get());
    }

    @Test
    void noKey_runsEveryTime() throws Exception {
        send(null, "{\"name\":\"java\"}");
        send(null, "{\"name\":\"java\"}");

        assertEquals(2, controller.calls.get());
    }

    @Test
    void differentKeys_areIndependent() throws Exception {
        send("a", "{\"name\":\"java\"}");
        send("b", "{\"name\":\"java\"}");

        assertEquals(2, controller.calls.get());
    }

    @Test
    void failedAttempt_isNotCached_soRetryRunsAgain() throws Exception {
        controller.fail = true;
        MvcResult failed = send("k3", "{\"name\":\"java\"}");
        assertEquals(500, failed.getResponse().getStatus());

        controller.fail = false;
        MvcResult retry = send("k3", "{\"name\":\"java\"}");

        assertEquals(201, retry.getResponse().getStatus());
        assertEquals(2, controller.calls.get());
    }

    @Test
    void unprotectedPath_isIgnoredByFilter() throws Exception {
        for (int i = 0; i < 2; i++) {
            mvc.perform(post("/api/demo/other").header(IdempotencyFilter.HEADER, "x")).andReturn();
        }
        assertEquals(2, controller.calls.get());
    }

    @Test
    void concurrentDuplicate_getsConflict_thenReplay() throws Exception {
        controller.entered = new CountDownLatch(1);
        controller.proceed = new CountDownLatch(1); // hold the first request inside the controller

        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            Future<MvcResult> firstRequest = pool.submit(() -> send("k4", "{\"name\":\"java\"}"));
            controller.entered.await(5, TimeUnit.SECONDS);

            MvcResult duplicate = send("k4", "{\"name\":\"java\"}");
            assertEquals(409, duplicate.getResponse().getStatus());
            assertEquals("1", duplicate.getResponse().getHeader("Retry-After"));

            controller.proceed.countDown();
            assertEquals(201, firstRequest.get(5, TimeUnit.SECONDS).getResponse().getStatus());

            MvcResult replay = send("k4", "{\"name\":\"java\"}");
            assertEquals(201, replay.getResponse().getStatus());
            assertEquals("true", replay.getResponse().getHeader(IdempotencyFilter.REPLAY_HEADER));
            assertEquals(1, controller.calls.get());
        } finally {
            pool.shutdownNow();
        }
    }
}
