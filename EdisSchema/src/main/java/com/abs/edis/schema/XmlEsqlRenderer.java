package com.abs.edis.schema;

import com.google.gson.JsonObject;
import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.KnowledgeTreeNode;
import soya.framework.tao.T123W;
import soya.framework.tao.TreeNode;
import soya.framework.tao.util.StringBuilderUtils;
import soya.framework.tao.xs.XsNode;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class XmlEsqlRenderer extends EdisRenderer {

    public static final String URI = "http://collab.safeway.com/it/architecture/info/default.aspx";
    //public static final String DOCUMENT_ROOT = "xmlDocRoot";

    private String brokerSchema;
    private String moduleName;

    private String inputRootVariable = "_inputRoot";
    private String inputRootReference;

    private KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase;

    //private Set<Procedure> procedures = new LinkedHashSet<>();
    //private Map<String, Object> constructions = new LinkedHashMap<>();

    //private Map<String, ConstructNode> constructNodeMap = new LinkedHashMap<>();

    public XmlEsqlRenderer brokerSchema(String brokerSchema) {
        this.brokerSchema = brokerSchema;
        return this;
    }

    public XmlEsqlRenderer moduleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public XmlEsqlRenderer inputRootVariable(String inputRootVariable) {
        this.inputRootVariable = inputRootVariable;
        return this;
    }

    public XmlEsqlRenderer inputRootReference(String inputRootReference) {
        this.inputRootReference = inputRootReference;
        return this;
    }

    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase) throws T123W.FlowExecutionException {
        this.knowledgeBase = knowledgeBase;

        StringBuilder builder = new StringBuilder();
        if (brokerSchema != null && brokerSchema.trim().length() > 0) {
            builder.append("BROKER SCHEMA ").append(brokerSchema.trim()).append("\n\n");
        }

        builder.append("CREATE COMPUTE MODULE ").append(moduleName);
        StringBuilderUtils.println(builder, 2);

        // UDP:
        StringBuilderUtils.println("-- Declare UDPs", builder, 1);
        JsonObject variables = knowledgeBase.getAnnotation(NAMESPACE_GLOBAL_VARIABLE, JsonObject.class);
        if(variables != null) {
            variables.entrySet().forEach(e -> {
                StringBuilderUtils.println("DECLARE " + e.getKey() + " EXTERNAL CHARACTER " + e.getValue().getAsString() + ";", builder, 1);
            });
        }
        StringBuilderUtils.println(builder);

        // Namespace
        declareNamespace(builder);

        StringBuilderUtils.println("CREATE FUNCTION Main() RETURNS BOOLEAN", builder, 1);
        begin(builder, 1);

        declareInputRoot(builder);

        // Declare Output Domain
        Construction rootConstruction = knowledgeBase.root().getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);
        StringBuilderUtils.indent(builder, 2);
        builder.append("-- Declare Output Message Root").append("\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("CREATE LASTCHILD OF OutputRoot DOMAIN ").append("'XMLNSC'").append(";\n\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("DECLARE ").append(rootConstruction.getAlias()).append(" REFERENCE TO OutputRoot.XMLNSC.").append(knowledgeBase.root().getName()).append(";\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("CREATE LASTCHILD OF OutputRoot.").append("XMLNSC AS ").append(rootConstruction.getAlias()).append(" TYPE XMLNSC.Folder NAME '").append(knowledgeBase.root().getName()).append("'").append(";\n");
        StringBuilderUtils.println("SET OutputRoot.XMLNSC." + knowledgeBase.root().getName() + ".(XMLNSC.NamespaceDecl)xmlns:Abs=Abs;",
                builder, 2);
        StringBuilderUtils.println(builder);

        printNode(knowledgeBase.root(), builder, 2);

        StringBuilderUtils.println("RETURN TRUE;", builder, 2);
        StringBuilderUtils.println("END;", builder, 1);
        StringBuilderUtils.println(builder);

        //printProcedures(builder);

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

    private void printNode(KnowledgeTreeNode<XsNode> node, StringBuilder builder, int indent) {
        if (node.getAnnotation(NAMESPACE_CONSTRUCTION) != null) {
            Construction construction = node.getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);
            if (construction.getFunctions().size() == 0) {
                printSimpleFolder(node, builder, indent);

            } else {
                construction.getFunctions().forEach(e -> {
                    printFunction(e, node, builder, indent);
                });
            }

        } else if (node.getAnnotation(NAMESPACE_ASSIGNMENT) != null) {
            printAssignment(node, builder, indent);

        } else {
            return;

        }
    }

    private void printFunction(Function function, KnowledgeTreeNode<XsNode> node, StringBuilder builder, int indent) {
        if(FUNCTION_LOOP.equals(function.name)) {
            printLoopFunction(function, node, builder, indent);
        }
    }

    private void printLoopFunction(Function function, KnowledgeTreeNode<XsNode> node, StringBuilder builder, int indent) {

        Construction parentConstruction = ((KnowledgeTreeNode<XsNode>)node.getParent()).getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);
        Construction construction = node.getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);
        String loopName = function.parameters[0];
        String srcPath = function.parameters[1];
        String variable = function.parameters[2];

        String source = srcPath;
        if(source.endsWith("[*]")) {
            source = source.substring(0, source.length() - 3) + ".Item";
        }

        if(source.startsWith("$.")) {
            source = inputRootVariable + source.substring(1);
        }

        StringBuilderUtils.println("-- LOOP FROM " + srcPath + " TO " + node.getPath() + ":", builder, construction.getLevel() + indent);
        StringBuilderUtils.println("DECLARE " + variable + " REFERENCE TO " + source + ";", builder, construction.getLevel() + indent);
        StringBuilderUtils.println(loopName + " : WHILE LASTMOVE(" + variable + ") DO", builder, construction.getLevel() + indent);
        StringBuilderUtils.println(builder);

        int offset = 1;

        StringBuilderUtils.println("-- " + node.getPath(), builder, construction.getLevel() + indent + offset);
        StringBuilderUtils.println("DECLARE " + construction.getAlias() + " REFERENCE TO " + parentConstruction.getAlias() + ";", builder, construction.getLevel() + indent + offset);
        StringBuilderUtils.println("CREATE LASTCHILD OF " + parentConstruction.getAlias() + " AS " + construction.getAlias() + " TYPE XMLNSC.Folder NAME '" + getFullName(node) + "';"
                , builder, construction.getLevel() + indent + offset);
        StringBuilderUtils.println(builder);

        for (TreeNode child : node.getChildren()) {
            System.out.println("============== " + node.getPath());
            printNode((KnowledgeTreeNode<XsNode>) child, builder, indent + offset);
        }
 /*

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
            printNode(child, builder, indent + offset);
        }

        if (node.getAnnotation(CONDITION) != null) {
            StringBuilderUtils.println("END IF;", builder, node.getLevel() + indent + 1);
            StringBuilderUtils.println(builder);
        }
*/

        StringBuilderUtils.println("MOVE " + variable + " NEXTSIBLING;", builder, construction.getLevel() + indent);
        StringBuilderUtils.println("END WHILE " + loopName + ";", builder, construction.getLevel() + indent);
        StringBuilderUtils.println(builder);

    }



