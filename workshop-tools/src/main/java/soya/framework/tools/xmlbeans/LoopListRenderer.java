package soya.framework.tools.xmlbeans;

import java.util.ArrayList;
import java.util.List;

public class LoopListRenderer extends XmlSchemaBaseRenderer implements MappingFeature {

    @Override
    public String render(XmlSchemaBase base) {
        List<Mapper> mapperList = new ArrayList<>();
        base.getMappings().entrySet().forEach(e -> {
            String key = e.getKey();
            XmlSchemaBase.MappingNode node = e.getValue();

            if (node.getNodeType().equals(XmlSchemaBase.NodeType.Folder) && node.getAnnotation(MAPPED) != null) {
                if (node.getAnnotation(CONSTRUCT) != null) {
                    Construct construct = node.getAnnotation(CONSTRUCT, Construct.class);
                    construct.loops.forEach(n -> {
                       mapperList.add(new Mapper(n.sourcePath, node.getPath()));
                    });
                }

            }
        });

        return GSON.toJson(mapperList);
    }
}
