package soya.framework.tools.xmlbeans;

import java.util.*;

public interface MappingFeature {
    String UNKNOWN_MAPPINGS = "UNKNOWN_MAPPINGS";
    String SOURCE_PATHS = "SOURCE_PATHS";
    String APPLICATION = "APPLICATION";
    String GLOBAL_VARIABLE = "GLOBAL_VARIABLE";

    String INPUT_ROOT = "$";
    String FUNCTION_PARAM = "$$";

    String MAPPED = "mapped";
    String MAPPINGS = "mappings";

    String MAPPING = "mapping";
    String CONSTRUCT = "construct";

    String CONDITION = "condition";
    String CONSTRUCTION = "construction";
    String LOOP = "loop";
    String BLOCK = "block";
    String PROCEDURE = "procedure";

    enum UnknownType {
        UNKNOWN_TARGET_PATH, UNKNOWN_MAPPING_RULE, ILLEGAL_SOURCE_PATH, UNKNOWN_SOURCE_PATH, TYPE_INCOMPATIBLE
    }

    class UnknownMapping {
        UnknownType unknownType;
        String targetPath;
        String mappingRule;
        String sourcePath;
        String desc;
        String fix;
    }

    class Mapping {
        protected String mappingRule;
        protected String sourcePath;

        protected String loop;
        protected String assignment;

        public Mapping() {
        }
    }

    class Mapper implements Comparable<Mapper> {
        protected String sourcePath;
        protected String targetPath;

        public Mapper(String sourcePath, String targetPath) {
            this.sourcePath = sourcePath;
            this.targetPath = targetPath;
        }

        public String getSourcePath() {
            return sourcePath;
        }

        public String getTargetPath() {
            return targetPath;
        }

        @Override
        public int compareTo(Mapper o) {
            return this.sourcePath.compareTo(o.sourcePath);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Mapper mapping = (Mapper) o;

            return Objects.equals(sourcePath, mapping.sourcePath);
        }

        @Override
        public int hashCode() {
            return sourcePath != null ? sourcePath.hashCode() : 0;
        }
    }

    class Variable {
        protected String name;
        protected String type;
        protected String defaultValue;

        public Variable() {
        }
    }

    class Construct {
        Procedure procedure;
        List<Constructor> constructors = new ArrayList<>();
        List<WhileLoop> loops = new ArrayList<>();

        public String toString() {
            StringBuilder builder = new StringBuilder("construct()");
            if (procedure != null) {
                builder.append(".").append(procedure.toString()).append(".end()");

            } else {
                if (!constructors.isEmpty()) {
                    constructors.forEach(c -> {
                        builder.append(".").append(c).append(".end()");
                    });
                }

                if (!loops.isEmpty()) {
                    loops.forEach(l -> {
                        builder.append(".").append(l).append(".end()");
                    });
                }

            }


            return builder.toString();
        }
    }

    abstract class ConstructNode {
        protected String name;
        protected String variable;
        protected String sourcePath;

    }

    class WhileLoop extends ConstructNode {

        protected transient MappingFeature.WhileLoop parent;

        protected int getDepth() {
            StringTokenizer tokenizer = new StringTokenizer(sourcePath, "*");
            return tokenizer.countTokens() - 1;
        }

        public static WhileLoop fromString(String chain) {
            String[] arr = chain.split("\\)\\.");
            WhileLoop loop = new WhileLoop();
            for (String func : arr) {
                if (func.endsWith(")")) {
                    func = func.substring(0, func.length() - 1);
                }

                int sep = func.indexOf("(");
                String funcName = func.substring(0, sep);
                String arg = func.substring(sep + 1);

                if ("loop".endsWith(funcName)) {
                    loop.name = arg.trim();

                } else if ("from".endsWith(funcName)) {
                    loop.sourcePath = arg.trim();

                } else if ("var".equals(funcName)) {
                    loop.variable = arg.trim();

                }
            }
            return loop;
        }

        public String toString() {
            return new StringBuilder("loop(").append(name).append(")")
                    .append(".from(").append(sourcePath).append(")")
                    .append(".var(").append(variable).append(")")
                    .toString();
        }
    }

    class Constructor extends ConstructNode {

        protected transient Map<String, String> assignments = new LinkedHashMap<>();

        public static Constructor fromString(String chain) {
            String[] arr = chain.split("\\)\\.");
            Constructor constructor = new Constructor();
            for (String func : arr) {
                if (func.endsWith(")")) {
                    func = func.substring(0, func.length() - 1);
                }

                int sep = func.indexOf("(");
                String funcName = func.substring(0, sep);
                String arg = func.substring(sep + 1);

                if ("constructor".endsWith(funcName)) {
                    constructor.name = arg.trim();

                } else if ("from".endsWith(funcName)) {
                    constructor.sourcePath = arg.trim();

                } else if ("var".equals(funcName)) {
                    constructor.variable = arg.trim();

                }
            }

            return constructor;
        }

