package com.abs.edis.mapping;

import com.google.gson.JsonElement;

public class EdisProject {

    private String name;
    private String application;
    private String source;
    private String consumer;
    private String version;

    private Mappings mappings;

    private MessageFlow messageFlow;

    // private String deployEgNumber;


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

    public Mappings getMappings() {
        return mappings;
    }

    public MessageFlow getMessageFlow() {
        return messageFlow;
    }

    static class Mappings {
        private String schema = "";
        private String mappingFile = "???";
        private String mappingSheet = "???";
        private String sampleSheet = "???";
        private String constructFile = "xpath-mapping.properties";

        public String getSchema() {
            return schema;
        }

        public String getMappingFile() {
            return mappingFile;
        }

        public String getMappingSheet() {
            return mappingSheet;
        }

        public String getSampleSheet() {
            return sampleSheet;
        }

        public String getConstructFile() {
            return constructFile;
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
