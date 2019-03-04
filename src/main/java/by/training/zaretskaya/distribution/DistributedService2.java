package by.training.zaretskaya.distribution;

import by.training.zaretskaya.config.Configuration;
import by.training.zaretskaya.config.Node;
import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.FailedOperationException;
import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.models.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DistributedService2 {
    private final String NAME_APPLICATION = "/rest/";
    private Map<Long, List<Node>> listGroups;
    private RestTemplate restTemplate;

    public DistributedService2() {
        listGroups = Configuration.getAllGroups();
        restTemplate = new RestTemplate();
    }

    public boolean isMyGroup(String id) {
        return defineIdGroup(id) == Configuration.getCurrentNode().getIdGroup();
    }

    public Object sendGetObject(int counter, String... parameters) {
        List<Node> list = listGroups.get(defineIdGroup(parameters[0]));
       // List<Node> newList = new ArrayList<Node>(list);
        counter++;
        if (counter == list.size()) {
            if (parameters.length == 2) {
                throw new ResourceNotFoundException(Constants.RESOURCE_DOCUMENT, parameters[1]);
            }
            throw new ResourceNotFoundException(Constants.RESOURCE_COLLECTION, parameters[0]);
        }
        int positionNodeToSend = defineNextNode(list);
        String uri = getURI(list.get(positionNodeToSend).getHost(), parameters);
        try {
            ResponseEntity<Object> responseEntity = restTemplate
                    .exchange(uri, HttpMethod.GET, new HttpEntity<>(getHeaders(counter)), Object.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                if (parameters.length == 2) {
                    throw new ResourceNotFoundException(Constants.RESOURCE_DOCUMENT, parameters[1]);
                }
                throw new ResourceNotFoundException(Constants.RESOURCE_COLLECTION, parameters[0]);
            }
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            //Костыль!
            counter = counter - 2;
            return sendGetObject(counter, parameters);
        }
    }

    public void sendUpdateObject(Object object, int counter, boolean flagRollback,
                                 String... parameters) {
        List<Node> list = listGroups.get(defineIdGroup(parameters[0]));
        int positionNodeToSend = 0;
        if (!flagRollback) {
            counter++;
            if (counter == list.size()) {
                return;
            }
            positionNodeToSend = defineNextNode(list);
        } else {
            if (counter == 0) {
                throw new FailedOperationException();
            }
            counter--;
            positionNodeToSend = definePreviousNode(list);
        }
        String uri = getURI(list.get(positionNodeToSend).getHost(), parameters);
        try {
            restTemplate.exchange(uri, HttpMethod.PUT,
                    getHttpEntity(object, getHeaders(counter, flagRollback)), Object.class);
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            throw new FailedOperationException();
        }
    }

    public void sendPostObject(Object object, int counter, boolean flagRollback, String... parameters) {
        List<Node> list = listGroups.get(defineIdGroup(parameters[0]));
        int positionNodeToSend = 0;
        if (!flagRollback) {
            counter++;
            if (counter == list.size()) {
                return;
            }
            positionNodeToSend = defineNextNode(list);
        } else {
            if (counter == 0) {
                throw new FailedOperationException();
            }
            counter--;
            positionNodeToSend = definePreviousNode(list);
        }
        String uri;
        if (object instanceof Collection) {
            uri = list.get(positionNodeToSend).getHost() + NAME_APPLICATION;
        } else {
            uri = list.get(positionNodeToSend).getHost() + NAME_APPLICATION + "docs";
        }
        try {
            restTemplate
                    .postForEntity(uri, getHttpEntity(object, getHeaders(counter, flagRollback)), Object.class);
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            throw new FailedOperationException();
        }
    }


    public void sendDeleteObject(int counter, boolean flagRollback, String... parameters) throws FailedOperationException {
        List<Node> list = listGroups.get(defineIdGroup(parameters[0]));
        int positionNodeToSend = 0;
        if (!flagRollback) {
            counter++;
            if (counter == list.size()) {
                return;
            }
            positionNodeToSend = defineNextNode(list);
        } else {
            if (counter == 0) {
                throw new FailedOperationException();
            }
            counter--;
            positionNodeToSend = definePreviousNode(list);
        }
        String uri = getURI(list.get(positionNodeToSend).getHost(), parameters);
        try {
            restTemplate.exchange(uri,
                    HttpMethod.DELETE, new HttpEntity<>(getHeaders(counter, flagRollback)), Object.class);
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            throw new FailedOperationException();
        }
    }

    public ResponseEntity redirectPost(Collection collection) {
        List<Node> list = listGroups.get(defineIdGroup(collection.getName()));
        try {
            return restTemplate.postForEntity(list.get(0).getHost() + NAME_APPLICATION,
                    getHttpEntity(collection, getHeaders()), Collection.class);
        } catch (ResourceAccessException e) {
            throw new FailedOperationException();
        }
    }

    public Object redirectPut(String id, Collection collection) {
        List<Node> list = listGroups.get(defineIdGroup(id));
        restTemplate = new RestTemplate();
        try {
            return restTemplate.exchange(constructURI(list.get(0).getHost(), id), HttpMethod.PUT,
                    getHttpEntity(collection, getHeaders()), Collection.class);
        } catch (ResourceAccessException e) {
            throw new FailedOperationException();
        }
    }

    public Object redirect(String id, HttpMethod method) {
        List<Node> list = listGroups.get(defineIdGroup(id));
        restTemplate = new RestTemplate();
        if (method == HttpMethod.GET) {
            for (int i = 0; i < list.size(); i++) {
                try {
                    return restTemplate.exchange(constructURI(list.get(i).getHost(), id), method,
                            new HttpEntity<>(getHeaders()), Collection.class);
                } catch (ResourceAccessException e) {
                    if (i == (list.size() - 1)) {
                        throw new FailedOperationException();
                    }
                }
            }
        }
        try {
            return restTemplate.exchange(constructURI(list.get(0).getHost(), id), method,
                    new HttpEntity<>(getHeaders()), Collection.class);
        } catch (ResourceAccessException e) {
            throw new FailedOperationException();
        }
    }

    private int defineIdGroup(String id) {
        return id.hashCode() % listGroups.size();
    }

    private String constructURI(String host, String id) {
        return host + NAME_APPLICATION + id;
    }

    private String constructURIForDocument(String host, String idCollection, String idDocument) {
        return constructURI(host, idCollection) + "/docs/" + idDocument;
    }

    private HttpEntity getHttpEntity(Object object, HttpHeaders headers) {
        return new HttpEntity<>(object, headers);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return headers;
    }

    private HttpHeaders getHeaders(int counter) {
        HttpHeaders headers = getHeaders();
        headers.add("counter", String.valueOf(counter));
        return headers;
    }

    private HttpHeaders getHeaders(int counter, boolean rollback) {
        HttpHeaders headers = getHeaders(counter);
        headers.add("rollback", String.valueOf(rollback));
        return headers;
    }

    private int defineNextNode(List<Node> list) {
        int positionNextNode = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == Configuration.getCurrentNode()) {
                positionNextNode = i + 1;
                if ((i + 1) == list.size()) {
                    positionNextNode = 0;
                }
                break;
            }
        }
        return positionNextNode;
    }

    private int definePreviousNode(List<Node> list) {
        int positionPreviousNode = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == Configuration.getCurrentNode()) {
                positionPreviousNode = i - 1;
                if ((i - 1) < 0) {
                    positionPreviousNode = list.size() - 1;
                }
                break;
            }
        }
        return positionPreviousNode;
    }

    private String getURI(String host, String... ids) {
        if (ids.length == 2) {
            return constructURIForDocument(host, ids[0], ids[1]);
        }
        return constructURI(host, ids[0]);
    }
}
