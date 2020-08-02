package soya.framework.tools.iib;

import soya.framework.tools.util.StringBuilderUtils;
import com.google.common.base.CaseFormat;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.LinkedHashSet;
import java.util.Set;

public class ESQLGenerator {

    public static final String DOCUMENT_ROOT = "xmlDocRoot";
    public static final String DOCUMENT_DATA = "DocumentData";

    public static final String INPUT_JSON_ROOT_VARIABLE = "_rootJsonObject";

    private MessageType inputType;
    private MessageType outputType;
    private Node root;

    private Node documentNode;
    private Node dataObjectNode;

    private Set<String> variables = new LinkedHashSet<String>();

    private String inputRootVariable;

    private Set<Node> subRoots = new LinkedHashSet<>();

    private ESQLGenerator(Node root, MessageType inputType, MessageType outputType) {
        this.inputType = inputType;
        if (MessageType.JSON.equals(inputType)) {
            inputRootVariable = INPUT_JSON_ROOT_VARIABLE;
        }

        this.outputType = outputType;

        this.root = root;
        for (Node n : root.getChildren()) {
            if (DOCUMENT_DATA.equals(n.getName())) {
                documentNode = n;
            } else {
                dataObjectNode = n;
            }
        }

        findSubRoots(root);
    }

    private void findSubRoots(Node node) {
        if (isSubRoot(node)) {
            subRoots.add(node);
        } else if (node.getChildren() != null && node.getChildren().size() > 0) {
            node.getChildren().forEach(e -> {
                findSubRoots(e);
            });
        }

    }

    private static boolean isSubRoot(Node node) {
        return NodeType.FOLDER.equals(node.getType())
                && node.getValue() != null
                && node.getValue().get("value") != null
                && node.getValue().get("value").getAsString().trim().length() > 0;
    }

    public static ESQLGenerator newInstance(Node root, MessageType inputType, MessageType outputType) {
        return new ESQLGenerator(root, inputType, outputType);
    }

    private static String getVariableName(String name) {
        String token = name;
        if (token.contains("/")) {
            token = token.substring(token.lastIndexOf("/") + 1);
        }
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, token);
    }

    public String generate(String brokerSchema, String moduleName) {
        StringBuilder builder = new StringBuilder();
        if(brokerSchema != null && brokerSchema.trim().length() > 0) {
            builder.append("BROKER SCHEMA ").append(brokerSchema.trim()).append(";").append("\n\n");
        }
        builder.append("CREATE COMPUTE MODULE ").append(moduleName);
        StringBuilderUtils.println(builder, 2);

        // main function:
        printMainFunction(builder, root);

        builder.append("END MODULE;");
        return builder.toString();
    }
