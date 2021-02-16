package soya.framework.tao.edis;

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
            Map<String, LinkedHashSet<Function>> loopFeature = loopFeature(knowledgeBase);
            knowledgeBase.paths().forEachRemaining(e -> {
                LinkedHashSet<Function> functions = loopFeature.get(e);
                if(functions.isEmpty()) {
                    builder.append(e).append("=").append(getAssignment(e, knowledgeBase)).append("\n");

                } else {
                    KnowledgeTreeNode<XsNode> treeNode = knowledgeBase.get(e);
                    if(XsNode.XsNodeType.Folder.equals(treeNode.origin().getNodeType())) {
                        Construction construction = treeNode.getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);
                        construction.add(functions.toArray(new Function[functions.size()]));
                        builder.append(e).append("=").append(construction.toString()).append("\n");

                    } else {
                        builder.append(e).append("=").append(Function.toString(functions.toArray(new Function[functions.size()]))).append("\n");

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
            Construction construction = node.getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);

            return construction.toString();

        } else {
            Assignment assignment = node.getAnnotation(NAMESPACE_ASSIGNMENT, Assignment.class);
            if (assignment == null) {
                return "";

            } else {
                MappingRule rule = MappingRule.fromString(assignment.rule);
                if (!assignment.functions.isEmpty()) {
                    return Function.toString(assignment.functions.toArray(new Function[assignment.functions.size()]));

                } else if (MappingRule.DirectMapping.equals(rule)) {
                    String param = "$." + assignment.source.replaceAll("/", ".");
                    return Function.newInstance(FUNCTION_ASSIGN, new String[]{param}).toString();

                } else if (MappingRule.DefaultMapping.equals(rule)) {
                    String value = getDefaultValue(assignment.rule);
                    if (value != null) {
                        return Function.newInstance(FUNCTION_DEFAULT, new String[]{value}).toString();

                    } else {
                        return "DEFAULT(???)";

                    }
                } else if (assignment.source != null) {
                    return Function.newInstance(FUNCTION_DEFAULT, new String[]{"???"}).toString();

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
