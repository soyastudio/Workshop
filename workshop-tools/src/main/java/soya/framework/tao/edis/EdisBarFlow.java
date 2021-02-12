package soya.framework.tao.edis;

import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.xs.SampleXmlRenderer;
import soya.framework.tao.xs.XsToAvroSchemaRenderer;
import soya.framework.tao.xs.XsKnowledgeBase;
import soya.framework.tao.xs.XsNode;

import java.io.File;

public class EdisBarFlow {

    //
    public static void main(String[] args) {
        KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeTree = XsKnowledgeBase.builder()
                .file(new File("D:/Workshop/Repository/CMM/BOD/GetCustomerPreferences.xsd"))
                .create().knowledgeBase();

        SchemaTypeSystem sts =knowledgeTree.origin();

        System.out.println(new SampleXmlRenderer().render(knowledgeTree));
        //System.out.println(new XsToAvroSchemaRenderer().render(knowledgeTree));
        //System.out.println(new XsTreeRenderer().render(knowledgeTree));
        // System.out.println(flow.flowInstance("JSON"));
    }


}
