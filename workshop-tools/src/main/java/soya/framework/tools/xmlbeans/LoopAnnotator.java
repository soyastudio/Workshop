package soya.framework.tools.xmlbeans;

import java.util.LinkedHashMap;
import java.util.Map;

public class LoopAnnotator implements Buffalo.Annotator<XmlSchemaBase> {

    private String name;
    private String sourcePath;
    private String targetPath;
    private String variable;

    private String parent;

    @Override
    public void annotate(XmlSchemaBase base) {
        Map<String, LoopDescription> loops = (Map<String, LoopDescription>) base.getAnnotations().get("loops");
        if(loops == null) {
            loops = new LinkedHashMap<>();
            base.annotate("loops", loops);
        }

        LoopDescription loop = new LoopDescription();
        loop.name = name;
        loop.sourcePath = sourcePath;
        loop.targetPath = targetPath;
        loop.variable = variable;
        loop.parent = parent;

        loops.put(loop.name, loop);
    }

    static class LoopDescription {
        private String name;
        private String sourcePath;
        private String targetPath;
        private String variable;
        private String parent;

        private LoopDescription() {
        }

    }
}
