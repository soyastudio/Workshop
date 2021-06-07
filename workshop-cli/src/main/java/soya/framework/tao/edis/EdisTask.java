package soya.framework.tao.edis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.KnowledgeTreeNode;
import soya.framework.tao.xs.XsNode;

import java.util.*;

public class EdisTask {

    public static String NAMESPACE_GLOBAL_VARIABLE = "GLOBAL_VARIABLE";
    public static String GLOBAL_VARIABLE_PREFIX = "$GLOBAL_VARIABLE.";

    public static String NAMESPACE_CONSTRUCTION = "construction";
    public static String NAMESPACE_ASSIGNMENT = "assignment";

    public static String FUNCTION_DEFAULT = "DEFAULT";
    public static String FUNCTION_ASSIGN = "ASSIGN";

    public static String FUNCTION_LOOP_ASSIGN = "LOOP_ASSIGN";

    public static String FUNCTION_LOOP = "loop";

    protected static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected Map<String, LinkedHashSet<Function>> loopFeature(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase) {
        Map<Mapping, String> nameBase = new LinkedHashMap<>();
        Map<String, LinkedHashSet<Function>> map = new LinkedHashMap<>();

        knowledgeBase.paths().forEachRemaining(e -> {
            map.put(e, new LinkedHashSet<>());

            KnowledgeTreeNode<XsNode> treeNode = knowledgeBase.get(e);
            if (treeNode.getAnnotation(NAMESPACE_ASSIGNMENT) != null) {
                String src = treeNode.getAnnotation(NAMESPACE_ASSIGNMENT, Assignment.class).source;
                if (src != null && src.contains("[*]")) {
                    int lastIndex = src.lastIndexOf("[*]");
                    String srcPath = src.substring(0, lastIndex + 3);
                    String srcName = src.substring(lastIndex + 1);

                    String arrNode = findNearestArray(e, knowledgeBase);
                    if (arrNode != null) {
                        Mapping key = new Mapping(srcPath, arrNode);
                        if (!nameBase.containsKey(key)) {
                            nameBase.put(key, "_lpv" + nameBase.size());
                        }

                        Mapping parent = getParentLoopMapping(key);
                        String name = nameBase.get(key);
                        String loopName = "loop" + name;

                        if (parent == null || !nameBase.containsKey(parent)) {
                            srcPath = "$." + srcPath.replaceAll("/", ".");
                            map.get(arrNode).add(Function.newInstance(FUNCTION_LOOP, new String[]{"loop" + name, srcPath, name}));

                        } else {
                            String parentName = nameBase.get(parent);
                            String pathRef = parentName + srcPath.substring(parent.getSource().length()).replaceAll("/", ".");
                            map.get(arrNode).add(Function.newInstance(FUNCTION_LOOP, new String[]{loopName, pathRef, name}));
                        }

                        String assign = src.substring(key.getSource().length());
                        assign = name + assign.replace(srcPath, name).replaceAll("/", ".");
                        map.get(e).add(Function.newInstance(FUNCTION_LOOP_ASSIGN, new String[]{loopName, assign}));

                    }

                } else {

                }
            }
        });

        return map;
    }

    protected String findNearestArray(String path, KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase) {
        KnowledgeTreeNode<XsNode> parent = knowledgeBase.get(path);
        while (parent != null) {
            XsNode xsNode = parent.origin();
            if (XsNode.XsNodeType.Folder.equals(xsNode.getNodeType())
                    && (parent.origin().getMaxOccurs() == null || parent.origin().getMaxOccurs().intValue() > 1)) {
                return parent.getPath();
            }

            parent = (KnowledgeTreeNode<XsNode>) parent.getParent();
        }

        return null;
    }

    protected Mapping getParentLoopMapping(Mapping mapping) {
        String src = mapping.getSource();
        String tgt = mapping.getTarget();

        src = src.substring(0, src.lastIndexOf("/"));
        tgt = tgt.substring(0, tgt.lastIndexOf("/"));

        while (!src.endsWith("[*]")) {
            try {
                src = src.substring(0, src.lastIndexOf("/"));
                tgt = tgt.substring(0, tgt.lastIndexOf("/"));

            } catch (Exception e) {
                return null;
            }
        }

        return new Mapping(src, tgt);

    }

    enum MappingRule {
        DirectMapping, DefaultMapping, Todo;

        static MappingRule fromString(String rule) {
            if (rule == null) {
                return null;

            } else {
                String token = rule.toUpperCase();
                if (token.contains("DIRECT") && token.contains("MAPPING")) {
                    return DirectMapping;

                } else if (token.contains("DEFAULT")) {
                    return DefaultMapping;

                } else {
                    return Todo;

                }

            }
        }
    }

    static class Construction {
        private String alias;
        private int level;

