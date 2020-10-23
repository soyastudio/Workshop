package soya.framework.tools.xmlbeans;

import java.util.Map;

public class NodeConstructorsAnnotator extends NodeMappingAnnotator {
    private String sourcePath;
    private String variable;
    private boolean loop;
    private Map<String, String> assignments;

    @Override
    protected void annotate(XmlSchemaBase.MappingNode node) {
        Constructor construction = new Constructor();
        construction.sourcePath = sourcePath;
        construction.variable = variable;
        construction.assignments = assignments;

        node.annotateAsArrayElement(CONSTRUCTION, construction);
    }
}
