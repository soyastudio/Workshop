package soya.framework.tools.xmlbeans;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class JsonSourceMappingRenderer extends XmlSchemaBaseRenderer implements MappingFeature {

    @Override
    public String render(XmlSchemaBase base) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(base.getAnnotation("SOURCE_MAPPING", JsonObject.class));
    }
}
