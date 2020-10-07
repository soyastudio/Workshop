package soya.framework.tools.xmlbeans;

public class UnknownMappingsRenderer extends MappingFeatureSupport implements Buffalo.Renderer<XmlSchemaBase> {
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String render(XmlSchemaBase base) {
        StringBuilder builder = new StringBuilder();

        return builder.toString();
    }
}
