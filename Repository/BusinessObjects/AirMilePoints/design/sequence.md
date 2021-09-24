``` mermaid
sequenceDiagram
        SRC(SOURCE)->>Kafka Inbound Topic: publish
        Kafka Inbound Topic-->>IIB Transformer: Json Message
        IIB Transformer-->>AVRO Converter: CMM XML
        AVRO Converter-->>Kafka Outbound Topic: AVRO
        Kafka Outbound Topic->>Consumer(CONSUMER): subscribe

```