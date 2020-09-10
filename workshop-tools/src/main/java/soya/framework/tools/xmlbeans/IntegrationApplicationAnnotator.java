package soya.framework.tools.xmlbeans;

import org.apache.commons.beanutils.ConvertUtils;

import java.lang.reflect.Field;
import java.util.Map;

public class IntegrationApplicationAnnotator implements Buffalo.Annotator<XmlSchemaBase>, IntegrationApplicationFeature {
    private Map<String, String> application;
    private Map<String, String> kafkaConsumer;
    private Map<String, String> kafkaProducer;
    private Map<String, String> auditValidateInput;
    private Map<String, String> auditValidateOutput;
    private Map<String, String> exceptionSubFlow;

    @Override
    public void annotate(XmlSchemaBase base) {
        Application app = new Application();
        if (application != null) {
            set(app, application);
        }

        if (kafkaConsumer != null) {
            app.kafkaConsumer = new KafkaConsumer();
            set(app.kafkaConsumer, kafkaConsumer);
        }
        set(app.kafkaProducer, kafkaProducer);
        set(app.auditValidateInput, auditValidateInput);
        set(app.auditValidateOutput, auditValidateOutput);
        set(app.exceptionSubFlow, exceptionSubFlow);

        base.annotate(APPLICATION, app);

    }

    private void set(Object o, Map<String, String> map) {
        if (map != null) {
            Class clazz = o.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                if (map.containsKey(field.getName())) {
                    String v = map.get(field.getName());
                    Class<?> t = field.getType();
                    field.setAccessible(true);
                    try {
                        field.set(o, ConvertUtils.convert(v, t));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
