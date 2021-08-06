package com.abs.edis.schema;

import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.T123W;
import soya.framework.tao.util.StringBuilderUtils;
import soya.framework.tao.xs.XsNode;

public class JsonEsqlRenderer extends EsqlRenderer {

    public JsonEsqlRenderer brokerSchema(String brokerSchema) {
        this.brokerSchema = brokerSchema;
        return this;
    }

    public JsonEsqlRenderer moduleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public JsonEsqlRenderer inputRootVariable(String inputRootVariable) {
        this.inputRootVariable = inputRootVariable;
        return this;
    }

    public JsonEsqlRenderer inputRootReference(String inputRootReference) {
        this.inputRootReference = inputRootReference;
        return this;
    }

    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase) throws T123W.FlowExecutionException {
        this.knowledgeBase = knowledgeBase;

        StringBuilder builder = new StringBuilder();
        if (brokerSchema != null && brokerSchema.trim().length() > 0) {
            builder.append("BROKER SCHEMA ").append(brokerSchema.trim()).append("\n\n");
        }

        builder.append("CREATE COMPUTE MODULE ").append(moduleName);
        StringBuilderUtils.println(builder, 2);

        // UDP:
        StringBuilderUtils.println("-- Declare UDPs", builder, 1);
        StringBuilderUtils.println("DECLARE VERSION_ID EXTERNAL CHARACTER '1.0.0';", builder, 1);
        StringBuilderUtils.println("DECLARE SYSTEM_ENVIRONMENT_CODE EXTERNAL CHARACTER 'PROD';", builder, 1);
        StringBuilderUtils.println(builder);

        StringBuilderUtils.println("CREATE FUNCTION Main() RETURNS BOOLEAN", builder, 1);
        begin(builder, 1);

        StringBuilderUtils.println("-- Declare Array Index Variables:", builder, 2);
        for (String index: INDEX) {
            StringBuilderUtils.println("DECLARE " + index + " INTEGER;",
                    builder, 2);
        }
        StringBuilderUtils.println(builder);

        declareInputRoot(builder);

        // Declare Output Domain
        Construction rootConstruction = knowledgeBase.root().getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);
        StringBuilderUtils.indent(builder, 2);
        builder.append("-- Declare Output Message Root").append("\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("CREATE FIELD OutputRoot.JSON.Data;").append("\n");
        StringBuilderUtils.indent(builder, 2);
        builder.append("DECLARE ").append(rootConstruction.getAlias()).append(" REFERENCE TO OutputRoot.JSON.Data;").append("\n");
        StringBuilderUtils.println(builder);

        printNode(knowledgeBase.root(), builder, 2);

        StringBuilderUtils.println("RETURN TRUE;", builder, 2);
        StringBuilderUtils.println("END;", builder, 1);
        StringBuilderUtils.println(builder);

        //printProcedures(builder);

        builder.append("END MODULE;");

        return builder.toString();
    }
}
