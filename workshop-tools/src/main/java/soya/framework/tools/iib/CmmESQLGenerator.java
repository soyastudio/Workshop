package soya.framework.tools.iib;

import soya.framework.tools.util.StringBuilderUtils;
import com.google.common.base.CaseFormat;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class CmmESQLGenerator {
    private static Logger logger = LoggerFactory.getLogger(CmmESQLGenerator.class);

    public static final String NAMESPACE = "https://collab.safeway.com/it/architecture/info/default.aspx";
    public static final String DOCUMENT_ROOT = "xmlDocRoot";
    public static final String DOCUMENT_DATA = "DocumentData";

    public static final String INPUT_ROOT_NODE_VARIABLE = "_inputRootNode";

    private String name;
    protected Node root;
    protected Node documentNode;
    protected Node dataObjectNode;

    protected Set<String> variables = new LinkedHashSet<String>();
    protected Set<Node> subRoots = new LinkedHashSet<>();

    protected CmmESQLGenerator(Node root) {
        this.name = getClass().getSimpleName();
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

    public String generate(ESQL esql) {
        logger.info("Generate ESQL Compute Module: {} by {}", esql.moduleName(), name);
        StringBuilder builder = new StringBuilder();
        printModule(builder, root, esql);
        return builder.toString();
    }

    public String generate(String brokerSchema, String moduleName) {
        logger.info("Generate ESQL Compute Module: {} by {}", moduleName, name);
        StringBuilder builder = new StringBuilder();
        printModule(builder, root, brokerSchema, moduleName, null);
        return builder.toString();
    }

    public String generate(String brokerSchema, String moduleName, List<String> settings) {
        logger.info("Generate ESQL Compute Module: {} by {}", moduleName, name);
        StringBuilder builder = new StringBuilder();
        printModule(builder, root, brokerSchema, moduleName, settings);
        return builder.toString();
    }

    protected void printModule(StringBuilder builder, Node root, ESQL esql) {
        builder.append("BROKER SCHEMA ").append(esql.brokerSchema()).append("\n\n");

        builder.append("CREATE COMPUTE MODULE ").append(esql.moduleName());
        StringBuilderUtils.println(builder, 2);

        // main function:
        printMainFunction(builder, root, null);

        builder.append("END MODULE;");

    }

    protected void printModule(StringBuilder builder, Node root, String broker, String moduleName, List<String> settings) {
        if (broker != null) {
            builder.append("BROKER SCHEMA ").append(broker).append("\n\n");
        }

        builder.append("CREATE COMPUTE MODULE ").append(moduleName);
        StringBuilderUtils.println(builder, 2);

        // main function:
        printMainFunction(builder, root, settings);

        builder.append("END MODULE;");

    }

    protected void printMainFunction(StringBuilder builder, Node root, List<String> settings) {
        StringBuilderUtils.indent(builder);
        builder.append("CREATE FUNCTION Main() RETURNS BOOLEAN");
        StringBuilderUtils.println(builder);
        begin(builder, 1);

        // Declare Namespace
        declareNamespace(builder);

        // Declare Output Domain
        StringBuilderUtils.println("-- Declare Output Message Root", builder, 2);
        StringBuilderUtils.println("CREATE LASTCHILD OF OutputRoot DOMAIN 'XMLNSC';", builder, 2);
        StringBuilderUtils.println(builder);

        StringBuilderUtils.println("DECLARE xmlDocRoot REFERENCE TO OutputRoot.XMLNSC." + root.getName() + ";", builder, 2);
        StringBuilderUtils.println("CREATE LASTCHILD OF OutputRoot.XMLNSC AS " + DOCUMENT_ROOT + " TYPE XMLNSC.Folder NAME '" + root.getName() + "';",
                builder, 2);
        StringBuilderUtils.println("SET OutputRoot.XMLNSC." + root.getName() + ".(XMLNSC.NamespaceDecl)xmlns:Abs=Abs;",
                builder, 2);
        StringBuilderUtils.println(builder);

        // Declare Output Variables
        declareOutputVariables(builder);

        // Declare Input Variables:
        declareInputVariables(builder);

        // Settings:
        printSettings(builder, settings);

        // Construct Document Data
        printDocumentData(builder);

        // Construct Business Object Data Model
        printBusinessObjectData(builder);

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
        StringBuilderUtils.println("-- Declare Namespace", builder, 2);
        StringBuilderUtils.println("DECLARE Abs NAMESPACE '" + NAMESPACE + "';", builder, 2);
        StringBuilderUtils.println(builder);
    }

    protected void declareOutputVariables(StringBuilder builder) {
        if (documentNode != null) {
            StringBuilderUtils.println("-- Declare Variables for " + documentNode.getName(), builder, 2);
            declareOutVar(builder, documentNode);
            StringBuilderUtils.println(builder);
        }

        if (dataObjectNode != null) {
            StringBuilderUtils.println("-- Declare Variables for " + dataObjectNode.getName(), builder, 2);
            declareOutVar(builder, dataObjectNode);
            StringBuilderUtils.println(builder);
        }
    }

    protected void declareOutVar(StringBuilder builder, Node node) {
        if (Node.isEmpty(node)) {
            return;
        }

        String var = getVariableName(node.getName());
        if (!variables.contains(var)) {
            StringBuilderUtils.indent(builder, 2);
            if (node.getLevel() == 1) {
                builder.append("DECLARE ").append(var).append(" REFERENCE TO ").append(DOCUMENT_ROOT).append(";\n");
            } else {
                builder.append("DECLARE ").append(var).append(" REFERENCE TO ").append(getVariableName(node.getParent())).append(";\n");
            }

            variables.add(var);
        }

        node.getChildren().forEach(e -> {
            if (NodeType.FOLDER.equals(e.getType())) {
                declareOutVar(builder, e);
            }
        });
    }

    protected void printSettings(StringBuilder builder, List<String> settings) {
        if (settings != null) {
            StringBuilderUtils.println("-- Settings", builder, 2);
            settings.forEach(e -> {
                StringBuilderUtils.println(e, builder, 2);
            });
            StringBuilderUtils.println(builder);
        }
    }

    protected void printDocumentData(StringBuilder builder) {
        if (documentNode != null) {
            StringBuilderUtils.println("-- Construct DocumentData", builder, 2);
            StringBuilderUtils.println("CREATE LASTCHILD OF " + DOCUMENT_ROOT + " AS " + getVariableName(documentNode.getName()) + " TYPE XMLNSC.Folder NAME '" + documentNode.getName() + "';",
                    builder, 2);

            documentNode.getChildren().forEach(o -> {
                printNode(builder, o);
            });
            StringBuilderUtils.println(builder);
        }
    }

    protected void printBusinessObjectData(StringBuilder builder) {

        StringBuilderUtils.println("-- Construct Business Object Data", builder, 2);
        StringBuilderUtils.println("CREATE LASTCHILD OF " + DOCUMENT_ROOT + " AS " + getVariableName(dataObjectNode.getName()) + " TYPE XMLNSC.Folder NAME '" + dataObjectNode.getName() + "';",
                builder, 2);
        StringBuilderUtils.println(builder);

        dataObjectNode.getChildren().forEach(o -> {
            printNode(builder, o);
        });

        StringBuilderUtils.println(builder);

    }

    protected void printNode(StringBuilder builder, Node node) {
        int depth = node.getPath().split("/").length;
        if (Node.isEmpty(node)) {
            // do nothing

        } else if (NodeType.FOLDER.equals(node.getType())) {
            if (!Node.isSubRoot(node)) {
                StringBuilderUtils.println("-- Generate Node " + node.getName(), builder, depth);
                StringBuilderUtils.println("CREATE LASTCHILD OF " + getVariableName(node.getParent()) + " AS " + getVariableName(node.getName()) + " TYPE XMLNSC.Folder NAME '" + node.getFullName() + "';",
                        builder, depth);
                node.getChildren().forEach(e -> {
                    printNode(builder, e);
                });
                StringBuilderUtils.println(builder);
            } else {
                String var = node.getValue().get("value").getAsString();
                if (var != null && var.trim().length() > 0) {
                    if (!var.contains(":")) {
                        int separator = var.lastIndexOf('=');
                        if (separator > 0) {
                            String parent = var.substring(separator + 1).trim();
                            var = var.substring(0, separator).trim();

                            StringBuilderUtils.println("DECLARE " + var + " REFERENCE TO " + parent + ";", builder, depth);

                        }

                        StringBuilderUtils.println("WHILE LASTMOVE(" + var + ") DO", builder, depth);
                        StringBuilderUtils.println("-- Generate Node " + node.getName(), builder, depth);
                        StringBuilderUtils.println("CREATE LASTCHILD OF " + getVariableName(node.getParent()) + " AS " + getVariableName(node.getName()) + " TYPE XMLNSC.Folder NAME '" + node.getFullName() + "';",
                                builder, depth);
                        node.getChildren().forEach(e -> {
                            printNode(builder, e);
                        });

                        StringBuilderUtils.println("MOVE " + var + " NEXTSIBLING;", builder, depth);
                        StringBuilderUtils.println("END WHILE;", builder, depth);
                        StringBuilderUtils.println(builder);

                    } else {

                    }
                }
            }

        } else if (!Node.isEmpty(node)) {
            String node_type = ".(XMLNSC.Field)";
            if (NodeType.ATTRIBUTE.equals(node.getType())) {
                node_type = ".(XMLNSC.Attribute)";
            }

            JsonObject value = node.getValue();
            String valueExp = value.get("value").getAsString();
            Evaluator evaluator = new Evaluator(valueExp);
            Function function = evaluator.getFunction();

            StringBuilderUtils.println("-- Set Field " + node.getName(), builder, depth - 1);
            if (function == null) {
                if (valueExp.startsWith("-- ")) {
                    StringBuilderUtils.println("-- SET " + getVariableName(node.getParent()) + node_type + node.getFullName() + " = " + valueExp.substring(3) + ";",
                            builder, depth - 1);
                } else {
                    StringBuilderUtils.println("SET " + getVariableName(node.getParent()) + node_type + node.getFullName() + " = " + valueExp + ";",
                            builder, depth - 1);
                }

            } else if ("todo".equalsIgnoreCase(function.getName())) {
                StringBuilderUtils.println("-- TODO: SET " + getVariableName(node.getParent()) + node_type + node.getFullName() + " = ?;",
                        builder, depth - 1);

            } else {
                String exp = evaluate(evaluator);
                if (exp != null) {
                    StringBuilderUtils.println("SET " + getVariableName(node.getParent()) + node_type + node.getFullName() + " = " + exp + ";",
                            builder, depth - 1);
                }
            }
            StringBuilderUtils.println(builder);

        }
    }

    protected static String getVariableName(String name) {
        String token = name;
        if (token.contains("/")) {
            token = token.substring(token.lastIndexOf("/") + 1);
        }
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, token);
    }

    protected void findSubRoots(Node node) {
        if (Node.isSubRoot(node)) {
            subRoots.add(node);
        } else if (node.getChildren() != null && node.getChildren().size() > 0) {
            node.getChildren().forEach(e -> {
                findSubRoots(e);
            });
        }

    }

    protected String evaluate(Evaluator evaluator) {
        if (evaluator.isCommented()) {
            return null;

        } else if (evaluator.isScriptlet()) {
            return evaluator.getExpression();

        } else if (evaluator.getFunctions().size() == 1) {
            return evaluator.getFunctions().get(0).toESQL(this);

        } else {
            return evaluator.getExpression();
        }
    }

    public String getInputRootVariable() {
        return INPUT_ROOT_NODE_VARIABLE;
    }

    protected abstract void declareInputVariables(StringBuilder builder);

    protected abstract void printSubRoot(StringBuilder builder, Node node);

}
