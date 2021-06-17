package soya.framework.tao.edis;

import org.apache.xmlbeans.SchemaTypeSystem;
import org.checkerframework.checker.units.qual.A;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.KnowledgeTreeNode;
import soya.framework.tao.TreeNode;
import soya.framework.tao.util.StringBuilderUtils;
import soya.framework.tao.xs.XsNode;

import java.math.BigInteger;
import java.util.*;

public abstract class EsqlRenderer extends EdisRenderer {

    public static final String URI = "http://collab.safeway.com/it/architecture/info/default.aspx";
    //public static final String DOCUMENT_ROOT = "xmlDocRoot";

    public static String[] INDEX = {"i", "j", "k", "l", "m", "n"};

    protected String brokerSchema;
    protected String moduleName;

    protected String inputRootVariable = "_inputRoot";
    protected String inputRootReference;

    protected KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase;

    protected int currentArrayDepth = -1;
    protected Stack<Array> arrayStack = new Stack<>();

    //protected Set<Procedure> procedures = new LinkedHashSet<>();
    //protected Map<String, Object> constructions = new LinkedHashMap<>();

    //protected Map<String, ConstructNode> constructNodeMap = new LinkedHashMap<>();

    public EsqlRenderer brokerSchema(String brokerSchema) {
        this.brokerSchema = brokerSchema;
        return this;
    }

    public EsqlRenderer moduleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public EsqlRenderer inputRootVariable(String inputRootVariable) {
        this.inputRootVariable = inputRootVariable;
        return this;
    }

    public EsqlRenderer inputRootReference(String inputRootReference) {
        this.inputRootReference = inputRootReference;
        return this;
    }

    protected void begin(StringBuilder builder, int indent) {
        for (int i = 0; i < indent; i++) {
            builder.append("\t");
        }

        builder.append("BEGIN").append("\n");
    }

    protected void declareInputRoot(StringBuilder builder) {
        StringBuilderUtils.println("-- Declare Input Message Root", builder, 2);
        StringBuilderUtils.println("DECLARE " + inputRootVariable + " REFERENCE TO " + inputRootReference + ";", builder, 2);
        StringBuilderUtils.println(builder);
    }

    protected void declareNamespace(StringBuilder builder) {
        StringBuilderUtils.println("-- Declare Namespace", builder, 1);
        StringBuilderUtils.println("DECLARE " + "Abs" + " NAMESPACE " + "'https://collab.safeway.com/it/architecture/info/default.aspx'" + ";", builder, 1);
        StringBuilderUtils.println(builder);
    }

