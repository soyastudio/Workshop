package soya.framework.tools.xmlbeans;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class LoopAnalyzeRenderer implements Buffalo.Renderer<XmlSchemaBase> {
    @Override
    public String render(XmlSchemaBase base) {
        LinkedHashSet<Mapping> list = new LinkedHashSet<>();

        base.getMappings().entrySet().forEach(e -> {
            String extraction = (String) e.getValue().getAnnotation("extract");
            if (extraction != null) {
                Function function = Function.parse(extraction);
                if (function.getName().equals("jsonpath")) {
                    String token = function.getArgument();
                    int index = token.lastIndexOf("/Item[*]");
                    if (index > 0) {
                        token = token.substring(0, index) + "/Item[*]";

                        XmlSchemaBase.MappingNode node = e.getValue();
                        while(node != null && node.getAnnotation("array") == null) {
                            node = node.getParent();
                        }

                        list.add(new Mapping(token, node.getPath()));
                    }
                }
            }
        });

        return new GsonBuilder().setPrettyPrinting().create().toJson(list);
    }

    public static class Mapping implements Comparable<Mapping>{
        private String sourcePath;
        private String targetPath;

        public Mapping(String sourcePath, String targetPath) {
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
        public int compareTo(Mapping o) {
            return this.sourcePath.compareTo(o.sourcePath);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Mapping mapping = (Mapping) o;

            return sourcePath != null ? sourcePath.equals(mapping.sourcePath) : mapping.sourcePath == null;
        }

        @Override
        public int hashCode() {
            return sourcePath != null ? sourcePath.hashCode() : 0;
        }
    }
}
