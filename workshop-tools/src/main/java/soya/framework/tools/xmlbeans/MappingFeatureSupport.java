package soya.framework.tools.xmlbeans;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class MappingFeatureSupport implements MappingFeature {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected Mapping getMapping(XmlSchemaBase.MappingNode node) {
        return node.getAnnotation(MAPPING, Mapping.class);
    }

    protected String toXPath(String source) {
        return source;
    }
}