        public String toString() {
            return new StringBuilder("constructor(").append(name).append(")")
                    .append(".from(").append(sourcePath).append(")")
                    .append(".var(").append(variable).append(")")
                    .toString();
        }
    }

    class Procedure {
        String name;
        List<ProcedureParameter> parameters = new ArrayList<>();

        transient String body;

        String signature() {
            StringBuilder builder = new StringBuilder(name).append("(");
            for (int i = 0; i < parameters.size(); i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append("IN ").append(parameters.get(i).name).append(" REFERENCE");
            }
            builder.append(")");
            return builder.toString();
        }

        String invocation() {
            StringBuilder builder = new StringBuilder(name).append("(");
            for (int i = 0; i < parameters.size(); i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(parameters.get(i).name);
            }
            builder.append(")");
            return builder.toString();
        }

        public static Procedure fromString(String exp) {
            String token = exp;
            Procedure procedure = new Procedure();
            while (token.contains("(") && token.contains(")")) {
                int l = token.indexOf("(");
                int r = token.indexOf(")");

                String func = token.substring(0, l);
                if (func.startsWith(".")) {
                    func = func.substring(1);
                }
                String arg = token.substring(l + 1, r);

                if ("procedure".equals(func)) {
                    procedure.name = arg.trim();

                } else if ("param".equals(func)) {
                    ProcedureParameter parameter = new ProcedureParameter();
                    parameter.name = arg.trim();
                    procedure.parameters.add(parameter);

                } else if ("from".equals(func)) {
                    procedure.parameters.get(procedure.parameters.size() - 1).assignment = arg.trim();
                }

                token = token.substring(r + 1);
            }

            return procedure;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder("procedure(").append(name).append(")");
            parameters.forEach(e -> {
                builder.append(".").append(e.toString());
            });

            return builder.toString();
        }
    }

    class ProcedureParameter {
        protected String name;
        protected String assignment;

        public String toString() {
            StringBuilder builder = new StringBuilder("param(").append(name).append(")");
            if (assignment != null) {
                builder.append(".from(").append(assignment).append(")");
            }
            return builder.toString();
        }
    }

    class ConstructTree {
        private XmlSchemaBase.MappingNode node;
        private Construct construct;

        private Map<String, TreeNode<WhileLoop>> loopTree = new LinkedHashMap<>();
        private Map<String, TreeNode<Constructor>> constructorTree = new LinkedHashMap<>();

        public ConstructTree(XmlSchemaBase.MappingNode node) {
            this.node = node;
            this.construct = node.getAnnotation(CONSTRUCT, Construct.class);

            construct.loops.forEach(e -> {
                loopTree.put(e.sourcePath, new TreeNode<>(e));
            });

            construct.constructors.forEach(e -> {
                constructorTree.put(e.sourcePath, new TreeNode<>(e));
            });

            node.getChildren().forEach(e -> {
                loopTree.entrySet().forEach(n -> {
                    if (inLoop(e, n.getValue().object)) {
                        n.getValue().nodes.add(e);
                    }
                });
            });

        }

        private boolean inLoop(XmlSchemaBase.MappingNode node, WhileLoop loop) {

            String source = loop.sourcePath + "/";
            if (node.getAnnotation(MAPPING) != null) {
                Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);

                return mapping.sourcePath != null && (mapping.sourcePath.equals(loop.sourcePath) || mapping.sourcePath.startsWith(source));

            } else if (XmlSchemaBase.NodeType.Folder.equals(node.getNodeType())) {
                for (XmlSchemaBase.MappingNode child : node.getChildren()) {
                    if (inLoop(child, loop)) {
                        return true;
                    }
                }
            }

            return false;
        }

        public XmlSchemaBase.MappingNode getNode() {
            return node;
        }

        public Construct getConstruct() {
            return construct;
        }

        public Map<String, TreeNode<WhileLoop>> getLoopTree() {
            return loopTree;
        }

        public Map<String, TreeNode<Constructor>> getConstructorTree() {
            return constructorTree;
        }
    }

    class TreeNode<T> {
        private T object;
        private List<XmlSchemaBase.MappingNode> nodes = new ArrayList<>();

        public TreeNode(T object) {
            this.object = object;
        }

        public T getObject() {
            return object;
        }

        public List<XmlSchemaBase.MappingNode> getNodes() {
            return nodes;
        }
    }
}
