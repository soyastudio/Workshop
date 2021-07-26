package com.albertsons.edis.iib.util;

import com.ibm.broker.plugin.MbElement;
import org.w3c.dom.Node;

public class MbElementUtils {

    public static void check(MbElement mbElement) throws Exception {
        Node node = mbElement.getDOMNode();
        if (node.getTextContent() != null) {

        } else if (node.getChildNodes().getLength() > 0) {
            if("Item".endsWith(node.getFirstChild().getNodeName())) {

            } else {
                node.getChildNodes();
            }

        }
    }
}
