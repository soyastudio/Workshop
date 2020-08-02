package soya.framework.tools.iib;

import soya.framework.tools.util.StringBuilderUtils;

public class JsonToCmmESQL extends CmmESQLGenerator {

    protected JsonToCmmESQL(Node root) {
        super(root);
    }

    @Override
    protected void declareInputVariables(StringBuilder builder) {
        StringBuilderUtils.indent(builder, 2);
        builder.append("-- Declare Variables for Input Message").append("\n");

        StringBuilderUtils.indent(builder, 2);
        builder.append("DECLARE ").append(getInputRootVariable()).append(" REFERENCE TO InputRoot.JSON.Data;").append("\n");
        StringBuilderUtils.println(builder);
    }

    @Override
    protected void printSubRoot(StringBuilder builder, Node node) {
        String exp = node.getValue().get("value").getAsString();
        Function function = new Function(exp);
        if (FUNCTIONS.JsonObjectToXml.name().equals(function.getName())) {
            printJsonObjectToXML(function, node, builder);
        } else if (FUNCTIONS.JsonArrayToXml.name().equals(function.getName())) {
            printJsonArrayToXML(function, node, builder);
        }
    }

    protected void printJsonObjectToXML(Function function, Node node, StringBuilder builder) {
        String var = function.getParam().replace("$", getInputRootVariable());
        StringBuilderUtils.println("-- Process " + function.getName() + " from " + var + " to" + node.getName(), builder, 2);
        int lastPoint = var.lastIndexOf(".");
        String jsonObject = "_" + var.substring(lastPoint + 1);
        StringBuilderUtils.println("DECLARE " + jsonObject + " REFERENCE TO " + var + ";", builder, 2);
        // StringBuilderUtils.println("IF (" + jsonObject + " IS NOT NULL) THEN ", builder, 2);

        StringBuilderUtils.println("CREATE LASTCHILD OF "
                + getVariableName(node.getParent())
                + " AS "
                + getVariableName(node.getName())
                + " TYPE XMLNSC.Folder NAME '"
                + node.getFullName() + "';", builder, 2);
        StringBuilderUtils.println(builder);

        node.getChildren().forEach(e -> {
            printNode(builder, e);
        });

        // StringBuilderUtils.println("END IF;", builder, 2);
        StringBuilderUtils.println(builder);
    }

    protected void printJsonArrayToXML(Function function, Node node, StringBuilder builder) {
        String var = function.getParam().replace("$", getInputRootVariable());
        if (!var.endsWith(".Item")) {
            var = var + ".Item";
        }

        StringBuilderUtils.println("-- Process " + function.getName() + " for " + node.getName(), builder, 2);
        StringBuilderUtils.println("DECLARE _item REFERENCE TO " + var + ";", builder, 2);
        StringBuilderUtils.println("WHILE LASTMOVE(_item) DO", builder, 2);

        StringBuilderUtils.println("-- Generate Node " + node.getName(), builder, 3);
        StringBuilderUtils.println("CREATE LASTCHILD OF "
                + getVariableName(node.getParent())
                + " AS "
                + getVariableName(node.getName())
                + " TYPE XMLNSC.Folder NAME '"
                + node.getFullName() + "';", builder, 3);
        StringBuilderUtils.println(builder);

        node.getChildren().forEach(e -> {
            printNode(builder, e);
        });

        StringBuilderUtils.println(builder);
        StringBuilderUtils.println("MOVE _item NEXTSIBLING;", builder, 3);
        StringBuilderUtils.println("END WHILE;", builder, 2);
        StringBuilderUtils.println(builder);
    }

}
