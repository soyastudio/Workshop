package soya.framework.tools.xmlbeans;

import java.util.LinkedHashMap;
import java.util.Map;

public class AssignmentAnnotator extends MappingFeatureSupport implements Buffalo.Annotator<XmlSchemaBase> {

    private transient Map<String, ConstructNode> constructions = new LinkedHashMap<>();

    @Override
    public void annotate(XmlSchemaBase base) {
        base.getMappings().entrySet().forEach(e -> {
            String key = e.getKey();
            XmlSchemaBase.MappingNode node = e.getValue();

            if (node.getAnnotation(MAPPING) != null) {
                Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
                if(mapping.assignment == null) {
                    String assignment = getAssignment(mapping, constructions);

                    node.annotateAsMappedElement(MAPPING, "assignment", assignment);
                }

            } else if (node.getNodeType().equals(XmlSchemaBase.NodeType.Folder) && node.getAnnotation(MAPPED) != null) {
                if(node.getAnnotation(CONSTRUCT) != null) {
                    Construct construct = node.getAnnotation(CONSTRUCT, Construct.class);
                    construct.loops.forEach(n -> {
                        constructions.put(n.sourcePath, n);
                    });

                    construct.constructors.forEach(n -> {
                        constructions.put(n.sourcePath, n);
                    });
                }

            }
        });
    }

    protected String getAssignment(Mapping mapping, Map<String, ConstructNode> constructions) {
        if (mapping.assignment != null) {
            return mapping.assignment;

        } else if (mapping.mappingRule != null) {
            if (mapping.mappingRule.toUpperCase().startsWith("DEFAULT TO ")) {
                if (mapping.mappingRule.contains("'")) {
                    int start = mapping.mappingRule.indexOf("'");
                    int end = mapping.mappingRule.lastIndexOf("'");

                    return mapping.mappingRule.substring(start, end + 1);

                } else {
                    return UNKNOWN;
                }

            } else if (mapping.mappingRule.toUpperCase().contains("DIRECT")
                    && mapping.mappingRule.toUpperCase().contains("MAPPING")
                    && mapping.sourcePath != null) {

                String path = mapping.sourcePath.trim();
                if (path.contains(" ") || path.contains("\n")) {
                    return UNKNOWN;

                } else {
                    String parent = findParent(path, constructions);
                    if (parent != null) {
                        path = getAssignment(path, parent);

                    } else {
                        path = INPUT_ROOT + "." + path.replaceAll("/", ".");
                    }

                    return path;
                }
            } else {
                return UNKNOWN;

            }

        } else {
            return null;
        }
    }

    private String getAssignment(String path, String parent) {
        ConstructNode node = constructions.get(parent);
        if (node != null) {
            return node.variable + path.substring(parent.length()).replaceAll("/", ".");

        } else {
            return UNKNOWN;
        }
    }

    protected String findParent(String path, Map<String, ConstructNode> constructions) {
        String parent = path;
        while (parent.contains("/") && !constructions.containsKey(parent)) {
            parent = parent.substring(0, parent.lastIndexOf("/"));
        }

        return constructions.containsKey(parent) ? parent : null;
    }

}
