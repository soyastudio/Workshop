package com.abs.edis.commons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class XmlToJsonConverterTest {

    public static void main(String[] args) {

        Gson gson= new GsonBuilder().setPrettyPrinting().create();

        InputStream inputStream = XmlToJsonConverterTest.class.getClassLoader().getResourceAsStream("cmm.xml");
        DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
        try {

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new File("C:/Studio/test/cmm.xml"));
            document.getDocumentElement().normalize();

            //Here comes the root node
            Element root = document.getDocumentElement();

            Map<String, String> properties = new LinkedHashMap<String, String>();
            InputStream typeStream = XmlToJsonConverterTest.class.getClassLoader().getResourceAsStream("xpath-json-type.txt");
            String attr = IOUtils.toString(typeStream, StandardCharsets.UTF_8);

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

            System.out.println(converter.convert(root));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Node getFirstElementChild(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i ++) {
            if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return nodeList.item(i);
            }
        }

        return null;
    }
}
