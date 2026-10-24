// Client side of idempotency.
//
// Rule: one Idempotency-Key per *logical operation* (e.g. "add this question").
// Every retry of that operation must send the SAME key, so the server can
// recognise it and replay the first result instead of creating a duplicate.

export const IDEMPOTENCY_HEADER = "Idempotency-Key";

// POST endpoints that create data. These must match idempotency.protected-paths on the backend.
const IDEMPOTENT_POST_PATHS = [
  "/quiz/create",
  "/questions/addQuestion",
  "/auth/signup",
  "/auth/admin/create",
];

const MAX_RETRIES = 2;

export function generateIdempotencyKey() {
  if (typeof crypto !== "undefined" && typeof crypto.randomUUID === "function") {
    return crypto.randomUUID();
  }
  // Fallback for non-secure contexts (plain http on a non-localhost host)
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0;
    return (c === "x" ? r : (r & 0x3) | 0x8).toString(16);
  });
}

function readHeader(headers, name) {
  if (!headers) return undefined;
  return typeof headers.get === "function" ? headers.get(name) : headers[name];
}

function writeHeader(headers, name, value) {
  if (typeof headers.set === "function") {
    headers.set(name, value);
  } else {
    headers[name] = value;
  }
}

function isIdempotentPost(config) {
  if ((config.method || "get").toLowerCase() !== "post") return false;
  const url = (config.url || "").split("?")[0];
  return IDEMPOTENT_POST_PATHS.some((path) => url.endsWith(path));
}

/**
 * Request interceptor helper: adds an Idempotency-Key to create-style POSTs,
 * unless the caller already supplied one (e.g. a form that reuses its key).
 * Because a retry re-sends the same config, the key survives automatically.
 */
export function attachIdempotencyKey(config) {
  if (!isIdempotentPost(config)) return config;
  config.headers = config.headers || {};
  if (!readHeader(config.headers, IDEMPOTENCY_HEADER)) {
    writeHeader(config.headers, IDEMPOTENCY_HEADER, generateIdempotencyKey());
  }
  return config;
}

/**
 * Retrying is only safe because the key makes the POST idempotent, so we only
 * retry requests that carry a key. Retry when we do not know the outcome
 * (network error / timeout / gateway errors) or when the server says the first
 * attempt is still running (409 + Retry-After).
 */
export function shouldRetry(error) {
  const config = error && error.config;
  if (!config || !readHeader(config.headers, IDEMPOTENCY_HEADER)) return false;
  if ((config.__retryCount || 0) >= MAX_RETRIES) return false;

  const response = error.response;
  if (!response) return true; // no response at all: the request may or may not have been processed
  if ([502, 503, 504].includes(response.status)) return true;
  return response.status === 409 && Boolean(readHeader(response.headers, "retry-after"));
}

export function retryDelayMs(error) {
  const config = error.config;
  const attempt = config.__retryCount || 1;
  const retryAfter = Number(readHeader(error.response && error.response.headers, "retry-after"));
  if (retryAfter > 0) return retryAfter * 1000;
  return 300 * 2 ** (attempt - 1); // 300ms, 600ms
}
