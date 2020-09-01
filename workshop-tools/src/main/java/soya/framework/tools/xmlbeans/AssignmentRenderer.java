package soya.framework.tools.xmlbeans;

public class AssignmentRenderer implements Buffalo.Renderer<XmlSchemaBase>, MappingFeature {

    @Override
    public String render(XmlSchemaBase base) {
        StringBuilder builder = new StringBuilder();

        base.getMappings().entrySet().forEach(e -> {
            XmlSchemaBase.MappingNode node = e.getValue();
            if(!node.getNodeType().equals(XmlSchemaBase.NodeType.Folder) && node.getAnnotation(MAPPING) != null) {
                Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
                builder.append(e.getKey()).append("=").append(mapping.assignment).append("\n");
            }
        });

        return builder.toString();
    }
}
