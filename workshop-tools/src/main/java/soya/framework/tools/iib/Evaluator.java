package soya.framework.tools.iib;

import java.util.ArrayList;
import java.util.List;

public class Evaluator {
    protected boolean available = true;
    protected boolean optional;
    protected String value;

    public static final String SCRIPTLET_PREFIX = "%%";
    public static final String COMMENT_PREFIX = "--";

    private String expression;
    private boolean commented;
    private boolean scriptlet;

    private List<Function> functions = new ArrayList<>();

    public Evaluator(String expression) {
        if (expression.startsWith(COMMENT_PREFIX)) {
            this.expression = expression.substring(2).trim();
            commented = true;

        } else if (expression.startsWith(SCRIPTLET_PREFIX)) {
            this.expression = expression.substring(2).trim();
            this.scriptlet = true;

        } else if (expression.contains("(")) {
            this.expression = expression.trim();
            String[] arr = (expression.trim() + ".").split("\\)\\.");
            for (String token : arr) {
                if (token.trim().length() > 0) {
                    functions.add(new Function(token + ")"));
                }
            }
        } else {
            this.expression = expression.trim();
        }
    }

    public boolean isCommented() {
        return commented;
    }

    public boolean isScriptlet() {
        return scriptlet;
    }

    public String getExpression() {
        return expression;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public Function getFunction() {
        return functions.size() > 0 ? functions.get(0) : null;
    }
}
