package soya.framework.tools.xmlbeans;

public class XPathMappingsRenderer extends XPathRenderer {

    @Override
    protected String printConstruction(XmlSchemaBase.MappingNode node) {
        return "";
    }

    @Override
    protected String printAssignment(XmlSchemaBase.MappingNode node) {
        Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
        if (mapping != null) {
            if (mapping.mappingRule != null) {
                Function[] functions = parse(mapping);
                if (functions.length == 1) {
                    if (Function.DEFAULT_FUNCTION.equalsIgnoreCase(functions[0].getName())) {
                        return functions[0].toString();

                    } else if (Function.FROM_FUNCTION.equalsIgnoreCase(functions[0].getName())) {
                        return functions[0].toString();

                    }
                }
            }
        }

        return TODO;
    }
}
