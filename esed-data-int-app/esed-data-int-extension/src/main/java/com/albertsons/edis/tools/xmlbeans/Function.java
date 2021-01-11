package com.albertsons.edis.tools.xmlbeans;

import java.util.ArrayList;
import java.util.List;

public class Function {
    public static final String CHAIN = "chain().";

    public static final String DEFAULT_FUNCTION = "DEFAULT";
    public static final String FROM_FUNCTION = "FROM";

    private String name;
    private String[] arguments;

    private Function(String name, String[] args) {
        this.name = name;
        this.arguments = new String[args.length];
        for(int i = 0; i < args.length; i ++) {
            this.arguments[i] = args[i].trim();
        }
    }

    public String getName() {
        return name;
    }

    public String[] getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name).append("(");
        for (int i = 0; i < arguments.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }

            sb.append(arguments[i]);
        }
        sb.append(")");
        return sb.toString();
    }

    public static String toString(Function[] functions) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < functions.length; i ++) {
            if(i > 0) {
                sb.append(".");
            }
            sb.append(functions[i].toString());
        }
        return sb.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Function[] fromString(String func) {
        String[] array = func.split("\\)\\.");
        Function[] functions = new Function[array.length];
        for(int i = 0; i < array.length; i ++) {
            String exp = array[i];
            if(exp.endsWith(")")) {
                exp = exp.substring(0, exp.length() - 1);
            }

            int sep = exp.indexOf('(');
            String funcName = exp.substring(0, sep);
            String[] args = exp.substring(sep + 1).split(",");

            functions[i] = new Function(funcName, args);
        }

        return functions;
    }

    public static class Builder {
        private String name;
        private List<String> arguments = new ArrayList<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder argument(String arg) {
            this.arguments.add(arg);
            return this;
        }

        public Function create() {
            return new Function(name, arguments.toArray(new String[arguments.size()]));
        }
    }
}
