package soya.framework.tools.xmlbeans;

public abstract class NodeMappingAnnotator implements Buffalo.Annotator<XmlSchemaBase>, MappingFeature {
    protected String path;

    protected XmlSchemaBase base;
    protected XmlSchemaBase.MappingNode node;

    @Override
    public void annotate(XmlSchemaBase base) {
        this.base = base;

        if(path == null) {
            throw new IllegalStateException("path is not specified");
        }

        if(base.get(path) == null) {
            throw new IllegalArgumentException("Cannot find node with path: " + path);
        }

        this.node = base.get(path);

        annotate(node);

    }

    protected abstract void annotate(XmlSchemaBase.MappingNode node);
}
