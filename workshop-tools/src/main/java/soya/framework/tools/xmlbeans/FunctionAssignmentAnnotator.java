package soya.framework.tools.xmlbeans;

import com.google.gson.JsonObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import soya.framework.tools.xmlbeans.Buffalo.Annotator;
import soya.framework.tools.xmlbeans.XmlSchemaBase.MappingNode;
import soya.framework.tools.xmlbeans.XmlSchemaBase.NodeType;

public class FunctionAssignmentAnnotator implements Annotator<XmlSchemaBase>, MappingFeature {
    private String function;

    private List<String> includes;

    private String field;
    private String value;
    private List<String> excludes;

    public FunctionAssignmentAnnotator() {
    }

    public void annotate(XmlSchemaBase base) {
        if (this.includes != null) {
            this.includes.forEach((e) -> {
                base.get(e);
            });
        } else {
            Set<String> set = new HashSet();
            if (this.excludes != null) {
                set.addAll(this.excludes);
            }

            base.getMappings().entrySet().forEach((e) -> {
                if (!set.contains(e.getKey())) {
                    MappingNode node = (MappingNode)e.getValue();
                    if (!node.getNodeType().equals(NodeType.Folder) && node.getAnnotation("mapping") != null) {
                        JsonObject mapping = (JsonObject)node.getAnnotation("mapping", JsonObject.class);
                        String prop = mapping.get(this.field).getAsString();
                        String assignment = mapping.get("assignment").getAsString();
                        if (this.value.equalsIgnoreCase(prop)) {
                            assignment = this.convert(this.function, assignment);
                            node.annotateAsMappedElement("mapping", "assignment", assignment);
                        }
                    }
                }

            });
        }

    }

    private String convert(String function, String assignment) {
        String token;
        for(token = function; token.contains("$$"); token = token.replace("$$", assignment)) {
        }

        return token;
    }
}
