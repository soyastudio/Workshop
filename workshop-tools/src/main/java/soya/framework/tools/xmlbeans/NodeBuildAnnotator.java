package soya.framework.tools.xmlbeans;

import java.util.Map;

public class NodeBuildAnnotator implements Buffalo.Annotator<XmlSchemaBase>, MappingFeature {

    private String targetPath;

    private String sourcePath;
    private String condition;
    private Map<String, ?> construction;

    @Override
    public void annotate(XmlSchemaBase base) {

    }
}
