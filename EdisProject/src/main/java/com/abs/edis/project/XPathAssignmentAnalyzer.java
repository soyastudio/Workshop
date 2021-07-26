package com.abs.edis.project;

import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.KnowledgeTreeNode;
import soya.framework.tao.T123W;
import soya.framework.tao.xs.XsNode;

import java.util.LinkedHashSet;
import java.util.Map;

public class XPathAssignmentAnalyzer extends EdisRenderer {
    private boolean enableLoopFeature;

    public XPathAssignmentAnalyzer enableLoopFeature() {
        enableLoopFeature = true;
        return this;
    }

    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase) throws T123W.FlowExecutionException {
        StringBuilder builder = new StringBuilder();
        if (enableLoopFeature) {
            Map<String, LinkedHashSet<EdisTask.Function>> loopFeature = loopFeature(knowledgeBase);

            knowledgeBase.paths().forEachRemaining(e -> {
                LinkedHashSet<EdisTask.Function> functions = loopFeature.get(e);
                if(functions.isEmpty()) {
                    builder.append(e).append("=").append(getAssignment(e, knowledgeBase)).append("\n");

                } else {
                    KnowledgeTreeNode<XsNode> treeNode = knowledgeBase.get(e);
                    if(XsNode.XsNodeType.Folder.equals(treeNode.origin().getNodeType())) {
                        EdisTask.Construction construction = treeNode.getAnnotation(NAMESPACE_CONSTRUCTION, EdisTask.Construction.class);
                        construction.add(functions.toArray(new EdisTask.Function[functions.size()]));
                        builder.append(e).append("=").append(construction.toString()).append("\n");

                    } else {
                        builder.append(e).append("=").append(EdisTask.Function.toString(functions.toArray(new EdisTask.Function[functions.size()]))).append("\n");

                    }

                }
            });
        } else {
            knowledgeBase.paths().forEachRemaining(e -> {
                builder.append(e).append("=").append(getAssignment(e, knowledgeBase)).append("\n");
            });
        }

        return builder.toString();
    }

    protected String getAssignment(String path, KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase) {
        KnowledgeTreeNode<XsNode> node = knowledgeBase.get(path);
        if (XsNode.XsNodeType.Folder.equals(node.origin().getNodeType()) && node.getAnnotation(NAMESPACE_CONSTRUCTION) != null) {
            EdisTask.Construction construction = node.getAnnotation(NAMESPACE_CONSTRUCTION, EdisTask.Construction.class);

            return construction.toString();

        } else {
            EdisTask.Assignment assignment = node.getAnnotation(NAMESPACE_ASSIGNMENT, EdisTask.Assignment.class);
            if (assignment == null) {
                return "";

            } else {
                EdisTask.MappingRule rule = EdisTask.MappingRule.fromString(assignment.rule);
                if (!assignment.functions.isEmpty()) {
                    return EdisTask.Function.toString(assignment.functions.toArray(new EdisTask.Function[assignment.functions.size()]));

                } else if (EdisTask.MappingRule.DirectMapping.equals(rule)) {
                    if(assignment.source != null) {
                        String param = "$." + assignment.source.replaceAll("/", ".");
                        return EdisTask.Function.newInstance(FUNCTION_ASSIGN, new String[]{param}).toString();

                    } else {
                        return "???";
                    }

                } else if (EdisTask.MappingRule.DefaultMapping.equals(rule)) {
                    String value = getDefaultValue(assignment.rule);
                    if (value != null) {
                        return EdisTask.Function.newInstance(FUNCTION_DEFAULT, new String[]{value}).toString();

                    } else {
                        return "DEFAULT(???)";

                    }
                } else if (assignment.source != null) {
                    return EdisTask.Function.newInstance(FUNCTION_DEFAULT, new String[]{"???"}).toString();

                } else {
                    return "???";

                }
            }

        }
    }

    private String getDefaultValue(String rule) {
        try {
            return rule.substring(rule.indexOf("'"), rule.lastIndexOf("'") + 1);

        } catch (Exception e) {
            return null;
        }
    }
}
