package soya.framework.tools.xmlbeans;

import soya.framework.tools.util.StringBuilderUtils;

public class EsqlRenderer implements XmlGenerator.Renderer {
    public static final String DOCUMENT_ROOT = "xmlDocRoot";

    private String brokerSchema;
    private String moduleName = "MODULE_NAME";

    public void setBrokerSchema(String brokerSchema) {
        this.brokerSchema = brokerSchema;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String render(XmlGenerator base) {
        StringBuilder builder = new StringBuilder();
        if(brokerSchema != null && brokerSchema.trim().length() > 0) {
            builder.append("BROKER SCHEMA ").append(brokerSchema.trim()).append(";").append("\n\n");
        }

        builder.append("CREATE COMPUTE MODULE ").append(moduleName);
        StringBuilderUtils.println(builder, 2);

        StringBuilderUtils.println("CREATE FUNCTION Main() RETURNS BOOLEAN", builder, 1);
        begin(builder, 1);

        declareNamespace(builder);
        // Declare Output Domain
        StringBuilderUtils.indent(builder, 2);
        builder.append("-- Declare Output Message Root").append("\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("CREATE LASTCHILD OF OutputRoot DOMAIN ").append("'XMLNSC'").append(";\n\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("DECLARE xmlDocRoot REFERENCE TO OutputRoot.XMLNSC.").append(base.getRoot().getName()).append(";\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("CREATE LASTCHILD OF OutputRoot.").append("XMLNSC AS ").append(DOCUMENT_ROOT).append(" TYPE XMLNSC.Folder NAME '").append(base.getRoot().getName()).append("'").append(";\n");
        StringBuilderUtils.println(builder);

        printNode(base.getRoot(), builder);



        StringBuilderUtils.println("RETURN TRUE;", builder, 2);
        StringBuilderUtils.println("END;", builder, 1);
        StringBuilderUtils.println(builder);

        builder.append("END MODULE;");

        return builder.toString();
    }

    private void begin(StringBuilder builder, int indent) {
        for (int i = 0; i < indent; i++) {
            builder.append("\t");
        }
        builder.append("BEGIN").append("\n");
    }

    private void declareNamespace(StringBuilder builder) {
        StringBuilderUtils.indent(builder, 2);
        builder.append("-- Declare Namespace").append("\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("DECLARE ").append("Abs").append(" NAMESPACE ").append("'https://collab.safeway.com/it/architecture/info/default.aspx'").append(";").append("\n");

        StringBuilderUtils.println(builder);
    }

    private void printNode(XmlGenerator.MappingNode e, StringBuilder builder) {
        if(e.getLevel() > 1) {
            StringBuilderUtils.println("--  " + e.getPath(), builder, e.getLevel());
        }

        if (XmlGenerator.NodeType.Folder.equals(e.getNodeType())) {
            if (e.getParent() != null) {
                StringBuilderUtils.println("DECLARE " + e.getAlias() + " REFERENCE TO " + e.getParent().getAlias() + ";", builder, e.getLevel());
                StringBuilderUtils.println("CREATE LASTCHILD OF " + e.getParent().getAlias() + " AS " + e.getAlias() + " TYPE XMLNSC.Folder NAME 'Abs:" + e.getName() + "';"
                        , builder, e.getLevel());
                StringBuilderUtils.println(builder);

            }

            e.getChildren().forEach(n -> {
                printNode(n, builder);
            });

        } else if (XmlGenerator.NodeType.Field.equals(e.getNodeType())) {
            StringBuilderUtils.println("SET " + e.getParent().getAlias() + ".(XMLNSC.Field)Abs:" + e.getName() + " = '" + "?" + "';", builder, e.getLevel());

        } else if (XmlGenerator.NodeType.Attribute.equals(e.getNodeType())) {
            StringBuilderUtils.println("SET " + e.getParent().getAlias() + ".(XMLNSC.Attribute)Abs:" + e.getName() + " = '" + "?" + "';", builder, e.getLevel());

        }

        StringBuilderUtils.println(builder);
    }
}
