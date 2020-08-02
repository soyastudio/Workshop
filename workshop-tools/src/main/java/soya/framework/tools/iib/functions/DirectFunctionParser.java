package soya.framework.tools.iib.functions;

import soya.framework.tools.iib.CmmESQLGenerator;

public class DirectFunctionParser extends AbstractFunctionParser {
    private static final String name = "direct";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String parse(String exp, CmmESQLGenerator context) {
        return "'" + exp + "'";
    }
}
