package soya.framework.tao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class XsdTreeRenderer implements Barflow.Renderer<XsdTreeBase> {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

     @Override
    public String render(XsdTreeBase baseline) throws Barflow.FlowExecutionException {
        KnowledgeTree<XsdTreeBase.XsNode> tree = baseline.knowledgeBase();
        JsonObject jsonObject = new JsonObject();
        KnowledgeTreeNode<XsdTreeBase.XsNode> root = tree.root();
        jsonObject.add(root.getName(), createJsonElement(root));

        return gson.toJson(jsonObject);
    }

    private JsonElement createJsonElement(KnowledgeTreeNode<XsdTreeBase.XsNode> treeNode) {
        JsonElement element = null;
        XsdTreeBase.XsNode node = treeNode.origin();
        if(XsdTreeBase.XsNodeType.Folder.equals(node.getNodeType())) {
            JsonObject object = new JsonObject();
            treeNode.getChildren().forEach(e -> {
                object.add(e.getName(), createJsonElement((KnowledgeTreeNode<XsdTreeBase.XsNode>) e));
            });

            element = object;

        } else {
            element = gson.toJsonTree(node);
        }

        return element;
    }
}
