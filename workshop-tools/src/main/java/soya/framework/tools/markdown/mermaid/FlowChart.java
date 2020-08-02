package soya.framework.tools.markdown.mermaid;

import com.google.common.collect.ImmutableList;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class FlowChart {
    public static final String LINK_TOKEN = "-->";
    public static final String OPEN_LINK_TOKEN = "---";

    private final String name;
    private final String annotation;

    private final String graph;
    private final ImmutableList<SubGraph> subGraphs;
    private final ImmutableList<Link> links;

    private transient Map<String, Node> nodes = new LinkedHashMap<>();

    private FlowChart(String name, String annotation, String graph, ImmutableList<SubGraph> subGraphs, ImmutableList<Link> links) {
        this.name = name;
        this.annotation = annotation;
        this.graph = graph;
        this.subGraphs = subGraphs;
        this.links = links;

        links.forEach(e -> {
            if (!nodes.containsKey(e.from.id)) {
                nodes.put(e.from.id, e.from);
            }

            if (!nodes.containsKey(e.to.id)) {
                nodes.put(e.to.id, e.to);
            }
        });
    }

    public String getName() {
        return name;
    }

    public String getAnnotation() {
        return annotation;
    }

    public String getGraph() {
        return graph;
    }

    public ImmutableList<SubGraph> getSubGraphs() {
        return subGraphs;
    }

    public ImmutableList<Link> getLinks() {
        return links;
    }

    public List<Node> getNodes() {
        return ImmutableList.copyOf(nodes.values());
    }

    public Node getNode(String id) {
        return nodes.get(id);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("```mermaid");
        builder.append("\n");
        builder.append("graph ").append(graph).append("\n");

        if (subGraphs != null && !subGraphs.isEmpty()) {
            subGraphs.forEach(g ->{
                builder.append("\t");
                builder.append("subgraph ").append(g.name).append("\n");

                g.links.forEach(e -> {
                    builder.append("\t");
                    builder.append(e).append("\n");
                });

                builder.append("\t");
                builder.append("end").append("\n");
            });
        }

        links.forEach(e -> {
            builder.append("\t");
            builder.append(e).append("\n");
        });

        builder.append("```");

        return builder.toString();
    }

    public static FlowChartBuilder builder() {
        return new FlowChartBuilder();
    }

    public static List<FlowChart> scan(String markdown) {
        Parser parser = Parser.builder().build();
        org.commonmark.node.Node node = parser.parse(markdown);
        FlowChartVisitor visitor = new FlowChartVisitor();
        node.accept(visitor);
        return ImmutableList.copyOf(visitor.flowCharts);
    }

    public static class FlowChartBuilder {
        private String name;
        private String annotation;
        private GraphType graphType = GraphType.DT;
        private Set<SubGraph> subgraphs = new LinkedHashSet<>();
        private SubGraph subgraph;

        private Set<Link> links = new LinkedHashSet<>();

        private FlowChartBuilder() {
        }

        public FlowChartBuilder info(String info) {
            String declare = info.trim();
            if ("mermaid".equals(declare)) {


            } else if (declare.startsWith("mermaid ") || declare.startsWith("mermaid:")) {
                declare = declare.substring("mermaid".length() + 1).trim();
                if (declare.trim().length() > 0) {
                    // TODO: parse
                }
            }
            return this;
        }

        public FlowChartBuilder annotate(String annotation) {
            this.annotation = annotation;
            return this;
        }

        public FlowChartBuilder graph(GraphType graphType) {
            this.graphType = graphType;
            return this;
        }

        public FlowChartBuilder startSubGraph(String subgraph) {
            String name = subgraph.substring(subgraph.indexOf("subgraph ") + "subgraph ".length()).trim();
            this.subgraph = new SubGraph(name);
            return this;
        }

        public FlowChartBuilder endSubGraph() {
            if (subgraph != null) {
                subgraphs.add(subgraph);
            }
            this.subgraph = null;
            return this;
        }

        public FlowChartBuilder addLink(String line) {
            Link link = new Link(line);
            if (subgraph != null) {
                subgraph.links.add(link);
            } else {
                links.add(link);
            }
            return this;
        }

        public FlowChart build() {
            return new FlowChart(name, annotation, graphType.name(), ImmutableList.copyOf(subgraphs), ImmutableList.copyOf(links));
        }
    }

    private static class FlowChartVisitor extends AbstractVisitor {
        private List<FlowChart> flowCharts = new ArrayList<>();

        @Override
        public void visit(FencedCodeBlock fencedCodeBlock) {
            String info = fencedCodeBlock.getInfo();
            if (info.startsWith("mermaid")) {
                String codeBlock = fencedCodeBlock.getLiteral();
                FlowChartBuilder builder = builder();
                try {
                    String readLine = "";
                    BufferedReader b = new BufferedReader(new StringReader(codeBlock));
                    while ((readLine = b.readLine()) != null) {
                        process(readLine, builder);
                    }

                    flowCharts.add(builder.build());

                } catch (IOException e) {

                }

            }
        }

        private void process(String readLine, FlowChartBuilder builder) {
            String line = readLine.trim();
            if (line.startsWith("graph ")) {
                String gt = line.substring("graph ".length()).trim();
                if(GraphType.valueOf(gt) != null) {
                    builder.graph(GraphType.valueOf(gt));
                }

            } else if (line.startsWith("subgraph ")) {
                builder.startSubGraph(line);

            } else if (line.equals("end")) {
                builder.endSubGraph();

            } else if (line.contains(LINK_TOKEN)) {
                String ln = line.trim();
                if (ln.endsWith(";")) {
                    ln = ln.substring(0, ln.length() - 1);
                }
                String[] arr = ln.split(LINK_TOKEN);
                String from = null;
                for (String token : arr) {
                    String point = token.trim();
                    if (from != null) {
                        String linkString = from + " " + LINK_TOKEN + " " + point;
                        builder.addLink(linkString);

                    }
                    from = point;
                }
            } else if (line.contains(OPEN_LINK_TOKEN)) {
                String ln = line.trim();
                if (ln.endsWith(";")) {
                    ln = ln.substring(0, ln.length() - 1);
                }
                String[] arr = ln.split(OPEN_LINK_TOKEN);
                String from = null;
                for (String token : arr) {
                    String point = token.trim();
                    if (from != null) {
                        String linkString = from + " " + OPEN_LINK_TOKEN + " " + point;
                        builder.addLink(linkString);

                    }
                    from = point;
                }
            }
        }
    }

    static class Link {
        private String linkToken;
        private Node from;
        private Node to;
        private String text;

        public Link(String link) {
            int index = link.indexOf(LINK_TOKEN);
            if (index > 0) {
                linkToken = LINK_TOKEN;
            } else {
                index = link.indexOf(OPEN_LINK_TOKEN);
                if (index > 0) {
                    linkToken = OPEN_LINK_TOKEN;
                } else {
                    throw new IllegalArgumentException();
                }
            }

            from = new Node(link.substring(0, index));
            String rightPart = link.substring(index + 3).trim();
            if (rightPart.startsWith("[")) {
                int i = rightPart.indexOf("]");
                text = rightPart.substring(1, i);
                rightPart = rightPart.substring(i + 1);
            }
            to = new Node(rightPart);

            from.toLinks.add(this);
            to.fromLinks.add(this);
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append(from.toString())
                    .append(" ")
                    .append(linkToken)
                    .append(" ")
                    .append(to.toString())
                    .append(";")
                    .toString();
        }
    }

    static class SubGraph {
        private String name;
        private Set<Link> links = new LinkedHashSet<>();

        SubGraph(String name) {
            this.name = name;
        }
    }

    static class Node {
        private String id;
        private NodeType type = NodeType.DEFAULT;
        private String text;

        private transient Set<Link> toLinks = new LinkedHashSet<>();
        private transient Set<Link> fromLinks = new LinkedHashSet<>();

        public Node(String node) {
            String token = node.trim();
            int startTokenIndex = -1;
            int endTokenIndex = -1;
            int len = 0;

            if (token.endsWith(NodeType.CIRCLE.endToken) && token.indexOf(NodeType.CIRCLE.startToken) > 0) {
                type = NodeType.CIRCLE;
                startTokenIndex = token.indexOf(NodeType.CIRCLE.startToken);
                endTokenIndex = token.lastIndexOf(NodeType.CIRCLE.endToken);
                len = 2;

            } else if (token.endsWith(NodeType.CYLINDER.endToken) && token.indexOf(NodeType.CYLINDER.startToken) > 0) {
                type = NodeType.CYLINDER;
                startTokenIndex = token.indexOf(NodeType.CYLINDER.startToken);
                endTokenIndex = token.lastIndexOf(NodeType.CYLINDER.endToken);
                len = 2;

            } else if (token.endsWith(NodeType.STADIUM.endToken) && token.indexOf(NodeType.STADIUM.startToken) > 0) {
                type = NodeType.STADIUM;
                startTokenIndex = token.indexOf(NodeType.STADIUM.startToken);
                endTokenIndex = token.lastIndexOf(NodeType.STADIUM.endToken);
                len = 2;

            } else if (token.endsWith(NodeType.HEXAGON.endToken) && token.indexOf(NodeType.HEXAGON.startToken) > 0) {
                type = NodeType.HEXAGON;
                startTokenIndex = token.indexOf(NodeType.HEXAGON.startToken);
                endTokenIndex = token.lastIndexOf(NodeType.HEXAGON.endToken);
                len = 2;

            } else if (token.endsWith(NodeType.LEFT_PARALLELOGRAM.endToken) && token.indexOf(NodeType.LEFT_PARALLELOGRAM.startToken) > 0) {
                type = NodeType.LEFT_PARALLELOGRAM;
                startTokenIndex = token.indexOf(NodeType.LEFT_PARALLELOGRAM.startToken);
                endTokenIndex = token.lastIndexOf(NodeType.LEFT_PARALLELOGRAM.endToken);
                len = 2;

            } else if (token.endsWith(NodeType.RIGHT_PARALLELOGRAM.endToken) && token.indexOf(NodeType.RIGHT_PARALLELOGRAM.startToken) > 0) {
                type = NodeType.RIGHT_PARALLELOGRAM;
                startTokenIndex = token.indexOf(NodeType.RIGHT_PARALLELOGRAM.startToken);
                endTokenIndex = token.lastIndexOf(NodeType.RIGHT_PARALLELOGRAM.endToken);
                len = 2;

            } else if (token.endsWith(NodeType.UPPER_TRAPEZOID.endToken) && token.indexOf(NodeType.UPPER_TRAPEZOID.startToken) > 0) {
                type = NodeType.UPPER_TRAPEZOID;
                startTokenIndex = token.indexOf(NodeType.UPPER_TRAPEZOID.startToken);
                endTokenIndex = token.lastIndexOf(NodeType.UPPER_TRAPEZOID.endToken);
                len = 2;

            } else if (token.endsWith(NodeType.DOWN_TRAPEZOID.endToken) && token.indexOf(NodeType.DOWN_TRAPEZOID.startToken) > 0) {
                type = NodeType.DOWN_TRAPEZOID;
                startTokenIndex = token.indexOf(NodeType.DOWN_TRAPEZOID.startToken);
                endTokenIndex = token.lastIndexOf(NodeType.DOWN_TRAPEZOID.endToken);
                len = 2;

            } else if (token.endsWith(NodeType.RECTANGLE.endToken) && token.indexOf(NodeType.RECTANGLE.startToken) > 0) {
                type = NodeType.RECTANGLE;
                startTokenIndex = token.indexOf(NodeType.RECTANGLE.startToken);
                endTokenIndex = token.lastIndexOf(NodeType.RECTANGLE.endToken);
                len = 1;

            } else if (token.endsWith(NodeType.ROUND_RECTANGLE.endToken) && token.indexOf(NodeType.ROUND_RECTANGLE.startToken) > 0) {
                type = NodeType.ROUND_RECTANGLE;
                startTokenIndex = token.indexOf(NodeType.ROUND_RECTANGLE.startToken);
                endTokenIndex = token.lastIndexOf(NodeType.ROUND_RECTANGLE.endToken);
                len = 1;

            } else if (token.endsWith(NodeType.RHOMBUS.endToken) && token.indexOf(NodeType.RHOMBUS.startToken) > 0) {
                type = NodeType.RHOMBUS;
                startTokenIndex = token.indexOf(NodeType.RHOMBUS.startToken);
                endTokenIndex = token.lastIndexOf(NodeType.RHOMBUS.endToken);
                len = 1;

            } else if (token.endsWith(NodeType.ASYMMETRIC.endToken) && token.indexOf(NodeType.ASYMMETRIC.startToken) > 0) {
                type = NodeType.ASYMMETRIC;
                startTokenIndex = token.indexOf(NodeType.ASYMMETRIC.startToken);
                endTokenIndex = token.lastIndexOf(NodeType.ASYMMETRIC.endToken);
                len = 1;

            } else {
                id = token;
            }

            if (startTokenIndex > 0 && endTokenIndex > 0 && startTokenIndex < endTokenIndex) {
                id = token.substring(0, startTokenIndex);
                text = token.substring(startTokenIndex + len, endTokenIndex);
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(id);
            if (!NodeType.DEFAULT.equals(type)) {
                builder.append(type.startToken).append(text).append(type.endToken);
            }
            return builder.toString();
        }
    }

    public static enum GraphType {
        TD, LR, RL, DT;
    }

    public static enum NodeType {
        DEFAULT(null, null),
        RECTANGLE("[", "]"),
        ROUND_RECTANGLE("(", ")"),
        STADIUM("([", "])"),
        CYLINDER("[(", ")]"),
        CIRCLE("((", "))"),
        RHOMBUS("{", "}"),
        HEXAGON("{{", "}}"),
        LEFT_PARALLELOGRAM("[/", "/]"),
        RIGHT_PARALLELOGRAM("[\\", "\\]"),
        UPPER_TRAPEZOID("[/", "\\]"),
        DOWN_TRAPEZOID("[\\", "/]"),
        ASYMMETRIC(">", "]");

        private final String startToken;
        private final String endToken;

        NodeType(String startToken, String endToken) {
            this.startToken = startToken;
            this.endToken = endToken;
        }
    }
}
