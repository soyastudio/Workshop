package soya.framework.tools.iib.functions;

import soya.framework.tools.iib.CmmESQLGenerator;

public class JsonpathFunctionParser extends AbstractFunctionParser {

    private static final String name = "jsonpath";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String parse(String exp, CmmESQLGenerator context) {
        return exp.replace("$", context.getInputRootVariable());
    }
}
