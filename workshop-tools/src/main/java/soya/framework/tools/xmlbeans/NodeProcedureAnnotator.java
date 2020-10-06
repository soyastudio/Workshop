package soya.framework.tools.xmlbeans;

import java.util.List;
import java.util.Map;

public class NodeProcedureAnnotator extends NodeMappingAnnotator {

    private String procedure;
    private List<Map<String, String>> parameters;
    private String body;

    @Override
    protected void annotate(XmlSchemaBase.MappingNode node) {
        if(procedure == null) {
            throw new IllegalArgumentException("procedure is not set.");
        }

        if(parameters == null) {
            throw new IllegalArgumentException("parameters is not set.");
        }

        Procedure proc = new Procedure();
        proc.name = procedure;
        parameters.forEach(e -> {
            ProcedureParameter param = GSON.fromJson(GSON.toJson(e), ProcedureParameter.class);
            proc.parameters.add(param);

        });
        proc.body = body;

        node.annotate(PROCEDURE, proc);
    }
}
