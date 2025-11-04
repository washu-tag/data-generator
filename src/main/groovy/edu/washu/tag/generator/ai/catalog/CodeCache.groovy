package edu.washu.tag.generator.ai.catalog

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.util.concurrent.RateLimiter
import edu.washu.tag.generator.ai.catalog.attribute.DiagnosisCodeDesignator
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.function.Supplier

class CodeCache {

    private static RateLimitedHttpClient httpClient
    private static final int MAX_RETRIES = 5
    private static final ObjectMapper objectMapper = new ObjectMapper()
    private static final Logger logger = LoggerFactory.getLogger(CodeCache)
    public static final Map<DiagnosisCodeDesignator, Map<String, String>> codes = [:]
    public static final Map<DiagnosisCodeDesignator, List<String>> knownBadCodes = [:]

    static void initializeCache(int concurrentExecution) {
        if (httpClient == null) {
            httpClient = new RateLimitedHttpClient(concurrentExecution)
            ['C3', 'C5', 'C7', 'G9', 'I2', 'I5', 'I6', 'I7', 'J', 'K5', 'K7', 'K8', 'M4', 'M5', 'N6', 'N8', 'R9'].each { code ->
                cacheCodesFromSearch(DiagnosisCodeDesignator.ICD_10, code)
            }
        } else {
            logger.info("httpClient with rate limiting has already been initialized")
        }
    }

    static boolean validateCode(DiagnosisCodeDesignator designator, String code) {
        cacheCodesFromSearch(designator, code) == QueryStatus.MATCH
    }

    static String lookupCode(DiagnosisCodeDesignator designator, String code) {
        codes[designator][code]
    }

    private static QueryStatus cacheCodesFromSearch(DiagnosisCodeDesignator designator, String search) {
        final Map<String, String> codesForDesignator = codes.get(designator, [:])
        final List<String> knownBad = knownBadCodes.get(designator, [])
        if (knownBad.contains(search)) {
            return QueryStatus.NO_MATCH
        }
        if (codesForDesignator.containsKey(search)) {
            return QueryStatus.MATCH
        }
        final String url = designator.getUrlForSearch(search)
        logger.info("Querying URL for coding information: ${url}...")
        final HttpResponse<String> response = httpClient.issueQueryWithRetries({
            HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build()
        })
        if (response == null) {
            return QueryStatus.API_UNAVAILABLE
        } else {
            final List<Object> parsed = objectMapper.readValue(response.body(), List)
            parsed[3].each { List<String> code ->
                codesForDesignator.put(code[0], code[1])
            }
            if (codesForDesignator.containsKey(search)) {
                return QueryStatus.MATCH
            } else {
                knownBad << search
                return QueryStatus.NO_MATCH
            }
        }
    }

    private enum QueryStatus {
        MATCH, NO_MATCH, API_UNAVAILABLE
    }

    /**
     * The goal is that even if batches are being fulfilled on n different temporal workers, each worker
     * will consume at most (1/n) of the rate limit we want to enforce. This shouldn't cause significant performance
     * issues because:
     *   1) We cache a ton of the most commonly requested codes at the beginning with a relatively small amount of calls.
     *   2) We cache codes whenever we request them.
     *   3) We cache codes known to not be valid.
     */
    private static class RateLimitedHttpClient {
        private final HttpClient client
        private final RateLimiter rateLimiter
        private static final double MAX_REQUESTS_PER_SECOND = 25.0

        RateLimitedHttpClient(int concurrentExecution) {
            client = HttpClient.newHttpClient()
            rateLimiter = RateLimiter.create(MAX_REQUESTS_PER_SECOND / concurrentExecution)
        }

        HttpResponse<String> issueQuery(Supplier<HttpRequest> requestSupplier) {
            rateLimiter.acquire()
            client.send(requestSupplier.get(), HttpResponse.BodyHandlers.ofString())
        }

        HttpResponse<String> issueQueryWithRetries(Supplier<HttpRequest> requestSupplier, int maxRetries = MAX_RETRIES) {
            int counter = 0
            while (counter < maxRetries) {
                try {
                    final HttpResponse<String> response = issueQuery(requestSupplier)
                    if (response.statusCode() == 200) {
                        return response
                    } else {
                        pauseQuery()
                        counter++
                    }
                } catch (ConnectException connectException) {
                    pauseQuery()
                    counter++
                }
            }
            null
        }

        private void pauseQuery() {
            Thread.sleep(Duration.ofSeconds(1))
        }
    }

}
