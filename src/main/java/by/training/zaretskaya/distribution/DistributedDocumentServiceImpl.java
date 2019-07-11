package by.training.zaretskaya.distribution;

import by.training.zaretskaya.config.Node;
import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.BadRequestException;
import by.training.zaretskaya.exception.FailedOperationException;
import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.services.DistributedDocumentService;
import by.training.zaretskaya.services.IDocumentService;
import by.training.zaretskaya.models.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;

@Service
public class DistributedDocumentServiceImpl extends DistributedServiceBase<Document>
        implements DistributedDocumentService<Document> {

    private static final Logger log = LogManager.getLogger(DistributedDocumentServiceImpl.class);

    private IDocumentService<Document> documentService;
    private List<List<Node>> listGroups;
    private Node node;

    @Autowired
    public DistributedDocumentServiceImpl(List<List<Node>> listGroups,
                                          @Qualifier("DocumentService") IDocumentService<Document> documentService,
                                          Node node) {
        super(Document.class);
        System.out.println("FROM DOCDIST" + listGroups);
        this.documentService = documentService;
        this.listGroups = listGroups;
        this.node = node;
    }

    @Override
    public void create(String nameCollection, Document document) {
        try {
            callNodes(listGroups.get(defineIdGroup(document.getKey())), HttpMethod.POST, null, document,
                    nameCollection);
        } catch (FailedOperationException e) {
            documentService.delete(nameCollection, document.getKey());
            throw e;
        }
    }

    @Override
    public Document get(String nameCollection, String nameResource) {
        return get(listGroups.get(defineIdGroup(nameResource)), nameCollection, nameResource).getBody();
    }

    @Override
    public void delete(String nameCollection, String nameResource) {
        Document documentOldValue = new Document(documentService.get(nameCollection, nameResource));
        try {
            callNodes(listGroups.get(defineIdGroup(nameResource)), HttpMethod.DELETE, documentOldValue, null,
                    nameCollection, nameResource);
        } catch (FailedOperationException e) {
            documentService.create(nameCollection, documentOldValue);
            throw new FailedOperationException();
        }
    }

    @Override
    public void update(String nameCollection, String nameResource, Document document) {
        Document documentOldValue = new Document(documentService.get(nameCollection, nameResource));
        try {
            callNodes(listGroups.get(defineIdGroup(nameResource)), HttpMethod.PUT, documentOldValue, document,
                    nameCollection, nameResource);
        } catch (FailedOperationException e) {
            documentService.update(nameCollection, nameResource, document);
            throw new FailedOperationException();
        }
    }

    @Override
    public List<Document> list(String nameCollection, String objectToCompare, int size) {
        List<List<Node>> list = new ArrayList<>(listGroups);
        list.remove(node.getIdGroup().intValue());
        List<Document> documents = new ArrayList<>();
        for (List<Node> group : list) {
            boolean groupIsNotAvailable = true;
            for (Node node : group) {
                try {
                    List<Document> body = new ListRestCommand<>(HttpMethod.GET, Document.class,
                            node.getHost(), objectToCompare, size, nameCollection)
                            .executeList().getBody();
                    if (body != null) {
                        log.debug("List was received with " + body.size() + " size from " + node.getName());
                        documents.addAll(body);
                        groupIsNotAvailable = false;
                        break;
                    }
                } catch (ResourceAccessException | HttpServerErrorException.ServiceUnavailable e) {
                    log.error("Node " + node.getName() + " is unavailable.", e);
                } catch (HttpClientErrorException e) {
                    log.error("ClientError is received from " + node.getName(), e);
                    throw new ResourceNotFoundException(new JSONObject(e.getResponseBodyAsString())
                            .getString("message"));
                }
            }
            if (groupIsNotAvailable) {
                log.error("Group  " + group.get(0).getIdGroup() + " is unavailable.");
                throw new FailedOperationException();
            }
        }
        return documents;
    }

    @Override
    public boolean isMyGroup(String id) {
        return defineIdGroup(id) == node.getIdGroup();
    }

    @Override
    public ResponseEntity redirectQuery(HttpMethod method, Document entity, String... ids) {
        List<Node> list = listGroups.get(defineIdGroup(ids[Constants.POSITION_ID_DOCUMENT]));
        try {
            if (method == HttpMethod.GET) {
                return get(list, ids);
            }
            RestCommand<Document> command = new RestCommand<>(method, Document.class, list.get(0).getHost(), entity, ids);
            command.turnOffFlagReplica();
            return command.execute();
        } catch (ResourceAccessException | HttpServerErrorException.ServiceUnavailable e) {
            log.error("Node " + list.get(0).getName() + " is unavailable.", e);
            throw new FailedOperationException();
        } catch (HttpClientErrorException e) {
            log.error("ClientError is received from " + list.get(0).getName(), e);
            JSONObject json = new JSONObject(e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException(json.getString("message"));
            }
            throw new BadRequestException(json.getString("message"));
        }
    }

    @Override
    public List<Document> listFromReplica(String nameCollection, String objectToCompare, int size) {
        for (Node node : listGroups.get(node.getIdGroup())) {
            try {
                ResponseEntity<List<Document>> entity = new ListRestCommand<>(HttpMethod.GET, Document.class,
                        node.getHost(), objectToCompare, size, nameCollection)
                        .executeList();
                log.info("Response is received from " + node.getHost());
                return entity.getBody();
            } catch (ResourceAccessException | HttpServerErrorException.ServiceUnavailable e) {
                log.error("Node " + node.getName() + " is unavailable.", e);
            } catch (HttpClientErrorException e) {
                log.error("ClientError is received from " + node.getName(), e);
                throw new ResourceNotFoundException(new JSONObject(e.getResponseBodyAsString())
                        .getString("message"));
            }
        }
        log.error("Operation \"List documents\" was failed");
        throw new FailedOperationException();
    }

    private int defineIdGroup(String id) {
        return Math.abs(id.hashCode()) % listGroups.size();
    }
}
