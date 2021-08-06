package soya.framework.tao.xs;

import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.T123W;

public class SampleXmlRenderer implements XsKnowledgeRenderer {
    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledgeBase) throws T123W.FlowExecutionException {
        return SampleXmlUtil.createSampleForType(knowledgeBase.origin().documentTypes()[0]);
    }
}
