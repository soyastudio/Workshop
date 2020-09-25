package soya.framework.tools.xmlbeans;

import com.google.gson.Gson;
import soya.framework.tools.util.StringBuilderUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class XmlConstructEsqlRenderer extends XmlSchemaBaseRenderer implements MappingFeature, IntegrationApplicationFeature {
    public static final String URI = "http://collab.safeway.com/it/architecture/info/default.aspx";
    public static final String DOCUMENT_ROOT = "xmlDocRoot";

    private static Gson GSON = new Gson();

    private String brokerSchema;
    private String moduleName;

    private String inputRootVariable = "_inputRoot";
    private String inputRootReference;

    private XmlSchemaBase base;
    private Map<String, WhileLoop> loopMap = new LinkedHashMap<>();

    @Override
    public String render(XmlSchemaBase base) {
        this.base = base;
        StringBuilder builder = new StringBuilder();

        if (brokerSchema != null && brokerSchema.trim().length() > 0) {
            builder.append("BROKER SCHEMA ").append(brokerSchema.trim()).append("\n\n");
        }

        builder.append("CREATE COMPUTE MODULE ").append(moduleName);
        StringBuilderUtils.println(builder, 2);

        // UDP:
        Variable[] variables = base.getAnnotation(GLOBAL_VARIABLE, Variable[].class);
        if (variables != null && variables.length > 0) {
            StringBuilderUtils.println("-- Declare UDPs", builder, 1);
            for (Variable v : variables) {
                StringBuilderUtils.println("DECLARE " + v.name + " EXTERNAL " + v.type + " " + v.defaultValue + ";", builder, 1);
            }

            StringBuilderUtils.println(builder);
        }

        // Namespace
        declareNamespace(builder);

        StringBuilderUtils.println("CREATE FUNCTION Main() RETURNS BOOLEAN", builder, 1);
        begin(builder, 1);

        declareInputRoot(builder);

        // Declare Output Domain
        StringBuilderUtils.indent(builder, 2);
        builder.append("-- Declare Output Message Root").append("\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("CREATE LASTCHILD OF OutputRoot DOMAIN ").append("'XMLNSC'").append(";\n\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("DECLARE xmlDocRoot REFERENCE TO OutputRoot.XMLNSC.").append(base.getRoot().getName()).append(";\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("CREATE LASTCHILD OF OutputRoot.").append("XMLNSC AS ").append(DOCUMENT_ROOT).append(" TYPE XMLNSC.Folder NAME '").append(base.getRoot().getName()).append("'").append(";\n");
        StringBuilderUtils.println("SET OutputRoot.XMLNSC." + base.getRoot().getName() + ".(XMLNSC.NamespaceDecl)xmlns:Abs=Abs;",
                builder, 2);
        StringBuilderUtils.println(builder);

        XmlSchemaBase.MappingNode root = base.getRoot();
        printNode(root, builder, 0);

        StringBuilderUtils.println("RETURN TRUE;", builder, 2);
        StringBuilderUtils.println("END;", builder, 1);
        StringBuilderUtils.println(builder);

        builder.append("END MODULE;");

        return builder.toString();
    }

    private void begin(StringBuilder builder, int indent) {
        for (int i = 0; i < indent; i++) {
            builder.append("\t");
        }

        builder.append("BEGIN").append("\n");
    }

    private void declareInputRoot(StringBuilder builder) {
        StringBuilderUtils.println("-- Declare Input Message Root", builder, 2);
        StringBuilderUtils.println("DECLARE " + inputRootVariable + " REFERENCE TO " + inputRootReference + ";", builder, 2);
        StringBuilderUtils.println(builder);
    }

    private void declareNamespace(StringBuilder builder) {
        StringBuilderUtils.println("-- Declare Namespace", builder, 1);
        StringBuilderUtils.println("DECLARE " + "Abs" + " NAMESPACE " + "'https://collab.safeway.com/it/architecture/info/default.aspx'" + ";", builder, 1);
        StringBuilderUtils.println(builder);
    }

    private void printNode(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
        if (!isMapped(node)) {
            return;
        }

        if (node.getAnnotation(MAPPING) != null && node.getAnnotation(MAPPING, Mapping.class).assignment != null) {
            printAssignment(node, builder, indent);

        } else if (node.getAnnotation(LOOP) != null) {
            printLoop(node, builder, indent);

        } else if (node.getAnnotation(BLOCK) != null) {
            printBlock(node, builder, indent);

        } else if (XmlSchemaBase.NodeType.Folder.equals(node.getNodeType())) {
            if (node.getAnnotation(CONDITION) != null) {
                String condition = node.getAnnotation(CONDITION, String.class);

                StringBuilderUtils.println("IF " + condition + " THEN", builder, node.getLevel() + indent);
                printSimpleFolder(node, builder, indent + 1);
                StringBuilderUtils.println("END IF;", builder, node.getLevel() + indent);
                StringBuilderUtils.println(builder);

            } else {
                printSimpleFolder(node, builder, indent);
            }

        }
    }

    private void printLoop(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {


        WhileLoop[] loops = node.getAnnotation(LOOP, WhileLoop[].class);
        for (WhileLoop loop : loops) {
            loop.parent = findParent(loop.sourcePath);
            loopMap.put(loop.sourcePath, loop);

            StringBuilderUtils.println("-- LOOP FROM " + loop.sourcePath + " TO " + node.getPath() + ":", builder, node.getLevel() + indent);
            StringBuilderUtils.println("DECLARE " + loop.variable + " REFERENCE TO " + getAssignment(loop, inputRootVariable) + ";", builder, node.getLevel() + indent);
            StringBuilderUtils.println(loop.name + " : WHILE LASTMOVE(" + loop.variable + ") DO", builder, node.getLevel() + indent);
            StringBuilderUtils.println(builder);

            int offset = 1;
            if (node.getAnnotation(CONDITION) != null) {
                String condition = node.getAnnotation(CONDITION, String.class);
                StringBuilderUtils.println("IF " + condition + " THEN", builder, node.getLevel() + indent + 1);
                StringBuilderUtils.println(builder);
                offset ++;
            }


            StringBuilderUtils.println("-- " + node.getPath(), builder, node.getLevel() + indent + offset);
            StringBuilderUtils.println("DECLARE " + node.getAlias() + " REFERENCE TO " + node.getParent().getAlias() + ";", builder, node.getLevel() + indent + offset);
            StringBuilderUtils.println("CREATE LASTCHILD OF " + node.getParent().getAlias() + " AS " + node.getAlias() + " TYPE XMLNSC.Folder NAME '" + getFullName(node) + "';"
                    , builder, node.getLevel() + indent + offset);
            StringBuilderUtils.println(builder);

            for (XmlSchemaBase.MappingNode child : node.getChildren()) {
                if (inLoop(child, loop)) {
                    printNode(child, builder, indent + offset);
                }
            }

            if (node.getAnnotation(CONDITION) != null) {
                StringBuilderUtils.println("END IF;", builder, node.getLevel() + indent + 1);
                StringBuilderUtils.println(builder);
            }

            StringBuilderUtils.println("MOVE " + loop.variable + " NEXTSIBLING;", builder, node.getLevel() + indent);
            StringBuilderUtils.println("END WHILE " + loop.name + ";", builder, node.getLevel() + indent);
            StringBuilderUtils.println(builder);
        }
    }

    private void printBlock(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {

        StringBuilderUtils.println("-- " + node.getPath(), builder, node.getLevel() + indent);
        StringBuilderUtils.println("DECLARE " + node.getAlias() + " REFERENCE TO " + node.getParent().getAlias() + ";", builder, node.getLevel() + indent);
        StringBuilderUtils.println("CREATE LASTCHILD OF " + node.getParent().getAlias() + " AS " + node.getAlias() + " TYPE XMLNSC.Folder NAME '" + getFullName(node) + "';"
                , builder, node.getLevel() + indent);
        StringBuilderUtils.println(builder);

        String[] lines = node.getAnnotation(BLOCK, String[].class);
        for(String line: lines) {
            String ln = line.trim();
            int offset = 0;
            while (ln.startsWith("+")) {
                ln = ln.substring(1).trim();
                offset ++;
            }
            StringBuilderUtils.println(ln, builder, node.getLevel() + indent + offset);
        }
        StringBuilderUtils.println(builder);

    }

    private void printSimpleFolder(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
        if (node.getParent() != null) {
            StringBuilderUtils.println("-- " + node.getPath(), builder, node.getLevel() + indent);
            StringBuilderUtils.println("DECLARE " + node.getAlias() + " REFERENCE TO " + node.getParent().getAlias() + ";", builder, node.getLevel() + indent);
            StringBuilderUtils.println("CREATE LASTCHILD OF " + node.getParent().getAlias() + " AS " + node.getAlias() + " TYPE XMLNSC.Folder NAME '" + getFullName(node) + "';"
                    , builder, node.getLevel() + indent);
            StringBuilderUtils.println(builder);
        }

        node.getChildren().forEach(n -> {
            printNode(n, builder, indent);
        });
    }

    private void printAssignment(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
        Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
        if (mapping != null && mapping.assignment != null) {
            String assignment = getAssignment(mapping, inputRootVariable);
            StringBuilderUtils.println("-- " + node.getPath(), builder, node.getLevel() + indent);
            if (XmlSchemaBase.NodeType.Attribute.equals(node.getNodeType())) {
                // Attribute:
                StringBuilderUtils.println("SET " + node.getParent().getAlias() + ".(XMLNSC.Attribute)" + getFullName(node) + " = " + assignment + ";", builder, node.getLevel() + indent);

            } else {
                // Field:
                StringBuilderUtils.println("SET " + node.getParent().getAlias() + ".(XMLNSC.Field)" + getFullName(node) + " = " + assignment + ";", builder, node.getLevel() + indent);

            }

            StringBuilderUtils.println(builder);
        }
    }

    private WhileLoop findParent(String path) {
        String token = path;
        int index = token.lastIndexOf('/');
        while (index > 0) {
            token = token.substring(0, index);
            if (loopMap.containsKey(token)) {
                return loopMap.get(token);
            }
            index = token.lastIndexOf('/');
        }

        return null;
    }

    private String getFullName(XmlSchemaBase.MappingNode node) {
        String fullName = node.getName();
        if (node.getNamespaceURI() != null && node.getNamespaceURI().equals(URI)) {
            fullName = "Abs:" + fullName;
        }

        return fullName;
    }

    private String getAssignment(Mapping mapping, String inputRootVariable) {
        if (mapping == null) {
            return null;
        }

        String assignment = "'???'";
        if (mapping.assignment != null) {
            assignment = mapping.assignment;
            if (assignment.contains(INPUT_ROOT)) {
                assignment = assignment.replace(INPUT_ROOT, inputRootVariable + ".");
            }

        } else if (mapping.sourcePath != null) {
            String path = mapping.sourcePath;
            assignment = inputRootVariable + "." + path.replaceAll("/", "\\.");
        }

        return assignment;
    }

    private String getAssignment(WhileLoop wl, String inputRoot) {
        if (wl.parent == null) {
            return inputRoot + "." + wl.sourcePath.replace("[*]", "/Item").replaceAll("/", "\\.");

        } else {
            WhileLoop parent = wl.parent;
            String path = wl.sourcePath.substring(parent.sourcePath.length() + 1);
            return wl.parent.variable + "." + path.replace("[*]", "/Item").replaceAll("/", "\\.");

        }

    }

    private boolean isMapped(XmlSchemaBase.MappingNode node) {
        if (XmlSchemaBase.NodeType.Folder.equals(node.getNodeType())) {
            if (node.getAnnotation(MAPPED) != null
                    || node.getAnnotation(MAPPING) != null
                    || node.getAnnotation(LOOP) != null
                    || node.getAnnotation(BLOCK) != null) {

                return true;
            }

        } else {
            return node.getAnnotation(MAPPING) != null;
        }

        return false;
    }

    private boolean inLoop(XmlSchemaBase.MappingNode node, WhileLoop loop) {
        String source = loop.sourcePath + "/";

        if (node.getAnnotation(MAPPING) != null) {
            Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
            return mapping.sourcePath != null && mapping.sourcePath.startsWith(source);

        } else if (XmlSchemaBase.NodeType.Folder.equals(node.getNodeType())) {
            for (XmlSchemaBase.MappingNode child : node.getChildren()) {
                if (inLoop(child, loop)) {
                    return true;
                }
            }
        }

        return false;
    }
}
