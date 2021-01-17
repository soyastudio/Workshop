package soya.framework.tools.xmlbeans;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.LinkedHashSet;
import java.util.Set;

public class LoopAnalyzeRenderer extends XmlSchemaBaseRenderer implements MappingFeature {

    @Override
    public String render(XmlSchemaBase base) {
        Set<Mapper> mappers = findLoops(base);
        return new GsonBuilder().setPrettyPrinting().create().toJson(mappers);
    }
}
