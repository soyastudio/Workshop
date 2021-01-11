package com.albertsons.edis.tools.xmlbeans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnknownMappingsRenderer extends MappingFeatureSupport implements Buffalo.Renderer<XmlSchemaBase> {
    private String name;
    private List<String> types;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String render(XmlSchemaBase base) {
        Set<UnknownType> typeSet = new HashSet<>();
        if (types != null) {
            types.forEach(e -> {
                String type = e.toUpperCase();
                if (UnknownType.valueOf(type) != null) {
                    typeSet.add(UnknownType.valueOf(type));
                }

            });
        } else {
            typeSet.add(UnknownType.UNKNOWN_TARGET_PATH);
            typeSet.add(UnknownType.UNKNOWN_MAPPING_RULE);
            typeSet.add(UnknownType.UNKNOWN_SOURCE_PATH);
            typeSet.add(UnknownType.ILLEGAL_SOURCE_PATH);
        }

        List<UnknownMapping> list = new ArrayList<>();
        UnknownMapping[] unknownMappings = base.getAnnotation(UNKNOWN_MAPPINGS, UnknownMapping[].class);
        for (UnknownMapping unknownMapping : unknownMappings) {
            if (typeSet.contains(unknownMapping.unknownType)) {
                list.add(unknownMapping);
            }
        }

        return GSON.toJson(list);
    }
}
