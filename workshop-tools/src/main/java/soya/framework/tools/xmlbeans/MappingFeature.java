package soya.framework.tools.xmlbeans;

public interface MappingFeature {
    String INPUT_ROOT = "$.";
    String FUNCTION_PARAM = "$$";
    String GLOBAL_VARIABLE = "global_variable";
    String MAPPING = "mapping";
    String LOOP = "loop";

    static class WhileLoop {
        protected String name;
        protected String sourcePath;
        protected String variable;
        protected MappingFeature.WhileLoop parent;
        protected int depth = 1;

        public WhileLoop() {
        }

        protected int getDepth() {
            return this.parent == null ? this.depth : this.parent.getDepth() + this.depth;
        }
    }

    static class Mapping {
        protected String dataType;
        protected String cardinality;
        protected String mappingRule;
        protected String sourcePath;
        protected String loop;
        protected String assignment;

        public Mapping() {
        }
    }

    static class Variable {
        protected String name;
        protected String type;
        protected String defaultValue;

        public Variable() {
        }
    }
}
