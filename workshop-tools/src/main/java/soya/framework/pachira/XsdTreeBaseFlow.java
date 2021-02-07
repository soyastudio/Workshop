package soya.framework.pachira;

import java.io.File;
import java.util.Iterator;

public class XsdTreeBaseFlow extends TreeBaseFlow<XsdTreeBase> {
    //
    public static void main(String[] args) {
        XsdTreeBase treeBase = XsdTreeBaseFlow.newInstance().baseline(
                XsdTreeBase.builder()
                        .name("Wen Qun")
                        .file(new File("C:/github/Workshop/Repository/CMM/BOD/GetCustomerPreferences.xsd"))).getBaseLine();

        Iterator<String> iter = treeBase.knowledgeBase().paths();
        while (iter.hasNext()) {
            String path = iter.next();
            System.out.println(path + "=");
        }
    }

    //
    public static XsdTreeBaseFlow newInstance() {
        return new XsdTreeBaseFlow();
    }

    public static XsdTreeBaseFlow fromYaml(String yaml) {
        return null;
    }

    public static XsdTreeBaseFlow fromJson(String json) {
        return null;
    }

    public static XsdTreeBaseFlow fromXml(String xml) {
        return null;
    }

    public class TreeBaseBuilder implements BaselineBuilder<XsdTreeBase> {

        @Override
        public BaselineBuilder<XsdTreeBase> digester(Digester<?, ?> digester) {
            return null;
        }

        @Override
        public XsdTreeBase create() {
            return null;
        }
    }

}
