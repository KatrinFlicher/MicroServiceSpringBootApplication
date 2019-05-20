package by.training.zaretskaya.config;

import by.training.zaretskaya.constants.ConfigurationConstants;
import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.impl.LFUCacheImpl;
import by.training.zaretskaya.interfaces.ICache;
import by.training.zaretskaya.models.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class AppConfig {
    private static final Logger log = LogManager.getLogger(AppConfig.class);
    private Map<String, Node> nodesAll;

    @Value("#{systemProperties['dirNode']?:'nodes.json'}")
    private String directoryNodes;

    @Value("#{systemProperties['dirDB']?:'dbconf.json'}")
    private String directoryDBconf;

    @Value("#{systemProperties['name']}")
    private String idCurrentNode;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);
        return new RestTemplate(factory);
    }

    @Bean
    public List<List<Node>> listNodes() {
        List<List<Node>> listNodes = new ArrayList<>();
        nodesAll = new HashMap<>();
        if (directoryNodes.equals("nodes.json")) {
            directoryNodes = new File(directoryNodes).getAbsolutePath();
        }
        try {
            JSONArray listGroupsNodes = new JSONArray(new String(new FileSystemResource(directoryNodes)
                    .getInputStream().readAllBytes()));
            for (int i = 0; i < listGroupsNodes.length(); i++) {
                JSONArray group = listGroupsNodes.getJSONArray(i);
                List<Node> nodes = new ArrayList<>();
                for (int j = 0; j < group.length(); j++) {
                    JSONObject nodeJson = group.getJSONObject(j);
                    Node node = new Node(nodeJson.getString(ConfigurationConstants.NODE_NAME),
                            nodeJson.getString(ConfigurationConstants.NODE_HOST), i);
                    nodes.add(node);
                    nodesAll.put(node.getName(), node);
                }
                listNodes.add(nodes);
            }
        } catch (Exception e) {
            log.error("Problem with reading file with configuration nodes", e);
            System.exit(1);
        }
        return listNodes;
    }

    @Bean
    public Node currentNode(List<List<Node>> list) {
        if (idCurrentNode == null) {
            log.error("Problem with configuration Node name");
            System.exit(1);
        }
        Node node = nodesAll.get(idCurrentNode);
        list.get(node.getIdGroup()).remove(node);
        return node;
    }

    @Bean
    @Qualifier("nodes")
    public Map<String, Node> nodesAll(Node node) {
        nodesAll.remove(node.getName());
        return nodesAll;
    }

    @Bean
    @DependsOn("currentNode")
    public DataSource dataSource() {
        if (directoryDBconf.equals("dbconf.json")) {
            directoryDBconf = new File(directoryDBconf).getAbsolutePath();
        }
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        try {
            JSONObject dbConfig = new JSONObject(new String(new FileSystemResource(directoryDBconf)
                    .getInputStream().readAllBytes()));
            JSONObject nodeConfig = dbConfig.getJSONObject(idCurrentNode);
            dataSourceBuilder.driverClassName(ConfigurationConstants.DATA_SOURCE_DRIVER_POSTGRESQL);
            dataSourceBuilder.url(nodeConfig.getString(ConfigurationConstants.DATA_SOURCE_URL));
            dataSourceBuilder.username(nodeConfig.getString(ConfigurationConstants.DATA_SOURCE_USERNAME));
            dataSourceBuilder.password(nodeConfig.getString(ConfigurationConstants.DATA_SOURCE_PASSWORD));
            dataSourceBuilder.type(ConfigurationConstants.DATA_SOURCE_TYPE);
        } catch (Exception e) {
            log.error("Problem with reading file with configuration Data base", e);
            System.exit(1);
        }
        return dataSourceBuilder.build();
    }

    @Bean
    public ICache<String, Collection> iCache(){
        return new LFUCacheImpl(Constants.MAX_SIZE_FOR_CACHE_COLLECTIONS);
    }
}

