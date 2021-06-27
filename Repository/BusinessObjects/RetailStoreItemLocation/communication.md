To: 

Hi Robert and Sarath:

We are facing an issue in one of our BOD: RetailStoreItemLocation.

The issue related to the connection between IIB server to Atlas Kafka through SASL in java compute node. 
The current version of IIB not support Kafka Producer with Header. We have to direct publish message in Jave Compute Node.
To do this, we need to connect Kafka Server through Kafka client api. We have tested in dev/qa, but failed in production deployment.

For QA, we have following setting:

com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.bootstrapServers = qgv013bb3.safeway.com:9095,qgv013bb7.safeway.com:9095,qgv013bb8.safeway.com:9095
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.topicName = ESED_RetailStoreItemLocation
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.clientId = ESED_CG_001_RetailStoreItemLocation
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.acks = 1
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.timeout = 60000
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.securityProtocol = SASL_SSL
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.saslMechanism = SCRAM-SHA-512
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.scramLoginUsername = ESED02
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.scramLoginPassword = mLWKkM36vKe8M+WHz08=

And for Production, the settings are 

com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.bootstrapServers = pgv013e8b.safeway.com:9095,pgv013e8d.safeway.com:9095,pgv013e8e.safeway.com:9095,pgv013e8f.safeway.com:9095,pgv013e90.safeway.com:9095,pgv013e91.safeway.com:9095
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.topicName = ESED_RetailStoreItemLocation
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.clientId = ESED_CG_001_RetailStoreItemLocation
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.acks = 1
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.timeout = 60000
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.securityProtocol = SASL_SSL
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.saslMechanism = SCRAM-SHA-512
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.scramLoginUsername = ESED04
com.abs.irol.rsil.ESED_RSIL_CT2020_IH_Publisher#KafkaProducer.scramLoginPassword = Z8gZQTX0lEGVXNQ88fRM+o3rMM4=

First please confirm the settings for production is correct or not.

If the settings is correct, are there any special configuration differences between QA and Prod. Is it possible to develop a testing flow to test the connectivity for this approach?


