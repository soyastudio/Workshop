package com.abs.edis.commons;

import com.google.gson.*;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedHashMap;
import java.util.Map;

public class XmlToJsonConverter {

    private Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Map<String, String> mappings;

    private Map<String, JsonArray> arrayMap = new LinkedHashMap<>();

    public XmlToJsonConverter(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    public String convert(Node node) {
        arrayMap.clear();

        return GSON.toJson(estimate(node));
    }

    public JsonElement estimate(Node node) {
        String path = path(node);
        if (!mappings.containsKey(path)) {
            if (node.getTextContent() != null) {
                return new JsonPrimitive(node.getTextContent());

            } else if (node.getChildNodes().getLength() > 0) {
                JsonObject obj = new JsonObject();
                NamedNodeMap attributes = node.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node attr = attributes.item(i);
                    obj.add(attr.getLocalName(), estimate(attr));
                }

                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node child = list.item(i);
                    obj.add(child.getLocalName(), estimate(child));
                }

                return obj;

            } else {
                return null;
            }

        } else {
            String type = mappings.get(path);
            if (type.toLowerCase().contains("array")) {
                JsonArray array = arrayMap.get(path);
                if (array == null) {
                    array = new JsonArray();
                    arrayMap.put(path, array);
                }

                if ("booleanArray".equals(type)) {
                    array.add(new JsonPrimitive(Boolean.parseBoolean(node.getTextContent())));

                } else if ("shortArray".equals(type)) {
                    array.add(new JsonPrimitive(Short.parseShort(node.getTextContent())));

                } else if ("integerArray".equals(type)) {
                    array.add(new JsonPrimitive(Integer.parseInt(node.getTextContent())));

                } else if ("longArray".equals(type)) {
                    array.add(new JsonPrimitive(Long.parseLong(node.getTextContent())));

                } else if ("floatArray".equals(type)) {
                    array.add(new JsonPrimitive(Float.parseFloat(node.getTextContent())));

                } else if ("doubleArray".equals(type)) {
                    array.add(new JsonPrimitive(Double.parseDouble(node.getTextContent())));

                } else if ("stringArray".equals(type)) {
                    String content = node.getTextContent() == null ? "" : node.getTextContent();
                    array.add(new JsonPrimitive(content));

                } else if ("array".equals(type)) {
                    JsonObject obj = new JsonObject();
                    NamedNodeMap attributes = node.getAttributes();
                    for (int i = 0; i < attributes.getLength(); i++) {
                        Node attr = attributes.item(i);
                        obj.add(attr.getLocalName(), estimate(attr));
                    }

                    NodeList list = node.getChildNodes();
                    for (int i = 0; i < list.getLength(); i++) {
                        Node child = list.item(i);
                        obj.add(child.getLocalName(), estimate(child));
                    }

                    array.add(obj);
                }

                return array;

            } else if (node.getTextContent() != null) {
                if ("boolean".equals(type)) {
                    return new JsonPrimitive(Boolean.parseBoolean(node.getTextContent()));

                } else if ("short".equals(type)) {
                    return new JsonPrimitive(Short.parseShort(node.getTextContent()));

                } else if ("integer".equals(type)) {
                    return new JsonPrimitive(Integer.parseInt(node.getTextContent()));

                } else if ("long".equals(type)) {
                    return new JsonPrimitive(Long.parseLong(node.getTextContent()));

                } else if ("float".equals(type)) {
                    return new JsonPrimitive(Float.parseFloat(node.getTextContent()));

                } else if ("double".equals(type)) {
                    return new JsonPrimitive(Double.parseDouble(node.getTextContent()));

                } else {
                    return new JsonPrimitive(node.getTextContent());
                }

            } else if (node.getChildNodes().getLength() > 0) {
                JsonObject obj = new JsonObject();
                NamedNodeMap attributes = node.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node attr = attributes.item(i);
                    obj.add(attr.getLocalName(), estimate(attr));
                }

                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node child = list.item(i);
                    obj.add(child.getLocalName(), estimate(child));
                }

                return obj;
            }

            return null;
        }
    }

    private String path(Node node) {
        String path = node.getLocalName();
        Node parent = node.getParentNode();
        while (parent != null && !"XMLNSC".equals(parent.getLocalName())) {
            path = parent.getLocalName() + "/" + path;
            parent = parent.getParentNode();

        }

        return path;
    }
}