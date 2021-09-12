package com.abs.edis.schema;


import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XmlToAvroConverter {

    public static final String PREFIX = "Abs:";

    private Schema schema;

    public XmlToAvroConverter(Schema schema) {
        this.schema = schema;
    }

    public static byte[] convert(Node xml, Schema schema) throws IOException {
        return write(schema, new XmlToAvroConverter(schema).createRecord(schema, xml));
    }

    public static byte[] write(Schema schema, GenericRecord record) throws IOException {
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(new GenericDatumWriter<>(schema));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        dataFileWriter.create(schema, outputStream);
        dataFileWriter.append(record);
        dataFileWriter.close();

        return outputStream.toByteArray();
    }

    public static byte[] write(Schema schema, List<GenericRecord> records) throws IOException {

        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(new GenericDatumWriter<>(schema));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        dataFileWriter.create(schema, outputStream);
        records.forEach(e -> {
            try {
                dataFileWriter.append(e);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });
        dataFileWriter.close();

        return outputStream.toByteArray();
    }

    public static GenericData.Record createRecord(Schema schema, Node node) {
        GenericData.Record record = new GenericData.Record(schema);
        List<Schema.Field> fields = schema.getFields();
        fields.forEach(e -> {
            record.put(e.name(), generateObject(e.name(), e.schema(), node));
        });

        return record;
    }

    private static GenericData.Record createEmptyRecord(Schema schema) {
        GenericData.Record record = new GenericData.Record(schema);
        List<Schema.Field> fields = schema.getFields();
        fields.forEach(e -> {
            record.put(e.name(), getDefaultValue(e.schema()));
        });
        return record;
    }

    private static Object generateObject(String name, Schema schema, Node node) {

        Schema.Type type = schema.getType();
        Element element = (Element) node;

        if (element.getAttribute(name) != null && !element.getAttribute(name).isEmpty()) {
            return element.getAttribute(name);

        } else if (element.getAttribute(PREFIX + name) != null && !element.getAttribute(PREFIX + name).isEmpty()) {
            return element.getAttribute(PREFIX + name);

        } else if (type.equals(Schema.Type.ARRAY)) {
            return generateArray(name, schema, node);

        } else if (type.equals(Schema.Type.RECORD)) {
            NodeList nodeList = element.getElementsByTagName(PREFIX + name);
            if (nodeList != null && nodeList.getLength() == 1 && nodeList.item(0).getNodeType() == Node.ELEMENT_NODE) {
                return createRecord(schema, nodeList.item(0));

            } else {
                return new GenericData.Record(schema);
            }

        } else if (type.equals(Schema.Type.UNION)) {
            return generateUnion(name, schema, node);

        } else {
            String value = null;
            NodeList nodeList = element.getElementsByTagName(PREFIX + name);
            if (nodeList != null && nodeList.getLength() == 1) {
                value = nodeList.item(0).getTextContent();
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

            /*case ENUM:
                return generateEnumSymbol(name, schema, node);

            case FIXED:
                return generateFixed(name, schema, node);

            case MAP:
                return generateMap(name, schema, node);*/
            default:
                throw new RuntimeException("Unrecognized schema type: " + schema.getType());
        }
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
                return createEmptyRecord(schema);

            case ARRAY:
                return new ArrayList<>();

            case UNION:
                return null;

            /*case ENUM:
                return generateEnumSymbol(name, schema, node);

            case FIXED:
                return generateFixed(name, schema, node);

            case MAP:
                return generateMap(name, schema, node);*/
            default:
                throw new RuntimeException("Unrecognized schema type: " + schema.getType());
        }
    }

    private static Object generateUnion(String name, Schema type, Node node) {
        Element element = (Element) node;
        NodeList nodeList = element.getElementsByTagName(name);
        if (nodeList.getLength() == 0) {
            nodeList = element.getElementsByTagName(PREFIX + name);
        }

        if (nodeList != null && nodeList.getLength() > 0) {
            List<Schema> schemas = type.getTypes();
            for (Schema sc : schemas) {
                if (!Schema.Type.NULL.equals(sc.getType())) {
                    return generateObject(name, sc, node);
                }
            }
        }

        return null;
    }

    private static Object generateArray(String name, Schema schema, Node node) {

        Element element = (Element) node;
        NodeList nodeList = element.getElementsByTagName(name);
        if (nodeList.getLength() == 0) {
            nodeList = element.getElementsByTagName(PREFIX + name);
        }

        int length = nodeList.getLength();
        Collection<Object> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(createRecord(schema.getElementType(), nodeList.item(i)));
        }

        return result;
    }

    private Object generateMap(String name, Schema type, Node node) {
        return null;
    }

    private Object generateFixed(String name, Schema type, Node node) {
        return null;
    }

    private Object generateEnumSymbol(String name, Schema type, Node node) {
        return null;
    }
}

