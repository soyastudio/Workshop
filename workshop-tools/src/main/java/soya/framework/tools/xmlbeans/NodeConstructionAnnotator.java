package soya.framework.tools.xmlbeans;

import java.util.Map;

public class NodeConstructionAnnotator extends NodeMappingAnnotator {
    private String sourcePath;
    private String variable;
    private String condition;
    private boolean loop;
    private Map<String, String> assignments;

    @Override
    protected void annotate(XmlSchemaBase.MappingNode node) {
        Construction construction = new Construction();
        construction.sourcePath = sourcePath;
        construction.variable = variable;
        construction.condition = condition;
        construction.loop = loop;
        construction.assignments = assignments;

        node.annotateAsArrayElement(CONSTRUCTION, construction);
    }
}
