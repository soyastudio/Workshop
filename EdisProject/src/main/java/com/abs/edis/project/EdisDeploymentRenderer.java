package com.abs.edis.project;

import org.apache.xmlbeans.SchemaTypeSystem;
import soya.framework.tao.KnowledgeTree;
import soya.framework.tao.T123W;
import soya.framework.tao.xs.XsNode;

import java.io.File;

public class EdisDeploymentRenderer extends EdisRenderer {

    private File workspace;
    private String version;

    public EdisDeploymentRenderer workspace(File dir) {
        this.workspace = workspace;
        return this;
    }

    public EdisDeploymentRenderer version(String version) {
        this.version = version;
        return this;
    }

    @Override
    public String render(KnowledgeTree<SchemaTypeSystem, XsNode> knowledge) throws T123W.FlowExecutionException {

        return null;
    }
}
