package com.abs.edis.commons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class XmlToJsonConverterTest {

    public static void main(String[] args) {
        InputStream inputStream = XmlToJsonConverterTest.class.getClassLoader().getResourceAsStream("cmm.xml");
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            Map<String, String> properties = new LinkedHashMap<String, String>();
            String attr = null;
            if (attr != null) {
                String[] arr = attr.split(";");
                for (String exp : arr) {
                    int begin = exp.indexOf("(");
                    int end = exp.indexOf(")");
                    String xpath = exp.substring(begin + 1, end);
                    String mapping = exp.substring(0, begin);

                    properties.put(xpath, mapping);
                }
            }

            XmlToJsonConverter converter = new XmlToJsonConverter(properties);

            JsonElement jsonElement = converter.estimate(document);

            Gson gson= new GsonBuilder().setPrettyPrinting().create();

            System.out.println(gson.toJson(jsonElement));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
