package com.abs.edis.project;

import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.KnowledgeTreeNode;
import soya.framework.tao.T123W;
import soya.framework.tao.xs.XsNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ESQLValidator extends EdisRenderer {

    private File esql;

    public ESQLValidator esql(File esql) {
        this.esql = esql;
        return this;
    }

    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledge) throws T123W.FlowExecutionException {
        try {
            Visitor visitor = new Visitor(knowledge);
            visitor.visit(esql);

            StringBuilder builder = new StringBuilder();
            knowledge.paths().forEachRemaining(e -> {
                KnowledgeTreeNode<XsNode> node = knowledge.get(e);

                if (node.getAnnotation(EdisTask.NAMESPACE_ASSIGNMENT) != null) {
                    if (!visitor.assignments.containsKey(e)) {
                        builder.append("Not assigned: ")
                                .append(e).append(": ")
                                .append(node.getAnnotation(EdisTask.NAMESPACE_ASSIGNMENT, EdisTask.Assignment.class).toString())
                                .append("\n");

                    } else {
                        EdisTask.Assignment assignment = node.getAnnotation(EdisTask.NAMESPACE_ASSIGNMENT, EdisTask.Assignment.class);
                        ESQLVisitor.Setter setter = visitor.assignments.get(e);

                        String token = setter.value;
                        if (token.contains("CAST(")) {
                            token = token.substring(token.indexOf("CAST(") + "CAST(".length());
                            if (token.contains(" ")) {
                                token = token.substring(0, token.indexOf(" "));
                            }
                        }

                        if (token.indexOf(".") > 0 && assignment.source != null) {
                            token = token.substring(token.lastIndexOf('.'));
                            String src = "$." + assignment.source.replaceAll("/", ".");

                            if (!src.endsWith(token)) {
                                System.out.println("ERROR:  " + e + ": "
                                        + node.getAnnotation(EdisTask.NAMESPACE_ASSIGNMENT, EdisTask.Assignment.class).toString()
                                        + " " + token);
                            }
                        }
                    }
                }
            });

            return builder.toString();

        } catch (FileNotFoundException e) {
            throw new T123W.FlowExecutionException(e);

        } catch (IOException e) {
            throw new T123W.FlowExecutionException(e);

        }
    }

    private static class Visitor extends ESQLVisitor {

        private transient KnowledgeTree<SchemaTypeSystem, XsNode> knowledge;
        private transient Map<String, Setter> assignments = new LinkedHashMap<>();

        private String xpath;

        public Visitor(KnowledgeTree<SchemaTypeSystem, XsNode> knowledge) {
            this.knowledge = knowledge;
        }

        @Override
        protected void visitComment(String line) {
            String token = line.trim().substring(3);
            if (knowledge.contains(token)) {
                xpath = token;
            }
        }

        @Override
        protected void visitDeclaration(String line) {

        }

        @Override
        protected void visitAssignment(String line) {
            Setter setter = new Setter(cursor, line);
            if (xpath != null) {
                assignments.put(xpath, setter);
                xpath = null;
            }
        }
    }


}
