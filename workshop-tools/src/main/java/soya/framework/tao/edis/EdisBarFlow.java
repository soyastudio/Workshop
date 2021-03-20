package soya.framework.tao.edis;

import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.xs.XsKnowledgeBase;
import soya.framework.tao.xs.XsNode;

import java.io.File;

public class EdisBarFlow {

    public static void main(String[] args) {
        KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree = XsKnowledgeBase.builder()
                .file(new File("C:/github/Workshop/Repository/CMM/BOD/GetCustomerPreferences.xsd"))
                .create().knowledgeBase();

        new XlsxMappingAnnotator()
                .mappingFile("C:/github/Workshop/Repository/BusinessObjects/CustomerPreferences/requirement/CustomerPreference-v3.2.8.xlsx")
                .mappingSheet("Mapping CFMS to Canonical")
                .annotate(knowledgeTree);
/*

        new XPathAssignmentAnnotator()
                .file("C:/github/Workshop/Repository/BusinessObjects/CustomerPreferences/xpath-assignment.properties")
                .annotate(knowledgeTree);


        System.out.println(new ConstructEsqlRenderer()
                .brokerSchema("com.abs.uca.cfms")
                .moduleName("ESED_CFMS_CMM_Transformer_Compute")
                .inputRootVariable("_inputRootNode")
                .inputRootReference("InputRoot.JSON.Data")
                .render(knowledgeTree));
*/

        //System.out.println(new ConstructTreeRenderer().render(knowledgeTree));
        //System.out.println(new XPathLoopAnalyzer().render(knowledgeTree));
        System.out.println(new XPathAssignmentAnalyzer().enableLoopFeature().render(knowledgeTree));
        //System.out.println(new SampleXmlRenderer().render(knowledgeTree));
        //System.out.println(new XsToAvroSchemaRenderer().render(knowledgeTree));
        //System.out.println(new XsTreeRenderer().render(knowledgeTree));
        // System.out.println(flow.flowInstance("JSON"));
    }


}
