package soya.framework.tools.xmlbeans;

import java.util.List;
import java.util.Map;

public class NodeLoopsAnnotator extends NodeMappingAnnotator {
    private List<Map<String, String>> loops;

    @Override
    protected void annotate(XmlSchemaBase.MappingNode node) {
        Construct construct = getConstruct(node);
        if (loops != null) {
            loops.forEach(e -> {
                WhileLoop whileLoop = GSON.fromJson(GSON.toJson(e), WhileLoop.class);
                construct.loops.add(whileLoop);
            });
        }

        setConstruct(construct, node);
    }
}
