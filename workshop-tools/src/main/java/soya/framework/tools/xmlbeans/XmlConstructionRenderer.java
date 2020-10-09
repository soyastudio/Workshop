package soya.framework.tools.xmlbeans;

import com.google.common.base.CaseFormat;
import soya.framework.tools.util.StringBuilderUtils;

public class XmlConstructionRenderer extends XmlConstructTree {

    private boolean includeUnmapped = false;
    private AssignmentType assignment;

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
            StringBuilderUtils.println(node.getName() + ":" + " # " + node.getPath(), builder, node.getLevel() + indent);
            for (XmlSchemaBase.MappingNode child : node.getChildren()) {
                printNode(child, builder, indent);
            }

        } else {
            StringBuilderUtils.println(node.getName() + ":" + " # " + node.getPath(), builder, node.getLevel() + indent);
            String assignment = getAssignment(node);
            if (assignment != null) {
                StringBuilderUtils.println("assignment: " + assignment, builder, node.getLevel() + indent + 1);

            }
        }
    }

    protected void printNode2(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
        if (!isMapped(node)) {
            return;
        }

        if (node.getNodeType().equals(XmlSchemaBase.NodeType.Folder)) {
            StringBuilderUtils.println(node.getName() + ":" + " # " + node.getPath(), builder, node.getLevel() + indent);

            if (node.getAnnotation(LOOP) != null) {
                WhileLoop[] loops = node.getAnnotation(LOOP, WhileLoop[].class);
                for (WhileLoop loop : loops) {
                    StringBuilderUtils.println("- loop:" + loop.name + "://" + loop.sourcePath.replaceAll("/", ".") + "/" + loop.variable + ":", builder, node.getLevel() + indent + 1);
                    for (XmlSchemaBase.MappingNode child : node.getChildren()) {
                        if (inLoop(child, loop)) {
                            printNode(child, builder, indent + 3);
                        }
                    }
                }

            } else if (node.getAnnotation(MAPPING) != null) {
                Mapping mapping = getMapping(node);
                String assignment = getAssignment(node);
                StringBuilderUtils.println("assignment: " + assignment, builder, node.getLevel() + indent + 1);

            } else {
                for (XmlSchemaBase.MappingNode child : node.getChildren()) {
                    printNode(child, builder, indent);
                }
            }

        } else {
            StringBuilderUtils.println(node.getName() + ":" + " # " + node.getPath(), builder, node.getLevel() + indent);
            Mapping mapping = getMapping(node);
            if (mapping != null) {
                if (mapping.mappingRule != null) {
                    //StringBuilderUtils.println("mapping: " + mapping.mappingRule, builder, node.getLevel() + indent + 1);
                }

                if (mapping.sourcePath != null) {
                    //StringBuilderUtils.println("source: " + mapping.sourcePath, builder, node.getLevel() + indent + 1);
                }

                if (mapping.assignment != null) {
                    String assignment = getAssignment(node);
                    StringBuilderUtils.println("assignment: " + assignment, builder, node.getLevel() + indent + 1);
                }
            }
        }
    }

    @Override
    protected boolean isMapped(XmlSchemaBase.MappingNode node) {
        if (includeUnmapped) {
            return true;

        } else {
            return super.isMapped(node);
        }
    }

    private String getAssignment(XmlSchemaBase.MappingNode node) {

        Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
        if (assignment == null) {
            if (mapping == null) {
                return includeUnmapped ? "NULL" : null;
            }

            if (mapping.assignment == null) {
                return "'???'";

            } else if (mapping.assignment.contains("'")) {
                return "\"" + mapping.assignment + "\"";

            } else {
                return mapping.assignment;
            }
        } else if (AssignmentType.DEFAULT_VALUE.equals(assignment)) {
            return getDefaultValue(node);

        } else {
            return "todo()";
        }
    }

    private String getDefaultValue(XmlSchemaBase.MappingNode node) {
        if (node.getNodeType().equals(XmlSchemaBase.NodeType.Attribute)) {
            return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, node.getName());

        } else if (node.getDataType().equalsIgnoreCase("boolean")) {
            return "true";

        } else if (node.getDataType().equalsIgnoreCase("string")) {
            return "\"" + node.getName() + "\"";

        } else {
            return node.getName();
        }
    }

    static enum AssignmentType {
        DEFAULT_VALUE
    }
}
