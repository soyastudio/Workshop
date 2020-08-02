package soya.framework.tools.iib;

public class ESQL {

    private final String brokerSchema;
    private final String moduleName;
    private final MessageType messageType;

    private String application;
    private String flowName;
    private String author;

    private ESQL(String brokerSchema, String moduleName, MessageType messageType) {
        this.brokerSchema = brokerSchema;
        this.moduleName = moduleName;
        this.messageType = messageType;
    }

    public String brokerSchema() {
        return brokerSchema;
    }

    public String moduleName() {
        return moduleName;
    }

    public MessageType messageType() {
        return messageType;
    }

    public String application() {
        return application;
    }

    public String flowName() {
        return flowName;
    }

    public String author() {
        return author;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String brokerSchema;
        private String moduleName;
        private MessageType messageType;
        private String application;
        private String flowName;
        private String author;

        public Builder brokerSchema(String brokerSchema) {
            this.brokerSchema = brokerSchema;
            return this;
        }

        public Builder moduleName(String moduleName) {
            this.moduleName = moduleName;
            return this;
        }

        public Builder messageType(MessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder application(String application) {
            this.application = application;
            return this;
        }

        public Builder flowName(String flowName) {
            this.flowName = flowName;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public ESQL create() {
            ESQL esql = new ESQL(brokerSchema, moduleName, messageType);
            esql.application = application;
            esql.flowName = flowName;
            esql.author = author;

            return esql;
        }
    }
}
