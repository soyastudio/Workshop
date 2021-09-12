package com.abs.edis.project;

import com.google.gson.JsonElement;

public class Project {

    private String name;
    private String application;
    private String source = "{{source}}";
    private String version = "{{version}}";
    private String consumer = "{{consumer}}";
    private String schemaFile;
    private String mappingFile;

    private boolean enabled;

    private MessageFlow messageFlow;

    // private String deployEgNumber;

    public Project() {
    }

    public Project(String name, String source, String version) {
        this.name = name;
        if (source != null) {
            this.source = source;
        }

        if (version != null) {
            this.version = version;
        }

        this.application = "ESED_" + name + "_" + this.source + "_IH_Publisher";
        this.schemaFile = "BOD/Get" + name + ".xsd";
        this.mappingFile = name + "_" + this.source + "_TO_Canonical_Mapping_" + this.version + ".xlsx";

        this.messageFlow = new MessageFlow();
        messageFlow.name = this.application;

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

    public String getSchemaFile() {
        return schemaFile;
    }

    public String getMappingFile() {
        return mappingFile;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public MessageFlow getMessageFlow() {
        return messageFlow;
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
