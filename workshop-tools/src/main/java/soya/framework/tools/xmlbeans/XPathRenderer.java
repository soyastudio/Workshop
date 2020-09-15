package soya.framework.tools.xmlbeans;


public class XPathRenderer extends XmlSchemaBaseRenderer implements MappingFeature {
    private boolean printUnmapped;

    @Override
    public String render(XmlSchemaBase base) {
        StringBuilder builder = new StringBuilder();
        base.getMappings().entrySet().forEach(e -> {
            String key = e.getKey();
            XmlSchemaBase.MappingNode node = e.getValue();
            if (node.getNodeType().equals(XmlSchemaBase.NodeType.Folder)) {
                if (node.getAnnotation(MAPPED) != null) {
                    builder.append(key).append("=");

                    if(node.getAnnotation(LOOP) != null) {
                        builder.append("loop(");
                        WhileLoop[] whileLoops = node.getAnnotation(LOOP, WhileLoop[].class);
                        for(int i = 0; i < whileLoops.length; i ++) {
                            if(i > 0) {
                                builder.append(", ");
                            }
                            builder.append(whileLoops[i].variable);
                        }

                        builder.append(")");

                    } else if(node.getAnnotation(MAPPINGS) != null) {
                        builder.append("from()");

                    } else if(node.getAnnotation(BLOCK) != null) {
                        builder.append("block()");
                    }

                    builder.append("\n");

                } else if (printUnmapped) {
                    builder.append("# ").append(key).append("=").append("\n");

                }

            } else {
                if (node.getAnnotation(MAPPING) != null) {
                    Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
                    builder.append(key).append("=").append(mapping.assignment).append("\n");

                } else if (printUnmapped) {
                    builder.append("# ").append(key).append("=").append("\n");

                }
            }
        });
        return builder.toString();
    }
}
