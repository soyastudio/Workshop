package soya.framework.tools.xmlbeans;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ApplicationAnnotator implements Buffalo.Annotator<XmlSchemaBase>, MappingFeature {
    private String uri;

    @Override
    public void annotate(XmlSchemaBase base) {
        String json = WorkshopRepository.getResourceAsString(uri);
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        base.annotate(APPLICATION, jsonObject);
    }
}
