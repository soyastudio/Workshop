package soya.framework.tools.xmlbeans;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

public class NodeBuildAnnotator implements Buffalo.Annotator<XmlSchemaBase>, MappingFeature {

    private String path;
    private Map<String, ?> block;

    @Override
    public void annotate(XmlSchemaBase base) {
        Gson gson = new Gson();
        XmlSchemaBase.MappingNode node = base.get(path);

        if(block != null) {
            JsonObject codeBlock = JsonParser.parseString(gson.toJson(block)).getAsJsonObject();
            node.annotate(BLOCK, codeBlock);
        }
    }
}
