package soya.framework.tools.xmlbeans;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class NodeLoopsAnnotator extends NodeMappingAnnotator {
    private List<Map<String, String>> loops;

    @Override
    protected void annotate(XmlSchemaBase.MappingNode node) {
        Gson gson = new Gson();
        if(loops != null) {
            loops.forEach(e -> {
                WhileLoop whileLoop = gson.fromJson(gson.toJson(e), WhileLoop.class);
                node.annotateAsArrayElement(LOOP, whileLoop);

            });
        }
    }
}
