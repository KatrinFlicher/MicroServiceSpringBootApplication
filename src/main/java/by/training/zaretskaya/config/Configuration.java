package by.training.zaretskaya.config;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {
    private static final Logger log = LogManager.getLogger(Configuration.class);
    private static Map<Integer, List<Node>> listNodes;
    private static Map<String, Node> nodesAll;
    private static Node currentNode;

    static {
        listNodes = new HashMap<>();
        nodesAll = new HashMap<>();
        try {
            JSONObject fileNodes = new JSONObject(new String
                    (Files.readAllBytes(Paths.get("src/main/resources/nodes.json"))));
            JSONArray groups = fileNodes.getJSONArray("groups");
            for (int i = 0; i < groups.length(); i++) {
                JSONObject group = groups.getJSONObject(i);
                Integer nameGroup = group.getInt("id");
                JSONArray listNodesInGroup = group.getJSONArray("list");
                List<Node> nodes = new ArrayList<>();
                for (int j = 0; j < listNodesInGroup.length(); j++) {
                    JSONObject nodeJson = listNodesInGroup.getJSONObject(j);
                    String nameNode = nodeJson.getString("name");
                    String hostNode = nodeJson.getString("host");
                    Node node = new Node(nameNode, hostNode, nameGroup);
                    nodes.add(node);
                    nodesAll.put(nameNode, node);
                }
                listNodes.put(nameGroup, nodes);
            }
        } catch (IOException e) {
            log.error("Problem with reading file with configuration nodes", e);
        }
    }

    public static void startUp(String idNode) {
        currentNode = nodesAll.get(idNode);
        System.setProperty("server.port", currentNode.getHost().replace("http://localhost:", ""));
        try {
            JSONObject dbConfig = new JSONObject(new String
                    (Files.readAllBytes(Paths.get("src/main/resources/dbconf.json"))));
            JSONObject nodeConfig = dbConfig.getJSONObject(idNode);
            System.setProperty("spring.datasource.url", nodeConfig.getString("url"));
            System.setProperty("spring.datasource.username", nodeConfig.getString("username"));
            System.setProperty("spring.datasource.password", nodeConfig.getString("password"));
        } catch (IOException e) {
            log.error("Problem with reading file with configuration Data base", e);
        }
    }

    public static Map<Integer, List<Node>> getAllGroups() {
        return listNodes;
    }

    public static Node getCurrentNode() {
        return currentNode;
    }
}
