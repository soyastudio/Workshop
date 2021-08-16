package com.abs.edis.project;

import com.google.gson.JsonElement;

public class Project {

    private String name;
    private String application;
    private String source;
    private String consumer;
    private String version;
    private boolean enabled;

    private Mappings mappings;

    private MessageFlow messageFlow;

    // private String deployEgNumber;


    public Project() {
    }

    public Project(String name) {
        this.name = name;
        this.application = "ESED_" + name + "_{{project.source}}_IH_Publisher";
        this.source = "{{project.source}}";
        this.consumer = "{{project.consumer}}";
        this.version = "{{project.version}}";

        this.mappings = new Mappings();
        mappings.schema = "BOD/Get" + name + ".xsd";
        mappings.mappingFile = name + "/requirement/" + name + "_{{project.source}}" + "_TO_Canonical_Mapping_{{project.version}}.xlsx";

        mappings.xpathDataType = name + "/work" + mappings.xpathDataType;
        mappings.xpathJsonType = name + "/work" + mappings.xpathJsonType;
        mappings.xpathMappings = name + "/work" + mappings.xpathMappings;
        mappings.xpathMappingAdjustments = name + "/work" + mappings.xpathMappingAdjustments;
        mappings.xpathConstruction = name + "/work" + mappings.xpathConstruction;

        this.messageFlow = new MessageFlow();
        messageFlow.name = "ESED_" + name + "_{{project.source}}_IH_Publisher";


    }

    public String getName() {
        return name;
    }

    public String getApplication() {
        return application;
    }

    public String getSource() {
        return source;
    }

    public String getConsumer() {
        return consumer;
    }

    public String getVersion() {
        return version;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Mappings getMappings() {
        return mappings;
    }

    public MessageFlow getMessageFlow() {
        return messageFlow;
    }

    static class Mappings {
        private String schema = "";
        private String mappingFile = "???";

        private String xpathDataType = "xpath-data-type.properties";
        private String xpathJsonType = "xpath-json-type.properties";

        private String xpathMappings = "xpath-mappings.properties";
        private String xpathMappingAdjustments = "xpath-mapping-adjustment.properties";
        private String xpathConstruction = "xpath-construction.properties";

        public String getSchema() {
            return schema;
        }

        public String getMappingFile() {
            return mappingFile;
        }

        public String getXpathDataType() {
            return xpathDataType;
        }

        public String getXpathJsonType() {
            return xpathJsonType;
        }

        public String getXpathMappings() {
            return xpathMappings;
        }

        public String getXpathMappingAdjustments() {
            return xpathMappingAdjustments;
        }

        public String getXpathConstruction() {
            return xpathConstruction;
        }
    }

    static class MessageFlow {
        private String name;
        private String brokerSchema;
        private String packageURI;
        private JsonElement properties;

        private Node input;
        private Node output;
        private Node transformer;

        private Node inputAuditor;
        private Node outputAuditor;
        private Node exceptionHandler;

        public String getName() {
            return name;
        }

        public String getBrokerSchema() {
            return brokerSchema;
        }

        public String getPackageURI() {
            return packageURI;
        }

        public JsonElement getProperties() {
            return properties;
        }

        public Node getInput() {
            return input;
        }

        public Node getOutput() {
            return output;
        }

        public Node getTransformer() {
            return transformer;
        }

        public Node getInputAuditor() {
            return inputAuditor;
        }

        public Node getOutputAuditor() {
            return outputAuditor;
        }

        public Node getExceptionHandler() {
            return exceptionHandler;
        }
    }

    static class Node {
        private String name;
        private JsonElement properties;

        public String getName() {
            return name;
        }

        public JsonElement getProperties() {
            return properties;
        }
    }

}
