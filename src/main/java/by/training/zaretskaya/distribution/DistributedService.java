package by.training.zaretskaya.distribution;

import by.training.zaretskaya.config.Configuration;
import by.training.zaretskaya.config.Node;
import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.OperationFailedException;
import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.models.Collection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class DistributedService {
    private final String NAME_APPLICATION = "/rest/";
    private Map<Long, List<Node>> listGroups;
    private Map<String, Node> nodesAll;
    private int counter;
    private RestTemplate restTemplate;

    public DistributedService() {
        listGroups = Configuration.getAllGroups();
        nodesAll = Configuration.getAllNodes();
    }

    public boolean isMyGroup(String id) {
        return defineIdGroup(id) == Configuration.getCurrentNode().getIdGroup();
    }

    public boolean groupConsistReplicas(String id) {
        int idGroup = defineIdGroup(id);
        return listGroups.get(idGroup).size() == 1;
    }

    public Object sendGet(String id, String counterFromRequest) {
        List<Node> list = listGroups.get(defineIdGroup(id));
        counter = Integer.valueOf(counterFromRequest);
        counter++;
        if (counter == list.size()) {
            throw new ResourceNotFoundException(Constants.RESOURCE_COLLECTION, id);
        }
        int positionNextNode = defineNextNode(list);
        HttpHeaders headers = new HttpHeaders();
        headers.add("counter", String.valueOf(counter));
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        headers.add("Content-Type", "application/json");
        restTemplate = new RestTemplate();
        ResponseEntity<Collection> responseEntity = restTemplate
                .exchange(list.get(positionNextNode).getHost(), HttpMethod.GET, httpEntity, Collection.class, id);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            throw new ResourceNotFoundException(Constants.RESOURCE_COLLECTION, id);
        }
    }

    public void sendUpdate(String id, String counterStr, Collection collection, String variableField) {
        List<Node> list = listGroups.get(defineIdGroup(id));
        incrementCounter(counterStr);
        if (counter == list.size()) {
            return;
        }
        int positionNextNode = defineNextNode(list);
        restTemplate = new RestTemplate();
        ResponseEntity<Collection> responseEntity = restTemplate.exchange
                (constructURI(list.get(positionNextNode).getHost(), id, variableField), HttpMethod.PUT,
                        getHttpEntity(collection, getHeaders(counter)), Collection.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return;
        }
    }

    public void sendPost(Collection collection, String counterStr) {
        List<Node> list = listGroups.get(defineIdGroup(collection.getName()));
        incrementCounter(counterStr);
        if (counter == list.size()) {
            return;
        }
        int positionNextNode = defineNextNode(list);
        restTemplate = new RestTemplate();
        ResponseEntity<Collection> responseEntity = restTemplate
                .postForEntity(list.get(positionNextNode).getHost() + NAME_APPLICATION,
                        getHttpEntity(collection, getHeaders(counter)), Collection.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return;
        }
    }

//    public void sendPost(Collection collection, String counterStr, String flagRollback) {
//        List<Node> list = listGroups.get(defineIdGroup(collection.getName()));
//        int positionNodeToSend = 0;
//        if (!Boolean.valueOf(flagRollback)) {
//            incrementCounter(counterStr);
//            if (counter == list.size()) {
//                return;
//            }
//            positionNodeToSend = defineNextNode(list);
//        } else {
//            decrementCounter(counterStr);
//            if (counter == 0) {
//                throw new OperationFailedException();
//            }
//            positionNodeToSend = definePreviousNode(list);
//        }
//        restTemplate = new RestTemplate();
//        ResponseEntity<Collection> responseEntity = restTemplate
//                .postForEntity(list.get(positionNodeToSend).getHost() + NAME_APPLICATION,
//                        getHttpEntity(collection, getHeaders(counter, flagRollback)), Collection.class);
//        checkStatusCode(responseEntity);
//    }

    public void sendDelete(String id, String counterStr) {
        List<Node> list = listGroups.get(defineIdGroup(id));
        incrementCounter(counterStr);
        if (counter == list.size()) {
            return;
        }
        int positionNextNode = defineNextNode(list);
        restTemplate = new RestTemplate();
        ResponseEntity<Collection> responseEntity = restTemplate
                .exchange(constructURI(list.get(positionNextNode).getHost(), id),
                        HttpMethod.DELETE, new HttpEntity<>(getHeaders(counter)), Collection.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return;
        }
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

    private HttpEntity getHttpEntity(Collection collection, HttpHeaders headers) {
        return new HttpEntity<>(collection, headers);
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

    private void incrementCounter(String counterStr) {
        counter = Integer.valueOf(counterStr);
        counter++;
    }

}
