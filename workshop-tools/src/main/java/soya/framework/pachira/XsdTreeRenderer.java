package soya.framework.pachira;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class XsdTreeRenderer implements Barflow.Renderer<XsdTreeBase> {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String render(XsdTreeBase baseline) throws Barflow.FlowExecutionException {
        KnowledgeTree<?> tree = baseline.knowledgeBase();
        JsonObject jsonObject = new JsonObject();
        tree.root().getChildren().forEach(e -> {
            //jsonObject.add(e.getName(), createJsonElement(e));
        });
        return null;
    }

    private JsonElement createJsonElement(KnowledgeTreeNode<XsdTreeBase.XsNode> treeNode) {
        return null;
    }
}
