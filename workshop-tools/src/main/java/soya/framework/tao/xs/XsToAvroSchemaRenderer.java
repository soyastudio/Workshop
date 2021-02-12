package soya.framework.tao.xs;

import org.apache.avro.Schema;
import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.T123W;
import soya.framework.tools.avro.XmlToAvroSchema;

public class XsToAvroSchemaRenderer implements XsKnowledgeRenderer {

    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase) throws T123W.FlowExecutionException {
        Schema schema = XmlToAvroSchema.fromXmlSchema(knowledgeBase.origin());
        return schema.toString(true);
    }
}
