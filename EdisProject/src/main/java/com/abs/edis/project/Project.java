package com.abs.edis.project;

public class Project {

    private String name;
    private String application = "ESED_{{name}}_{{source}}_IH_Publisher";
    private String version = "{{version}}";

    private String source = "{{source}}";
    private String inboundType = "KAFKA_TOPIC";
    private String inboundDestination = "{{inboundDestination}}";

    private String consumer = "{{consumer}}";
    private String outboundType = "KAFKA_TOPIC";
    private String outboundDestination = "{{outboundDestination}}";

    private String schemaFile;
    private String mappingFile;
    private String mappingSheets;

    private String egNumber;

    private boolean enabled;

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

    public String getInboundDestination() {
        return inboundDestination;
    }

    public String getConsumer() {
        return consumer;
    }

    public String getOutboundDestination() {
        return outboundDestination;
    }

    public String getVersion() {
        return version;
    }

    public String getEgNumber() {
        return egNumber;
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

}
