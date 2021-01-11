package com.albertsons.edis.tools.xmlbeans;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class XPathAutoAnnotateRenderer extends XmlSchemaBaseRenderer implements MappingFeature {

    private boolean printUnmapped = true;
    private transient Map<String, WhileLoop> loops = new LinkedHashMap<>();

    @Override
    public String render(XmlSchemaBase base) {

        Set<Mapper> mappers = findLoops(base);
        mappers.forEach(e -> {
            XmlSchemaBase.MappingNode node = base.get(e.targetPath);
            node.annotate(CONSTRUCT, null);
        });

        mappers.forEach(e -> {
            WhileLoop loop = annotate(e, base);
            loops.put(loop.sourcePath, loop);
        });

        StringBuilder builder = new StringBuilder();
        base.getMappings().entrySet().forEach(e -> {
            String key = e.getKey();
            XmlSchemaBase.MappingNode node = e.getValue();

            if (node.getAnnotation(MAPPING) != null) {
                String assignment = getAssignment(node.getAnnotation(MAPPING, Mapping.class), loops);
                builder.append(key).append("=").append(assignment).append("\n");

            } else if (node.getNodeType().equals(XmlSchemaBase.NodeType.Folder)) {
                if (node.getAnnotation(MAPPED) != null) {
                    builder.append(key).append("=");
                    if (node.getAnnotation(CONSTRUCT) != null) {
                        builder.append(node.getAnnotation(CONSTRUCT, Construct.class).toString());
                    }
                    builder.append("\n");

                } else if (printUnmapped) {
                    builder.append("# ").append(key).append("=").append("\n");
                }

            } else if (printUnmapped) {
                builder.append("# ").append(key).append("=").append("\n");

            }
        });

        return builder.toString();
    }


    protected String getAssignment(Mapping mapping, Map<String, WhileLoop> constructions) {
        if (mapping.mappingRule != null) {
            if (mapping.mappingRule.toUpperCase().startsWith("DEFAULT TO ")) {
                if (mapping.mappingRule.contains("'")) {
                    int start = mapping.mappingRule.indexOf("'");
                    int end = mapping.mappingRule.lastIndexOf("'");

                    return new StringBuilder("ASSIGN(")
                            .append(mapping.mappingRule.substring(start, end + 1))
                            .append(")")
                            .toString();

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
                    String parent = findParent(path, loops);
                    if (parent != null) {
                        path = getAssignment(path, parent);

                    } else {
                        path = new StringBuilder("ASSIGN(")
                                .append(INPUT_ROOT).append(".")
                                .append(path.replaceAll("/", "."))
                                .append(")")
                                .toString();
                    }

                    return path;
                }
            } else {
                return UNKNOWN;

            }

        } else {
            return "";
        }
    }

    private String getAssignment(String path, String parent) {
        WhileLoop loop = loops.get(parent);
        if (loop != null) {
            return new StringBuilder("FOR(")
                    .append(loop.name)
                    .append(")")
                    .append(".")
                    .append("ASSIGN(")
                    .append(loop.variable)
                    .append(path.substring(parent.length()).replaceAll("/", "."))
                    .append(")")
                    .toString();

        } else {
            return UNKNOWN;
        }
    }

    protected String findParent(String path, Map<String, WhileLoop> constructions) {
        String parent = path;
        while (parent.contains("/") && !constructions.containsKey(parent)) {
            parent = parent.substring(0, parent.lastIndexOf("/"));
        }

        return constructions.containsKey(parent) ? parent : null;
    }


    private WhileLoop annotate(Mapper mapper, XmlSchemaBase base) {
        XmlSchemaBase.MappingNode node = base.get(mapper.getTargetPath());
        Construct construct = node.getAnnotation(CONSTRUCT, Construct.class);
        if (construct == null) {
            construct = new Construct();
        }

        String source = mapper.getSourcePath();
        String name = "LOOP_" + source.replaceAll("/", "_").replaceAll("\\[\\*\\]", "").toUpperCase();

        String variable = source;
        if (variable.contains("/")) {
            variable = variable.substring(variable.lastIndexOf("/") + 1);
        }
        if (variable.endsWith("[*]")) {
            variable = variable.substring(0, variable.length() - 3);
        }
        variable = "_" + variable;

        WhileLoop loop = new WhileLoop();
        loop.sourcePath = source;
        loop.name = name;
        loop.variable = variable;
        construct.loops.add(loop);

        node.annotate(CONSTRUCT, construct);

        return loop;

    }
}
