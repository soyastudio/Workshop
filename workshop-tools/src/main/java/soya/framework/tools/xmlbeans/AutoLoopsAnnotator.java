package soya.framework.tools.xmlbeans;

import java.util.LinkedHashMap;
import java.util.Map;

public class AutoLoopsAnnotator implements Buffalo.Annotator<XmlSchemaBase>, MappingFeature {

    private transient Map<String, WhileLoop> loopMap = new LinkedHashMap<>();

    @Override
    public void annotate(XmlSchemaBase base) {
        base.getMappings().entrySet().forEach(e -> {

            XmlSchemaBase.MappingNode node = e.getValue();
            Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);

            if (mapping != null && mapping.sourcePath != null && mapping.sourcePath.contains("[*]")) {
                XmlSchemaBase.MappingNode parent = findParent(e.getValue());
                if (mapping.sourcePath.endsWith("[*]") && node.getNodeType().equals(XmlSchemaBase.NodeType.Field)) {
                    if (parent != null) {
                        String sourcePath = mapping.sourcePath;
                        WhileLoop loop = loopMap.get(sourcePath);
                        if (loop == null) {
                            loop = new WhileLoop();
                            loop.sourcePath = sourcePath;

                            String name = sourcePath.substring(0, sourcePath.lastIndexOf("[*]"));
                            if (name.lastIndexOf("/") > 0) {
                                name = name.substring(name.lastIndexOf("/") + 1);
                            }

                            loop.variable = "_" + name;
                            loop.name = name + "_loop";

                            annotateLoop(parent, loop);
                            loopMap.put(sourcePath, loop);
                        }

                        annotateLoopAssignment(node, loop);
                    }

                } else if (mapping.sourcePath.contains("[*]/")) {
                    if (parent != null) {
                        String sourcePath = mapping.sourcePath;
                        sourcePath = sourcePath.substring(0, sourcePath.lastIndexOf("[*]/") + 3);

                        WhileLoop loop = loopMap.get(sourcePath);
                        if (loop == null) {
                            loop = new WhileLoop();
                            loop.sourcePath = sourcePath;

                            String name = sourcePath.substring(0, sourcePath.lastIndexOf("[*]"));
                            if (name.lastIndexOf("/") > 0) {
                                name = name.substring(name.lastIndexOf("/") + 1);
                            }

                            loop.variable = "_" + name;
                            loop.name = name + "_loop";

                            annotateLoop(parent, loop);
                            //parent.annotateAsArrayElement(LOOP, loop);
                            loopMap.put(sourcePath, loop);

                        }
                        annotateLoopAssignment(node, loop);
                    }
                }
            }
        });
    }

    private void annotateLoop(XmlSchemaBase.MappingNode node, WhileLoop loop) {
        String construction = node.getAnnotation(CONSTRUCTION, String.class);

        StringBuilder builder = new StringBuilder("loop(").append(loop.name).append(")")
                .append(".from(").append(loop.sourcePath).append(")")
                .append(".as(").append(loop.variable).append(")").append(".end()");

        String wl = builder.toString();
        if (construction == null) {
            construction = wl;

        } else if (!construction.contains(wl)) {
            construction = construction + "." + wl;
        }

        node.annotate(CONSTRUCTION, construction);

    }

    private void annotateLoopAssignment(XmlSchemaBase.MappingNode node, WhileLoop loop) {
        Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
        String sourcePath = mapping.sourcePath;
        if (sourcePath != null && sourcePath.startsWith(loop.sourcePath)) {
            String assignment = sourcePath.substring(loop.sourcePath.length()).replaceAll("/", ".");
            assignment = new StringBuilder("for(").append(loop.name).append(")")
                    .append(".assign(").append(loop.variable).append(assignment).append(")")
                    .append(".end()").toString();

            node.annotateAsMappedElement(MAPPING, "assignment", assignment);
        }
    }

    private XmlSchemaBase.MappingNode findParent(XmlSchemaBase.MappingNode mappingNode) {
        XmlSchemaBase.MappingNode node = mappingNode.getParent();
        while (node != null) {
            Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
            if (!node.getCardinality().endsWith("-1")) {
                break;
            }
            node = node.getParent();
        }

        return node;
    }

    private WhileLoop findParent(String path) {
        if (path == null) {
            return null;
        }

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

}
