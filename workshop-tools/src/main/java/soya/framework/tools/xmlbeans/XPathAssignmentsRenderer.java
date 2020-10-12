package soya.framework.tools.xmlbeans;


public class XPathAssignmentsRenderer extends XPathRenderer {

    private String inputRoot = "$";

    @Override
    protected String printConstruction(XmlSchemaBase.MappingNode node) {
        if (node.getAnnotation(CONSTRUCTION) != null) {
            return node.getAnnotation(CONSTRUCTION, String.class);
        } else {
            return "";
        }
    }

    @Override
    protected String printAssignment(XmlSchemaBase.MappingNode node) {
        Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
        return printAssignment(mapping);
    }

    protected String printAssignment(Mapping mapping) {
        if (mapping.assignment != null) {
            return mapping.assignment;

        } else if (mapping.mappingRule != null) {
            Function[] functions = parse(mapping);
            if (functions.length == 1) {
                if (Function.DEFAULT_FUNCTION.equalsIgnoreCase(functions[0].getName())) {
                    return functions[0].getArguments()[0];

                } else if (Function.FROM_FUNCTION.equalsIgnoreCase(functions[0].getName())) {
                    String token = "$." + functions[0].getArguments()[0].replaceAll("/", ".");

                    return token;

                } else {
                    return UNKNOWN;

                }
            } else {
                return UNKNOWN;

            }

        } else {
            return UNKNOWN;

        }
    }


    protected Function[] parse(Mapping mapping) {
        String mappingRule = mapping.mappingRule;
        StringBuilder builder = new StringBuilder();
        if (mappingRule.toUpperCase().startsWith("DEFAULT TO ")) {
            builder.append("DEFAULT(");
            if (mappingRule.contains("'")) {
                String token = mappingRule.substring(mappingRule.indexOf("'"));
                builder.append(token);
            } else {
                builder.append(UNKNOWN);
            }
            builder.append(")");

        } else if (mappingRule.toUpperCase().contains("DIRECT") && mappingRule.toUpperCase().contains("MAPPING")) {
            builder.append("FROM(");
            if (mapping.sourcePath != null && !mapping.sourcePath.contains(" ") && !mapping.sourcePath.contains("\n")) {
                builder.append(mapping.sourcePath);
            } else {
                builder.append(UNKNOWN);
            }

            builder.append(")");

        } else {
            builder.append(TODO);
        }

        return Function.fromString(builder.toString());
    }

}
