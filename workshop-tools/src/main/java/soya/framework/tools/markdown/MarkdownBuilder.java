package soya.framework.tools.markdown;

public class MarkdownBuilder {

    private static String[] HEADINGS = new String[]{"# ", "## ", "### ", "#### ", "##### ", "###### ", "####### ", "######### ", "########## ", "########### "};

    private StringBuilder builder;

    private MarkdownBuilder() {
        this.builder = new StringBuilder();
    }

    public MarkdownBuilder heading(String text) {
        builder.append(HEADINGS[0]).append(text).append("\n");
        return this;
    }

    public MarkdownBuilder heading(String text, int level) {
        builder.append(HEADINGS[level - 1]).append(text).append("\n");
        return this;
    }

    public MarkdownBuilder text(String text) {
        builder.append(text).append("\n");
        return this;
    }

    public MarkdownBuilder fencedCodeBlock(String text) {
        builder.append("```")
                .append("\n")
                .append(text)
                .append("\n")
                .append("\n")
                .append("```")
                .append("\n");

        return this;
    }

    public String toString() {
        return builder.toString();
    }

    public static MarkdownBuilder newInstance() {
        return new MarkdownBuilder();
    }
}
