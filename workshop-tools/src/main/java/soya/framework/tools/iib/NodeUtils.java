package soya.framework.tools.iib;

import soya.framework.tools.poi.XlsxUtils;
import soya.framework.tools.util.StringBuilderUtils;
import com.google.gson.*;

import java.io.File;
import java.util.*;

public class NodeUtils {
    private static Gson gson;

    private NodeUtils() {
    }

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    public static JsonObject fromPropertiesToProcessTree(Properties properties) throws Exception {
        Map<String, Node> map = new LinkedHashMap<>();

        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String xpath = (String) enumeration.nextElement();
            String value = properties.getProperty(xpath);

            Node node = Node.createNode(xpath, value);
            map.put(xpath, node);

            while (node.getParent() != null) {
                String parent = node.getParent();
                if (map.containsKey(parent)) {
                    map.get(parent).getChildren().add(node);
                    break;
                } else {
                    Node pn = Node.createNode(parent);
                    pn.getChildren().add(node);
                    map.put(parent, pn);

                    node = pn;
                }
            }
        }

        Node root = null;
        for (String key : map.keySet()) {
            if (!key.contains("/")) {
                root = map.get(key);
                break;
            }
        }

        return gson.toJsonTree(root).getAsJsonObject();
    }

    public static Node fromProperties(Properties properties) {
        Map<String, Node> map = new LinkedHashMap<>();

        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String xpath = (String) enumeration.nextElement();
            String value = properties.getProperty(xpath);

            Node node = Node.createNode(xpath, value);
            map.put(xpath, node);

            while (node.getParent() != null) {
                String parent = node.getParent();
                if (map.containsKey(parent)) {
                    map.get(parent).getChildren().add(node);
                    break;
                } else {
                    Node pn = Node.createNode(parent);
                    pn.getChildren().add(node);
                    map.put(parent, pn);

                    node = pn;
                }
            }
        }

        Node root = null;
        for (String key : map.keySet()) {
            if (!key.contains("/")) {
                root = map.get(key);
                break;
            }
        }

        return root;
    }

    public static Node fromExcelToProcessTree(File file, String worksheet, String pathColumn) throws Exception {
        return arrayToTree(XlsxUtils.fromWorksheet(file, worksheet), pathColumn);
    }

    public static Node arrayToTree(JsonArray array, String pathProp) {
        Map<String, Node> map = new LinkedHashMap<>();
        array.forEach(e -> {
            if (e.isJsonObject()) {
                JsonObject object = e.getAsJsonObject();
                if (object.get(pathProp) != null) {
                    String xpath = object.get(pathProp).getAsString().trim();
                    if(xpath != null && !xpath.isEmpty()) {
                        Node node = Node.createNode(xpath, object);
                        map.put(xpath, node);

                        while (node.getParent() != null) {
                            String parent = node.getParent();
                            if (map.containsKey(parent)) {
                                map.get(parent).addChild(node);
                                break;

                            } else {
                                Node pn = Node.createNode(parent);
                                pn.addChild(node);
                                map.put(parent, pn);

                                node = pn;
                            }
                        }
                    }
                }
            }
        });

        Node root = null;
        for (String key : map.keySet()) {
            if (!key.contains("/")) {
                root = map.get(key);
                break;
            }
        }

        return root;
    }

    public static JsonObject reverseInputTree(Properties properties) {
        JsonObject root = new JsonObject();

        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String xpath = (String) enumeration.nextElement();
            String value = properties.getProperty(xpath);

            if (value != null && value.trim().length() > 0) {
                System.out.println("---------------------- " + value);
            }
        }

        return root;
    }

    public static String reverseInputSchema(Properties properties) {
        Set<String> set = new LinkedHashSet<>();
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String xpath = (String) enumeration.nextElement();
            String value = properties.getProperty(xpath);

            if (value != null && value.trim().length() > 0) {
                String col = fromExp(value);
                if (col != null && col.length() > 0) {
                    set.add(col);
                }
            }
        }

        return printRowsetXsd(set);
    }

    public static String reverseInputSchema(File file, String worksheet) throws Exception {
        Set<String> set = new LinkedHashSet<>();
        JsonArray array = XlsxUtils.fromWorksheet(file, worksheet);

        array.forEach(e -> {
            JsonElement value = e.getAsJsonObject().get("VALUE");
            if(value != null && value.isJsonPrimitive() && value.getAsString().trim().length() > 0) {
                set.add(value.getAsString());
            }
        });

        return printRowsetXsd(set);
    }

    private static String printRowsetXsd(Set<String> set) {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n");
        builder.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">").append("\n");

        StringBuilderUtils.println("<xs:element name=\"ROWSET\">", builder, 1);
        StringBuilderUtils.println("<xs:complexType>", builder, 2);
        StringBuilderUtils.println("<xs:sequence minOccurs=\"0\" maxOccurs=\"unbounded\">", builder, 3);
        StringBuilderUtils.println("<xs:element name=\"ROW\">", builder, 4);
        StringBuilderUtils.println("<xs:complexType>", builder, 5);
        StringBuilderUtils.println("<xs:sequence>", builder, 6);

        set.forEach(e -> {
            String element = "<xs:element name=\"" + e + "\" type=\"xs:string\"/>";
            StringBuilderUtils.println(element, builder, 7);
        });

        StringBuilderUtils.println("</xs:sequence>", builder, 6);
        StringBuilderUtils.println("</xs:complexType>", builder, 5);
        StringBuilderUtils.println("</xs:element>", builder, 4);
        StringBuilderUtils.println("</xs:sequence>", builder, 3);
        StringBuilderUtils.println("</xs:complexType>", builder, 2);
        StringBuilderUtils.println("</xs:element>", builder, 1);

        builder.append("</xs:schema>");
        return builder.toString();
    }

    private static String fromExp(String exp) {
        String token = exp.trim();
        if (token.startsWith("path(")) {
            int end = token.indexOf(")");
            String value = token.substring("path(".length(), end);
            if (value.startsWith("$.")) {
                value = value.substring(2);
                return value;
            }
        }
        return null;
    }

    public static JsonObject reverseInputTree(JsonArray array) {
        JsonObject root = new JsonObject();

        array.forEach(e -> {
            JsonObject o = e.getAsJsonObject();
            if (o.get("VALUE") != null && o.get("VALUE").getAsString().trim().length() > 0) {
                String path = o.get("VALUE").getAsString();
                System.out.println("---------------------- " + path);
            }
        });

        return root;
    }

    public static String toYaml(Node node) {
        StringBuilder builder = new StringBuilder();
        printYaml(node, builder);
        return builder.toString();
    }

    private static void printYaml(Node node, StringBuilder builder) {
        if (NodeType.FOLDER.equals(node.getType())) {
            if(node.getValue() != null && node.getValue().get("value") != null && node.getValue().get("value").getAsString().trim().length() > 0) {
                String func = node.getValue().get("value").getAsString().trim();
                StringBuilderUtils.println(node.getName() + "@" + func + ":", builder, node.getLevel());
            } else {
                StringBuilderUtils.println(node.getName() + ":", builder, node.getLevel());
            }

            node.getChildren().forEach(e -> {
                printYaml(e, builder);
            });
        } else if (NodeType.FIELD.equals(node.getType())) {
            StringBuilderUtils.println(node.getName() + ":", builder, node.getLevel());

            StringBuilderUtils.println("_mapping:", builder, node.getLevel() + 1);
            JsonObject jsonObject = node.getValue();
            jsonObject.entrySet().forEach(e -> {
                if (e.getValue() != null && e.getValue().getAsString().trim().length() > 0) {
                    StringBuilderUtils.println(e.getKey() + ": " + e.getValue().getAsString(), builder, node.getLevel() + 2);
                }
            });
        }
    }
}