    protected void printNode(KnowledgeTreeNode<XsNode> node, StringBuilder builder, int indent) {
        if (node.getAnnotation(NAMESPACE_ASSIGNMENT) != null) {
            printAssignment(node, builder, indent);

        } else if (node.getAnnotation(NAMESPACE_CONSTRUCTION) != null) {
            Construction construction = node.getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);

            if (BigInteger.ONE.equals(node.origin().getMaxOccurs())) {
                if (construction.getFunctions().size() == 0) {
                    printSimpleFolder(node, builder, indent);

                } else {
                    StringBuilderUtils.println("-- " + node.getPath(), builder, construction.getLevel() + indent);
                    StringBuilderUtils.println("-- TODO: ", builder, construction.getLevel() + indent);
                    StringBuilderUtils.println(builder);
                }

            } else {
                printArrayFolder(node, builder, indent);
            }

        } else {
            return;

        }
    }

    protected void printLoopFunction(Function function, KnowledgeTreeNode<XsNode> node, StringBuilder builder, int indent) {

        Construction parentConstruction = ((KnowledgeTreeNode<XsNode>) node.getParent()).getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);
        Construction construction = node.getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);

        String loopName = function.parameters[0];
        String srcPath = function.parameters[1];
        String variable = function.parameters[2];

        String source = srcPath;
        if (source.endsWith("[*]")) {
            source = source.substring(0, source.length() - 3) + ".Item";
        }

        if (source.startsWith("$.")) {
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


        StringBuilderUtils.println("MOVE " + variable + " NEXTSIBLING;", builder, construction.getLevel() + indent);
        StringBuilderUtils.println("END WHILE " + loopName + ";", builder, construction.getLevel() + indent);
        StringBuilderUtils.println(builder);

    }

    protected void printArrayFolder(KnowledgeTreeNode<XsNode> node, StringBuilder builder, int indent) {
        currentArrayDepth++;
        String indexVar = INDEX[currentArrayDepth];

        Construction construction = node.getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);
        Construction parentConstruction = ((KnowledgeTreeNode<XsNode>) node.getParent()).getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);

        StringBuilderUtils.println("-- " + node.getPath(), builder, construction.getLevel() + indent);
        StringBuilderUtils.println("CREATE FIELD " + parentConstruction.getAlias() + "." + node.getName() + " IDENTITY (JSON.Array)" + node.getName() + ";"
                , builder, construction.getLevel() + indent);
        StringBuilderUtils.println("DECLARE " + "arr_" + construction.getAlias() + " REFERENCE TO " + parentConstruction.getAlias() + "." + node.getName() + ";", builder, construction.getLevel() + indent);
        StringBuilderUtils.println(builder);

        construction.getFunctions().forEach(e -> {

            if (FUNCTION_ARRAY.equals(e.name) && e.parameters.length == 3) {
                Array array = new Array();
                array.name = e.parameters[0];
                array.var = e.parameters[1];
                array.assignment = e.parameters[2];
                boolean print = false;
                if (array.assignment.startsWith("$.")) {
                    print = true;

                } else {
                    String parentVar = array.assignment.substring(0, array.assignment.indexOf("."));
                    Iterator<Array> iterator = arrayStack.iterator();
                    while (iterator.hasNext()) {
                        if (array.var.equals(iterator.next().var)) {
                            print = true;
                            break;
                        }
                    }
                }

                if (print) {
                    arrayStack.push(array);

                    String assign = array.assignment.replace("[*]", ".Item");
                    if (assign.startsWith("$.")) {
                        assign = inputRootVariable + assign.substring(1);
                    }
                    StringBuilderUtils.println("DECLARE " + array.var + " REFERENCE TO " + assign + ";",
                            builder, construction.getLevel() + indent);
                    StringBuilderUtils.println(builder);

                    StringBuilderUtils.println("SET " + indexVar + " = 1;", builder, construction.getLevel() + indent);
                    StringBuilderUtils.println(array.name + " : WHILE LASTMOVE(" + array.var + ") DO", builder, construction.getLevel() + indent);

                    StringBuilderUtils.println("DECLARE " + construction.getAlias() + " REFERENCE TO " + "arr_" + parentConstruction.getAlias() + "." + node.getName() + ".Item[" + indexVar + "];", builder, construction.getLevel() + indent + 1);
                    StringBuilderUtils.println(builder);

                    node.getChildren().forEach(n -> {
                        printNode((KnowledgeTreeNode<XsNode>) n, builder, indent + 1);
                    });

                    StringBuilderUtils.println("SET " + indexVar + " = " + indexVar + " + 1;", builder, construction.getLevel() + indent);
                    StringBuilderUtils.println("MOVE " + array.var + " NEXTSIBLING;", builder, construction.getLevel() + indent);
                    StringBuilderUtils.println("END WHILE " + array.name + ";", builder, construction.getLevel() + indent);

                    StringBuilderUtils.println(builder);

                    arrayStack.pop();

                }
            }
        });

        currentArrayDepth--;

    }

    protected void printSimpleFolder(KnowledgeTreeNode<XsNode> node, StringBuilder builder, int indent) {
        Construction construction = node.getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);
        if (node.getParent() != null) {
            Construction parentConstruction = ((KnowledgeTreeNode<XsNode>) node.getParent()).getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);
            StringBuilderUtils.println("-- " + node.getPath(), builder, construction.getLevel() + indent);
            StringBuilderUtils.println("CREATE FIELD " + parentConstruction.getAlias() + "." + node.getName() + ";"
                    , builder, construction.getLevel() + indent);
            StringBuilderUtils.println("DECLARE " + construction.getAlias() + " REFERENCE TO " + parentConstruction.getAlias() + "." + node.getName() + ";", builder, construction.getLevel() + indent);

            StringBuilderUtils.println(builder);
        }

        node.getChildren().forEach(n -> {
            printNode((KnowledgeTreeNode<XsNode>) n, builder, indent);
        });
    }

    protected void printAssignment(KnowledgeTreeNode<XsNode> node, StringBuilder builder, int indent) {
        Assignment assignment = node.getAnnotation(NAMESPACE_ASSIGNMENT, Assignment.class);
        Construction construction = ((KnowledgeTreeNode<XsNode>) node.getParent()).getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);

        //String assignment = getAssignment(mapping, inputRootVariable);
        if (assignment != null && !assignment.functions.isEmpty()) {
            boolean print = true;

            System.out.println("================= " + assignment.functions);
/*

            if () {
                // FIXME

            } else if (assignment.source.startsWith("$.")) {
                print = true;

            } else if (assignment.source.indexOf(".") > 0) {
                String parentVar = assignment.source.substring(0, assignment.source.indexOf("."));
                Iterator<Array> iterator = arrayStack.iterator();
                while (iterator.hasNext()) {
                    if (parentVar.equals(iterator.next().var)) {
                        print = true;
                        break;
                    }
                }
            } else {
                print = true;
            }
*/

            if (print) {
                StringBuilderUtils.println("-- " + node.getPath(), builder, construction.getLevel() + indent + 1);
                if (assignment.functions.size() == 1) {
                    Function function = assignment.getFirst();
                    StringBuilderUtils.println("SET " + construction.getAlias()
                            + "." + node.origin().getName().getLocalPart()
                            + " = " + getAssignment(function) + ";", builder, construction.getLevel() + indent + 1);
                }
                StringBuilderUtils.println(builder);

            }
        }
    }

    protected String getAssignment(Function function) {
        if (FUNCTION_DEFAULT.equalsIgnoreCase(function.name)) {
            return function.parameters[0];

        } else if (FUNCTION_ASSIGN.equalsIgnoreCase(function.name)) {
            String assignment = function.parameters[0];
            if (assignment.startsWith("$.")) {
                assignment = inputRootVariable + assignment.substring(1);
            }
            return assignment;

        } else if (FUNCTION_LOOP_ASSIGN.equals(function.name)) {
            return function.parameters[1];
        }

        return "XXX";
    }

    protected String getFullName(KnowledgeTreeNode<XsNode> node) {
        String fullName = node.getName();
        if (fullName.startsWith("@")) {
            fullName = fullName.substring(1);
        }
        String uri = node.origin().getName().getNamespaceURI();
        if (uri != null && uri.equals(URI)) {
            fullName = "Abs:" + fullName;
        }

        return fullName;
    }

