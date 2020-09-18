package soya.framework.tools.xmlbeans;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AssignmentAnnotator implements Buffalo.Annotator<XmlSchemaBase>, MappingFeature {

    private List<?> globalVariables;

    private boolean autoAnnotateLoop;
    private List<Map<String, String>> loops;

    private List<?> assignments;

    private transient Map<String, WhileLoop> loopMap = new LinkedHashMap<>();

    @Override
    public void annotate(XmlSchemaBase base) {
        // Global Variables:
        if (globalVariables != null) {
            annotateGlobalVariables(base);
        }

        // Loops:
        if (autoAnnotateLoop) {
            annotateLoops(base);

        } else if (loops != null) {
            loops.forEach(l -> {
                String sourcePath = l.get("sourcePath");
                String targetPath = l.get("targetPath");

                String baseName = sourcePath.substring(0, sourcePath.lastIndexOf("[*]"));
                int slash = baseName.lastIndexOf("/");
                if(slash > 0) {
                    baseName = baseName.substring(slash + 1);
                }

                String name = l.get("name");
                if(name == null || name.trim().length() == 0) {
                    name = baseName + "_loop";
                }

                String variable = l.get("variable");
                if(variable == null || variable.trim().length() == 0) {
                    variable = "_" + baseName;
                }

                WhileLoop whileLoop = new WhileLoop();
                whileLoop.sourcePath = sourcePath;
                whileLoop.name = name;
                whileLoop.variable = variable;

                if (!loopMap.containsKey(whileLoop.sourcePath)) {
                    XmlSchemaBase.MappingNode node = base.get(targetPath);
                    loopMap.put(whileLoop.sourcePath, whileLoop);
                    node.annotateAsArrayElement(LOOP, whileLoop);
                }
            });
        }

        // Auto Assignments:
        base.getMappings().entrySet().forEach(e -> {
            XmlSchemaBase.MappingNode node = e.getValue();
            if (node.getAnnotation(LOOP) != null) {
                try {
                    WhileLoop[] whileLoops = node.getAnnotation(LOOP, WhileLoop[].class);
                    for (WhileLoop whileLoop : whileLoops) {
                        whileLoop.parent = findParent(whileLoop.sourcePath);
                        loopMap.put(whileLoop.sourcePath, whileLoop);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            if (node.getAnnotation(MAPPING) != null) {
                Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
                WhileLoop whileLoop = findParent(mapping.sourcePath);
                if (whileLoop != null) {
                    node.annotateAsMappedElement(MAPPING, LOOP, whileLoop.name);
                }

                if (mapping.assignment == null) {
                    node.annotateAsMappedElement(MAPPING, "assignment", generateAssignment(node));
                }
            }
        });

        //
        if (assignments != null) {
            assignments.forEach(e -> {
                if (e instanceof Map) {
                    Map<String, String> map = (Map<String, String>) e;
                    map.entrySet().forEach(c -> {
                        String path = c.getKey();
                        String function = c.getValue();

                        XmlSchemaBase.MappingNode node = base.get(path);
                        if (node != null) {
                            String assignment = null;
                            if (node.getAnnotation(MAPPING) != null) {
                                Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
                                assignment = mapping.assignment;
                            }
                            assignment = convert(function, assignment);
                            node.annotateAsMappedElement(MAPPING, "assignment", assignment);
                        }
                    });
                }
            });
        }
    }

    private void annotateGlobalVariables(XmlSchemaBase base) {
        Gson gson = new Gson();
        for (Object o : globalVariables) {
            Variable v = gson.fromJson(gson.toJson(o), Variable.class);
            base.annotateAsArrayElement(GLOBAL_VARIABLE, v);
        }
    }

    private void annotateLoops(XmlSchemaBase base) {
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

                            parent.annotateAsArrayElement(LOOP, loop);
                            loopMap.put(sourcePath, loop);
                        }

                        node.annotateAsMappedElement(MAPPING, LOOP, loop.name);
                        node.annotateAsMappedElement(MAPPING, "assignment", loop.variable);
                    }

                } else if (mapping.sourcePath.contains("[*]/")) {
                    if (parent != null) {
                        String sourcePath = mapping.sourcePath;
                        sourcePath = sourcePath.substring(0, sourcePath.lastIndexOf("[*]/") + 3);

                        if (!loopMap.containsKey(sourcePath)) {
                            WhileLoop loop = new WhileLoop();
                            loop.sourcePath = sourcePath;

                            String name = sourcePath.substring(0, sourcePath.lastIndexOf("[*]"));
                            if (name.lastIndexOf("/") > 0) {
                                name = name.substring(name.lastIndexOf("/") + 1);
                            }

                            loop.variable = "_" + name;
                            loop.name = name + "_loop";

                            parent.annotateAsArrayElement(LOOP, loop);
                            loopMap.put(sourcePath, loop);

                        }
                    }
                }
            }
        });
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

    private String generateAssignment(XmlSchemaBase.MappingNode node) {

        String value = "'???'";
        Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
        if (mapping.mappingRule != null && mapping.mappingRule.trim().length() > 0) {
            String rule = mapping.mappingRule.trim();

            String uppercase = rule.toUpperCase();

            if (uppercase.startsWith("DEFAULT TO ")) {
                value = rule.substring("DEFAULT TO ".length()).trim();

            } else if (uppercase.equals("DIRECT MAPPING") || uppercase.equals("DIRECTMAPPING")) {
                value = fromSourcePath(node);
            }
        }

        return value;
    }

    private String fromSourcePath(XmlSchemaBase.MappingNode node) {

        Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
        String token = mapping.sourcePath;

        WhileLoop whileLoop = findParent(token);
        if (whileLoop != null) {
            token = whileLoop.variable + "." + token.substring(whileLoop.sourcePath.length() + 1);
        } else {
            token = INPUT_ROOT + mapping.sourcePath.replace("[*]", "/Item");
        }

        token = token.replaceAll("/", "\\.");

        return token;
    }

    private String convert(String function, String assignment) {
        String token = function;
        while (token.contains(FUNCTION_PARAM)) {
            token = token.replace(FUNCTION_PARAM, assignment);
        }

        return token;
    }
}
