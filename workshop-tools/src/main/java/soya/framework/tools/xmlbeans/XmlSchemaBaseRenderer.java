package soya.framework.tools.xmlbeans;

public abstract class XmlSchemaBaseRenderer extends MappingFeatureSupport implements Buffalo.Renderer<XmlSchemaBase> {
    private String name;

    @Override
    public String getName() {
        return name;
    }

}
