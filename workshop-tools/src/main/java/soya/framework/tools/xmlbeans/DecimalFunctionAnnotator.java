package soya.framework.tools.xmlbeans;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DecimalFunctionAnnotator implements Buffalo.Annotator<XmlSchemaBase>, MappingFeature {
    private List<String> excludes;

    @Override
    public void annotate(XmlSchemaBase base) {
        Set<String> set = new HashSet();
        if (this.excludes != null) {
            set.addAll(this.excludes);
        }

        base.getMappings().entrySet().forEach((e) -> {
            if (!set.contains(e.getKey())) {
                XmlSchemaBase.MappingNode node = (XmlSchemaBase.MappingNode) e.getValue();
                Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
                if (mapping != null && node.getDataType() != null && "decimal".equalsIgnoreCase(node.getDataType()) && node.getRestriction() != null) {

                    Map<String, String> restriction = node.getRestriction();
                    String totalDigits = restriction.get("totalDigits");
                    String fractionDigits = restriction.get("fractionDigits");

                    System.out.println("==================== " + e.getKey());
                    if (totalDigits != null && fractionDigits != null) {
                        StringBuilder builder = new StringBuilder();

                        builder.append("CAST(").append(mapping.assignment)
                                .append(" AS DECIMAL(").append(totalDigits).append(", ").append(fractionDigits).append("))");

                        node.annotateAsMappedElement(MAPPING, "assignment", builder.toString());
                    }
                }
            }

        });
    }
}
