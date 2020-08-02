package soya.framework.tools.iib;

import org.apache.avro.Schema;

public class AvroToCmmESQL {

    public static String fromAvroSchema(Schema schema) {

        StringBuilder builder = new StringBuilder();

        schema.getFields().forEach(e -> {
            System.out.println("---------- " + e.name() + ": " + e.schema().getType());
        });

        builder.append(schema.getName());

        return builder.toString();
    }


}
