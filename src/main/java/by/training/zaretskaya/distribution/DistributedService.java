package by.training.zaretskaya.distribution;

import by.training.zaretskaya.config.Configuration;
import by.training.zaretskaya.config.Node;
import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.FailedOperationException;
import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.models.Collection;
import by.training.zaretskaya.models.Document;
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
    private Map<Integer, List<Node>> listGroups;
    private RestTemplate restTemplate;

    public DistributedService() {
        listGroups = Configuration.getAllGroups();
        restTemplate = new RestTemplate();
    }

    public boolean isMyGroup(String id) {
        return defineIdGroup(id).equals(Configuration.getCurrentNode().getIdGroup());
    }

    //GET send and redirect

    public Object sendGetObject(String... parameters) {
        List<Node> list = new ArrayList<>(listGroups.get(Configuration.getCurrentNode().getIdGroup()));
        list.remove(Configuration.getCurrentNode());
        for (Node node : list) {
            try {
                ResponseEntity<Object> responseEntity = restTemplate
                        .exchange(getURI(node.getHost(), parameters), HttpMethod.GET,
                                new HttpEntity<>(getHeaders(Constants.REPLICA_ON)), Object.class);
                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    return responseEntity.getBody();
                }
            } catch (ResourceAccessException | HttpServerErrorException.ServiceUnavailable e) {
            }
        }
        if (parameters.length == 2) {
            throw new ResourceNotFoundException(Constants.RESOURCE_DOCUMENT,
                    parameters[Constants.POSITION_ID_DOCUMENT]);
        } else {
            throw new ResourceNotFoundException(Constants.RESOURCE_COLLECTION,
                    parameters[Constants.POSITION_ID_COLLECTION]);
        }
    }

    public Object redirectGet(String... ids) {
        List<Node> list = listGroups.get(defineIdGroup(ids[Constants.POSITION_ID_COLLECTION]));
        for (Node node : list) {
            try {
                return restTemplate.exchange(getURI(node.getHost(), ids), HttpMethod.GET,
                        new HttpEntity<>(getHeaders()), Object.class);
            } catch (ResourceAccessException e) {
            }
        }
        throw new FailedOperationException();
    }

    //POST send and redirect

    public void sendPostObject(Object object, int counter, boolean flagRollback, String idCollection) {
        List<Node> list = listGroups.get(defineIdGroup(idCollection));
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
        try {
            restTemplate
                    .postForEntity(getURI(object, list.get(positionNodeToSend).getHost(), idCollection),
                            getHttpEntity(object, getHeaders(counter, flagRollback)), Object.class);
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            throw new FailedOperationException();
        }
    }

    public ResponseEntity redirectPost(Object object, String idCollection) {
        List<Node> list = listGroups.get(defineIdGroup(idCollection));
        try {
            return restTemplate.postForEntity(getURI(object, list.get(0).getHost(), idCollection),
                    getHttpEntity(object, getHeaders()), Object.class);
        } catch (ResourceAccessException e) {
            throw new FailedOperationException();
        }
    }

    //UPDATE send and redirect

    public void sendUpdateObject(Object object, int counter, boolean flagRollback, String... parameters) {
        List<Node> list = listGroups.get(Configuration.getCurrentNode().getIdGroup());
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
        try {
            restTemplate.put(getURI(list.get(positionNodeToSend).getHost(), parameters),
                    getHttpEntity(object, getHeaders(counter, flagRollback)));
//            restTemplate.exchange(getURI(list.get(positionNodeToSend).getHost(), parameters), HttpMethod.PUT,
//                    ), Object.class);
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            throw new FailedOperationException();
        }
    }

    public void redirectPut(Object object, String... ids) {
        List<Node> list = listGroups.get(defineIdGroup(ids[Constants.POSITION_ID_COLLECTION]));
        try {
            restTemplate.put(getURI(list.get(0).getHost(), ids), getHttpEntity(object, getHeaders()));
//            restTemplate.exchange(getURI(list.get(0).getHost(), ids), HttpMethod.PUT,
//                    getHttpEntity(object, getHeaders()), Object.class);
        } catch (ResourceAccessException e) {
            throw new FailedOperationException();
        }
    }

    //DELETE send and redirect

    public void sendDeleteObject(int counter, boolean flagRollback, String... parameters) {
        List<Node> list = listGroups.get(Configuration.getCurrentNode().getIdGroup());
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
        try {
            restTemplate.exchange(getURI(list.get(positionNodeToSend).getHost(), parameters),
                    HttpMethod.DELETE, new HttpEntity<>(getHeaders(counter, flagRollback)), Object.class);
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            throw new FailedOperationException();
        }
    }

    public Object redirectDelete(String... ids) {
        try {
            return restTemplate.exchange(
                    getURI(listGroups.get(defineIdGroup(ids[Constants.POSITION_ID_COLLECTION])).get(0).getHost(), ids),
                    HttpMethod.DELETE,
                    new HttpEntity<>(getHeaders()), Object.class);
        } catch (ResourceAccessException e) {
            throw new FailedOperationException();
        }
    }

    //LIST send and redirect

    public List<Collection> redirectListCollection(String objectToCompare, int size, List<Collection> collections) {
        Map<Integer, List<Node>> mapGroups = new HashMap<>(listGroups);
        mapGroups.remove(Configuration.getCurrentNode().getIdGroup());
        for (Map.Entry<Integer, List<Node>> entry : mapGroups.entrySet()) {
            boolean groupIsNotAvailable = true;
            for (Node node : entry.getValue()) {
                try {
                    List<Collection> body = restTemplate.exchange(
                            constructURI(node.getHost()),
                            HttpMethod.GET,
                            new HttpEntity<>(getHeadersForNotMainGroup(Constants.MAIN_GROUP_OFF)),
                            new ParameterizedTypeReference<List<Collection>>() {
                            },
                            getParamsForRequest(objectToCompare, size))
                            .getBody();
                    if (body != null) {
                        collections.addAll(body);
                        groupIsNotAvailable = false;
                        break;
                    }
                } catch (ResourceAccessException e) {
                }
            }
            if (groupIsNotAvailable) {
                throw new FailedOperationException();
            }
        }
        collections.sort(Comparator.comparing(Collection::getName));
        return collections.subList(0, size);
    }

    public List<Object> sendListToReplica(String objectToCompare, int size, String... params) {
        List<Node> list = new ArrayList<>(listGroups.get(Configuration.getCurrentNode().getIdGroup()));
        list.remove(Configuration.getCurrentNode());
        boolean groupIsNotAvailable = true;
        List<Object> objects = null;
        for (Node node : list) {
            try {
                objects = restTemplate.exchange(
                        params.length==0?constructURI(node.getHost()): constructURIForDocument(node.getHost(), params[0]),
                        HttpMethod.GET,
                        new HttpEntity<>(getHeaders(Constants.REPLICA_ON)),
                        new ParameterizedTypeReference<List<Object>>() {
                        },
                        getParamsForRequest(objectToCompare, size))
                        .getBody();
                if (objects != null) {
                    groupIsNotAvailable = false;
                    break;
                }
            } catch (ResourceAccessException | HttpServerErrorException.ServiceUnavailable e) {
            }
        }
        if (groupIsNotAvailable) {
            throw new FailedOperationException();
        }
        return objects;
    }

    public List<Document> redirectListDocument(String idCollection, String objectToCompare, int size) {
        List<Node> list = listGroups.get(defineIdGroup(idCollection));
        for (Node node : list) {
            try {
                return restTemplate.exchange(
                        constructURIForDocument(node.getHost(), idCollection),
                        HttpMethod.GET,
                        new HttpEntity<>(getHeaders()),
                        new ParameterizedTypeReference<List<Document>>() {
                        },
                        getParamsForRequest(objectToCompare, size))
                        .getBody();
            } catch (ResourceAccessException e) {
            }
        }
        throw new FailedOperationException();
    }


    private Map<String, String> getParamsForRequest(String objectToCompare, int size) {
        return Map.of("compare", objectToCompare, "size", String.valueOf(size));
    }


    private Integer defineIdGroup(String id) {
        return id.hashCode() % listGroups.size();
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

    private HttpHeaders getHeaders(boolean replica) {
        HttpHeaders headers = getHeaders();
        headers.add("replica", String.valueOf(replica));
        return headers;
    }

    private HttpHeaders getHeaders(int counter, boolean rollback) {
        HttpHeaders headers = getHeaders(counter);
        headers.add("rollback", String.valueOf(rollback));
        return headers;
    }

    private HttpHeaders getHeadersForNotMainGroup(boolean mainGroup) {
        HttpHeaders headers = getHeaders();
        headers.add("main", String.valueOf(mainGroup));
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
}
