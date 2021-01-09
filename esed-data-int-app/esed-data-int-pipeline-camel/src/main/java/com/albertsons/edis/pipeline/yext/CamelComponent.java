package com.albertsons.edis.pipeline.yext;

import com.albertsons.edis.PipelineComponent;
import com.albertsons.edis.PipelineProcessException;
import org.apache.camel.CamelContext;

public class CamelComponent extends PipelineComponent<CamelComponent.Processor> {

    public static class Processor extends PipelineComponent.Processor {

        CamelContext camelContext;
        @Override
        protected void init() {
            camelContext = pipelineContext.getService(CamelContext.class);
            System.out.println("---------------- " + camelContext);
        }

        @Override
        protected void destroy() {
            super.destroy();
        }

        @Override
        public void process() throws PipelineProcessException {

        }
    }
}
