package soya.framework.tools.xmlbeans;

import java.util.List;
import java.util.Map;

public class DirectMappingAnnotator implements Buffalo.Annotator<XmlSchemaBase>, MappingFeature {

    private List<Map<String, String>> mappings;

    @Override
    public void annotate(XmlSchemaBase base) {
        if(mappings != null) {
            mappings.forEach(e -> {
                String targetPath = e.get("targetPath");
                String mappingRule = e.get("mappingRule");
                String sourcePath = e.get("sourcePath");

                XmlSchemaBase.MappingNode node = base.get(targetPath);
                if(node != null) {
                    if(mappingRule != null) {
                        Mapping mapping = new Mapping();
                        mapping.mappingRule = mappingRule;
                        mapping.sourcePath = sourcePath;
                        node.annotate(MAPPING, mapping);

                    } else {
                        node.annotate(MAPPING, null);
                    }
                }
            });
        }
    }
}
