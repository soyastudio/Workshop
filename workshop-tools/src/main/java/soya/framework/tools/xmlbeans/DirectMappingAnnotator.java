package soya.framework.tools.xmlbeans;

public class DirectMappingAnnotator implements Buffalo.Annotator<XmlSchemaBase> {
    private String targetPath;
    private String sourcePath;
    private String function;

    @Override
    public void annotate(XmlSchemaBase base) {
        XmlSchemaBase.MappingNode node = base.getMappings().get(targetPath);
        node.annotateAsMappedElement("mapping", "sourcePath", sourcePath);
        if (function != null) {
            node.annotateAsMappedElement("mapping", "function", function);
        }
    }
}
