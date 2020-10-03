package soya.framework.tools.xmlbeans;

import com.google.gson.JsonObject;

import java.util.Map;
import java.util.StringTokenizer;

public interface MappingFeature {
    String UNKNOWN_MAPPINGS = "UNKNOWN_MAPPINGS";

    String SOURCE_PATHS = "SOURCE_PATHS";

    String GLOBAL_VARIABLE = "GLOBAL_VARIABLE";
    String INPUT_ROOT = "$.";
    String FUNCTION_PARAM = "$$";

    String MAPPED = "mapped";
    String MAPPINGS = "mappings";

    String MAPPING = "mapping";
    String CONDITION = "condition";
    String CONSTRUCTION = "construction";
    String LOOP = "loop";
    String BLOCK = "block";
    String PROCEDURE = "procedure";

    enum  UnknownType {
        UNKNOWN_TARGET_PATH, UNKNOWN_MAPPING_RULE, UNKNOWN_SOURCE_PATH
    }

    class UnknownMapping {
        UnknownType unknownType;
        String targetPath;
        String mappingRule;
        String sourcePath;
        String fix;
    }

    class WhileLoop {
        protected String name;
        protected String sourcePath;
        protected String variable;

        protected MappingFeature.WhileLoop parent;

        protected int getDepth() {
            StringTokenizer tokenizer = new StringTokenizer(sourcePath, "*");
            return tokenizer.countTokens() - 1;
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
        protected String sourcePath;
        protected String variable;
        protected String condition;
        protected boolean loop;
        protected Map<String, String> assignments;
    }
}
