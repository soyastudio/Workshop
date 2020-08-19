package soya.framework.tools.xmlbeans;

import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public class JsonMappingRenderer implements Buffalo.Renderer<XmlSchemaBase> {
    @Override
    public String render(XmlSchemaBase base) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("annotations", base.getAnnotations());
        map.put("mappings", base.getMappings());

        return new GsonBuilder().setPrettyPrinting().create().toJson(map);
    }
}
