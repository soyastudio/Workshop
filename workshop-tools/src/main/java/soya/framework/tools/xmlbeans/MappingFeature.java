package soya.framework.tools.xmlbeans;

public interface MappingFeature {
    String INPUT_ROOT = "$.";
    String FUNCTION_PARAM = "$$";

    String GLOBAL_VARIABLE = "global_variable";
    String MAPPING = "mapping";
    String LOOP = "loop";

    class Variable {
        protected String name;
        protected String type;
        protected String defaultValue;

    }

    class Mapping {
        protected String cardinality;
        protected String mappingRule;
        protected String sourcePath;

        protected String loop;
        protected String assignment;
    }

    class WhileLoop {

        protected String name;
        protected String sourcePath;
        protected String variable;

        protected WhileLoop parent;
        protected int depth = 1;
        protected int getDepth() {
            return parent == null ? depth : parent.getDepth() + depth;
        }
    }

}
