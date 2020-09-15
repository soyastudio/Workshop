package soya.framework.tools.xmlbeans;

import com.google.gson.JsonObject;

public interface MappingFeature {
    String INPUT_ROOT = "$.";
    String FUNCTION_PARAM = "$$";
    String GLOBAL_VARIABLE = "global_variable";

    String MAPPED = "mapped";
    String MAPPING = "mapping";
    String MAPPINGS = "mappings";
    String LOOP = "loop";
    String BLOCK = "block";

    class WhileLoop {
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

    class Mapping {
        protected String mappingRule;
        protected String sourcePath;

        protected String loop;
        protected String assignment;

        public Mapping() {
        }
    }

    class Variable {
        protected String name;
        protected String type;
        protected String defaultValue;

        public Variable() {
        }
    }

    class Construction {
        protected String from;
        protected String condition;
        protected JsonObject assignments;
    }

    class Block {

    }

}
