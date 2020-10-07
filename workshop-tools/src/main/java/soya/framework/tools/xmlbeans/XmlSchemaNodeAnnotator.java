package soya.framework.tools.xmlbeans;

import com.google.gson.Gson;

import java.util.Map;

public class XmlSchemaNodeAnnotator implements Buffalo.Annotator<XmlSchemaBase> {
    private static Gson GSON = new Gson();
    private Map<String, Map<String, ?>> nodes;

    @Override
    public void annotate(XmlSchemaBase base) {
        if (nodes != null) {
            nodes.entrySet().forEach(e -> {
                String path = e.getKey();
                XmlSchemaBase.MappingNode node = base.get(path);
                if (node != null) {
                    Map<String, ?> value = e.getValue();
                    NodeChange nc = GSON.fromJson(GSON.toJson(value), NodeChange.class);
                    applyChange(node, nc);

                }
            });
        }
    }

    private void applyChange(XmlSchemaBase.MappingNode node, NodeChange change) {
        if (change != null) {
            if (change.namespaceURI != null && change.namespaceURI.trim().length() > 0) {
                node.setNamespaceURI(change.namespaceURI);
            }

            if (change.alias != null && change.alias.trim().length() > 0) {
                node.setAlias(change.alias);
            }

            if (change.defaultValue != null && change.defaultValue.trim().length() > 0) {
                node.setDefaultValue(change.defaultValue);
            }
        }
    }

    static class NodeChange {
        private String namespaceURI;
        private String alias;
        private String defaultValue;

    }
}
