package com.albertsons.edis.tools.xmlbeans;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UnknownMappingFixAnnotator implements Buffalo.Annotator<XmlSchemaBase>, MappingFeature {
    private static Gson GSON = new Gson();
    private List<Map<String, String>> fixes;

    @Override
    public void annotate(XmlSchemaBase base) {
        UnknownMapping[] mappings = base.getAnnotation(UNKNOWN_MAPPINGS, UnknownMapping[].class);
        Map<String, UnknownMapping> mappingFixes = new LinkedHashMap<>();
        if (mappings != null && fixes != null) {
            fixes.forEach(e -> {
                UnknownMapping unknownMapping = GSON.fromJson(GSON.toJson(e), UnknownMapping.class);
                mappingFixes.put(unknownMapping.targetPath, unknownMapping);
            });
        }

        if (mappings != null) {
            // update node mappings:
            for (UnknownMapping unknownMapping : mappings) {
                if (mappingFixes.containsKey(unknownMapping.targetPath) && mappingFixes.get(unknownMapping.targetPath).fix != null) {

                    unknownMapping.fix = mappingFixes.get(unknownMapping.targetPath).fix;

                    UnknownType ut = unknownMapping.unknownType;
                    String path = UnknownType.UNKNOWN_TARGET_PATH.equals(ut) ? unknownMapping.fix : unknownMapping.targetPath;

                    XmlSchemaBase.MappingNode node = base.get(path);
                    if (node == null) {
                        throw new NullPointerException("Cannot find node: " + path);
                    }

                    if (!node.getNodeType().equals(XmlSchemaBase.NodeType.Folder)) {
                        Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
                        if (mapping == null) {
                            mapping = new Mapping();
                        }
                        if (ut.equals(UnknownType.UNKNOWN_TARGET_PATH)) {
                            mapping.mappingRule = unknownMapping.mappingRule;
                            mapping.sourcePath = unknownMapping.sourcePath;

                        } else if (ut.equals(UnknownType.UNKNOWN_MAPPING_RULE)) {
                            mapping.mappingRule = unknownMapping.fix;

                        } else if (ut.equals(UnknownType.UNKNOWN_SOURCE_PATH)) {
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
}
