package com.albertsons.esed.monitor.service;

import com.albertsons.esed.monitor.server.*;
import com.google.common.eventbus.Subscribe;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Service
public class PipelineService implements ServiceEventListener<PipelineEvent> {
    static Logger logger = LoggerFactory.getLogger(PipelineService.class);

    @Autowired
    private PipelineContext pipelineContext;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private ApiInvocationService invoker;

    @Autowired
    private DataAccessService das;

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
                                long delay = pipeline.getBod().getDelay();
                                if (delay <= 0) {
                                    delay = new Random().nextInt(30000);
                                }

                                JobDetail job = JobBuilder.newJob(PipelineExecutionJob.class)
                                        .withIdentity(pipeline.getBod().getName(), "pipeline-execution")
                                        .build();
                                job.getJobDataMap().put("PIPELINE", pipeline.getBod().getName());

                                Trigger scanTrigger = TriggerBuilder.newTrigger().withIdentity(pipeline.getBod().getName(), "pipeline-execution")
                                        .startAt(new Date(System.currentTimeMillis() + delay))
                                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                                .withIntervalInSeconds(Integer.parseInt(pipeline.getBod().getCalendar()))
                                                .repeatForever())
                                        .build();

                                try {
                                    scheduler.scheduleJob(job, scanTrigger);

                                } catch (SchedulerException e) {
                                    throw new RuntimeException(e);
                                }

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
