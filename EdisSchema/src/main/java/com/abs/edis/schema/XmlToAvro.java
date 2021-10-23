package com.abs.edis.schema;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class XmlToAvro {

    public static void main(String[] args) throws Exception {

        File avsc = new File("C:\\github\\Workshop\\Repository\\BusinessObjects\\AirMilePoints\\GetAirMilePoints.avsc");
        File xml = new File("C:\\github\\Workshop\\Repository\\BusinessObjects\\AirMilePoints\\GetAirMilePoints.xml");

        File avro = new File("C:\\github\\Workshop\\AppBuild\\BusinessObjects\\AirMilePoints\\test\\AirMilePoints.avro");


        read(avro);

        /*Schema schema = new Schema.Parser().parse(new FileInputStream(avsc));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        // parse XML file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(xml);
        document.getDocumentElement().normalize();

        //Here comes the root node
        Element root = document.getDocumentElement();

        //System.out.println(XmlToAvroConverter.toXmlString(root));

        GenericData.Record record =  XmlToAvroConverter.createRecord(schema, root);

        byte[] out = XmlToAvroConverter.convert(root, schema);

        GenericRecord result = XmlToAvroConverter.read(out, schema);
        System.out.println(result.toString());*/

    }

    private static void printNode(Node node) {
        if (node.getChildNodes().getLength() == 1 && node.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE) {
            System.out.println(node.getNodeName() + ": " + node.getTextContent());

        } else {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println(node.getNodeName() );
                NodeList nodeList = node.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node nd = nodeList.item(i);
                    if (nd.getTextContent() != null) {
                        printNode(nodeList.item(i));
                    }
                }
            }
        }
    }

    private static void toByteArray(GenericData.Record record, Schema schema, File avro) throws Exception {
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(new GenericDatumWriter<>(schema));
        FileOutputStream outputStream = new FileOutputStream(avro);
        dataFileWriter.create(schema, outputStream);

        dataFileWriter.append(record);
        dataFileWriter.close();

    }

    private static void write(GenericData.Record record, Schema schema, File avro) throws Exception {
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(new GenericDatumWriter<>(schema));
        FileOutputStream outputStream = new FileOutputStream(avro);
        dataFileWriter.create(schema, outputStream);

        dataFileWriter.append(record);
        dataFileWriter.close();

    }

    private static void write(List<GenericData.Record> records, Schema schema, File avro) throws Exception {
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(new GenericDatumWriter<>(schema));
        FileOutputStream outputStream = new FileOutputStream(avro);
        dataFileWriter.create(schema, outputStream);
        records.forEach(e -> {
            try {
                dataFileWriter.append(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        dataFileWriter.close();

    }

    private static void read(File avro) throws Exception {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>();
        DataFileReader<GenericRecord> dataFileReader =
                new DataFileReader<GenericRecord>(avro, datumReader);

        // System.out.println(dataFileReader.getSchema().toString(true));
        dataFileReader.forEach(e -> {
            System.out.println(e);
            System.out.println();
        });

    }
}
