package soya.framework.tools.xmlbeans;

public interface MappingFeature {
    String ANNOTATION = "mapping";

    class Mapping {
        protected String cardinality;
        protected String assignment;
        protected String sourcePath;
        protected String function;
    }
}
