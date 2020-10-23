package soya.framework.tools.xmlbeans;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.checkerframework.checker.units.qual.C;

public abstract class NodeMappingAnnotator extends MappingFeatureSupport implements Buffalo.Annotator<XmlSchemaBase> {

    protected static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected String path;
    protected String condition;

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

        if(condition != null) {
            node.annotate(CONDITION, condition);
        }

        annotate(node);

    }

    protected Construct getConstruct(XmlSchemaBase.MappingNode node) {
        Construct construct = node.getAnnotation(CONSTRUCT, Construct.class);
        if(construct == null) {
            construct = new Construct();

        }

        return construct;
    }

    protected void  setConstruct(Construct construct, XmlSchemaBase.MappingNode node) {
        node.annotate(condition, node);
    }

    protected abstract void annotate(XmlSchemaBase.MappingNode node);
}
