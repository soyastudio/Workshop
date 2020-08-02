package soya.framework.tools.iib;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Node {
    private transient String path;
    private transient int level;

    private NodeType type = NodeType.FOLDER;
    private String name;
    private Set<Node> children = new LinkedHashSet<>();
    private JsonObject value;

    private Node(String name, NodeType type, Node parent) {
        this.name = name;
        this.type = type;
        if (parent == null) {
            path = name;
        } else {
            path = parent.path + "/" + name;
            this.level = parent.level + 1;

        }
    }

    private Node(String path) {
        this.path = path;
        int slash = path.lastIndexOf('/');
        if (slash > 0) {
            this.name = path.substring(slash + 1);
        } else {
            name = path;
        }

        StringBuilder sb = new StringBuilder();
        String[] arr = path.split("/");
        level = arr.length - 1;
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(".");
            }

            if (i > 1) {
                sb.append("Abs:");
            }

            sb.append(arr[i]);
        }
    }

    private Node(String path, Object value) {
        this.path = path;
        this.value = (JsonObject) value;
        int slash = path.lastIndexOf('/');
        if (slash > 0) {
            this.name = path.substring(slash + 1);
            if (name.startsWith("@")) {
                name = name.substring(1);
                type = NodeType.ATTRIBUTE;
            } else {
                type = NodeType.FIELD;
            }
        } else {
            this.name = path;
            this.type = NodeType.FOLDER;
        }

        StringBuilder sb = new StringBuilder();
        String[] arr = path.split("/");
        level = arr.length - 1;
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(".");
            }

            if (i > 1) {
                sb.append("Abs:");
            }

            sb.append(arr[i]);
        }
    }

    public String getPath() {
        return path;
    }

    public int getLevel() {
        return level;
    }

    public NodeType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return level > 1 ? "Abs:" + name : name;
    }

    public Set<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        this.children.add(child);
        this.type = NodeType.FOLDER;
    }

    public JsonObject getValue() {
        return value;
    }

    public String getParent() {
        int slash = path.lastIndexOf('/');
        if (slash > 0) {
            return path.substring(0, slash);
        }

        return null;
    }

    public static boolean isEmpty(Node node) {
        if (!NodeType.FOLDER.equals(node.getType())) {
            return node.getValue() == null || node.getValue().get("value") == null || node.getValue().get("value").getAsString().trim().length() == 0;

        } else if (node.getChildren() == null || node.getChildren().isEmpty()) {
            return true;

        } else {
            for (Node n : node.getChildren()) {
                if (!isEmpty(n)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isSubRoot(Node node) {
        return NodeType.FOLDER.equals(node.getType())
                && node.getValue() != null
                && node.getValue().get("value") != null
                && node.getValue().get("value").getAsString().trim().length() > 0;
    }

    public static Node createNode(String path) {
        return new Node(path);
    }

    public static Node createNode(String path, Object value) {
        return new Node(path, value);
    }

    public static Node fromJson(String json) {
        JsonElement jsonElement = JsonParser.parseString(json);
        if (!jsonElement.isJsonObject()) {
            throw new IllegalArgumentException("Json Object is expected.");
        }

        return fromJsonObject(jsonElement.getAsJsonObject(), null);
    }

    public static Node fromYaml(String yaml) {
        Node node = null;
        Map<String, Object> map = new Yaml().load(yaml);
        if (!map.isEmpty()) {
            for (Map.Entry<String, Object> e : map.entrySet()) {
                node = new Node(e.getKey(), NodeType.FOLDER, null);
                buildFromMap((Map<String, Object>) e.getValue(), node);
            }
        }

        return node;
    }

    private static void buildFromMap(Map<String, Object> map, Node node) {
        map.entrySet().forEach(e -> {
            String key = e.getKey();
            if (e.getValue() != null && e.getValue() instanceof Map) {
                Map<String, Object> value = (Map<String, Object>) e.getValue();
                if (value.containsKey("_mapping")) {
                    Node sub = new Node(key, NodeType.FIELD, node);
                    Map<String, Object> mapping = (Map<String, Object>) value.get("_mapping");
                    JsonObject jsonObject = new JsonObject();
                    mapping.forEach((k, v) -> {
                        jsonObject.addProperty(k, (String) v);
                    });
                    sub.value = jsonObject;
                    node.addChild(sub);

                } else {
                    String nodeName = key;
                    String nodeValue = null;
                    if(nodeName.indexOf('@') > 0) {
                        int index = nodeName.indexOf('@');
                        nodeValue = nodeName.substring(index + 1);
                        nodeName = nodeName.substring(0, index);
                    }

                    Node sub = new Node(nodeName, NodeType.FOLDER, node);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("path", sub.getPath());
                    if(nodeValue != null) {
                        jsonObject.addProperty("value", nodeValue);
                    }
                    sub.value = jsonObject;
                    node.addChild(sub);
                    buildFromMap(value, sub);
                }

            }
        });
    }

    private static Node fromJsonObject(JsonObject jsonObject, Node parent) {
        String name = jsonObject.get("Name").getAsString();
        NodeType type = NodeType.valueOf(jsonObject.get("Type").getAsString());

        Node node = new Node(name, type, parent);

        if (jsonObject.get("Children") != null) {
            JsonArray array = jsonObject.get("Children").getAsJsonArray();
            array.forEach(e -> {
                JsonObject o = e.getAsJsonObject();
                Node child = fromJsonObject(o, node);
                node.addChild(child);
            });
        }

        if (jsonObject.get("Value") != null && jsonObject.get("Value").isJsonObject()) {
            JsonObject value = jsonObject.get("Value").getAsJsonObject();
            node.value = value;
        }

        return node;
    }
}