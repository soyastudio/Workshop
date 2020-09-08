package soya.framework.tools.xmlbeans;

import com.google.gson.GsonBuilder;

import java.util.LinkedHashSet;

public class LoopAnalyzeRenderer implements Buffalo.Renderer<XmlSchemaBase>, MappingFeature {

    @Override
    public String render(XmlSchemaBase base) {
        LinkedHashSet<Mapper> list = new LinkedHashSet<>();
        base.getMappings().entrySet().forEach(e -> {
            Mapper mapping = e.getValue().getAnnotation(MAPPING, Mapper.class);
            if (mapping != null && mapping.sourcePath != null && mapping.sourcePath.contains("[*]/")) {
                XmlSchemaBase.MappingNode node = findParent(e.getValue());
                if (node != null) {
                    String sourcePath = mapping.sourcePath;
                    sourcePath = sourcePath.substring(0, sourcePath.lastIndexOf("[*]/") + 3);
                    list.add(new Mapper(sourcePath, node.getPath()));
                }
            }
        });

        return new GsonBuilder().setPrettyPrinting().create().toJson(list);
    }

    private XmlSchemaBase.MappingNode findParent(XmlSchemaBase.MappingNode mappingNode) {
        XmlSchemaBase.MappingNode node = mappingNode.getParent();
        while (node != null) {
            Mapping mapping = node.getAnnotation(MAPPING, Mapping.class);
            if (!node.getCardinality().endsWith("-1")) {
                break;
            }
            node = node.getParent();
        }

        return node;
    }

    public static class Mapper implements Comparable<Mapper> {
        private String sourcePath;
        private String targetPath;

        public Mapper(String sourcePath, String targetPath) {
            this.sourcePath = sourcePath;
            this.targetPath = targetPath;
        }

        public String getSourcePath() {
            return sourcePath;
        }

        public String getTargetPath() {
            return targetPath;
        }

        @Override
        public int compareTo(Mapper o) {
            return this.sourcePath.compareTo(o.sourcePath);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Mapper mapping = (Mapper) o;

            return sourcePath != null ? sourcePath.equals(mapping.sourcePath) : mapping.sourcePath == null;
        }

        @Override
        public int hashCode() {
            return sourcePath != null ? sourcePath.hashCode() : 0;
        }
    }
}
