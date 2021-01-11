package com.albertsons.edis.tools.xmlbeans;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoopAutoAnnotator extends MappingFeatureSupport implements Buffalo.Annotator<XmlSchemaBase> {

    private List<String> excludes;

    @Override
    public void annotate(XmlSchemaBase base) {
        Set<String> set = new HashSet<>();
        if (excludes != null) {
            set.addAll(excludes);
        }

        Set<Mapper> mapperSet = findLoops(base);
        mapperSet.forEach(e -> {
            if (!set.contains(e.getSourcePath())) {
                annotate(e, base);
            }
        });
    }

    private void annotate(Mapper mapper, XmlSchemaBase base) {
        XmlSchemaBase.MappingNode node = base.get(mapper.getTargetPath());
        Construct construct = node.getAnnotation(CONSTRUCT, Construct.class);
        if (construct == null) {
            construct = new Construct();
        }

        String source = mapper.getSourcePath();
        String name = "LOOP_" + source.replaceAll("/", "_").replaceAll("\\[\\*\\]", "").toUpperCase();

        String variable = source;
        if(variable.contains("/")) {
            variable = variable.substring(variable.lastIndexOf("/") + 1);
        }
        if (variable.endsWith("[*]")) {
            variable = variable.substring(0, variable.length() - 3);
        }
        variable = "_" + variable;

        WhileLoop loop = new WhileLoop();
        loop.sourcePath = source;
        loop.name = name;
        loop.variable = variable;
        construct.loops.add(loop);

        node.annotate(CONSTRUCT, construct);

    }
}
