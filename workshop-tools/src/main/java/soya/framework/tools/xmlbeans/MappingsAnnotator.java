package soya.framework.tools.xmlbeans;

import java.util.List;
import java.util.Map;

public class MappingsAnnotator extends MappingFeatureSupport implements Buffalo.Annotator<XmlSchemaBase> {

    private List<?> globalVariables;
    private Map<String, Map<String, ?>> mappings;

    @Override
    public void annotate(XmlSchemaBase base) {

        // Global Variables:
        if (globalVariables != null) {
            for (Object o : globalVariables) {
                Variable v = GSON.fromJson(GSON.toJson(o), Variable.class);
                base.annotateAsArrayElement(GLOBAL_VARIABLE, v);
            }
        }

        // Components:
        if (mappings != null) {
            mappings.entrySet().forEach(e -> {
                String path = e.getKey();
                XmlSchemaBase.MappingNode node = base.get(path);
                Map<String, ?> settings = e.getValue();
                if (settings.containsKey(MAPPING)) {
                    Mapping mapping = GSON.fromJson(GSON.toJson(settings.get(MAPPING)), Mapping.class);
                    node.annotate(MAPPING, mapping);

                } else if (settings.containsKey(LOOP)) {
                    WhileLoop[] loops = GSON.fromJson(GSON.toJson(settings.get(LOOP)), WhileLoop[].class);
                    for(WhileLoop loop: loops) {
                        node.annotateAsArrayElement(LOOP, loop);
                    }
                }
            });
        }
    }
}
