package soya.framework.tools.xmlbeans;

import soya.framework.tools.util.StringBuilderUtils;

import java.util.*;

public class XmlConstructEsqlRenderer extends XmlConstructTree implements IntegrationApplicationFeature {

    public static final String URI = "http://collab.safeway.com/it/architecture/info/default.aspx";
    public static final String DOCUMENT_ROOT = "xmlDocRoot";

    private String brokerSchema;
    private String moduleName;

    private String inputRootVariable = "_inputRoot";
    private String inputRootReference;

    private XmlSchemaBase base;
    private Map<String, WhileLoop> loopMap = new LinkedHashMap<>();
    private Set<Procedure> procedures = new LinkedHashSet<>();

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

        printProcedures(builder);

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

    protected void printNode(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
        if (!isMapped(node)) {
            return;
        }

        if (node.getAnnotation(PROCEDURE) != null) {
            printProcedureCall(node, builder, indent);

        } else if (node.getAnnotation(MAPPING) != null && node.getAnnotation(MAPPING, Mapping.class).assignment != null) {
            printAssignment(node, builder, indent);

        } else if (node.getAnnotation(BLOCK) != null) {
            printBlock(node, builder, indent);

        } else if (node.getAnnotation(CONSTRUCTION) != null) {
            printConstructions(node, builder, indent);

        } else if (node.getAnnotation(LOOP) != null) {
            printLoop(node, builder, indent);

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

    private void printProcedureCall(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {

        Procedure procedure = node.getAnnotation(PROCEDURE, Procedure.class);
        procedures.add(procedure);

        StringBuilderUtils.println("-- " + node.getPath(), builder, node.getLevel() + indent);

        // FIXME:
        StringBuilderUtils.println("DECLARE " + node.getAlias() + " REFERENCE TO " + node.getParent().getAlias() + ";", builder, node.getLevel() + indent);
        StringBuilderUtils.println("CREATE LASTCHILD OF " + node.getParent().getAlias() + " AS " + node.getAlias() + " TYPE XMLNSC.Folder NAME '" + getFullName(node) + "';"
                , builder, node.getLevel() + indent);

        StringBuilderUtils.println(builder);
        StringBuilderUtils.println("CALL " + procedure.invocation() + ";", builder, node.getLevel() + indent);
        StringBuilderUtils.println(builder);

    }

    private void printProcedures(StringBuilder builder) {
        procedures.forEach(e -> {
            printProcedure(e, builder);
        });
    }

    private void printProcedure(Procedure procedure, StringBuilder builder) {

        StringBuilderUtils.println("CREATE PROCEDURE " + procedure.signature(), builder, 1);
        StringBuilderUtils.println("BEGIN", builder, 2);
        if (procedure.body != null) {
            StringBuilderUtils.println(decode(procedure.body), builder);
        }
        StringBuilderUtils.println(builder);
        StringBuilderUtils.println("END;", builder, 2);

        StringBuilderUtils.println(builder);

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
                offset++;
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

    private void printConstructions(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
        StringBuilderUtils.println("-- " + node.getPath(), builder, node.getLevel() + indent);
        StringBuilderUtils.println(builder);

        Construction[] constructions = node.getAnnotation(CONSTRUCTION, Construction[].class);
        for (int i = 0; i < constructions.length; i++) {
            Construction construction = constructions[i];
            String suffix = i == 0 ? "" : "" + i;
            if (construction.condition != null) {
                StringBuilderUtils.println("IF " + construction.condition + " THEN", builder, node.getLevel() + indent);
                printConstruction(construction, suffix, node, builder, indent + 1);
                StringBuilderUtils.println("END IF;", builder, node.getLevel() + indent);
                StringBuilderUtils.println(builder);

            } else {
                printConstruction(construction, suffix, node, builder, indent);
            }
        }
    }

    private void printConstruction(Construction construction, String suffix, XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
        StringBuilderUtils.println("-- FROM " + construction.sourcePath + ":", builder, node.getLevel() + indent);
        StringBuilderUtils.println("DECLARE " + construction.variable + " REFERENCE TO " + inputRootVariable + "." + construction.sourcePath.replaceAll("/", ".") + ";", builder, node.getLevel() + indent);
        StringBuilderUtils.println("DECLARE " + node.getAlias() + suffix + " REFERENCE TO " + node.getParent().getAlias() + ";", builder, node.getLevel() + indent);
        StringBuilderUtils.println("CREATE LASTCHILD OF " + node.getParent().getAlias() + " AS " + node.getAlias() + suffix + " TYPE XMLNSC.Folder NAME '" + getFullName(node) + "';"
                , builder, node.getLevel() + indent);

        String basePath = node.getPath() + "/";
        Map<String, XmlSchemaBase.MappingNode> map = new LinkedHashMap<>();
        Map<String, String> assignments = new HashMap<>();

        Map<String, String> jsonObject = construction.assignments;
        jsonObject.entrySet().forEach(e -> {
            String path = e.getKey();
            assignments.put(basePath + path, e.getValue());

            List paths = getAllPath(path);
            paths.forEach(p -> {
                String pp = basePath + p;
                map.put(pp, base.get(pp));
            });
        });

        map.entrySet().forEach(e -> {
            XmlSchemaBase.MappingNode n = e.getValue();

            StringBuilderUtils.println("-- " + n.getPath(), builder, n.getLevel() + indent);
            if (n.getNodeType().equals(XmlSchemaBase.NodeType.Folder)) {
                StringBuilderUtils.println("DECLARE " + n.getAlias() + suffix + " REFERENCE TO " + n.getParent().getAlias() + ";", builder, n.getLevel() + indent);
                StringBuilderUtils.println("CREATE LASTCHILD OF " + n.getParent().getAlias() + suffix + " AS " + n.getAlias() + suffix + " TYPE XMLNSC.Folder NAME '" + getFullName(n) + "';"
                        , builder, n.getLevel() + indent);

            } else if (n.getNodeType().equals(XmlSchemaBase.NodeType.Field)) {
                StringBuilderUtils.println("SET " + n.getParent().getAlias() + suffix + ".(XMLNSC.Field)" + getFullName(n) + " = " + assignments.get(n.getPath()) + ";", builder, n.getLevel() + indent);

            } else if (n.getNodeType().equals(XmlSchemaBase.NodeType.Attribute)) {
                StringBuilderUtils.println("SET " + n.getParent().getAlias() + suffix + ".(XMLNSC.Attribute)" + getFullName(n) + " = " + assignments.get(n.getPath()) + ";", builder, n.getLevel() + indent);

            }
            StringBuilderUtils.println(builder);
        });

        StringBuilderUtils.println(builder);
    }

    private void printBlock(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {

        StringBuilderUtils.println("-- " + node.getPath(), builder, node.getLevel() + indent);
        StringBuilderUtils.println("DECLARE " + node.getAlias() + " REFERENCE TO " + node.getParent().getAlias() + ";", builder, node.getLevel() + indent);
        StringBuilderUtils.println("CREATE LASTCHILD OF " + node.getParent().getAlias() + " AS " + node.getAlias() + " TYPE XMLNSC.Folder NAME '" + getFullName(node) + "';"
                , builder, node.getLevel() + indent);
        StringBuilderUtils.println(builder);

        String[] lines = node.getAnnotation(BLOCK, String[].class);
        for (String line : lines) {
            String ln = line.trim();
            int offset = 0;
            while (ln.startsWith("+")) {
                ln = ln.substring(1).trim();
                offset++;
            }

            if (ln.equals("")) {
                StringBuilderUtils.println(builder);
            } else {
                StringBuilderUtils.println(ln, builder, node.getLevel() + indent + offset);
            }
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

    private List<String> getAllPath(String path) {
        List<String> list = new ArrayList();
        list.add(path);
        String token = path;
        while (token.contains("/")) {
            token = token.substring(0, token.lastIndexOf("/"));
            list.add(0, token);
        }

        return list;
    }

    private String decode(String contents) {
        return new String(Base64.getDecoder().decode(contents.getBytes()));
    }
}