/*

        if (node.getAnnotation(MAPPING) != null && node.getAnnotation(MAPPING, Mapping.class).assignment != null) {
            printAssignment(node, builder, indent);

        } else if (node.getAnnotation(CONSTRUCT) != null) {
            printConstruct(node, builder, indent);

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
*/

    private void printSimpleFolder(KnowledgeTreeNode<XsNode> node, StringBuilder builder, int indent) {
        Construction construction = node.getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);
        if (node.getParent() != null) {
            Construction parentConstruction = ((KnowledgeTreeNode<XsNode>) node.getParent()).getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);
            StringBuilderUtils.println("-- " + node.getPath(), builder, construction.getLevel() + indent);
            StringBuilderUtils.println("DECLARE " + construction.getAlias() + " REFERENCE TO " + parentConstruction.getAlias() + ";", builder, construction.getLevel() + indent);
            StringBuilderUtils.println("CREATE LASTCHILD OF " + parentConstruction.getAlias() + " AS " + construction.getAlias() + " TYPE XMLNSC.Folder NAME '" + getFullName(node) + "';"
                    , builder, construction.getLevel() + indent);
            StringBuilderUtils.println(builder);
        }

        node.getChildren().forEach(n -> {
            printNode((KnowledgeTreeNode<XsNode>) n, builder, indent);
        });
    }

    private void printAssignment(KnowledgeTreeNode<XsNode> node, StringBuilder builder, int indent) {
        Assignment assignment = node.getAnnotation(NAMESPACE_ASSIGNMENT, Assignment.class);
        Construction construction = ((KnowledgeTreeNode<XsNode>)node.getParent()).getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);

        //String assignment = getAssignment(mapping, inputRootVariable);
        if (assignment != null) {
            StringBuilderUtils.println("-- " + node.getPath(), builder, construction.getLevel() + indent + 1);
            if(assignment.functions.size() == 1) {
                Function function = assignment.getFirst();
                StringBuilderUtils.println("SET " + construction.getAlias()
                        + ".(XMLNSC." + node.origin().getNodeType() + ")" + getFullName(node)
                        + " = " + getAssignment(function) + ";", builder, construction.getLevel() + indent + 1);
            }
            StringBuilderUtils.println(builder);
        }
    }

    private String getAssignment(Function function) {
        if(FUNCTION_DEFAULT.equalsIgnoreCase(function.name)) {
            return function.parameters[0];

        } else if(FUNCTION_ASSIGN.equalsIgnoreCase(function.name)) {
            String assignment = function.parameters[0];
            if(assignment.startsWith("$.")) {
                assignment = inputRootVariable + assignment.substring(1);
            }
            return assignment;

        } else if(FUNCTION_LOOP_ASSIGN.equals(function.name)) {
            System.out.println("==================== function: " + function.toString());
            return function.parameters[1];
        }

        return "XXX";
    }

    private String getFullName(KnowledgeTreeNode<XsNode> node) {
        String fullName = node.getName();
        if(fullName.startsWith("@")) {
            fullName = fullName.substring(1);
        }
        String uri = node.origin().getName().getNamespaceURI();
        if (uri != null && uri.equals(URI)) {
            fullName = "Abs:" + fullName;
        }

        return fullName;
    }

