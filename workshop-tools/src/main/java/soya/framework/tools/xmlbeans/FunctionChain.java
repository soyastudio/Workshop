package soya.framework.tools.xmlbeans;

import java.util.ArrayList;
import java.util.List;

public class FunctionChain {

    ConstructChainBuilder constructChainBuilder() {
        return new ConstructChainBuilder();
    }

    static class ConstructChainBuilder {
        private String from;
        private String as;
        private List<Function> functions = new ArrayList<>();

        private ConstructChainBuilder() {
        }

        public ConstructChainBuilder from(String from) {
            this.from = from;
            return this;
        }

        public ConstructChainBuilder as(String as) {
            this.as = as;
            return this;
        }
    }
}