/*
    protected void printConstruct(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {

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

    protected void printLoop(WhileLoop loop, XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {

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

    protected void printConstructor(Constructor constructor, XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
        constructions.put(constructor.sourcePath, constructor);
        constructNodeMap.put(constructor.variable, constructor);
    }

    protected void printProcedureCall(Procedure procedure, XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {

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

    protected void printProcedures(StringBuilder builder) {
        procedures.forEach(e -> {
            printProcedure(e, builder);
        });
    }

    protected void printProcedure(Procedure procedure, StringBuilder builder) {

        StringBuilderUtils.println("CREATE PROCEDURE " + procedure.signature(), builder, 1);
        StringBuilderUtils.println("BEGIN", builder, 2);
        if (procedure.body != null) {
            StringBuilderUtils.println(decode(procedure.body), builder);
        }
        StringBuilderUtils.println(builder);
        StringBuilderUtils.println("END;", builder, 2);

        StringBuilderUtils.println(builder);

    }

    protected void printConstructions(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
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

    protected void sort(XmlSchemaBase.MappingNode node, Map<String, Constructor> constructionMap) {
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

    protected Constructor createConstruction(Function[] functions) {
        Constructor construction = null;


        return construction;
    }

    protected void printConstruction(Constructor construction, String suffix, XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {

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

    protected void printBlock(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {

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

    protected void printAssignment(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
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

    protected WhileLoop findParent(String path) {
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

    protected String getFullName(XmlSchemaBase.MappingNode node) {
        String fullName = node.getName();
        if (node.getNamespaceURI() != null && node.getNamespaceURI().equals(URI)) {
            fullName = "Abs:" + fullName;
        }

        return fullName;
    }

    protected String getAssignment(Mapping mapping, String inputRootVariable) {
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

    protected String getAssignment(WhileLoop wl, String inputRoot) {
        if (wl.parent == null) {
            return inputRoot + "." + wl.sourcePath.replace("[*]", "/Item").replaceAll("/", "\\.");

        } else {
            WhileLoop parent = wl.parent;
            String path = wl.sourcePath.substring(parent.sourcePath.length() + 1);
            return wl.parent.variable + "." + path.replace("[*]", "/Item").replaceAll("/", "\\.");

        }

    }
*/


    protected List<String> getAllPath(String path, String base) {
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
