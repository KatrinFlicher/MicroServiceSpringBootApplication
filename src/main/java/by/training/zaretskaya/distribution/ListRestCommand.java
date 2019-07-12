package by.training.zaretskaya.distribution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public class ListRestCommand<T> extends RestCommand<T> {
    private static final Logger log = LogManager.getLogger(ListRestCommand.class);
    private String objectToCompare;
    private int size;

    public ListRestCommand(HttpMethod method, Class<T> clazz, String host, String objectToCompare, int size,
                           String... params) {
        super(method, clazz, host, params);
        this.objectToCompare = objectToCompare;
        this.size = size;
    }

    ResponseEntity<List<T>> executeList() {
        log.info("Send " + getMethod().toString() + " request to " + getUri());
        return getRestTemplate().exchange(getUri(), getMethod(),
                getHttpEntity(), new ParameterizedTypeReference<List<T>>() {
                }, getParamsForRequest(objectToCompare, size));
    }

    private Map<String, String> getParamsForRequest(String objectToCompare, int size) {
        return Map.of("compare", objectToCompare, "size", String.valueOf(size));
    }
}
