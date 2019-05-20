package by.training.zaretskaya.distribution;

import by.training.zaretskaya.config.Node;
import by.training.zaretskaya.exception.FailedOperationException;
import by.training.zaretskaya.exception.ResourceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.Collection;

public class DistributedServiceBase<T> {
    private static final Logger log = LogManager.getLogger(DistributedServiceBase.class);

    private Class<T> clazz;

    public DistributedServiceBase(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ResponseEntity<T> get(Collection<Node> nodes, String... namesResources) {
        for (Node node : nodes) {
            try {
                ResponseEntity<T> entity = new RestCommand<>(HttpMethod.GET, clazz,
                        node.getHost(), namesResources).execute();
                log.info("Response is received from " + node.getHost());
                return entity;
            } catch (ResourceAccessException | HttpServerErrorException.ServiceUnavailable e) {
                log.error("Node " + node.getName() + " is unavailable.", e);
            } catch (HttpClientErrorException e) {
                log.error("ClientError is received from " + node.getName(), e);
                throw new ResourceNotFoundException(new JSONObject(e.getResponseBodyAsString())
                        .getString("message"));
            }
        }
        log.error("Operation \"GET" + clazz.getSimpleName() + "\" was failed");
        throw new FailedOperationException();
    }

    void callNodes(Collection<Node> nodes, HttpMethod method, T oldValue, T entity, String... namesResources) {
        ArrayList<RestCommand<T>> commands = new ArrayList<>();
        for (Node node : nodes) {
            try {
                RestCommand<T> command = new RestCommand<>(method, clazz,
                        node.getHost(), entity, namesResources);
                command.execute();
                commands.add(command);
                log.info("Response is received from " + node.getHost());
            } catch (ResourceAccessException | HttpServerErrorException.ServiceUnavailable e) {
                log.error("Operation \"" + method.toString() + " " + clazz.getSimpleName() +
                        "\" was failed. Start rollback.", e);
                try {
                    commands.forEach((command) -> command.rollback(oldValue));
                } catch (ResourceAccessException | HttpServerErrorException.ServiceUnavailable e1) {
                    log.fatal("Problem with rollback. The application doesn't work correctly", e1);
                }
                throw new FailedOperationException();
            }
        }
    }
}
