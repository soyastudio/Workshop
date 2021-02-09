package soya.framework.tao.edis;

import soya.framework.tao.Barflow;

public abstract class EdisAnnotator extends EdisTask implements Barflow.Annotator<XsdTreeBase> {

    protected String name;
    protected String namespace;
}
