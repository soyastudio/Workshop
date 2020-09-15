package soya.framework.tools.xmlbeans;

public class Function {
    public static final String CHAIN = "chain().";

    public static final Function TODO = new Function("todo", "");
    public static final Function IGNORE = new Function("ignore", "");

    private String name;
    private String argument;

    public Function(String name, String argument) {
        this.name = name;
        this.argument = argument;
    }

    public String getName() {
        return name;
    }

    public String getArgument() {
        return argument;
    }

    @Override
    public String toString() {
        return name + "(" + argument + ")";
    }

    public static Function parse(String func) {
        int start = func.indexOf('(');
        int end = func.indexOf(')');

        String name = func.substring(0, start);
        String arg = func.substring(start + 1, end);

        return new Function(name, arg);
    }
}
