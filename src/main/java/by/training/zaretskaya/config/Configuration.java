package by.training.zaretskaya.config;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {
    static Map<Integer, List> listNodes;
    static Map<String, Node> nodesAll;
    static private Node currentNode;


    static {
        listNodes = new HashMap<>();
        nodesAll = new HashMap<>();
        JSONParser parser = new JSONParser();
        try {
            JSONObject fileNodes = (JSONObject) parser
                    .parse(new FileReader("src/main/resources/nodes.json"));
            JSONArray groups = (JSONArray) fileNodes.get("groups");
            for (int i = 0; i < groups.size(); i++) {
                JSONObject group = (JSONObject) groups.get(i);
                Integer nameGroup = Integer.valueOf((String) group.get("id"));
                JSONArray listNodes = (JSONArray) group.get("list");
                List<Node> nodes = new ArrayList<>();
                for (int j = 0; j < listNodes.size(); j++) {
                    JSONObject nodeJson = (JSONObject) listNodes.get(j);
                    String nameNode = (String) nodeJson.get("name");
                    String hostNode = (String) nodeJson.get("host");
                    Node node = new Node(nameNode, hostNode, nameGroup);
                    nodes.add(node);
                    nodesAll.put(nameNode, node);
                }
                Configuration.listNodes.put(nameGroup, nodes);
            }
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startUp(String idNode) {
        currentNode = nodesAll.get(idNode);
        System.setProperty("server.port", currentNode.getHost().replace("http://localhost:",""));
        JSONParser parser = new JSONParser();
        try {
            JSONObject dbConfig = (JSONObject) parser
                    .parse(new FileReader("src/main/resources/dbconf.json"));
            JSONObject nodeConfig = (JSONObject) dbConfig.get(idNode);
            System.setProperty("spring.datasource.url", (String) nodeConfig.get("url"));
            System.setProperty("spring.datasource.username", (String) nodeConfig.get("username"));
            System.setProperty("spring.datasource.password", (String) nodeConfig.get("password"));
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map getAllNodes() {
        return nodesAll;
    }

    public static Map getAllGroups() {
        return listNodes;
    }

    public static Node getCurrentNode(){
        return currentNode;
    }
}
