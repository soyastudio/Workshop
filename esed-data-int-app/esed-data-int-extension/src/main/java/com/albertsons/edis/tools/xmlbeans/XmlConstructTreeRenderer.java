package com.albertsons.edis.tools.xmlbeans;

import soya.framework.tools.util.StringBuilderUtils;

import java.util.List;

public class XmlConstructTreeRenderer extends XmlConstructTree {

    public static final String ROOT_PREFIX = "root://";
    public static final String CREATE_PREFIX = "create://";
    public static final String ASSIGN_PREFIX = "assign://";
    public static final String CONSTRUCT_PREFIX = "construct://";
    public static final String LOOP_PREFIX = "loop://";
    public static final String CONSTRUCTOR_PREFIX = "constructor://";
    public static final String PROCEDURE_PREFIX = "procedure://";
    public static final String PARAM_PREFIX = "param://";

    @Override
    public String render(XmlSchemaBase base) {
        StringBuilder builder = new StringBuilder();
        XmlSchemaBase.MappingNode root = base.getRoot();
        printNode(root, builder, 0);

        return builder.toString();
    }

    protected void printNode(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
        if (!isMapped(node)) {
            return;
        }

        if (node.getNodeType().equals(XmlSchemaBase.NodeType.Folder)) {
            if (node.getAnnotation(CONSTRUCT) != null) {
                ConstructTree tree = new ConstructTree(node);
                Construct construct = tree.getConstruct();
                StringBuilderUtils.println(CONSTRUCT_PREFIX + node.getName() + ":" + " # " + node.getPath(), builder, node.getLevel() + indent);

                if (construct.procedure != null) {
                    Procedure procedure = construct.procedure;
                    StringBuilderUtils.println(PROCEDURE_PREFIX + procedure.name + ":", builder, node.getLevel() + indent + 1);
                    procedure.parameters.forEach(e -> {
                        StringBuilderUtils.println("- " + PARAM_PREFIX + e.name, builder, node.getLevel() + indent + 2);
                    });

                    StringBuilderUtils.println(builder);

                } else {
                    tree.getLoopTree().entrySet().forEach(e -> {
                        WhileLoop loop = e.getValue().getObject();
                        StringBuilderUtils.println("- " + LOOP_PREFIX + loop.name + ":", builder, node.getLevel() + indent + 1);
                        List<XmlSchemaBase.MappingNode> l = e.getValue().getNodes();
                        l.forEach(ln -> {
                            printNode(ln, builder, indent + 1);
                        });
                    });

                    tree.getConstructorTree().entrySet().forEach(e -> {
                        Constructor constructor = e.getValue().getObject();
                        StringBuilderUtils.println("- " + CONSTRUCTOR_PREFIX + constructor.name + ":", builder, node.getLevel() + indent + 1);
                        List<XmlSchemaBase.MappingNode> l = e.getValue().getNodes();
                        l.forEach(ln -> {
                            printNode(ln, builder, indent + 1);
                        });

                    });
                }
            } else {
                StringBuilderUtils.println(CREATE_PREFIX + node.getName() + ":" + " # " + node.getPath(), builder, node.getLevel() + indent);
                if (node.getAnnotation(MAPPING) != null) {
                    String assignment = getAssignment(node);
                    StringBuilderUtils.println("assignment: " + assignment, builder, node.getLevel() + indent + 1);
                    StringBuilderUtils.println(builder);

                } else {
                    for (XmlSchemaBase.MappingNode child : node.getChildren()) {
                        printNode(child, builder, indent);
                    }
                }
            }
        } else {
            StringBuilderUtils.println(ASSIGN_PREFIX + node.getName() + ":" + " # " + node.getPath(), builder, node.getLevel() + indent);
            String assignment = getAssignment(node);
            StringBuilderUtils.println("assignment: " + assignment, builder, node.getLevel() + indent + 1);
            StringBuilderUtils.println(builder);
        }
    }

    private String getAssignment(XmlSchemaBase.MappingNode node) {

        if (node.getAnnotation(MAPPING) == null) {
            return null;
        }

        Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
        if (mapping.assignment == null) {
            return "'???'";

        } else if (mapping.assignment.contains("'")) {
            return "\"" + mapping.assignment + "\"";

        } else {
            return mapping.assignment;
        }

    }
}
