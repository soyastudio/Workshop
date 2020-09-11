package soya.framework.tools.xmlbeans;

public class XPathRenderer extends XmlSchemaBaseRenderer {

    @Override
    public String render(XmlSchemaBase base) {
        StringBuilder builder = new StringBuilder();
        base.getMappings().keySet().forEach(e -> {
            builder.append(e).append("\n");
        });
        return builder.toString();
    }
}
