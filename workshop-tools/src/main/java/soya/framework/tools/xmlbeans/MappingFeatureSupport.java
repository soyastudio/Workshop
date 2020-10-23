package soya.framework.tools.xmlbeans;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

public abstract class MappingFeatureSupport implements MappingFeature {

    public static String TODO = "TODO(???)";
    public static String UNKNOWN = "???";

    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected Mapping getMapping(XmlSchemaBase.MappingNode node) {
        return node.getAnnotation(MAPPING, Mapping.class);
    }

    protected String toXPath(String source) {
        return source;
    }

    protected Set<Mapper> findLoops(XmlSchemaBase base) {
        String[] sourcePaths = base.getAnnotation(SOURCE_PATHS, String[].class);
        Set<String> sourcePathSet = new HashSet<>(Arrays.asList(sourcePaths));

        LinkedHashSet<Mapper> list = new LinkedHashSet<>();
        base.getMappings().entrySet().forEach(e -> {
            Mapper mapping = e.getValue().getAnnotation(MAPPING, Mapper.class);
            if (mapping != null && mapping.sourcePath != null && mapping.sourcePath.contains("[*]/")) {

                XmlSchemaBase.MappingNode node = findParent(e.getValue());
                if (node != null) {
                    String sourcePath = mapping.sourcePath;
                    if (sourcePathSet.contains(sourcePath)) {
                        sourcePath = sourcePath.substring(0, sourcePath.lastIndexOf("[*]/") + 3);
                        list.add(new Mapper(sourcePath, node.getPath()));
                    }

                }
            }
        });

        return list;
    }

    private XmlSchemaBase.MappingNode findParent(XmlSchemaBase.MappingNode mappingNode) {
        XmlSchemaBase.MappingNode node = mappingNode.getParent();
        while (node != null) {
            if (node.getCardinality() != null && !node.getCardinality().endsWith("-1")) {
                break;
            }
            node = node.getParent();
        }

        return node;
    }

    protected Mapping createMapping(String exp) {
        Mapping mapping = new Mapping();
        String token = exp;
        if (token.startsWith("mapping()")) {
            token = token.substring("mapping()".length());
        }

        if (token.endsWith(".end()")) {
            token = token.substring(0, token.length() - ".end()".length());
        }

        while (token.contains("(") && token.contains(")")) {
            int l = token.indexOf("(");
            int r = token.indexOf(")");

            String func = token.substring(0, l);
            if (func.startsWith(".")) {
                func = func.substring(1);
            }
            String arg = token.substring(l + 1, r);

            if (func.equals("from")) {
                mapping.sourcePath = arg.trim();
            } else if (func.equals("assign")) {
                mapping.assignment = arg.trim();
            }

            token = token.substring(r + 1);
        }
        return mapping;
    }

    protected Construct createConstruct(String exp) {
        Construct construct = new Construct();
        String token = exp;
        if (token.startsWith("construct().")) {
            token = token.substring("construct().".length());
        }

        String[] arr = token.split("\\.end\\(\\)");
        for (String fun : arr) {
            if (fun.trim().length() > 0) {
                if (fun.startsWith(".")) {
                    fun = fun.substring(1);
                }

                Object obj = fromDsl(fun);
                if (obj instanceof WhileLoop) {
                    construct.loops.add((WhileLoop) obj);

                } else if (obj instanceof Constructor) {
                    construct.constructors.add((Constructor) obj);

                } else if (obj instanceof Procedure) {
                    construct.procedure = (Procedure) obj;
                }
            }
        }

        return construct;
    }

    private Object fromDsl(String dsl) {
        if (dsl.startsWith("loop(")) {
            return WhileLoop.fromString(dsl);

        } else if (dsl.startsWith("constructor(")) {
            return Constructor.fromString(dsl);

        } else if (dsl.startsWith("procedure(")) {
            return Procedure.fromString(dsl);

        }

        return null;
    }


    protected String getAssignment(Mapping mapping, Map<String, Object> constructions) {
        if (mapping.assignment != null) {
            return mapping.assignment;

        } else if (mapping.mappingRule != null) {
            Function[] functions = parse(mapping);
            if (functions.length == 1) {
                Function func = functions[0];
                if (Function.DEFAULT_FUNCTION.equalsIgnoreCase(func.getName())) {
                    return func.getArguments()[0];

                } else if (Function.FROM_FUNCTION.equalsIgnoreCase(func.getName())) {
                    String path = func.getArguments()[0];
                    String parent = findParent(path, constructions);
                    if (parent != null) {
                        String pn = null;
                        String var = null;
                        Object o = constructions.get(parent);
                        if (o instanceof WhileLoop) {
                            pn = ((WhileLoop) o).name;
                            var = ((WhileLoop) o).variable;

                        }

                        path = var + path.substring(parent.length());
                        path = path.replaceAll("/", ".");

                        return "for(" + pn + ").assign(" + path + ").end()";

                    } else {
                        return  "$." + path.replaceAll("/", ".");

                    }

                } else {
                    return UNKNOWN;

                }
            } else {
                return UNKNOWN;

            }

        } else {
            return UNKNOWN;

        }
    }

    private String findParent(String path, Map<String, Object> constructions) {
        String parent = path;
        while (parent.contains("/") && !constructions.containsKey(parent)) {
            parent = parent.substring(0, parent.lastIndexOf("/"));
        }

        return constructions.containsKey(parent) ? parent : null;
    }


    protected Function[] parse(Mapping mapping) {
        String mappingRule = mapping.mappingRule;
        StringBuilder builder = new StringBuilder();
        if (mappingRule.toUpperCase().startsWith("DEFAULT TO ")) {
            builder.append("DEFAULT(");
            if (mappingRule.contains("'")) {
                String token = mappingRule.substring(mappingRule.indexOf("'"));
                builder.append(token);
            } else {
                builder.append(UNKNOWN);
            }
            builder.append(")");

        } else if (mappingRule.toUpperCase().contains("DIRECT") && mappingRule.toUpperCase().contains("MAPPING")) {
            builder.append("FROM(");
            if (mapping.sourcePath != null && !mapping.sourcePath.contains(" ") && !mapping.sourcePath.contains("\n")) {
                builder.append(mapping.sourcePath);
            } else {
                builder.append(UNKNOWN);
            }

            builder.append(")");

        } else {
            builder.append(TODO);
        }

        return Function.fromString(builder.toString());
    }
}
