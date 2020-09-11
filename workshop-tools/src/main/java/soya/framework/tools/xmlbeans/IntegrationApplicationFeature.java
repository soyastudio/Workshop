package soya.framework.tools.xmlbeans;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public interface IntegrationApplicationFeature {
    String APPLICATION = "APPLICATION";
    Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    class Application {
        protected String boName = "{{BO_NAME}}";
        protected String applicationName = "{{APPLICATION_NAME}}";
        protected String brokerSchema = "{{BROKER_SCHEMA}}";
        protected String flowName = "{{FLOW_NAME}}";
        
        protected KafkaConsumer kafkaConsumer;
        protected KafkaProducer kafkaProducer = new KafkaProducer();
        protected AuditValidateInput auditValidateInput = new AuditValidateInput();
        protected AuditValidateOutput auditValidateOutput = new AuditValidateOutput();
        protected ExceptionSubFlow exceptionSubFlow = new ExceptionSubFlow();

        protected List<KeyValuePair> properties = new ArrayList<>();
        
    }
    
    class KafkaConsumer {
        protected String topicName = "{{KAFKA_CONSUMER_TOPIC}}";
        protected String bootstrapServers 
                = "pgv013e8b.safeway.com:9093,pgv013e8d.safeway.com:9093,pgv013e8e.safeway.com:9093,pgv013e8f.safeway.com:9093,pgv013e90.safeway.com:9093,pgv013e91.safeway.com:9093";
        protected String groupId = "delivery_slot_consumer_group";
        protected String initialOffset = "latest";
        protected boolean enableAutoCommit = true;
        protected String clientId = "delivery_slot_consumer";
        protected boolean useClientIdSuffix = true;
        protected int connectionTimeout = 15;
        protected int sessionTimeout = 10;
        protected int receiveBatchSize = 1;
        protected String securityProtocol = "SSL";
        protected String sslProtocol = "TLSv1.2";
        protected String validateMaster = "none";
        protected String componentLevel = "flow";
        protected int additionalInstances = 0;
    }

    class KafkaProducer {
        protected String topicName = "{{KAFKA_PRODUCER_TOPIC}}";
        protected String bootstrapServers 
                = "pgv013e8b.safeway.com:9093,pgv013e8d.safeway.com:9093,pgv013e8e.safeway.com:9093,pgv013e8f.safeway.com:9093,pgv013e90.safeway.com:9093,pgv013e91.safeway.com:9093";
        protected String clientId = "delivery_slot_producer";
        protected boolean useClientIdSuffix = true;
        protected int acks = 1;
        protected int timeout = 60;
        protected String securityProtocol = "PLAINTEXT";
        protected String sslProtocol = "TLSv1.2";
        protected String validateMaster = "inherit";
    }
    
    class AuditValidateInput {
        protected String APPLICATION_NAME = "{{APPLICATION_NAME}}";
        protected String APPLICATION_DESC = "Transform Json to Canonical Message and Publish to Kafka";
        protected String BO_NAME = "{{BO_NAME}}";
        protected String COMPONENT_DESC = "{{APPLICATION_NAME}}";
        protected String COMPONENT_TYPE;
        protected String COMPONENT_INPUT_TYPE = "KAFKA";
        protected String SOURCE_SYSTEM_NAME = "OSMS";
        protected String AUDIT_REQD;
        protected String AUDIT_SRC_UNQ_ID;
        protected String PATH_SRC_UNQ_ID = "InputRoot.JSON.Data.slotId";
        protected String AUDIT_TRGT_UNQ_ID;
        protected String PATH_TRGT_UNQ_ID = "InputRoot.JSON.Data.slotId";
        protected String VALIDATION_REQD_SOURCE;
        protected String VALIDATION_REQD_TARGET;
        protected String STORE_SRC_MSG;
        protected String STORE_TRGT_MSG;
        protected String COMPONENT_INPUT_NAME = "{{KAFKA_CONSUMER_TOPIC}}";
        protected String queueName = "ESEDPR.AUDIT.MSG";
        protected String AUDIT_MDL_UNQ_ID;
        protected String PATH_MDL_UNQ_ID = "InputRoot.JSON.Data.slotId";
        protected String STORE_MDL_MSG;

    }
    
    class AuditValidateOutput {
        protected String queueName = "ESEDPR.AUDIT.MSG";
    }
    
    class ExceptionSubFlow {
        protected String HOSTNAME;
        protected String STOREMSG = "Y";
        protected String APPLICATIONNAME = "{{APPLICATION_NAME}}";
        protected String BONAME = "{{BO_NAME}}";
        protected String INPUTTYPE = "KAFKA";
        protected String INPUTNAME = "{{KAFKA_CONSUMER_TOPIC}}";
        protected String REPLAYRULE = "Y";
        protected int MAXREPLAYCOUNT = 3;
        protected String COMPONENTNAME = "{{APPLICATION_NAME}}";
        protected String queueName = "ESEDPR.EXCEPTION.MSG";
    }

    class KeyValuePair {
        protected String key;
        protected String value;

        public KeyValuePair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