/*
    private void printConstruct(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {

        Construct construct = node.getAnnotation(CONSTRUCT, Construct.class);

        if (construct.procedure != null) {
            printProcedureCall(construct.procedure, node, builder, indent);

        } else {
            construct.loops.forEach(e -> {
                printLoop(e, node, builder, indent);
            });

            construct.constructors.forEach(e -> {
                printConstructor(e, node, builder, indent);

            });

        }
    }

    private void printLoop(WhileLoop loop, XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {

        constructions.put(loop.sourcePath, loop);

        constructNodeMap.put(loop.variable, loop);

        loop.parent = findParent(loop.sourcePath);

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
            printNode(child, builder, indent + offset);
        }

        if (node.getAnnotation(CONDITION) != null) {
            StringBuilderUtils.println("END IF;", builder, node.getLevel() + indent + 1);
            StringBuilderUtils.println(builder);
        }

        StringBuilderUtils.println("MOVE " + loop.variable + " NEXTSIBLING;", builder, node.getLevel() + indent);
        StringBuilderUtils.println("END WHILE " + loop.name + ";", builder, node.getLevel() + indent);
        StringBuilderUtils.println(builder);

    }

    private void printConstructor(Constructor constructor, XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
        constructions.put(constructor.sourcePath, constructor);
        constructNodeMap.put(constructor.variable, constructor);
    }

    private void printProcedureCall(Procedure procedure, XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {

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

    private void printConstructions(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
        Map<String, Constructor> constructionMap = new LinkedHashMap<>();
        String exp = node.getAnnotation(CONSTRUCTION, String.class);
        String[] definitions = exp.split(".end\\(\\)");
        for (int i = 0; i < definitions.length; i++) {
            Function[] functions = Function.fromString(definitions[i]);
            Constructor construction = createConstruction(functions);
            if (construction != null) {
                constructionMap.put(construction.name, construction);
            }
        }

        node.getChildren().forEach(c -> {
            sort(c, constructionMap);
        });

        List<Constructor> list = new ArrayList<>(constructionMap.values());
        for (int i = 0; i < list.size(); i++) {
            String suffix = i == 0 ? "" : "" + i;
            Constructor construction = list.get(i);
            printConstruction(construction, suffix, node, builder, indent);
        }

    }

    private void sort(XmlSchemaBase.MappingNode node, Map<String, Constructor> constructionMap) {
        if (node.getAnnotation(MAPPING) != null) {
            Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
            String assignment = mapping.assignment;
            if (assignment != null && assignment.startsWith("for(")) {
                String[] arr = assignment.split("end\\(\\)");
                for (String exp : arr) {
                    Function[] assignments = Function.fromString(exp);
                    String dest = assignments[0].getArguments()[0];
                    String assign = assignments[1].getArguments()[0];

                    if (constructionMap.containsKey(dest)) {
                        constructionMap.get(dest).assignments.put(node.getPath(), assign);
                    }
                }

            }
        }
    }

    private Constructor createConstruction(Function[] functions) {
        Constructor construction = null;


        return construction;
    }

    private void printConstruction(Constructor construction, String suffix, XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {

        StringBuilderUtils.println("-- Construct " + node.getName() + " FROM " + construction.sourcePath + ":", builder, node.getLevel() + indent);
        StringBuilderUtils.println("DECLARE " + construction.variable + " REFERENCE TO " + inputRootVariable + "." + construction.sourcePath.replaceAll("/", ".") + ";", builder, node.getLevel() + indent);
        StringBuilderUtils.println(builder);


        Map<String, XmlSchemaBase.MappingNode> map = new LinkedHashMap<>();
        Map<String, String> assignments = construction.assignments;
        assignments.entrySet().forEach(e -> {
            String path = e.getKey();
            List<String> paths = getAllPath(path, node.getPath());
            paths.forEach(p -> {
                map.put(p, base.get(p));
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

    private void printAssignment(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
        Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
        String assignment = getAssignment(mapping, inputRootVariable);

        if (assignment != null) {

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
            if (constructions.containsKey(token)) {
                return (WhileLoop) constructions.get(token);
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
            if (assignment.contains(INPUT_ROOT + ".")) {
                assignment = assignment.replace(INPUT_ROOT + ".", inputRootVariable + ".");
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
*/


    private List<String> getAllPath(String path, String base) {
        List<String> list = new ArrayList();
        list.add(path);
        String token = path;
        while (!token.equals(base)) {
            token = token.substring(0, token.lastIndexOf("/"));
            list.add(0, token);
        }

        return list;
    }

    private String decode(String contents) {
        return new String(Base64.getDecoder().decode(contents.getBytes()));
    }


}
