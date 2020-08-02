package soya.framework.tools.iib;

import soya.framework.tools.util.StringBuilderUtils;

public class CmmGenerator {

    public static final String NAMESPACE = "https://collab.safeway.com/it/architecture/info/default.aspx";
    public static final String DOCUMENT_ROOT = "xmlDocRoot";
    public static final String DOCUMENT_DATA = "DocumentData";

    protected Node root;
    protected Node documentNode;
    protected Node dataObjectNode;

    public CmmGenerator(Node root) {
        this.root = root;
        for (Node n : root.getChildren()) {
            if (DOCUMENT_DATA.equals(n.getName())) {
                documentNode = n;
            } else {
                dataObjectNode = n;
            }
        }
    }

    public String generate() {
        StringBuilder builder = new StringBuilder();
        StringBuilderUtils.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", builder);
        StringBuilderUtils.println("<" + root.getName()  + "  xmlns:Abs=\"http://collab.safeway.com/it/architecture/info/default.aspx\">", builder);

        // DocumentData
        StringBuilderUtils.println("<" + documentNode.getName()  + ">", builder, 1);
        documentNode.getChildren().forEach(e -> {
            printNode(e, builder, "Abs");
        });
        StringBuilderUtils.println("</" + documentNode.getName()  + ">", builder, 1);

        // Business Object Data
        StringBuilderUtils.println("<" + dataObjectNode.getName()  + ">", builder, 1);
        dataObjectNode.getChildren().forEach(e -> {
            printNode(e, builder, "Abs");
        });
        StringBuilderUtils.println("</" + dataObjectNode.getName()  + ">", builder, 1);

        StringBuilderUtils.println("</" + root.getName() + ">", builder);
        return builder.toString();
    }


    private void printNode(Node node, StringBuilder builder) {
        if (Node.isEmpty(node)) {
            return;
        }

        if (NodeType.FIELD.equals(node.getType()) && !Node.isEmpty(node)) {
            String value = node.getValue().get("value").getAsString().trim();
            StringBuilderUtils.println("<" + node.getName() + ">" + value + "</" + node.getName() + ">", builder, node.getLevel());

        } else if (NodeType.ATTRIBUTE.equals(node.getType())) {

        } else {
            StringBuilderUtils.println("<" + node.getName() + ">", builder, node.getLevel());

            node.getChildren().forEach(e -> {
                printNode(e, builder);
            });

            StringBuilderUtils.println("</" + node.getName() + ">", builder, node.getLevel());
        }
    }

    private void printNode(Node node, StringBuilder builder, String ns) {

        if (Node.isEmpty(node)) {
            return;
        } else if (ns == null) {
            printNode(node, builder);
            return;
        }

        if (NodeType.FIELD.equals(node.getType()) && !Node.isEmpty(node)) {
            String value = node.getValue().get("value").getAsString().trim();
            StringBuilderUtils.println("<" + ns + ":" + node.getName() + ">" + value + "</" + ns + ":" + node.getName() + ">", builder, node.getLevel());

        } else if (NodeType.ATTRIBUTE.equals(node.getType())) {

        } else {
            StringBuilderUtils.println("<" + ns + ":" + node.getName() + ">", builder, node.getLevel());

            node.getChildren().forEach(e -> {
                printNode(e, builder, ns);
            });

            StringBuilderUtils.println("</" + ns + ":" + node.getName() + ">", builder, node.getLevel());
        }
    }

}
