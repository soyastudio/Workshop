package soya.framework.tao.edis;

import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.KnowledgeTreeNode;
import soya.framework.tao.T123W;
import soya.framework.tao.xs.XsNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class XPathAssignmentAnnotator extends EdisAnnotator {
    private String file;

    public XPathAssignmentAnnotator file(String file) {
        this.file = file;
        return this;
    }

    @Override
    public void annotate(KnowledgeTree<SchemaTypeSystem, XsNode> knowlegeBase) throws T123W.FlowExecutionException {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(file)));
        } catch (IOException e) {
            throw new T123W.FlowExecutionException(e);
        }

        Map<String, String> globalVariable = new LinkedHashMap<>();
        Enumeration enumeration = properties.propertyNames();
        while(enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            if(key.startsWith(GLOBAL_VARIABLE_PREFIX)) {
                String varName = key.substring(GLOBAL_VARIABLE_PREFIX.length());
                String varValue = properties.getProperty(key);
                globalVariable.put(varName, varValue);
            }
        }
        knowlegeBase.annotate(NAMESPACE_GLOBAL_VARIABLE, globalVariable);


        knowlegeBase.paths().forEachRemaining(e -> {
            KnowledgeTreeNode<XsNode> treeNode = knowlegeBase.get(e);
            XsNode xsNode = treeNode.origin();

            String value = properties.getProperty(e);
            Function[] funcs = Function.fromString(value);
            if(funcs != null && funcs.length > 0) {
                if(XsNode.XsNodeType.Folder.equals(xsNode.getNodeType())) {
                    Construction construction = treeNode.getAnnotation(NAMESPACE_CONSTRUCTION, Construction.class);
                    if(construction == null) {
                        construction = new Construction();
                    }

                    construction.add(funcs);

                    treeNode.annotate(NAMESPACE_CONSTRUCTION, construction);

                } else {
                    Assignment assignment = knowlegeBase.get(e).getAnnotation(NAMESPACE_ASSIGNMENT, Assignment.class);
                    if(assignment == null) {
                        assignment = new Assignment();
                    }

                    assignment.add(funcs);
                    knowlegeBase.get(e).annotate(NAMESPACE_ASSIGNMENT, assignment);
                }
            }
        });
    }
}
