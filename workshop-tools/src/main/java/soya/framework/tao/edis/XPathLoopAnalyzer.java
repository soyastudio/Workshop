package soya.framework.tao.edis;

import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.T123W;
import soya.framework.tao.xs.XsNode;

import java.util.LinkedHashSet;
import java.util.Map;

public class XPathLoopAnalyzer extends EdisRenderer {

    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase) throws T123W.FlowExecutionException {
        Map<String, LinkedHashSet<Function>> map = loopFeature(knowledgeBase);
        StringBuilder builder = new StringBuilder();
        map.entrySet().forEach(e -> {
            LinkedHashSet<Function> value = e.getValue();
            Function[] functions = value.toArray(new Function[value.size()]);
            builder.append(e.getKey()).append("=").append(Function.toString(functions)).append("\n");
        });

        return builder.toString();
    }
}
