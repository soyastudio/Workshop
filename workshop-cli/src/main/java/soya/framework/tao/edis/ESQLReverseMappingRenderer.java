package soya.framework.tao.edis;

import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.T123W;
import soya.framework.tao.xs.XsNode;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class ESQLReverseMappingRenderer extends EdisRenderer {

    private File esql;

    public ESQLReverseMappingRenderer esql(File esql) {
        this.esql = esql;
        return this;
    }


    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledge) throws T123W.FlowExecutionException {
        return null;
    }

    private static class Visitor extends ESQLVisitor {

        private transient KnowledgeTree<SchemaTypeSystem, XsNode> knowledge;
        private transient Map<String, ESQLVisitor.Setter> assignments = new LinkedHashMap<>();

        private String xpath;

        public Visitor(KnowledgeTree<SchemaTypeSystem, XsNode> knowledge) {
            this.knowledge = knowledge;
        }

        @Override
        protected void visitComment(String line) {

        }

        @Override
        protected void visitDeclaration(String line) {

        }

        @Override
        protected void visitAssignment(String line) {

        }
    }
}
