package soya.framework.tools.xmlbeans;

import soya.framework.tools.util.StringBuilderUtils;

public class XmlConstructStructureRenderer extends XmlConstructTree {

    @Override
    public String render(XmlSchemaBase base) {
        StringBuilder builder = new StringBuilder();
        XmlSchemaBase.MappingNode root = base.getRoot();
        printNode(root, builder, 0);

        return builder.toString();
    }

    protected void printNode(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
        if(!isMapped(node)) {
            return;
        }

        if (node.getNodeType().equals(XmlSchemaBase.NodeType.Folder)) {
            StringBuilderUtils.println(node.getName() + ":" + " # " + node.getPath(), builder, node.getLevel() + indent);
            for (XmlSchemaBase.MappingNode child : node.getChildren()) {
                printNode(child, builder, indent);
            }

        } else {
            StringBuilderUtils.println(node.getName() + ":" + " # " + node.getPath(), builder, node.getLevel() + indent);
            Mapping mapping = getMapping(node);
            if (mapping == null) {


            } else if (mapping.assignment != null) {
                String assignment = getAssignment(mapping);
                StringBuilderUtils.println("assignment: " + assignment, builder, node.getLevel() + indent + 1);

            } else if (mapping.mappingRule != null) {
                StringBuilderUtils.println("assignment: todo()", builder, node.getLevel() + indent + 1);
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
                String assignment = getAssignment(mapping);
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
                    String assignment = getAssignment(mapping);
                    StringBuilderUtils.println("assignment: " + assignment, builder, node.getLevel() + indent + 1);
                }
            }
        }
    }

    private String getAssignment(Mapping mapping) {
        if (mapping == null) {
            return null;
        }

        if (mapping.assignment == null) {
            return "'???'";

        } else if (mapping.assignment.contains("'")) {
            return "\"" + mapping.assignment + "\"";

        } else {
            return mapping.assignment;
        }

    }
}
