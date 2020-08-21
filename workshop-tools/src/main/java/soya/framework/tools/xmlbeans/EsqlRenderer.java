package soya.framework.tools.xmlbeans;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import soya.framework.tools.util.StringBuilderUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class EsqlRenderer implements Buffalo.Renderer<XmlSchemaBase> {
    public static final String DOCUMENT_ROOT = "xmlDocRoot";

    private static Gson GSON = new Gson();

    private String brokerSchema;
    private String moduleName = "MODULE_NAME";

    private Map<String, WhileLoop> loopMap = new LinkedHashMap<>();

    public void setBrokerSchema(String brokerSchema) {
        this.brokerSchema = brokerSchema;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String render(XmlSchemaBase base) {
        StringBuilder builder = new StringBuilder();
        if (brokerSchema != null && brokerSchema.trim().length() > 0) {
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

    private void printNode(XmlSchemaBase.MappingNode e, StringBuilder builder) {
        if (e.getLevel() > 1) {
            StringBuilderUtils.println("--  " + e.getPath(), builder, e.getLevel());
        }

        if (e.getAnnotation("loop") != null) {
            JsonArray loops = e.getAnnotation("loop", JsonArray.class);
            loops.forEach(l -> {
                WhileLoop wl = GSON.fromJson(l, WhileLoop.class);
                wl.parent = findParent(wl.sourcePath);
                loopMap.put(wl.sourcePath, wl);

                if (wl.parent == null) {
                    StringBuilderUtils.println("DECLARE " + wl.variable + " REFERENCE TO _inputRoot." + ";", builder, e.getLevel());
                }
                StringBuilderUtils.println("WHILE LASTMOVE(" + wl.variable + ") DO", builder, e.getLevel());
                StringBuilderUtils.println(builder);

                if (XmlSchemaBase.NodeType.Folder.equals(e.getNodeType())) {
                    if (e.getParent() != null) {
                        StringBuilderUtils.println("DECLARE " + e.getAlias() + " REFERENCE TO " + e.getParent().getAlias() + ";", builder, e.getLevel());
                        StringBuilderUtils.println("CREATE LASTCHILD OF " + e.getParent().getAlias() + " AS " + e.getAlias() + " TYPE XMLNSC.Folder NAME 'Abs:" + e.getName() + "';"
                                , builder, e.getLevel());
                        StringBuilderUtils.println(builder);

                    }

                    e.getChildren().forEach(n -> {
                        printNode(n, builder);
                    });

                } else if (XmlSchemaBase.NodeType.Field.equals(e.getNodeType())) {
                    StringBuilderUtils.println("SET " + e.getParent().getAlias() + ".(XMLNSC.Field)Abs:" + e.getName() + " = '" + "?" + "';", builder, e.getLevel());

                } else if (XmlSchemaBase.NodeType.Attribute.equals(e.getNodeType())) {
                    StringBuilderUtils.println("SET " + e.getParent().getAlias() + ".(XMLNSC.Attribute)Abs:" + e.getName() + " = '" + "?" + "';", builder, e.getLevel());

                }

                StringBuilderUtils.println("MOVE " + wl.variable + " NEXTSIBLING;", builder, e.getLevel());
                StringBuilderUtils.println("END WHILE;", builder, e.getLevel());
                StringBuilderUtils.println(builder);

            });

        } else {
            if (XmlSchemaBase.NodeType.Folder.equals(e.getNodeType())) {
                if (e.getParent() != null) {
                    StringBuilderUtils.println("DECLARE " + e.getAlias() + " REFERENCE TO " + e.getParent().getAlias() + ";", builder, e.getLevel());
                    StringBuilderUtils.println("CREATE LASTCHILD OF " + e.getParent().getAlias() + " AS " + e.getAlias() + " TYPE XMLNSC.Folder NAME 'Abs:" + e.getName() + "';"
                            , builder, e.getLevel());
                    StringBuilderUtils.println(builder);

                }

                e.getChildren().forEach(n -> {
                    printNode(n, builder);
                });

            } else if (XmlSchemaBase.NodeType.Field.equals(e.getNodeType())) {
                StringBuilderUtils.println("SET " + e.getParent().getAlias() + ".(XMLNSC.Field)Abs:" + e.getName() + " = '" + "?" + "';", builder, e.getLevel());

            } else if (XmlSchemaBase.NodeType.Attribute.equals(e.getNodeType())) {
                StringBuilderUtils.println("SET " + e.getParent().getAlias() + ".(XMLNSC.Attribute)Abs:" + e.getName() + " = '" + "?" + "';", builder, e.getLevel());

            }

        }

        StringBuilderUtils.println(builder);
    }

    private WhileLoop findParent(String path) {
        String token = path;
        int index = token.lastIndexOf('/');
        while (index > 0) {
            token = token.substring(0, index);
            if (loopMap.containsKey(token)) {
                return loopMap.get(token);
            }
            index = token.lastIndexOf('/');
        }

        return null;
    }

    private String getAssignment(WhileLoop wl, String inputRoot) {
        return inputRoot;

    }

    static class WhileLoop {
        private String name;
        private String sourcePath;
        private String variable;

        private WhileLoop parent;
    }
}
