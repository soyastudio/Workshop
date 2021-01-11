package com.albertsons.edis.tools.xmlbeans;

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
