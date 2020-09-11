package soya.framework.tools.xmlbeans;

public abstract class XmlSchemaBaseRenderer implements Buffalo.Renderer<XmlSchemaBase> {
    private String name;

    @Override
    public String getName() {
        return name;
    }
}
