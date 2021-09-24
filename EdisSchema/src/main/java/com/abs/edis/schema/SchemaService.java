package com.abs.edis.schema;

import com.google.gson.*;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.xs.XmlBeansUtils;
import soya.framework.tao.xs.XsdToAvsc;
import soya.framework.tao.xs.XsKnowledgeBase;
import soya.framework.tao.xs.XsNode;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class SchemaService {

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, Command> COMMANDS;

    static {
        COMMANDS = new LinkedHashMap<>();
        Class<?>[] classes = SchemaService.class.getDeclaredClasses();
        for (Class<?> c : classes) {
            if (Command.class.isAssignableFrom(c) && !c.isInterface()) {
                String name = c.getSimpleName();
                if (name.endsWith("Command")) {
                    name = name.substring(0, name.lastIndexOf("Command"));
                    try {
                        Command processor = (Command) c.newInstance();
                        COMMANDS.put(name.toUpperCase(), processor);

                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public static void main(String[] args) {
        File file = new File("C:\\Users\\qwen002\\IBM\\IIBT10\\workspace\\APPDEV_ESED1_SRC_TRUNK\\esed1_src\\CMM_dev\\BOD\\GetAirMilePoints.xsd");
        // File file = new File("C:\\Users\\qwen002\\IBM\\IIBT10\\workspace\\APPDEV_ESED1_SRC_TRUNK\\esed1_src\\CMM_dev\\BOD\\GetGroceryOrder.xsd");

        try {
            //String result = new XPathDataTypeCommand().execute(knowledgeTree(file));
            String result = new AvroSchemaCommand().execute(knowledgeTree(file));
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String process(Node input) throws Exception {
        JsonElement jsonElement = estimate(input).getAsJsonObject();

        if (jsonElement != null && jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String command = jsonObject.get("command").getAsString().toUpperCase();
            String file = jsonObject.get("file").getAsString();

            return COMMANDS.get(command).execute(knowledgeTree(new File(file)));
        }

        return null;
    }

    private static KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree(File xsd) {
        return XsKnowledgeBase.builder()
                .file(xsd)
                .create().knowledge();
    }

    private static JsonElement estimate(Node node) {
        if (node.getTextContent() != null) {
            return new JsonPrimitive(node.getTextContent());
        } else if (node.getChildNodes().getLength() > 0) {
            if ("Item".equals(node.getFirstChild().getNodeName())) {
                JsonArray arr = new JsonArray();
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node child = list.item(i);
                    arr.add(estimate(child));
                }
                return arr;

            } else {
                JsonObject obj = new JsonObject();
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node child = list.item(i);
                    obj.add(child.getNodeName(), estimate(child));
                }
                return obj;
            }
        }

        return null;
    }

    private static String getXsType(SchemaType schemaType) {
        SchemaType base = schemaType;
        while (base != null && !base.isSimpleType()) {
            base = base.getBaseType();
        }

        if (base == null || XmlBeansUtils.getXMLBuildInType(base) == null) {
            return "string";

        } else {
            XmlBeansUtils.XMLBuildInType buildInType = XmlBeansUtils.getXMLBuildInType(base);
            String type = buildInType.getName();
            if (type.startsWith("xs:")) {
                type = type.substring(3);
            }

            return type;
        }
    }

    private static String getSimpleType(SchemaType schemaType) {
        SchemaType base = schemaType;
        while (base != null && !base.isSimpleType()) {
            base = base.getBaseType();
        }

        if (base == null || XmlBeansUtils.getXMLBuildInType(base) == null) {
            return "string";

        } else {
            XmlBeansUtils.XMLBuildInType buildInType = XmlBeansUtils.getXMLBuildInType(base);
            String type = buildInType.getName();
            if (type.startsWith("xs:")) {
                type = type.substring(3);
            }

            switch (type) {
                case "normalizedString":
                case "date":
                case "dateTime":
                case "time":
                    return "string";

                default:
                    return type;
            }
        }
    }

    interface Command {
        String execute(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree) throws Exception;
    }

    static class XPathDataTypeCommand implements Command {

        @Override
        public String execute(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree) throws Exception {
            StringBuilder builder = new StringBuilder();
            Iterator<String> iterator = knowledgeTree.paths();
            while (iterator.hasNext()) {
                String path = iterator.next();
                XsNode node = knowledgeTree.get(path).origin();
                builder.append(path).append("=type(");
                if (XsNode.XsNodeType.Folder.equals(node.getNodeType())) {
                    builder.append("complex").append(")");

                } else if (XsNode.XsNodeType.Attribute.equals(node.getNodeType())) {
                    builder.append(getXsType(node.getSchemaType())).append(")");

                } else {
                    builder.append(getXsType(node.getSchemaType())).append(")");

                }

                builder.append("::").append("cardinality(").append(node.getMinOccurs()).append("-");
                if (node.getMaxOccurs() != null) {
                    builder.append(node.getMaxOccurs());
                } else {
                    builder.append("n");
                }

                builder.append(")").append("\n");
            }

            return builder.toString();
        }
    }

    static class XPathJsonTypeCommand implements Command {

        @Override
        public String execute(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree) throws Exception {
            StringBuilder builder = new StringBuilder();
            Iterator<String> iterator = knowledgeTree.paths();
            while (iterator.hasNext()) {
                String path = iterator.next();
                XsNode node = knowledgeTree.get(path).origin();
                builder.append(path).append("=type(");
                if (XsNode.XsNodeType.Folder.equals(node.getNodeType())) {
                    builder.append("complex").append(")");

                } else if (XsNode.XsNodeType.Attribute.equals(node.getNodeType())) {
                    builder.append(getSimpleType(node.getSchemaType())).append(")");

                } else {
                    builder.append(getSimpleType(node.getSchemaType())).append(")");

                }

                builder.append("::").append("cardinality(").append(node.getMinOccurs()).append("-");

                if (node.getMaxOccurs() != null) {
                    builder.append(node.getMaxOccurs());

                } else {
                    builder.append("n");
                }

                builder.append(")").append("\n");
            }
            return builder.toString();
        }
    }

    static class SampleXmlCommand implements Command {

        @Override
        public String execute(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree) throws Exception {
            SchemaTypeSystem sts = knowledgeTree.origin();
            String result = SampleXmlUtil.createSampleForType(sts.documentTypes()[0]);
            return result.replace("xmlns:def", "xmlns:Abs").replaceAll("def:", "Abs:");
        }
    }

    static class AvroSchemaCommand implements Command {

        @Override
        public String execute(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree) throws Exception {
            Schema schema = XsdToAvsc.fromXmlSchema(knowledgeTree.origin());
            return schema.toString(true);
        }
    }

    static class SampleAvroCommand implements Command {

        @Override
        public String execute(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree) throws Exception {
            Schema schema = XsdToAvsc.fromXmlSchema(knowledgeTree.origin());
            Object result = new SampleAvroGenerator(schema, new Random(), 0).generate();
            GenericRecord genericRecord = (GenericRecord) result;

            /*File out = new File("C:/github/Workshop/Doc/AlaskaAirMile.avro");
            FileOutputStream fos = new FileOutputStream(out);
            GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
            //writer.getData().addLogicalTypeConversion(new TimeConversions.TimestampConversion());
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(fos, null);
            writer.write(genericRecord, encoder);
            encoder.flush();*/

            return genericRecord.toString();


        }
    }
}
