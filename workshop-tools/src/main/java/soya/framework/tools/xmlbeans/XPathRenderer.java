package soya.framework.tools.xmlbeans;


public abstract class XPathRenderer extends XmlSchemaBaseRenderer implements MappingFeature {
    public static String TODO = "TODO(???)";
    public static String UNKNOWN = "???";

    private boolean printUnmapped = true;

    @Override
    public String render(XmlSchemaBase base) {
        StringBuilder builder = new StringBuilder();
        base.getMappings().entrySet().forEach(e -> {
            String key = e.getKey();
            XmlSchemaBase.MappingNode node = e.getValue();

            if (node.getAnnotation(MAPPING) != null) {
                builder.append(key).append("=").append(printAssignment(node)).append("\n");

            } else if (node.getNodeType().equals(XmlSchemaBase.NodeType.Folder)) {
                if (node.getAnnotation(MAPPED) != null) {
                    builder.append(key).append("=");
                    builder.append(printConstruction(node));
                    builder.append("\n");

                } else if (printUnmapped) {
                    builder.append("# ").append(key).append("=").append("\n");

                }

            } else if (printUnmapped) {
                builder.append("# ").append(key).append("=").append("\n");

            }
        });

        return builder.toString();
    }

    protected abstract String printConstruction(XmlSchemaBase.MappingNode node);

    protected abstract String printAssignment(XmlSchemaBase.MappingNode node);

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
