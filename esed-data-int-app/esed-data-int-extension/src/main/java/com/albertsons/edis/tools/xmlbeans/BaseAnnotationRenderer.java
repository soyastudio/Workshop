package com.albertsons.edis.tools.xmlbeans;

public class BaseAnnotationRenderer extends XmlSchemaBaseRenderer implements MappingFeature {
    private String annotation;

    @Override
    public String render(XmlSchemaBase base) {
        if (annotation != null) {
            return GSON.toJson(base.getAnnotation(UNKNOWN_MAPPINGS));

        } else {
            return GSON.toJson(base.getAnnotations());

        }

    }
}
