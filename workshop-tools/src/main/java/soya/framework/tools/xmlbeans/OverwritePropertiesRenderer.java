package soya.framework.tools.xmlbeans;

import com.google.common.base.CaseFormat;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class OverwritePropertiesRenderer extends XmlSchemaBaseRenderer implements IntegrationApplicationFeature {
    private Map<String, String> properties;

    @Override
    public String render(XmlSchemaBase base) {
        Application application = base.getAnnotation(APPLICATION, Application.class);

        Map<String, String> map = new LinkedHashMap();
        if (application.kafkaConsumer != null) {
            dump(application, "KafkaConsumer", application.kafkaConsumer, map);
        }

        dump(application, "KafkaProducer", application.kafkaProducer, map);
        dump(application, "AuditValidateInput", application.auditValidateInput, map);
        dump(application, "AuditValidateOutput", application.auditValidateOutput, map);
        dump(application, "ExceptionSubFlow", application.exceptionSubFlow, map);

        dumpProperties(application, map);
        if (properties != null) {
            dumpProperties(application, properties, map);
        }

        StringBuilder builder = new StringBuilder();
        map.entrySet().forEach(e -> {
            builder.append(e.getKey()).append("=").append(e.getValue()).append("\n");
        });

        return builder.toString();
    }

    private void dump(Application application, String Name, Object object, Map<String, String> map) {
        String prefix = application.brokerSchema + "." + application.flowName + "#" + Name + ".";
        Class clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                if (value == null) {
                    value = "";
                } else if (value instanceof String) {
                    value = evaluate((String) value, application);
                }
                map.put(prefix + field.getName(), value.toString());

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void dumpProperties(Application application, Map<String, String> map) {
        String prefix = application.brokerSchema + "." + application.flowName + "#";
        application.properties.forEach(e -> {
            map.put(prefix + e.key, e.value);
        });
    }

    private void dumpProperties(Application application, Map<String, String> properties, Map<String, String> map) {
        String prefix = application.brokerSchema + "." + application.flowName + "#";
        properties.entrySet().forEach(e -> {
            map.put(prefix + e.getKey(), e.getValue());
        });
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
