package soya.framework.tools.xmlbeans;

import com.google.gson.JsonObject;
import soya.framework.tools.util.StringBuilderUtils;

import java.util.*;

public interface MappingFeature {
    String UNKNOWN_MAPPINGS = "UNKNOWN_MAPPINGS";

    String SOURCE_PATHS = "SOURCE_PATHS";

    String APPLICATION = "APPLICATION";
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
        UNKNOWN_TARGET_PATH, UNKNOWN_MAPPING_RULE, ILLEGAL_SOURCE_PATH, UNKNOWN_SOURCE_PATH
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
        protected String name;
        protected String type;
        protected String sourcePath;
        protected String variable;
        protected String condition;
        protected boolean loop;
        protected Map<String, String> assignments = new LinkedHashMap<>();
    }

    class Procedure {
        String name;
        List<ProcedureParameter> parameters = new ArrayList<>();
        String body;

        String signature() {
            StringBuilder builder = new StringBuilder(name).append("(");
            for (int i = 0; i <parameters.size(); i ++) {
                if(i > 0) {
                    builder.append(", ");
                }
                builder.append("IN ").append(parameters.get(i).name).append(" REFERENCE");
            }
            builder.append(")");
            return builder.toString();
        }

        String invocation() {
            StringBuilder builder = new StringBuilder(name).append("(");
            for (int i = 0; i <parameters.size(); i ++) {
                if(i > 0) {
                    builder.append(", ");
                }
                builder.append(parameters.get(i).name);
            }
            builder.append(")");
            return builder.toString();
        }
    }

    class ProcedureParameter {
        private String name;
        private String assignment;
    }
}
