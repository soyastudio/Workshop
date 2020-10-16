package soya.framework.tools.xmlbeans;

import com.google.gson.JsonObject;
import com.samskivert.mustache.Mustache;

public class OverridePropertiesRenderer extends XmlSchemaBaseRenderer {
    private String template;

    @Override
    public String render(XmlSchemaBase base) {
        JsonObject jsonObject = base.getAnnotation(APPLICATION, JsonObject.class);
        return Mustache.compiler().compile(WorkshopRepository.getResourceAsString(template)).execute(JsonUtils.toMap(jsonObject));
    }
}