/*

    public String generate(String moduleName) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE COMPUTE MODULE ").append(moduleName);
        StringBuilderUtils.println(builder, 2);

        // main function:
        printMainFunction(builder, root);

        builder.append("END MODULE;");
        return builder.toString();
    }
*/

    private void printMainFunction(StringBuilder builder, Node root) {
        StringBuilderUtils.indent(builder);
        builder.append("CREATE FUNCTION Main() RETURNS BOOLEAN");
        StringBuilderUtils.println(builder);
        begin(builder, 1);

        // Declare Namespace
        declareNamespace(builder);

        // Declare Output Domain
        StringBuilderUtils.indent(builder, 2);
        builder.append("-- Declare Output Message Root").append("\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("CREATE LASTCHILD OF OutputRoot DOMAIN ").append("'XMLNSC'").append(";\n\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("DECLARE xmlDocRoot REFERENCE TO OutputRoot.XMLNSC.").append(root.getName()).append(";\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("CREATE LASTCHILD OF OutputRoot.").append("XMLNSC AS ").append(DOCUMENT_ROOT).append(" TYPE XMLNSC.Folder NAME '").append(root.getName()).append("'").append(";\n");
        StringBuilderUtils.println(builder);

        // Declare Output Variables
        declareOutputVariables(builder, root);


        // Declare Input Variables:
        declareInputVariables(builder);

        printDocumentTransform(builder);

        printDataObjectTransform(builder);

        // Sub root nodes:
        subRoots.forEach(e -> {
            printSubRoot(builder, e);
        });

        StringBuilderUtils.println("RETURN TRUE;", builder, 2);
        StringBuilderUtils.println("END;", builder, 1);
        StringBuilderUtils.println(builder);
    }

    private void begin(StringBuilder builder, int indent) {
        for (int i = 0; i < indent; i++) {
            builder.append("\t");
        }
        builder.append("BEGIN").append("\n");
    }

    private void declareNamespace(StringBuilder builder) {
        StringBuilderUtils.indent(builder, 2);
        builder.append("-- Declare Namespace").append("\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("DECLARE ").append("Abs").append(" NAMESPACE ").append("'https://collab.safeway.com/it/architecture/info/default.aspx'").append(";").append("\n");

        StringBuilderUtils.println(builder);
    }

    private void declareInputVariables(StringBuilder builder) {

        if (MessageType.JSON.equals(inputType)) {

            StringBuilderUtils.indent(builder, 2);
            builder.append("-- Declare Variables for Input Message").append("\n");

            StringBuilderUtils.indent(builder, 2);
            builder.append("DECLARE ").append(inputRootVariable).append(" REFERENCE TO InputRoot.JSON.Data;").append("\n");
            StringBuilderUtils.println(builder);

        } else {
            StringBuilderUtils.indent(builder, 2);
            builder.append("-- Declare Variables for Input Message").append("\n");

            StringBuilderUtils.indent(builder, 2);
            builder.append("DECLARE _row REFERENCE TO InputRoot.XMLNSC.ROWSET.ROW;").append("\n");
            StringBuilderUtils.println(builder);
        }
    }

    private void declareOutputVariables(StringBuilder builder, Node root) {

        if (documentNode != null) {
            StringBuilderUtils.indent(builder, 2);
            builder.append("-- Declare Variables for ").append(documentNode.getName()).append("\n");
            declareOutVar(builder, documentNode);
            StringBuilderUtils.println(builder);
        }

        if (dataObjectNode != null) {
            StringBuilderUtils.indent(builder, 2);
            builder.append("-- Declare Variables for ").append(dataObjectNode.getName()).append("\n");

            declareOutVar(builder, dataObjectNode);
            StringBuilderUtils.println(builder);
        }
    }

    private void declareOutVar(StringBuilder builder, Node node) {
        if (Node.isEmpty(node)) {
            return;
        }

        String pathExp = node.getPath().replaceAll("/", ".");

        String var = getVariableName(node.getName());
        if (!variables.contains(var)) {
            StringBuilderUtils.indent(builder, 2);
            builder.append("DECLARE ").append(var).append(" REFERENCE TO OutputRoot.XMLNSC.").append(pathExp).append(";\n");
            variables.add(var);
        }

        node.getChildren().forEach(e -> {
            if (NodeType.FOLDER.equals(e.getType())) {
                declareOutVar(builder, e);
            }
        });
    }

    private void printDocumentTransform(StringBuilder builder) {
        if (documentNode != null) {
            StringBuilderUtils.indent(builder, 2);
            builder.append("-- Document Model Mapping").append("\n");

            StringBuilderUtils.indent(builder, 2);
            builder.append("CREATE LASTCHILD OF ")
                    .append(DOCUMENT_ROOT)
                    .append(" AS ")
                    .append(getVariableName(documentNode.getName()))
                    .append(" TYPE XMLNSC.Folder NAME '")
                    .append(documentNode.getName())
                    .append("'")
                    .append(";\n\n");

            documentNode.getChildren().forEach(o -> {
                printTransform(builder, o);
            });
            StringBuilderUtils.println(builder);
        }
    }

    private void printDataObjectTransform(StringBuilder builder) {

        StringBuilderUtils.indent(builder, 2);
        builder.append("-- Data Model Mapping").append("\n");

        StringBuilderUtils.indent(builder, 2);
        builder.append("CREATE LASTCHILD OF ")
                .append(DOCUMENT_ROOT)
                .append(" AS ")
                .append(getVariableName(dataObjectNode.getName()))
                .append(" TYPE XMLNSC.Folder NAME '")
                .append(dataObjectNode.getName())
                .append("'")
                .append(";\n\n");

        dataObjectNode.getChildren().forEach(o -> {
            printTransform(builder, o);
        });

        StringBuilderUtils.println(builder);

    }

    private void printDataObjectTransformIteratively(StringBuilder builder) {

        StringBuilderUtils.indent(builder, 2);
        builder.append("-- Data Model Mapping").append("\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("WHILE LASTMOVE(_row) DO").append("\n");

        StringBuilderUtils.indent(builder, 3);
        builder.append("CREATE LASTCHILD OF ")
                .append(DOCUMENT_ROOT)
                .append(" AS ")
                .append(getVariableName(dataObjectNode.getName()))
                .append(" TYPE XMLNSC.Folder NAME '")
                .append(dataObjectNode.getName())
                .append("'")
                .append(";\n\n");

        dataObjectNode.getChildren().forEach(o -> {
            printTransform(builder, o);
        });

        StringBuilderUtils.indent(builder, 3);
        builder.append("MOVE ").append(getVariableName(dataObjectNode.getName())).append(" NEXTSIBLING NAME '").append(getVariableName(dataObjectNode.getName())).append("';").append("\n");

        StringBuilderUtils.indent(builder, 2);
        builder.append("END WHILE;").append("\n");
        StringBuilderUtils.println(builder);

    }

    private static void printTransform(StringBuilder builder, Node node) {
        int depth = node.getPath().split("/").length;
        if (Node.isEmpty(node)) {
            // do nothing

        } else if (NodeType.FOLDER.equals(node.getType())) {
            if (!isSubRoot(node)) {
                StringBuilderUtils.indent(builder, depth);
                builder.append("-- Generate Node ").append(node.getName()).append("\n");
                StringBuilderUtils.indent(builder, depth);
                builder.append("CREATE LASTCHILD OF ")
                        .append(getVariableName(node.getParent()))
                        .append(" AS ")
                        .append(getVariableName(node.getName()))
                        .append(" TYPE XMLNSC.Folder NAME '")
                        .append(node.getName())
                        .append("'")
                        .append(";\n");
                node.getChildren().forEach(e -> {
                    printTransform(builder, e);
                });
                StringBuilderUtils.println(builder);

            }

        } else if (NodeType.FIELD.equals(node.getType()) && node.getValue() != null) {

            Object value = node.getValue();


            Mapping mapping = null;
            if (String.class.isInstance(value)) {
                mapping = fromDSL((String) value);

            } else if (JsonObject.class.isInstance(value)) {
                JsonObject jsonObject = (JsonObject) value;
                JsonElement v = jsonObject.get("value");
                if (v != null) {
                    mapping = fromDSL(v.getAsString());
                }
            }

            if (mapping != null) {
                StringBuilderUtils.indent(builder, depth - 1);
                builder.append("-- Set Field ").append(node.getName()).append("\n");

                StringBuilderUtils.indent(builder, depth - 1);
                if (!mapping.available) {
                    builder.append("-- ");
                }

                builder.append("SET ").append(getVariableName(node.getParent())).append(".(XMLNSC.Field)")
                        .append(node.getName()).append(" = ").append(mapping.toString()).append(";").append("\n\n");
            }

        }
    }

    private static void printSubRoot(StringBuilder builder, Node node) {
        String exp = node.getValue().get("value").getAsString();
        Function function = new Function(exp);
        if (FUNCTIONS.JsonObjectToXml.name().equals(function.getName())) {
            printJsonObjectToXML(function, node, builder);

        } else if (FUNCTIONS.JsonArrayToXml.name().equals(function.getName())) {
            printJsonArrayToXML(function, node, builder);
        }

    }

    private static void printJsonObjectToXML(Function function, Node node, StringBuilder builder) {
        String var = function.getParam().replace("$", INPUT_JSON_ROOT_VARIABLE);
        StringBuilderUtils.println("-- Process " + function.getName() + " from " + var + " to" + node.getName(), builder, 2);
        int lastPoint = var.lastIndexOf(".");
        String jsonObject = "_" + var.substring(lastPoint + 1);
        StringBuilderUtils.println("DECLARE " + jsonObject + " REFERENCE TO " + var + ";", builder, 2);
        // StringBuilderUtils.println("IF (" + jsonObject + " IS NOT NULL) THEN ", builder, 2);

        StringBuilderUtils.println("CREATE LASTCHILD OF "
                + getVariableName(node.getParent())
                + " AS "
                + getVariableName(node.getName())
                + " TYPE XMLNSC.Folder NAME '"
                + node.getName() + "';", builder, 2);
        StringBuilderUtils.println(builder);

        node.getChildren().forEach(e -> {
            printTransform(builder, e);
        });

        // StringBuilderUtils.println("END IF;", builder, 2);
        StringBuilderUtils.println(builder);
    }

    private static void printJsonArrayToXML(Function function, Node node, StringBuilder builder) {
        String var = function.getParam().replace("$", INPUT_JSON_ROOT_VARIABLE);
        if (!var.endsWith(".Item")) {
            var = var + ".Item";
        }

        StringBuilderUtils.println("-- Process " + function.getName() + " for " + node.getName(), builder, 2);
        StringBuilderUtils.println("DECLARE _item REFERENCE TO " + var + ";", builder, 2);
        StringBuilderUtils.println("WHILE LASTMOVE(_item) DO", builder, 2);

        StringBuilderUtils.println("-- Generate Node " + node.getName(), builder, 3);
        StringBuilderUtils.println("CREATE LASTCHILD OF "
                + getVariableName(node.getParent())
                + " AS "
                + getVariableName(node.getName())
                + " TYPE XMLNSC.Folder NAME '"
                + node.getName() + "';", builder, 3);
        StringBuilderUtils.println(builder);

        node.getChildren().forEach(e -> {
            printTransform(builder, e);
        });

        StringBuilderUtils.println(builder);
        StringBuilderUtils.println("MOVE _item NEXTSIBLING;", builder, 3);
        StringBuilderUtils.println("END WHILE;", builder, 2);
        StringBuilderUtils.println(builder);
    }

    private static Mapping fromDSL(String dsl) {
        Mapping mapping = new Mapping();
        if (dsl == null || dsl.trim().length() == 0 || dsl.trim().startsWith("--")) {
            mapping.available = false;

        } else if (dsl.contains("(") && dsl.endsWith(")")) {
            String[] arr = (dsl.trim() + ".").split("\\)\\.");
            for (String token : arr) {
                if (token.trim().length() > 0) {
                    int index = token.indexOf("(");
                    if (index > 0) {
                        FUNCTIONS func = FUNCTIONS.valueOf(token.substring(0, index));
                        String value = token.substring(index + 1);
                        if (FUNCTIONS.jsonpath.equals(func)) {
                            mapping.value = value.replace("$", INPUT_JSON_ROOT_VARIABLE);

                        } else if (FUNCTIONS.sql.equals(func)) {
                            mapping.value = "PASSTHRU('" + value.replace("$", INPUT_JSON_ROOT_VARIABLE) + "')";

                        } else if (FUNCTIONS.current.equals(func)) {
                            mapping.value = "CURRENT_TIMESTAMP";

                        } else if (FUNCTIONS.path.equals(func)) {
                            mapping.value = value.replace("$", "_row");
                        }

                    }
                }
            }
        }

        return mapping;
    }

    static class Mapping {
        private boolean available = true;
        private boolean optional;

        private String value;

        private Mapping() {
        }

        @Override
        public String toString() {
            return value != null ? value : "Mapping{}";
        }
    }


}
