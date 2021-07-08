# Project

# Business Object Design Document (BORD)

## Flow Chart
``` mermaid
graph LR
subgraph SRC [UCA]
UCA
end

UCA --> AKS
AKS --> KIT

subgraph EDIS [EDIS BOD - DigitalBanner]
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
        IIB[ESED_DigitalBanner_UCA_IH_Publisher]

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

## Message Flow
``` mermaid
        graph LR
        KIT --> KFC

        subgraph EDIS [IIB Message Flow]
        KFC --> AUI
        AUI --> |<Json Data>|IIB
        IIB --> |<CMM XML>|KFP
        KFC --> EXH
        end

        KFP --> KOT

        KIT[(Kafka Inbound Topic)]

        KFC([KafkaConsumer])
        AUI[Input Auditor]
        IIB(IIB Transformer)
        KFP([KafkaProducer])
        EXH[Exception Handler]

        KOT[(Kafka Outbound Topic)]

```


# IIB Command

```
mqsiapplybaroverride -k ESED_DigitalBanner_UCA_IH_Publisher -b ESED_DigitalBanner_UCA_IH_Publisher.bar -p ESED_DigitalBanner_UCA_IH_Publisher.BASE.override.properties

mqsireadbar -b ESED_DigitalBanner_UCA_IH_Publisher.bar -r

```

# Kafka Test
## Workspace


# SQL
```
    select * from MSGLOG.edis_msg_log t where t.bo_name = 'DigitalBanner' order by t.db_insert_timestamp desc;

    select * from MSGLOG.edis_exception_msg t where t.bo_name = 'DigitalBanner' order by t.db_insert_timestamp desc;

```

