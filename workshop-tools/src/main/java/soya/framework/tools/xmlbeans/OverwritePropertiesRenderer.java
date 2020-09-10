package soya.framework.tools.xmlbeans;

import com.google.common.base.CaseFormat;

import java.lang.reflect.Field;

public class OverwritePropertiesRenderer implements Buffalo.Renderer<XmlSchemaBase>, IntegrationApplicationFeature {

    @Override
    public String render(XmlSchemaBase base) {
        Application application = base.getAnnotation(APPLICATION, Application.class);

        StringBuilder builder = new StringBuilder();
        if (application.kafkaConsumer != null) {
            print(application, "KafkaConsumer", application.kafkaConsumer, builder);
        }

        print(application, "KafkaProducer", application.kafkaProducer, builder);
        print(application, "AuditValidateInput", application.auditValidateInput, builder);
        print(application, "AuditValidateOutput", application.auditValidateOutput, builder);
        print(application, "ExceptionSubFlow", application.exceptionSubFlow, builder);

        return builder.toString();
    }

    private void print(Application application, String Name, Object object, StringBuilder builder) {
        String prefix = application.brokerSchema + "." + application.flowName + "#" + Name + ".";
        Class clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                if(value instanceof String) {
                    value = evaluate((String)value, application);
                }
                builder.append(prefix).append(field.getName()).append("=").append(value).append("\n");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private String evaluate(String value, Application application) {
        String token = value;
        while (token.contains("{{") && token.contains("}}")) {
            int start = value.indexOf("{{");
            int end = value.indexOf("}}");

            String replace = token.substring(start, end + 2);

            String param = token.substring(start + 2, end);
            param = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, param);

            try {
                Field field = Application.class.getDeclaredField(param);
                field.setAccessible(true);
                String v = field.get(application).toString();
                token = token.substring(0, start) + v + token.substring(end + 2);
            } catch (Exception e) {
                return token;
            }
        }

        return token;
    }
}
