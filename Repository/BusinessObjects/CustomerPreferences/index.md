# Design
## Flow Chart
``` mermaid
graph LR
  subgraph SRC [UCA]
    UCA
  end

    UCA --> AKS
    AKS --> KIT

  subgraph EDIS [EDIS - Customer Preference Management]
    KIT --> |<Json Data>|IIB
    IIB --> |<CMM XML>|KOT
  end

    KOT --> AKT
    AKT --> END

  subgraph TGT [EDM]
    END
  end

  UCA((Source))
  AKS[(Azure Kafka SRC Topic)]
  
  KIT[(Kafka Consumer Topic)]
  IIB[ESED_Customer_Preference_Management_IH_Publisher]

  KOT[(Kafka Producer Topic)]

  AKT[(Azure Kafka TGT)]
  END((Consumer))         
            
```

## Sequence Chart
``` mermaid
sequenceDiagram
    SRC(UCA)->>Kafka Inbound Topic: publish
    Kafka Inbound Topic-->>IIB Transformer: Json Message
    IIB Transformer-->>Kafka Outbound Topic: CMM XML
    Kafka Outbound Topic->>Consumer(EDM): subscribe 

```



# RIT

## DEV:
RITM0701516

## QA:
RITM0702864

## PROD:
RITM0713755


# DEV
## 1. ssh dgv012efa

    su - esd02ed
    password: #Today@321!

    cd /appl/esed/kafka


## 2. Publish Message
    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer {TOPIC_NAME} {key}.txt '{MSG}'

    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer IAUC_C02_DEV_CFMS_AGGREGATE AnjanaAccount.txt '{"_id":"CustomerPreferences_28d13d32-81fd-48c9-bc7a-d822f8447368","aggregateId":"28d13d32-81fd-48c9-bc7a-d822f8447368","aggregateType":"CustomerPreferences","state":{"_class":"PreferenceAggregateDomain_v1","createTimestamp":1611875712045,"lastUpdateTimestamp":1611875712045,"createClientId":"UMA","lastUpdateClientId":"UMA","createHostName":"00bfc5b1-0b55-41a4-7ac1-0b11","lastUpdateHostName":"00bfc5b1-0b55-41a4-7ac1-0b11","aggregateId":"28d13d32-81fd-48c9-bc7a-d822f8447368","aggregateType":"CustomerPreferences","sequenceNumber":1,"timestamp":1571994962492,"aggregateRevision":1,"version":1,"eventId":"98d5779b-24a6-4aac-9c1c-4e5a4a3e677f","guid":"456-042-1611875708495","preferences":[{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":1,"type":"EMAIL_SAVE","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]},{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":2,"type":"J4U","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]},{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":3,"type":"GROCERY_DEL","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]},{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":4,"type":"PROD_RECALL","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]}]}}'

    

## 3. Consume Message
     java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer {TOPIC_NAME} latest {DEST_FILE_NAME}.text
     
      java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer ESED_C01_GroceryOrder latest GroceryOrder13.text &

# QA
## 1. ssh qgv012efb

    su - esq03ed
    password: #Today@321!

    cd /appl/esed/kafka

## 2. Publish Message
    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer {TOPIC_NAME} per.txt 'MSG'

    java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Producer IAUC_C02_QA_CFMS_AGGREGATE per123.txt '{"_id":"CustomerPreferences_28d13d32-81fd-48c9-bc7a-d822f8447368","aggregateId":"00000000-81fd-48c9-bc7a-d822f8447368","aggregateType":"CustomerPreferences","state":{"_class":"PreferenceAggregateDomain_v1","createTimestamp":1611875712045,"lastUpdateTimestamp":1611875712045,"createClientId":"UMA","lastUpdateClientId":"UMA","createHostName":"00bfc5b1-0b55-41a4-7ac1-0b11","lastUpdateHostName":"00bfc5b1-0b55-41a4-7ac1-0b11","aggregateId":"28d13d32-81fd-48c9-bc7a-d822f8447368","aggregateType":"CustomerPreferences","sequenceNumber":1,"timestamp":1571994962492,"aggregateRevision":1,"version":1,"eventId":"98d5779b-24a6-4aac-9c1c-4e5a4a3e677f","guid":"456-042-1611875708495","preferences":[{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":1,"type":"EMAIL_SAVE","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]},{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":2,"type":"J4U","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]},{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":3,"type":"GROCERY_DEL","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]},{"_class":"BasicPreferenceAggregateDomain_v1","preferenceId":4,"type":"PROD_RECALL","categoryCode":"COMMUNICATION","subCategoryCode":"PROMOTIONAL","lastUpdatedTimestamp":{"$numberLong":"1611875708566"},"optChoices":[{"_class":"OptChoiceAggregateDomain_v1","choice":"OPT_IN"}]}]}}'


## 3. Consume Message
     java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer {TOPIC_NAME} latest {DEST_FILE_NAME}.text
     
     java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer ESED_C01_GroceryOrder latest GroceryOrder_1_4.text
     
     java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer OSCO_ESED_C02_ORDER_QA latest GroceryOrder_OMS.text
     
     java -cp edis-kafka-0.0.2-SNAPSHOT-jar-with-dependencies.jar message.Consumer ESED_C01_GroceryOrder latest GroceryOrder_1_6.text


# Build
mqsiapplybaroverride -k ESED_CustomerPreferences_UCA_IH_Publisher -b ESED_CustomerPreferences_UCA_IH_Publisher.bar -p ESED_CustomerPreferences_UCA_IH_Publisher.BASE.override.properties

mqsireadbar -b ESED_CustomerPreferences_UCA_IH_Publisher.bar -r


  