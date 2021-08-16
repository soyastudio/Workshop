package com.abs.edis.commons;

import com.google.gson.*;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedHashMap;
import java.util.Locale;
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
                if (attributes != null) {
                    for (int i = 0; i < attributes.getLength(); i++) {
                        Node attr = attributes.item(i);
                        obj.add(this.getNodeName(attr), estimate(attr));
                    }
                }

                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node child = list.item(i);
                    obj.add(getNodeName(child), estimate(child));
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

                if ("array".equals(type)) {
                    JsonObject obj = new JsonObject();
                    NamedNodeMap attributes = node.getAttributes();
                    for (int i = 0; i < attributes.getLength(); i++) {
                        Node attr = attributes.item(i);
                        obj.add(getNodeName(attr), estimate(attr));
                    }

                    NodeList list = node.getChildNodes();
                    for (int i = 0; i < list.getLength(); i++) {
                        Node child = list.item(i);
                        obj.add(this.getNodeName(child), estimate(child));
                    }

                    array.add(obj);

                } else {
                    String elementType = type.toLowerCase().substring(0, type.lastIndexOf("array"));
                    JsonElement primitive = convert(node.getTextContent(), elementType);
                    array.add(primitive);
                }

                return array;

            } else if (node.getTextContent() != null) {
                return convert(node.getTextContent(), type);

            } else if (node.getChildNodes().getLength() > 0) {
                JsonObject obj = new JsonObject();
                NamedNodeMap attributes = node.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node attr = attributes.item(i);
                    obj.add(getNodeName(attr), estimate(attr));
                }

                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node child = list.item(i);
                    obj.add(getNodeName(child), estimate(child));
                }

                return obj;
            }

            return null;
        }
    }

    private JsonElement convert(String value, String type) {
        try {
            if ("boolean".equals(type)) {
                return new JsonPrimitive(Boolean.parseBoolean(value));

            } else if ("short".equals(type)) {
                return new JsonPrimitive(Short.parseShort(value));

            } else if ("integer".equals(type)) {
                return new JsonPrimitive(Integer.parseInt(value));

            } else if ("long".equals(type)) {
                return new JsonPrimitive(Long.parseLong(value));

            } else if ("float".equals(type)) {
                return new JsonPrimitive(Float.parseFloat(value));

            } else if ("double".equals(type)) {
                return new JsonPrimitive(Double.parseDouble(value));

            } else {
                return new JsonPrimitive(value);
            }
        } catch (ClassCastException e) {
            throw new RuntimeException("Cannot cast '" + value + "' to " + type );
        }
    }

    private String path(Node node) {
        String path = getNodeName(node);
        Node parent = node.getParentNode();
        while (parent != null && !"XMLNSC".equals(getNodeName(parent))) {
            path = getNodeName(parent) + "/" + path;
            parent = parent.getParentNode();

        }

        return path;
    }

    private String getNodeName(Node node) {
        String name = node.getNodeName();
        if (name.contains(":")) {
            name = name.substring(name.lastIndexOf(":") + 1);
        }

        return name;
    }
}