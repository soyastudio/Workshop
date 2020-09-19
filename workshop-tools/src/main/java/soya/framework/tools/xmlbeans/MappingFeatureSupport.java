package soya.framework.tools.xmlbeans;

public abstract class MappingFeatureSupport implements MappingFeature {

    protected Mapping getMapping(XmlSchemaBase.MappingNode node) {
        return node.getAnnotation(MAPPING, Mapping.class);
    }

    protected String toXPath(String source) {
        return source;
    }
}
