package com.albertsons.esed.monitor.server;

import com.albertsons.esed.monitor.service.ApiInvocationService;
import com.albertsons.esed.monitor.service.DataAccessService;
import com.google.common.eventbus.Subscribe;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

//@Service
public class PipelineService implements ServiceEventListener<PipelineEvent> {
    static Logger logger = LoggerFactory.getLogger(PipelineService.class);

    @Autowired
    private PipelineContext pipelineContext;

    @Autowired
    private ExecutorService executorService;

    private Map<String, Pipeline> pipelines = new ConcurrentHashMap<>();

    @Subscribe
    public void onEvent(PipelineEvent event) {
        Pipeline pipeline = pipelines.get(event.getPipeline());

        if (event instanceof PipelineEvent.PipelineCreationEvent) {
            onCreationEvent((PipelineEvent.PipelineCreationEvent) event);

        } else if (event instanceof PipelineExecutionEvent) {
            processPipelineExecutionEvent((PipelineExecutionEvent) event);

        }
    }

    private void onCreationEvent(PipelineEvent.PipelineCreationEvent event) {
        Pipeline pipeline = Pipeline.create(event.getFile());
        pipelines.put(pipeline.getName(), pipeline);

        if (pipeline.getProcessor() != null) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    // initialize processor:
                    pipeline.getProcessor().init(pipelineContext);

                    // schedule:
                    if (pipeline.getBod().getCalendar() != null) {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                PipelineServer.getInstance().publish(ScheduleEvent
                                        .pipelineScheduleEvent(event, "pipeline-execution", pipeline.getName(), pipeline.getBod().getDelay(), pipeline.getBod().getCalendar()));

                            }
                        }, new Random().nextInt(30000));
                    }
                }
            });

        }
    }

    private void processPipelineExecutionEvent(PipelineExecutionEvent event) {
        logger.info("Processing pipeline {}...", event.getPipeline());

        Pipeline pipeline = pipelines.get(event.getPipeline());
        pipeline.getProcessor().process(event);
    }

    static class PipelineExecutionJob implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            String pipeline = jobExecutionContext.getJobDetail().getJobDataMap().getString("PIPELINE");
            PipelineServer.getInstance().publish(new PipelineExecutionEvent(pipeline));
        }
    }
}
