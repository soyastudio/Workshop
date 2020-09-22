package soya.framework.tools.xmlbeans;

import java.util.ArrayList;
import java.util.List;

public class NodeBuildAnnotator implements Buffalo.Annotator<XmlSchemaBase>, MappingFeature {

    private String path;
    private List<String> block = new ArrayList<>();

    @Override
    public void annotate(XmlSchemaBase base) {
        XmlSchemaBase.MappingNode node = base.get(path);
        node.annotate(BLOCK, block);
    }
}
