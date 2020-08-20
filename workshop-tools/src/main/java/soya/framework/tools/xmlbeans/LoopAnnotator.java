package soya.framework.tools.xmlbeans;

public class LoopAnnotator implements Buffalo.Annotator<XmlSchemaBase> {

    private String name;
    private String targetPath;
    private String sourcePath;
    private String variable;

    private String parent;

    @Override
    public void annotate(XmlSchemaBase base) {

        XmlSchemaBase.MappingNode node = base.getMappings().get(targetPath);

        LoopDescription loop = new LoopDescription();
        loop.name = name;
        loop.sourcePath = sourcePath;
        loop.variable = variable;
        loop.parent = parent;

        node.annotateAsArrayElement("loop", loop);
    }

    static class LoopDescription {
        private String name;
        private String sourcePath;
        private String variable;
        private String parent;

        private LoopDescription() {
        }

    }
}
