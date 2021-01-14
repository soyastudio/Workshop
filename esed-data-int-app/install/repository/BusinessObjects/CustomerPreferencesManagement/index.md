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