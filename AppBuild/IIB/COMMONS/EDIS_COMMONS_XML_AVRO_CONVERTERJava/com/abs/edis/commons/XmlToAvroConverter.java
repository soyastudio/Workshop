package com.abs.edis.commons;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import javax.xml.parsers.ParserConfigurationException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class XmlToAvroConverter {

    public static final String ALPHABET = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";

    private XmlToAvroConverter() {
    }

    public static Schema parse(byte[] data) {
        return new Schema.Parser().parse(unzip(data));
    }

    public static byte[] zip(final String str) {
        if ((str == null) || (str.length() == 0)) {
            throw new IllegalArgumentException("Cannot zip null or empty string");
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
                gzipOutputStream.write(str.getBytes(StandardCharsets.UTF_8));
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to zip content", e);
        }
    }
    
    public static byte[] toJson(Node xml, Schema schema) {
    	return new GsonBuilder().setPrettyPrinting().create()
    			.toJson(JsonParser.parseString(createRecord(schema, xml).toString()))
    			.getBytes();
    }

    public static byte[] convert(Node xml, Schema schema) throws IOException, ParserConfigurationException, SAXException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        write(createRecord(schema, xml), schema, outputStream);
        return outputStream.toByteArray();
    }

    public static GenericData.Record createRecord(Schema schema, Node node) {
        GenericData.Record record = new GenericData.Record(schema);
        List<Schema.Field> fields = schema.getFields();
        fields.forEach(e -> {
            record.put(e.name(), create(e.name(), e.schema(), node));
        });

        return record;
    }

    public static GenericRecord read(byte[] data, Schema schema) throws Exception {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>();
        datumReader.setSchema(schema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
        return datumReader.read(null, decoder);
    }

    private static GenericData.Record createEmptyRecord(Schema schema) {
        GenericData.Record record = new GenericData.Record(schema);
        List<Schema.Field> fields = schema.getFields();
        fields.forEach(e -> {
            record.put(e.name(), getDefaultValue(e.schema()));
        });
        return record;
    }

    private static List<Node> getChildrenByName(Node node, String name) {
        List<Node> list = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i ++) {
            Node child = nodeList.item(i);
            String nodeName = child.getNodeName();
            if(nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(":") + 1);
            }

            if(name.equals(nodeName)) {
                list.add(child);
            }
        }
        return list;
    }

    private static Object create(String name, Schema schema, Node node) {

        Schema.Type type = schema.getType();
        Element element = (Element) node;

        if (element.getAttribute(name) != null && !element.getAttribute(name).isEmpty()) {
            return element.getAttribute(name);

        } else if (element.getAttribute(name) != null && !element.getAttribute(name).isEmpty()) {
            return element.getAttribute(name);

        } else if (type.equals(Schema.Type.ARRAY)) {
            return createArray(name, schema, node);

        } else if (type.equals(Schema.Type.RECORD) || type.equals(Schema.Type.MAP)) {
            List<Node> children = getChildrenByName(node, name);
            if (children.size() == 1 && children.get(0).getNodeType() == Node.ELEMENT_NODE) {
                return createRecord(schema, children.get(0));

            } else {
                return new GenericData.Record(schema);
            }

        } else if (type.equals(Schema.Type.UNION)) {
            return generateUnion(name, schema, node);

        } else {
            String value = null;
            List<Node> children = getChildrenByName(node, name);
            if (children.size() == 1) {
                value = children.get(0).getTextContent();
            }

            return convert(value, schema);
        }
    }

    private static Object convert(String value, Schema schema) {
        if (value == null) {
            return getDefaultValue(schema);
        }

        switch (schema.getType()) {
            case BOOLEAN:
                return Boolean.parseBoolean(value);

            case BYTES:
                return value.getBytes();

            case DOUBLE:
                return Double.parseDouble(value);

            case FLOAT:
                return Float.parseFloat(value);

            case INT:
                return Integer.parseInt(value);

            case LONG:
                return Long.parseLong(value);

            case NULL:
                return null;

            case STRING:
                return value;

            case ENUM:
                return generateEnumSymbol(value, schema);

            case FIXED:
                return generateFixed(value, schema);

            default:
                throw new RuntimeException("Unrecognized schema type: " + schema.getType());
        }
    }

    private static Object generateUnion(String name, Schema type, Node node) {
        List<Node> children = getChildrenByName(node, name);
        if(children.size() > 0) {
        	List<Schema> schemas = type.getTypes();
            for (Schema sc : schemas) {
                if (!Schema.Type.NULL.equals(sc.getType())) {
                    return create(name, sc, node);
                }
            }
        }

        return null;
    }

    private static Object createArray(String name, Schema schema, Node node) {

        Collection<Object> result = new ArrayList<>();
        List<Node> children = getChildrenByName(node, name);
        children.forEach(e -> {
            Schema elementType = schema.getElementType();
            if (Schema.Type.RECORD.equals(elementType.getType())) {
                result.add(createRecord(elementType, e));

            } else {
                result.add(convert(e.getTextContent(), elementType));

            }
        });

        return result;
    }

    private static Object generateFixed(String value, Schema schema) {
        if (value == null) {
            return getDefaultFixedValue(schema);

        } else {
            return value;

        }
    }

    private static Object generateEnumSymbol(String value, Schema schema) {
        List<String> values = schema.getEnumSymbols();
        for (String e : values) {
            if (e.equals(value)) {
                return e;
            }
        }

        throw new IllegalArgumentException("No value for enum type: " + value);
    }

    private static Object getDefaultValue(Schema schema) {
        switch (schema.getType()) {
            case BOOLEAN:
                return Boolean.FALSE;

            case BYTES:
                return "".getBytes();

            case DOUBLE:
                return 0.0;

            case FLOAT:
                return 0.0f;

            case INT:
                return 0;

            case LONG:
                return 0L;

            case NULL:
                return null;

            case STRING:
                return "";

            case RECORD:
            case MAP:
                return createEmptyRecord(schema);

            case ARRAY:
                return new ArrayList<>();

            case UNION:
                return getDefaultUnionValue(schema);

            case ENUM:
                return getDefaultEnumSymbol(schema);

            case FIXED:
                return getDefaultFixedValue(schema);

            default:
                throw new RuntimeException("Unrecognized schema type: " + schema.getType());
        }
    }

    private static Object getDefaultUnionValue(Schema schema) {
        Object value = null;
        List<Schema> list = schema.getTypes();
        for (Schema sc : list) {
            if (Schema.Type.NULL.equals(sc.getType())) {
                return null;

            } else if (value == null) {
                value = getDefaultValue(schema);
            }
        }

        return value;
    }

    private static Object getDefaultEnumSymbol(Schema schema) {
        List<String> values = schema.getEnumSymbols();
        if (values.size() > 0) {
            return values.get(0);

        } else {
            return null;

        }
    }

    private static Object getDefaultFixedValue(Schema schema) {
        StringBuilder builder = new StringBuilder();
        int len = schema.getFixedSize();
        char[] arr = ALPHABET.toCharArray();
        for (int i = 0; i < len; i++) {
            int random = ThreadLocalRandom.current().nextInt();
            if (random < 0) {
                random = -1 * random;
            }
            int index = random % 52;
            builder.append(arr[index]);
        }
        return builder.toString();
    }

    public static String getRandom(int len) {
        StringBuilder builder = new StringBuilder();
        char[] arr = ALPHABET.toCharArray();
        for (int i = 0; i < len; i++) {
            int random = ThreadLocalRandom.current().nextInt();
            if (random < 0) {
                random = -1 * random;
            }
            int index = random % 52;
            builder.append(arr[index]);
        }
        return builder.toString();
    }

    private static void write(GenericRecord record, Schema schema, OutputStream outputStream) throws IOException {
        GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);       
        //Encoder encoder = EncoderFactory.get().jsonEncoder(schema, outputStream);

        writer.write(record, encoder);
        encoder.flush();
        outputStream.close();
    }

    private static String unzip(final byte[] data) {
        if ((data == null) || (data.length == 0)) {
            throw new IllegalArgumentException("Cannot unzip null or empty bytes");
        }

        byte[] compressed = Base64.getDecoder().decode(data);

        if (!isZipped(compressed)) {
            return new String(compressed);
        }

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressed)) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
                try (InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
                    try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                        StringBuilder output = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            output.append(line);
                        }
                        return output.toString();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to unzip content", e);
        }
    }

    private static boolean isZipped(final byte[] compressed) {
        return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
                && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
    }

    public static String toXmlString(Node node) {
        StringBuilder builder = new StringBuilder();
        print(node, builder);
        return builder.toString();
    }

    private static void print(Node node, StringBuilder builder) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String name = node.getNodeName();
            if (name.contains(":")) {
                name = name.substring(name.indexOf(":") + 1);
            }
            builder.append("<").append(name).append(">");

            NodeList children = node.getChildNodes();
            if (children.getLength() == 1 && children.item(0).getNodeType() == Node.TEXT_NODE) {
                builder.append(node.getTextContent());
            } else {
                for (int i = 0; i < children.getLength(); i++) {
                    print(children.item(i), builder);
                }

            }

            builder.append("</").append(name).append(">");
        }
    }

}

