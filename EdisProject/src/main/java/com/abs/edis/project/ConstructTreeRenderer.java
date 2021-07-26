package com.abs.edis.project;

import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.KnowledgeTreeNode;
import soya.framework.tao.T123W;
import soya.framework.tao.xs.XsNode;

public class ConstructTreeRenderer extends EdisRenderer {
    private static String[] indents = {"",
            "\t",
            "\t\t",
            "\t\t\t",
            "\t\t\t\t",
            "\t\t\t\t\t",
            "\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t\t\t",
            "\t\t\t\t\t\t\t\t\t\t\t\t"};

    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase) throws T123W.FlowExecutionException {
        StringBuilder builder = new StringBuilder();
        KnowledgeTreeNode<XsNode> root = knowledgeBase.root();
        renderTreeNode(root, builder, 0);
        return builder.toString();
    }

    private void renderTreeNode(KnowledgeTreeNode<XsNode> treeNode, StringBuilder builder, int indent) {
        if (treeNode.getAnnotation(NAMESPACE_CONSTRUCTION) != null) {
            builder.append(indents[indent])
                    .append("folder://")
                    .append(treeNode.getPath())
                    .append(":\n");

            final int next = indent + 1;
            treeNode.getChildren().forEach(e -> {
                renderTreeNode((KnowledgeTreeNode<XsNode>) e, builder, next);
            });
        } else if (treeNode.getAnnotation(NAMESPACE_ASSIGNMENT) != null) {
            Assignment assignment = treeNode.getAnnotation(NAMESPACE_ASSIGNMENT, Assignment.class);
            if (assignment.functions.size() == 1) {
                Function function = assignment.getFirst();
                if (XsNode.XsNodeType.Field.equals(treeNode.origin().getNodeType())) {
                    builder.append(indents[indent]).append("field:").append("//").append(treeNode.getPath()).append(":\n");
                    builder.append(indents[indent + 1]).append(treeNode.getName()).append(": ").append(getAssignment(function)).append("\n");
                } else {
                    builder.append(indents[indent]).append("attribute:").append("//").append(treeNode.getPath()).append(":\n");
                    builder.append(indents[indent + 1]).append(treeNode.getName().substring(1)).append(": ").append(getAssignment(function)).append("\n");
                }


            }
        }

    }

    private String getAssignment(Function function) {
        if (FUNCTION_DEFAULT.equalsIgnoreCase(function.name)) {
            return "\"" + function.parameters[0] + "\"" ;

        } else if (FUNCTION_ASSIGN.equalsIgnoreCase(function.name)) {
            return function.parameters[0];

        } else if (FUNCTION_LOOP_ASSIGN.equalsIgnoreCase(function.name)) {
            return function.parameters[1];

        } else {
            return "???";
        }
    }
}
