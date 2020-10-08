package soya.framework.tools.xmlbeans;

import com.google.gson.Gson;

public abstract class XmlConstructTree extends XmlSchemaBaseRenderer implements MappingFeature {

    protected static Gson GSON = new Gson();


    protected boolean isMapped(XmlSchemaBase.MappingNode node) {
        if (node.getAnnotation(MAPPING) != null) {
            return true;

        } else if (XmlSchemaBase.NodeType.Folder.equals(node.getNodeType())) {
            if (node.getAnnotation(MAPPED) != null || node.getAnnotation(LOOP) != null
                    || node.getAnnotation(BLOCK) != null || node.getAnnotation(CONSTRUCTION) != null) {
                return true;
            }

        }

        return false;
    }

    protected boolean inLoop(XmlSchemaBase.MappingNode node, WhileLoop loop) {

        String source = loop.sourcePath + "/";
        if (node.getAnnotation(MAPPING) != null) {
            Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
            return mapping.sourcePath != null && (mapping.sourcePath.equals(loop.sourcePath) || mapping.sourcePath.startsWith(source));

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
