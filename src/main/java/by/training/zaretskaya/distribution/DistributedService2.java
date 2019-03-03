package by.training.zaretskaya.distribution;

import by.training.zaretskaya.config.Configuration;
import by.training.zaretskaya.config.Node;
import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.FailedOperationException;
import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.models.Collection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class DistributedService2 {
    private final String NAME_APPLICATION = "/rest/";
    private Map<Long, List<Node>> listGroups;
    private RestTemplate restTemplate;

    public DistributedService2() {
        listGroups = Configuration.getAllGroups();
    }

    public boolean isMyGroup(String id) {
        return defineIdGroup(id) == Configuration.getCurrentNode().getIdGroup();
    }

    public boolean groupConsistReplicas(String id) {
        int idGroup = defineIdGroup(id);
        return listGroups.get(idGroup).size() == 1;
    }

    public Object sendGetObject(int counter, String... parameters) {
        List<Node> list = listGroups.get(defineIdGroup(parameters[0]));
        counter++;
        if (counter == list.size()) {
            if (parameters.length == 2) {
                throw new ResourceNotFoundException(Constants.RESOURCE_DOCUMENT, parameters[1]);
            }
            throw new ResourceNotFoundException(Constants.RESOURCE_COLLECTION, parameters[0]);
        }
        int positionNodeToSend = defineNextNode(list);
        restTemplate = new RestTemplate();
        String uri = getURI(list.get(positionNodeToSend).getHost(), parameters);
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
    }

    public void sendUpdateObject(Object object, int counter, boolean flagRollback,
                                 String... parameters) throws ResourceAccessException {
        List<Node> list = listGroups.get(defineIdGroup(parameters[0]));
        int positionNodeToSend = 0;
        if (!flagRollback) {
            counter++;
            if (counter == list.size()) {
                return;
            }
            positionNodeToSend = defineNextNode(list);
        } else {
            counter--;
            if (counter == 0) {
                throw new FailedOperationException();
            }
            positionNodeToSend = definePreviousNode(list);
        }
        String uri = getURI(list.get(positionNodeToSend).getHost(), parameters);
        restTemplate = new RestTemplate();
        ResponseEntity<Object> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT,
                getHttpEntity(object, getHeaders(counter, flagRollback)), Object.class);
        checkStatusCode(responseEntity);
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
            counter--;
            if (counter == 0) {
                throw new FailedOperationException();
            }
            positionNodeToSend = definePreviousNode(list);
        }
        String uri;
        if (object instanceof Collection) {
            uri = list.get(positionNodeToSend).getHost() + NAME_APPLICATION;
        } else {
            uri = list.get(positionNodeToSend).getHost() + NAME_APPLICATION + "docs";
        }
        restTemplate = new RestTemplate();
        ResponseEntity<Object> responseEntity = restTemplate
                .postForEntity(uri, getHttpEntity(object, getHeaders(counter, flagRollback)), Object.class);
        checkStatusCode(responseEntity);
    }


    public void sendDeleteObject(int counter, boolean flagRollback, String... parameters) {
        List<Node> list = listGroups.get(defineIdGroup(parameters[0]));
        int positionNodeToSend = 0;
        if (!flagRollback) {
            counter++;
            if (counter == list.size()) {
                return;
            }
            positionNodeToSend = defineNextNode(list);
        } else {
            counter--;
            if (counter == 0) {
                throw new FailedOperationException();
            }
            positionNodeToSend = definePreviousNode(list);
        }
        String uri = getURI(list.get(positionNodeToSend).getHost(), parameters);
        restTemplate = new RestTemplate();
        ResponseEntity<Object> responseEntity = restTemplate
                .exchange(uri,
                        HttpMethod.DELETE, new HttpEntity<>(getHeaders(counter, flagRollback)), Object.class);
        checkStatusCode(responseEntity);
    }

    public ResponseEntity redirectPost(Collection collection) {
        List<Node> list = listGroups.get(defineIdGroup(collection.getName()));
        restTemplate = new RestTemplate();
        return restTemplate.postForEntity(list.get(0).getHost() + NAME_APPLICATION,
                getHttpEntity(collection, getHeaders()), Collection.class);
    }

    public Object redirectPut(String id, Collection collection, String variableField) {
        List<Node> list = listGroups.get(defineIdGroup(id));
        restTemplate = new RestTemplate();
        return restTemplate.exchange(constructURI(list.get(0).getHost(), id, variableField), HttpMethod.PUT,
                getHttpEntity(collection, getHeaders()), Collection.class);
    }

    public Object redirect(String id, HttpMethod method) {
        List<Node> list = listGroups.get(defineIdGroup(id));
        restTemplate = new RestTemplate();
        return restTemplate.exchange(constructURI(list.get(0).getHost(), id), method,
                new HttpEntity<>(getHeaders()), Collection.class);
    }

    private int defineIdGroup(String id) {
        return id.hashCode() % listGroups.size();
    }

    private String constructURI(String host, String id) {
        return host + NAME_APPLICATION + id;
    }

    private String constructURI(String host, String id, String variableField) {
        return constructURI(host, id) + "/" + variableField;
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

    private void checkStatusCode(ResponseEntity responseEntity) {
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return;
        }
        if (responseEntity.getStatusCode().is5xxServerError()) {
            throw new FailedOperationException();
        }
    }

    private String getURI(String host, String... ids) {
        if (ids.length == 2) {
            if (ids[1].equals(Constants.VARIABLE_FIELD_LIMIT) ||
                    ids[1].equals(Constants.VARIABLE_FIELD_ALGORITHM) ||
                    ids[1].equals(Constants.VARIABLE_FIELD_NAME)) {
                return constructURI(host, ids[0], ids[1]);
            }
            return constructURIForDocument(host, ids[0], ids[1]);
        }
        return constructURI(host, ids[0]);
    }
}
