package soya.framework.tools.iib;

import soya.framework.tools.util.StringBuilderUtils;
import com.google.gson.Gson;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;

public class JsonToXmlTransformer {
    public static final String DOCUMENT_DATA = "DocumentData";
    private Node root;

    private Node documentNode;
    private Node dataObjectNode;

    private JsonToXmlTransformer(Node root) {
        this.root = root;
        for (Node n : root.getChildren()) {
            if (DOCUMENT_DATA.equals(n.getName())) {
                documentNode = n;
            } else {
                dataObjectNode = n;
            }
        }
    }

    public static JsonToXmlTransformer newInstance(Node node) {
        return new JsonToXmlTransformer(node);
    }

    public String generate(String json) {
        DocumentContext ctx = JsonPath.parse(json);

        StringBuilder builder = new StringBuilder();
        print(root, builder, ctx);
        return builder.toString();
    }

    private void print(Node node, StringBuilder builder, DocumentContext ctx) {
        if(Node.isEmpty(node)) {
            return;
        }

        if (NodeType.FIELD.equals(node.getType())) {
            if (node.getValue() != null && node.getValue().get("value") != null && node.getValue().get("value").getAsString().trim().length() > 0) {
                Function function = new Function(node.getValue().get("value").getAsString().trim());
                String value = extract(function, ctx);

                if (value != null) {
                    StringBuilderUtils.println("<" + node.getName() + ">" + value + "</" + node.getName() + ">", builder, node.getLevel());
                }
            }

        } else if (NodeType.ATTRIBUTE.equals(node.getType())) {

        } else if (isSubRoot(node)) {
            printFunction(node, builder, ctx);

        } else {
            StringBuilderUtils.println("<" + node.getName() + ">", builder, node.getLevel());

            node.getChildren().forEach(e -> {
                print(e, builder, ctx);
            });

            StringBuilderUtils.println("</" + node.getName() + ">", builder, node.getLevel());
        }
    }

    private void printFunction(Node node, StringBuilder builder, DocumentContext ctx) {
        Function function = new Function(node.getValue().get("value").getAsString().trim());
        if (FUNCTIONS.JsonObjectToXml.name().equals(function.getName())) {
            printJsonObjectToXml(node, builder, ctx, function);

        } else if (FUNCTIONS.JsonArrayToXml.name().equals(function.getName())) {
            printJsonArrayToXml(node, builder, ctx, function);

        }
    }

    private void printJsonObjectToXml(Node node, StringBuilder builder, DocumentContext ctx, Function function) {

        Object o = ctx.read(function.getParam());
        String json = new Gson().toJson(o);
        DocumentContext context = JsonPath.parse(json);

        StringBuilderUtils.println("<" + node.getName() + ">", builder, node.getLevel());
        node.getChildren().forEach(e -> {
            try {
                print(e, builder, context);
            } catch (Exception ex) {
                // log
            }
        });

        StringBuilderUtils.println("</" + node.getName() + ">", builder, node.getLevel());
    }

    private void printJsonArrayToXml(Node node, StringBuilder builder, DocumentContext ctx, Function function) {
        ArrayList<Object> list = ctx.read(function.getParam());

        list.forEach(o -> {
            String json = new Gson().toJson(o);
            DocumentContext context = JsonPath.parse(json);

            StringBuilderUtils.println("<" + node.getName() + ">", builder, node.getLevel());
            node.getChildren().forEach(e -> {
                try {
                    print(e, builder, context);

                } catch (Exception ex) {
                    // log
                }
            });

            StringBuilderUtils.println("</" + node.getName() + ">", builder, node.getLevel());
        });
    }

    private String extract(Function function, DocumentContext ctx) {
        if (FUNCTIONS.jsonpath.name().equals(function.getName())) {
            String jpath = function.getParam();
            if (!jpath.startsWith("$.")) {
                int firstPoint = jpath.indexOf(".");
                jpath = "$" + jpath.substring(firstPoint);
            }

            return ctx.read(jpath, String.class);

        } else {
            return null;

        }
    }

    private static boolean isSubRoot(Node node) {
        return NodeType.FOLDER.equals(node.getType())
                && node.getValue() != null
                && node.getValue().get("value") != null
                && node.getValue().get("value").getAsString().trim().length() > 0;
    }
}
