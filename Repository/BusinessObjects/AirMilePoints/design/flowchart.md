## Flow Chart
``` mermaid
graph LR
subgraph SRC [SOURCE]
Details
Summary
end

Details --> AKS1
Summary --> AKS2
AKS1 --> KIT1
AKS2 --> KIT2

subgraph EDIS [ESED_AirMilePoints_IH_Publisher]
    KIT1 --> |<Json Data>|IIB1
    IIB1 --> |<CMM XML>|CVT

    KIT2 --> |<Json Data>|IIB2
    IIB2 --> |<CMM XML>|CVT

    CVT --> KOT


end

        KOT --> AKT
        AKT --> END

        subgraph TGT [CONSUMER]
        END
        end

        Details((Details))
        Summary((Summary))
        AKS1[(Azure Kafka Details Topic)]
        AKS2[(Azure Kafka Summary Topic)]

        KIT1[(Kafka Details Topic)]
        KIT2[(Kafka Summary Topic)]

        IIB1([AirMilePoints_Details_Transformer])
        IIB2([AirMilePoints_Summary_Transformer])

        CVT[XML-TO-AVRO Converter]

        KOT[(Kafka Producer Topic)]

        AKT[(Azure Kafka TGT)]
        END((Consumer))

```