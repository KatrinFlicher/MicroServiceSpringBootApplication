package by.training.zaretskaya.distribution;

import by.training.zaretskaya.constants.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

public class RestCommand<T> {
    private static final Logger log = LogManager.getLogger(RestCommand.class);

    private RestTemplate restTemplate;
    private HttpMethod method;
    private Class<T> clazz;
    private String uri;
    private T entity;
    private boolean flagReplica = true;

    {
        restTemplate = BeanUtil.getBean(RestTemplate.class);
    }

    public RestCommand(HttpMethod method, Class<T> clazz, String host, String... params) {
        this.method = method;
        this.clazz = clazz;
        this.uri = constructURI(host + Constants.NAME_APPLICATION, params);
    }

    public RestCommand(HttpMethod method, Class<T> clazz, String host, T entity, String... params) {
        this.method = method;
        this.clazz = clazz;
        this.uri = constructURI(host + Constants.NAME_APPLICATION, params);
        this.entity = entity;
    }

    private static String constructURI(String host, String... ids) {
        if (ids != null && ids.length != 0) {
            host = host + "/" + ids[0];
            int newlength = ids.length - 1;
            if (newlength != 0) {
                String[] dest = new String[newlength];
                System.arraycopy(ids, 1, dest, 0, newlength);
                host = constructURI(host, dest);
            }
        }
        return host;
    }

    ResponseEntity<T> execute() {
        log.info("Send " + method.toString() + " request to " + uri);
        return restTemplate.exchange(uri, method, getHttpEntity(), clazz);
    }

    ResponseEntity<T> rollback(@Nullable T oldValue) {
        entity = oldValue;
        return restTemplate.exchange(uri, Method.valueOf(method.toString()).getRollbackMethod(),
                getHttpEntity(), clazz);
    }

    HttpEntity getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (flagReplica) {
            headers.add("replica", String.valueOf(Constants.REPLICA_ON));
        }
        return new HttpEntity<>(entity, headers);
    }

    void turnOffFlagReplica() {
        this.flagReplica = false;
    }

    RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    String getUri() {
        return uri;
    }

    public T getEntity() {
        return entity;
    }

    enum Method {
        PUT(HttpMethod.PUT), DELETE(HttpMethod.POST), POST(HttpMethod.DELETE);

        private HttpMethod rollbackMethod;

        Method(HttpMethod rollbackMethod) {
            this.rollbackMethod = rollbackMethod;
        }

        public HttpMethod getRollbackMethod() {
            return rollbackMethod;
        }
    }
}