        private boolean array;
        private LinkedHashSet<Function> functions = new LinkedHashSet<>();

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public boolean isArray() {
            return array;
        }

        public void setArray(boolean array) {
            this.array = array;
        }

        public Construction add(Function... function) {
            for (Function func : function) {
                if (!func.name.equalsIgnoreCase("construct")) {
                    functions.add(func);
                }
            }
            return this;
        }

        public LinkedHashSet<Function> getFunctions() {
            return functions;
        }

        public Function[] getFunctionArray() {
            return functions.toArray(new Function[functions.size()]);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("construct()");
            if(array) {
                builder.append(".array()");
            }

            functions.forEach(e -> {
                builder.append(".").append(e.toString());
            });
            return builder.toString();
        }
    }

    static class Assignment {
        protected String rule;
        protected String source;
        protected LinkedHashSet<Function> functions = new LinkedHashSet<>();

        public Assignment add(Function... function) {
            for (Function func : function) {
                functions.add(func);
            }
            return this;
        }

        public Function getFirst() {
            Iterator<Function> iterator = functions.iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
            return null;
        }

        @Override
        public String toString() {

            if (!functions.isEmpty()) {
                return Function.toString(functions.toArray(new Function[functions.size()]));

            } else {
                MappingRule mappingRule = parse(rule);
                if (MappingRule.DirectMapping.equals(mappingRule)) {
                    if (source != null) {
                        String param = "$." + source.replaceAll("/", ".");
                        return Function.newInstance(FUNCTION_ASSIGN, new String[]{param}).toString();

                    } else {
                        return "???";
                    }

                } else if (MappingRule.DefaultMapping.equals(mappingRule)) {
                    String value = getDefaultValue(rule);
                    if (value != null) {
                        return Function.newInstance(FUNCTION_DEFAULT, new String[]{value}).toString();

                    } else {
                        return "TODO(???)";

                    }
                } else if (source != null) {
                    return Function.newInstance(FUNCTION_DEFAULT, new String[]{"???"}).toString();

                } else {
                    return "TODO(???)";

                }
            }
        }

        private MappingRule parse(String rule) {
            if (rule == null || rule.trim().length() == 0) {
                return null;
            }

            if (rule.toUpperCase().contains("DEFAULT")) {
                return MappingRule.DefaultMapping;
            }

            if (rule.toUpperCase().contains("DIRECT")) {
                return MappingRule.DirectMapping;
            }

            return MappingRule.Todo;
        }

        private String getDefaultValue(String rule) {
            try {
                return rule.substring(rule.indexOf("'"), rule.lastIndexOf("'") + 1);

            } catch (Exception e) {
                return null;
            }
        }
    }

    static class Function {
        String name;
        String[] parameters = new String[0];

        private Function(String name, String[] parameters) {
            this.name = name;
            this.parameters = parameters;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(name).append("(");
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    if (i > 0) {
                        builder.append(", ");
                    }
                    builder.append(parameters[i]);
                }

            }
            builder.append(")");
            return builder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Function function = (Function) o;
            return name.equals(function.name) && Arrays.equals(parameters, function.parameters);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(name);
            result = 31 * result + Arrays.hashCode(parameters);
            return result;
        }

        static Function[] fromString(String exp) {
            if (exp == null || exp.trim().length() == 0) {
                return null;
            }

            String[] arr = exp.split("\\)\\.");
            Function[] functions = new Function[arr.length];
            for (int i = 0; i < arr.length; i++) {
                String funcExp = arr[i];
                if (!funcExp.endsWith(")")) {
                    funcExp = funcExp + ")";
                }

                functions[i] = parse(funcExp);
            }
            return functions;

        }

        static Function parse(String exp) {
            if (exp.trim().length() > 0 && !exp.contains("(")) {
                return new Function("TODO", new String[0]);

            } else {
                int start = exp.indexOf("(");
                int end = exp.lastIndexOf(")");
                String name = exp.substring(0, start);
                String[] parameters = exp.substring(start + 1, end).split(",");
                for (int i = 0; i < parameters.length; i++) {
                    parameters[i] = parameters[i].trim();
                }

                return new Function(name, parameters);

            }
        }

        static String toString(Function[] functions) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < functions.length; i++) {
                if (i > 0) {
                    builder.append(".");
                }
                builder.append(functions[i].toString());
            }

            return builder.toString();
        }

        static Function newInstance(String name, String[] parameters) {
            return new Function(name, parameters);
        }
    }

    static class Mapping {
        private final String source;
        private final String target;

        public Mapping(String source, String target) {
            this.source = source;
            this.target = target;
        }

        public String getSource() {
            return source;
        }

        public String getTarget() {
            return target;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Mapping mapping = (Mapping) o;
            return source.equals(mapping.source) && target.equals(mapping.target);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, target);
        }
    }

}
