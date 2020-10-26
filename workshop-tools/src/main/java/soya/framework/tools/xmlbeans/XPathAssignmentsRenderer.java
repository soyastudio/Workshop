package soya.framework.tools.xmlbeans;


import java.util.LinkedHashMap;
import java.util.Map;

public class XPathAssignmentsRenderer extends XPathRenderer {

    private String inputRoot = "$";

    private transient Map<String, Object> constructions = new LinkedHashMap<>();

    @Override
    protected String printConstruction(XmlSchemaBase.MappingNode node) {
        if (node.getAnnotation(CONSTRUCT) != null) {
            Construct construct = node.getAnnotation(CONSTRUCT, Construct.class);
            construct.loops.forEach(e -> {
                constructions.put(e.sourcePath, e);
            });

            construct.constructors.forEach(e -> {
                constructions.put(e.sourcePath, e);
            });

            return construct.toString();

        } else {
            return "";

        }
    }

    @Override
    protected String printAssignment(XmlSchemaBase.MappingNode node) {
        Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
        return mapping.assignment;
    }

}
