package com.albertsons.edis.tools.xmlbeans;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class MappingAnnotator extends MappingFeatureSupport implements Buffalo.Annotator<XmlSchemaBase> {

    private List<?> globalVariables;
    private String mappingFile;

    @Override
    public void annotate(XmlSchemaBase base) {
        // Global Variables:
        if (globalVariables != null) {
            annotateGlobalVariables(base);
        }

        if (mappingFile != null) {
            Properties properties = new Properties();
            try {
                properties.load(WorkshopRepository.getResourceAsInputStream(mappingFile));
                Enumeration<?> enumeration = properties.propertyNames();
                while (enumeration.hasMoreElements()) {
                    String key = (String) enumeration.nextElement();
                    XmlSchemaBase.MappingNode node = base.get(key);
                    String value = properties.getProperty(key);
                    if (value != null && value.trim().length() > 0 && node != null) {
                        if (value.startsWith("construct()")) {
                            Construct construct = createConstruct(value);
                            node.annotate(CONSTRUCT, construct);

                        } else if (value.startsWith("mapping()")) {
                            Mapping mapping = createMapping(value);
                            node.annotate(MAPPING, mapping);

                        } else if (value.equalsIgnoreCase("IGNORE()")) {
                            node.annotate(MAPPING, null);

                        } else if (value.startsWith("ASSIGN(") && value.endsWith(")")) {
                            String assignment = value.substring("ASSIGN(".length(), value.length() - 1);
                            node.annotateAsMappedElement(MAPPING, "assignment", assignment);

                        } else {

                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void annotateGlobalVariables(XmlSchemaBase base) {
        Gson gson = new Gson();
        for (Object o : globalVariables) {
            Variable v = gson.fromJson(gson.toJson(o), Variable.class);
            base.annotateAsArrayElement(GLOBAL_VARIABLE, v);
        }
    }
}
