package soya.framework.tools.xmlbeans;

import java.util.ArrayList;
import java.util.List;

public class NodeCodeBlockAnnotator extends NodeMappingAnnotator {

    private List<String> block = new ArrayList<>();

    @Override
    protected void annotate(XmlSchemaBase.MappingNode node) {
        node.annotate(BLOCK, block);
    }
}
