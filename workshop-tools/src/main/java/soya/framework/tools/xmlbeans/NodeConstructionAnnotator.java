package soya.framework.tools.xmlbeans;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class NodeConstructionAnnotator implements Buffalo.Annotator<XmlSchemaBase>, MappingFeature {
    private Map<String, List<?>> mappings;

    @Override
    public void annotate(XmlSchemaBase base) {
        if (mappings == null) {
            return;
        }

        Gson gson = new Gson();
        mappings.entrySet().forEach(e -> {
            String key = e.getKey();
            XmlSchemaBase.MappingNode node = base.get(key);
            if (node != null) {
                List<?> list = e.getValue();
                list.forEach(l -> {
                    String json = gson.toJson(l);
                    Construction construction = gson.fromJson(json, Construction.class);
                    node.annotateAsArrayElement(MAPPINGS, construction);

                });
            }

            node.annotate(MAPPED, "true");
        });
    }
}
