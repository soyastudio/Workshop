package soya.framework.tools.xmlbeans;

import com.google.gson.GsonBuilder;

public class JsonMappingRenderer implements XmlGenerator.Renderer {
    @Override
    public String render(XmlGenerator base) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(base.getMappings());
    }
}
