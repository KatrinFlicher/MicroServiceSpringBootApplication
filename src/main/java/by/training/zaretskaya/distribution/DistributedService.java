package by.training.zaretskaya.distribution;

import by.training.zaretskaya.config.Configuration;
import by.training.zaretskaya.config.Node;
import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.*;
import by.training.zaretskaya.models.Collection;
import by.training.zaretskaya.models.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Component
public class DistributedService {
    private static final Logger log = LogManager.getLogger(DistributedService.class);
    private Map<Integer, List<Node>> listGroups;

    //    @Autowired
    private RestTemplate restTemplate;

    public DistributedService() {
        listGroups = Configuration.getAllGroups();
        restTemplate = new RestTemplate();
    }

    public boolean isMyGroup(String id) {
        log.info("Current " + Configuration.getCurrentNode().getName() + " defines group membership");
        return defineIdGroup(id) == Configuration.getCurrentNode().getIdGroup();
    }

    //GET send and redirect

    public Object sendGetObject(String nameClass, String... parameters) throws ClassNotFoundException {
        List<Node> list = new ArrayList<>(listGroups.get(Configuration.getCurrentNode().getIdGroup()));
        list.remove(Configuration.getCurrentNode());
        for (Node node : list) {
            try {
                log.info("Send GET request to " + node.getHost());
                ResponseEntity responseEntity = restTemplate
                        .exchange(getURI(node.getHost(), parameters), HttpMethod.GET,
                                new HttpEntity<>(getHeadersForReplica()), Class.forName(nameClass));
                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    log.info("Response is received from " + node.getHost());
                    return responseEntity.getBody();
                }
                if (responseEntity.getStatusCode().is4xxClientError()) {
                    throw new ResourceNotFoundException(responseEntity.getStatusCode().getReasonPhrase());
                }
            } catch (ResourceAccessException e) {
                log.error("Node " + node.getName() + " is unavailable.", e);
            } catch (HttpServerErrorException.ServiceUnavailable e) {
                throw new FailedOperationException();
            }
        }
        throw new FailedOperationException();
    }

    public Object redirectGet(String nameClass, String... ids) throws ClassNotFoundException {
        int idGroup = defineIdGroup(ids[Constants.POSITION_ID_COLLECTION]);
        log.info("Redirect GET request to " + idGroup + " group");
        List<Node> list = listGroups.get(idGroup);
        for (Node node : list) {
            try {
                ResponseEntity response = restTemplate.exchange
                        (getURI(node.getHost(), ids),
                                HttpMethod.GET,
                                new HttpEntity<>(getHeaders()), Class.forName(nameClass));
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Response is received from " + node.getHost());
                    return response.getBody();
                }
                if (response.getStatusCode().is4xxClientError()) {
                    throw new ResourceNotFoundException(response.getStatusCode().getReasonPhrase());
                }
            } catch (ResourceAccessException e) {
                log.error("Node " + node.getName() + " is unavailable.", e);
            } catch (HttpServerErrorException.ServiceUnavailable e) {
                throw new FailedOperationException();
            }
        }
        throw new FailedOperationException();
    }

    //POST send and redirect

    public void sendPostObject(Object object, int counter, boolean flagRollback, String idCollection) {
        List<Node> list = listGroups.get(defineIdGroup(idCollection));
        Node nodeToSend;
        if (!flagRollback) {
            counter++;
            if (counter == list.size()) {
                log.info("Objects are successfully created on all replicas");
                return;
            }
            nodeToSend = list.get(defineNextNode(list));
        } else {
            if (counter == 0) {
                log.info("Rollbacks are successfully made on all necessary replicas");
                throw new FailedOperationException();
            }
            counter--;
            nodeToSend = list.get(definePreviousNode(list));
        }
        try {
            log.info((flagRollback ? "Send rollback POST to " : "Send POST request to next node (")
                    + nodeToSend.getName() + ") in group " + nodeToSend.getIdGroup());
            restTemplate
                    .postForEntity(getURI(object, nodeToSend.getHost(), idCollection),
                            getHttpEntity(object, getHeaders(counter, flagRollback)), Object.class);
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            throw new FailedOperationException();
        } catch (ResourceAccessException e) {
            log.error("Node " + nodeToSend.getName() + " is unavailable.", e);
            throw new FailedOperationException();
        }
    }

    public ResponseEntity redirectPost(Object object, String idCollection) {
        Node nodeFirstInGroup = listGroups.get(defineIdGroup(idCollection)).get(0);
        try {
            log.info("Redirect POST request to node(" + nodeFirstInGroup.getName() + ") of "
                    + nodeFirstInGroup.getIdGroup() + " group");
            ResponseEntity<Object> responseEntity = restTemplate.postForEntity
                    (getURI(object, nodeFirstInGroup.getHost(), idCollection), getHttpEntity(object, getHeaders()),
                            Object.class);
            if (responseEntity.getStatusCode().is4xxClientError()) {
                checkTypeOfException(responseEntity.getStatusCode().getReasonPhrase());
            }
            return responseEntity;
        } catch (ResourceAccessException e) {
            log.error("Node " + nodeFirstInGroup.getName() + " is unavailable", e);
            throw new FailedOperationException();
        }
    }

    //UPDATE send and redirect

    public void sendUpdateObject(Object object, int counter, boolean flagRollback, String... parameters) {
        List<Node> list = listGroups.get(Configuration.getCurrentNode().getIdGroup());
        Node nodeToSend;
        if (!flagRollback) {
            counter++;
            if (counter == list.size()) {
                log.info("Changes are successfully made on all replicas");
                return;
            }
            nodeToSend = list.get(defineNextNode(list));
        } else {
            if (counter == 0) {
                log.info("Rollbacks are successfully made on all necessary replicas");
                return;
            }
            counter--;
            nodeToSend = list.get(definePreviousNode(list));
        }
        try {
            log.info((flagRollback ? "Send rollback PUT to " : "Send PUT request to next node (")
                    + nodeToSend.getName() + ") in group " + nodeToSend.getIdGroup());
//            restTemplate.exchange(getURI(nodeToSend.getHost(), parameters),
//                    HttpMethod.PUT, new HttpEntity<>(object, getHeaders(counter, flagRollback)), Object.class);
            restTemplate.put(getURI(nodeToSend.getHost(), parameters),
                    getHttpEntity(object, getHeaders(counter, flagRollback)));
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            throw new FailedOperationException();
        } catch (ResourceAccessException e) {
            log.error("Node " + nodeToSend.getName() + " is unavailable.", e);
            throw new FailedOperationException();
        }
    }

    public void redirectPut(Object object, String... ids) {
        Node nodeFirstInGroup = listGroups.get(defineIdGroup(ids[Constants.POSITION_ID_COLLECTION])).get(0);
        try {
            log.info("Redirect PUT request to node(" + nodeFirstInGroup.getName() + ") of "
                    + nodeFirstInGroup.getIdGroup() + " group");
            ResponseEntity<Object> responseEntity = restTemplate.exchange(getURI(nodeFirstInGroup.getHost(), ids),
                    HttpMethod.PUT, getHttpEntity(object, getHeaders()), Object.class);
            if (responseEntity.getStatusCode().is4xxClientError()) {
                checkTypeOfException(responseEntity.getStatusCode().getReasonPhrase());
            }
        } catch (ResourceAccessException e) {
            log.error("Node " + nodeFirstInGroup.getName() + " is unavailable.", e);
            throw new FailedOperationException();
        }
    }

    //DELETE send and redirect

    public void sendDeleteObject(int counter, boolean flagRollback, String... parameters) {
        List<Node> list = listGroups.get(Configuration.getCurrentNode().getIdGroup());
        Node nodeToSend;
        if (!flagRollback) {
            counter++;
            if (counter == list.size()) {
                log.info("Objects are successfully deleted on all replicas");
                return;
            }
            nodeToSend = list.get(defineNextNode(list));
        } else {
            if (counter == 0) {
                log.info("Rollbacks are successfully made on all necessary replicas");
                return;
            }
            counter--;
            nodeToSend = list.get(definePreviousNode(list));
        }
        try {
            log.info((flagRollback ? "Send rollback for CREATE request to " : "Send DELETE request to next node (")
                    + nodeToSend.getName() + ") in group " + nodeToSend.getIdGroup());
            restTemplate.exchange(getURI(nodeToSend.getHost(), parameters),
                    HttpMethod.DELETE, new HttpEntity<>(getHeaders(counter, flagRollback)), Object.class);
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            throw new FailedOperationException();
        } catch (ResourceAccessException e) {
            log.error("Node " + nodeToSend.getName() + " is unavailable.", e);
            throw new FailedOperationException();
        }

    }

    public void redirectDelete(String... ids) {
        Node nodeFirstInGroup = listGroups.get(defineIdGroup(ids[Constants.POSITION_ID_COLLECTION])).get(0);
        try {
            log.info("Redirect DELETE request to node(" + nodeFirstInGroup.getName() + ") of "
                    + nodeFirstInGroup.getIdGroup() + " group");
            ResponseEntity<Object> responseEntity = restTemplate.exchange(
                    getURI(nodeFirstInGroup.getHost(), ids),
                    HttpMethod.DELETE,
                    new HttpEntity<>(getHeaders()), Object.class);
            if (responseEntity.getStatusCode().is4xxClientError()) {
                checkTypeOfException(responseEntity.getStatusCode().getReasonPhrase());
            }
        } catch (ResourceAccessException e) {
            log.error("Node " + nodeFirstInGroup.getName() + " is unavailable.", e);
            throw new FailedOperationException();
        }
    }

    //LIST send and redirect

    public List<Collection> redirectListCollection(String objectToCompare, int size, List<Collection> collections) {
        Map<Integer, List<Node>> mapGroups = new HashMap<>(listGroups);
        mapGroups.remove(Configuration.getCurrentNode().getIdGroup());
        for (Map.Entry<Integer, List<Node>> group : mapGroups.entrySet()) {
            boolean groupIsNotAvailable = true;
            log.info("Send LIST request to group " + group.getKey());
            for (Node node : group.getValue()) {
                try {
                    log.info("Send LIST request to node " + node.getName());
                    List<Collection> body = restTemplate.exchange(
                            constructURI(node.getHost()),
                            HttpMethod.GET,
                            new HttpEntity<>(getHeadersForNotMainGroup()),
                            new ParameterizedTypeReference<List<Collection>>() {
                            },
                            getParamsForRequest(objectToCompare, size))
                            .getBody();
                    if (body != null) {
                        log.debug("List was received with " + body.size() + " size from " + node.getName());
                        collections.addAll(body);
                        groupIsNotAvailable = false;
                        break;
                    }
                } catch (ResourceAccessException e) {
                    log.error("Node " + node.getName() + " is unavailable.", e);
                } catch (HttpServerErrorException.ServiceUnavailable e) {
                    break;
                }
            }
            if (groupIsNotAvailable) {
                log.error("Group  " + group.getKey() + " is unavailable.");
                throw new FailedOperationException();
            }
        }
        collections.sort(Comparator.comparing(Collection::getName));
        log.info("Method LIST is successfully executed");
        return collections.subList(0, size);
    }

    public List<Object> sendListToReplica(String objectToCompare, int size, String... params) {
        List<Node> list = new ArrayList<>(listGroups.get(Configuration.getCurrentNode().getIdGroup()));
        list.remove(Configuration.getCurrentNode());
        boolean groupIsNotAvailable = true;
        List<Object> objects = null;
        for (Node node : list) {
            try {
                log.info("Send LIST request to replica " + node.getName() + " in group " + node.getIdGroup());
                objects = restTemplate.exchange(
                        params.length == 0 ? constructURI(node.getHost()) : constructURIForDocument(node.getHost(), params[0]),
                        HttpMethod.GET,
                        new HttpEntity<>(getHeadersForReplica()),
                        new ParameterizedTypeReference<List<Object>>() {
                        },
                        getParamsForRequest(objectToCompare, size))
                        .getBody();
                if (objects != null) {
                    groupIsNotAvailable = false;
                    break;
                }
            } catch (HttpServerErrorException.ServiceUnavailable e) {
                log.error("LIST request is failed in " + node.getName(), e);
            } catch (ResourceAccessException e) {
                log.error("Node " + node.getName() + " is unavailable.", e);
            }
        }
        if (groupIsNotAvailable) {
            log.error("Group  " + Configuration.getCurrentNode().getIdGroup() + " is unavailable.");
            throw new FailedOperationException();
        }
        return objects;
    }

    public List<Document> redirectListDocument(String idCollection, String objectToCompare, int size) {
        List<Node> list = listGroups.get(defineIdGroup(idCollection));
        for (Node node : list) {
            try {
                log.info("Redirect LIST request to " + node.getName() + " in group " + node.getIdGroup());
                ResponseEntity<List<Document>> responseEntity = restTemplate.exchange(
                        constructURIForDocument(node.getHost(), idCollection),
                        HttpMethod.GET,
                        new HttpEntity<>(getHeaders()),
                        new ParameterizedTypeReference<>() {
                        },
                        getParamsForRequest(objectToCompare, size));
                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    return responseEntity.getBody();
                }
            } catch (ResourceAccessException e) {
                log.error("Node " + node.getName() + " is unavailable.", e);
            } catch (HttpServerErrorException.ServiceUnavailable e) {
                log.error("LIST request is failed in " + node.getName(), e);
            }
        }
        throw new FailedOperationException();
    }


    private Map<String, String> getParamsForRequest(String objectToCompare, int size) {
        HashMap<String, String> map = new HashMap<>();
        map.put("compare", objectToCompare);
        map.put("size", String.valueOf(size));
        return map;
    }

    private int defineIdGroup(String id) {
        return Math.abs(id.hashCode()) % listGroups.size();
    }

    private String constructURI(String host) {
        return host + Constants.NAME_APPLICATION;
    }

    private String constructURI(String host, String id) {
        return constructURI(host) + id;
    }

    private String constructURIForDocument(String host, String idCollection) {
        return constructURI(host, idCollection) + Constants.DOCUMENTS;
    }

    private String constructURI(String host, String idCollection, String idDocument) {
        return constructURIForDocument(host, idCollection) + idDocument;
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

    private HttpHeaders getHeadersForReplica() {
        HttpHeaders headers = getHeaders();
        headers.add("replica", String.valueOf(Constants.REPLICA_ON));
        return headers;
    }

    private HttpHeaders getHeadersForNotMainGroup() {
        HttpHeaders headers = getHeaders();
        headers.add("main", String.valueOf(Constants.MAIN_GROUP_OFF));
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
            return constructURI(host, ids[Constants.POSITION_ID_COLLECTION],
                    ids[Constants.POSITION_ID_DOCUMENT]);
        }
        return constructURI(host, ids[Constants.POSITION_ID_COLLECTION]);
    }

    private String getURI(Object object, String host, String idCollection) {
        if (object instanceof Collection) {
            return constructURI(host);
        }
        return constructURIForDocument(host, idCollection);
    }

    private void checkTypeOfException(String message) {
        if (message.contains("with id ") && message.endsWith("is not found.")) {
            throw new ResourceNotFoundException(message);
        }
        if (message.contains("with id ") && message.endsWith("is already exist.")) {
            throw new ResourceIsExistException(message);
        }
        if (message.equals(Constants.COLLECTION_NAME_NOT_SUPPORTED)) {
            throw new CollectionNameNotSupportedException();
        }
        if (message.startsWith("Cache limit with") && message.endsWith("is impossible.") ||
                message.startsWith("Format") && message.endsWith("for cache algorithm is incompatible.")) {
            throw new CollectionWrongParameters(message);
        }
        if (message.equals(Constants.DOCUMENT_IS_INVALID_UNDER_THE_SCHEME)) {
            throw new DocumentIsInvalidUnderTheScheme();
        }
    }
}
