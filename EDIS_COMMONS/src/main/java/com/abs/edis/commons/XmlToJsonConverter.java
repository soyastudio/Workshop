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
        if (node.getTextContent() != null) {
            if (mappings.containsKey(path)) {
                String type = mappings.get(path);
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

                } else if ("array".equals(type)) {
                    JsonArray array = arrayMap.get(path);
                    if(array == null) {
                        array = new JsonArray();
                        arrayMap.put(path, array);
                    }

                    array.add(new JsonPrimitive(node.getTextContent()));

                    return array;

                } else {
                    return new JsonPrimitive(node.getTextContent());
                }

            } else {
                return new JsonPrimitive(node.getTextContent());
            }

        } else if (node.getChildNodes().getLength() > 0) {
            if (mappings.containsKey(path) && "array".equals(mappings.get(path))) {
                JsonArray array = arrayMap.get(path);
                if (!arrayMap.containsKey(path)) {
                    array = new JsonArray();
                    arrayMap.put(path, array);
                }

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


                return array;
            } else {
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

        } else {
            return new JsonPrimitive("");

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
