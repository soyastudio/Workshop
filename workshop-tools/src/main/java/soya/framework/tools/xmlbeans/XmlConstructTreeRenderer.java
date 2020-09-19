package soya.framework.tools.xmlbeans;

import soya.framework.tools.util.StringBuilderUtils;

public class XmlConstructTreeRenderer extends XmlSchemaBaseRenderer implements MappingFeature {

    @Override
    public String render(XmlSchemaBase base) {
        StringBuilder builder = new StringBuilder();
        XmlSchemaBase.MappingNode root = base.getRoot();
        printNode(root, builder, 0);

        return builder.toString();
    }

    private void printNode(XmlSchemaBase.MappingNode node, StringBuilder builder, int indent) {
        if (!isMapped(node)) {
            return;
        }

        if (node.getNodeType().equals(XmlSchemaBase.NodeType.Folder)) {
            StringBuilderUtils.println(node.getName() + ":", builder, node.getLevel() + indent);
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

            } else {
                for (XmlSchemaBase.MappingNode child : node.getChildren()) {
                    printNode(child, builder, indent);
                }
            }

        } else {
            StringBuilderUtils.println(node.getName() + ":", builder, node.getLevel() + indent);
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
        if(mapping == null) {
            return null;
        }

        if(mapping.assignment == null) {
            return "'???'";

        } else if(mapping.assignment.contains("'")){
            return "\"" + mapping.assignment + "\"";

        } else {
            return mapping.assignment;
        }

    }

    private boolean isMapped(XmlSchemaBase.MappingNode node) {
        if (XmlSchemaBase.NodeType.Folder.equals(node.getNodeType())) {
            if (node.getAnnotation(MAPPED) != null || node.getAnnotation(LOOP) != null || node.getAnnotation(BLOCK) != null) {
                return true;
            }

        } else {
            return node.getAnnotation(MAPPING) != null;
        }

        return false;
    }

    private boolean inLoop(XmlSchemaBase.MappingNode node, WhileLoop loop) {
        String source = loop.sourcePath + "/";

        if (XmlSchemaBase.NodeType.Folder.equals(node.getNodeType())) {
            for (XmlSchemaBase.MappingNode child : node.getChildren()) {
                if (inLoop(child, loop)) {
                    return true;
                }
            }

        } else {
            if (node.getAnnotation(MAPPING) != null) {
                Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
                return mapping.sourcePath != null && mapping.sourcePath.startsWith(source);
            }
        }

        return false;
    }
}
