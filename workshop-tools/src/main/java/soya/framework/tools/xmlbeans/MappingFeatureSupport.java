package soya.framework.tools.xmlbeans;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.LinkedHashSet;
import java.util.Set;

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
        JsonObject sourcePaths = base.getAnnotation(SOURCE_PATHS, JsonObject.class);
        LinkedHashSet<Mapper> list = new LinkedHashSet<>();
        base.getMappings().entrySet().forEach(e -> {
            Mapper mapping = e.getValue().getAnnotation(MAPPING, Mapper.class);
            if (mapping != null && mapping.sourcePath != null
                    && (mapping.sourcePath.endsWith("[*]") || mapping.sourcePath.contains("[*]/"))) {

                XmlSchemaBase.MappingNode node = findParent(e.getValue());
                if (node != null) {
                    String sourcePath = mapping.sourcePath;
                    if (sourcePaths.get(sourcePath) != null) {
                        if (!sourcePath.endsWith("[*]")) {
                            sourcePath = sourcePath.substring(0, sourcePath.lastIndexOf("[*]/") + 3);
                        }
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
}
