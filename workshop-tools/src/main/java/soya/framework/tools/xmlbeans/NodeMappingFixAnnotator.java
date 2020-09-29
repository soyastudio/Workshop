package soya.framework.tools.xmlbeans;

import com.google.gson.Gson;

import java.util.*;

public class NodeMappingFixAnnotator implements Buffalo.Annotator<XmlSchemaBase>, MappingFeature {
    private static Gson GSON = new Gson();

    private Map<String, Map<String, ?>> nodeFixes;

    private Map<String, String> targetPathFixes;
    private Map<String, String> sourcePathFixes;
    private Map<String, String> mappingRuleFixes;

    private List<String> excludes;
    private Set<String> ignores = new HashSet<>();

    @Override
    public void annotate(XmlSchemaBase base) {
        if (nodeFixes != null) {
            nodeFixes.entrySet().forEach(e -> {
                String path = e.getKey();
                XmlSchemaBase.MappingNode node = base.get(path);
                if (node != null) {
                    Map<String, ?> value = e.getValue();
                    NodeChange nc = GSON.fromJson(GSON.toJson(value), NodeChange.class);
                    applyChange(node, nc);

                }
            });
        }

        UnknownMapping[] mappings = base.getAnnotation(UNKNOWN_MAPPINGS, UnknownMapping[].class);
        if (mappings != null) {
            fixUnknownMappings(mappings);

            // update node mappings:
            for (UnknownMapping unknownMapping : mappings) {
                if (unknownMapping.fix != null) {
                    UnknownType ut = unknownMapping.unknownType;
                    String path = unknownMapping.unknownType.equals(ut) ? unknownMapping.fix : unknownMapping.targetPath;
                    XmlSchemaBase.MappingNode node = base.get(path);
                    if (node == null) {
                        throw new NullPointerException("Cannot find node: " + path);
                    }

                    if(!node.getNodeType().equals(XmlSchemaBase.NodeType.Folder)) {
                        Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
                        if (ut.equals(UnknownType.UNKNOWN_TARGET_PATH)) {
                            mapping.mappingRule = unknownMapping.mappingRule;
                            mapping.sourcePath = unknownMapping.sourcePath;

                        } else if(ut.equals(UnknownType.UNKNOWN_MAPPING_RULE)) {
                            mapping.mappingRule = unknownMapping.fix;

                        } else if(ut.equals(UnknownType.UNKNOWN_SOURCE_PATH)) {
                            mapping.sourcePath = unknownMapping.fix;
                        }
                        node.annotate(MAPPING, mapping);

                    } else {
                        // TODO: for folder type?
                    }
                }
            }

            base.annotate(UNKNOWN_MAPPINGS, mappings);
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

    private void fixUnknownMappings(UnknownMapping[] unknownMappings) {
        for (UnknownMapping unknownMapping : unknownMappings) {
            if (UnknownType.UNKNOWN_TARGET_PATH.equals(unknownMapping.unknownType)
                    && targetPathFixes != null && targetPathFixes.containsKey(unknownMapping.targetPath)) {
                unknownMapping.fix = targetPathFixes.get(unknownMapping.targetPath);

            } else if (UnknownType.UNKNOWN_SOURCE_PATH.equals(unknownMapping.unknownType)
                    && sourcePathFixes != null && sourcePathFixes.containsKey(unknownMapping.targetPath)) {
                unknownMapping.fix = sourcePathFixes.get(unknownMapping.targetPath);

            } else if (UnknownType.UNKNOWN_MAPPING_RULE.equals(unknownMapping.unknownType)
                    && mappingRuleFixes != null && mappingRuleFixes.containsKey(unknownMapping.targetPath)) {
                unknownMapping.fix = mappingRuleFixes.get(unknownMapping.targetPath);

            }
        }
    }

    static class NodeChange {
        private String namespaceURI;
        private String alias;
        private String defaultValue;

    }
}
