package soya.framework.tools.iib;

import soya.framework.tools.util.StringBuilderUtils;

public class XmlToCmmESQL extends CmmESQLGenerator {

    protected XmlToCmmESQL(Node node) {
        super(node);
    }

    @Override
    protected void declareInputVariables(StringBuilder builder) {
        StringBuilderUtils.println("-- Declare Variables for Input Message", builder, 2);
        StringBuilderUtils.println("DECLARE rowset REFERENCE TO InputRoot.XMLNSC.ROWSET;", builder, 2);
        StringBuilderUtils.println("DECLARE _row REFERENCE TO rowset.ROW;", builder, 2);
        StringBuilderUtils.println(builder);
    }

    protected void printBusinessObjectData(StringBuilder builder) {

        StringBuilderUtils.println("-- Construct Business Object Data", builder, 2);
        StringBuilderUtils.println("CREATE LASTCHILD OF " + DOCUMENT_ROOT + " AS " + getVariableName(dataObjectNode.getName()) + " TYPE XMLNSC.Folder NAME '" + dataObjectNode.getName() + "';",
                builder, 2);
        StringBuilderUtils.println(builder);

        dataObjectNode.getChildren().forEach(o -> {
            printNode(builder, o);
        });

        StringBuilderUtils.println(builder);

    }

    @Override
    protected void printSubRoot(StringBuilder builder, Node node) {

    }

}
