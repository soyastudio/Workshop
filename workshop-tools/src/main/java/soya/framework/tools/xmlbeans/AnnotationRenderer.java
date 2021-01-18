package soya.framework.tools.xmlbeans;

import com.google.gson.GsonBuilder;

public class AnnotationRenderer extends XmlSchemaBaseRenderer {
    private String annotationName;

    @Override
    public String render(XmlSchemaBase base) {
        Object object = base.getAnnotation(annotationName);
        return new GsonBuilder().setPrettyPrinting().create().toJson(object);
    }
}
