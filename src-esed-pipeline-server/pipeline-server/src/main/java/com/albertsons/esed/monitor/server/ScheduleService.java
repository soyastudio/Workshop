package com.albertsons.esed.monitor.server;

import com.google.common.eventbus.Subscribe;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class ScheduleService implements ServiceEventListener<ScheduleEvent> {
    static Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    private Scheduler scheduler;

    @Subscribe
    public void onEvent(ScheduleEvent scheduleEvent) {
        if (scheduleEvent instanceof ScheduleEvent.PipelineScheduleEvent) {
            processPipelineScheduleEvent((ScheduleEvent.PipelineScheduleEvent) scheduleEvent);
        }
    }

    private void processPipelineScheduleEvent(ScheduleEvent.PipelineScheduleEvent event) {
        logger.info("process pipeline schedule event: {}", event.getName());

        JobDetail job = JobBuilder.newJob(PipelineExecutionJob.class)
                .withIdentity(event.getName(), event.getGroup())
                .build();

        Trigger scanTrigger = TriggerBuilder.newTrigger().withIdentity(event.getName(), event.getGroup())
                .startAt(new Date(System.currentTimeMillis() + event.getDelay()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(Integer.parseInt(event.getCalendar()))
                        .repeatForever())
                .build();

        try {
            scheduler.scheduleJob(job, scanTrigger);

        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    static class PipelineExecutionJob implements Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            PipelineServer.getInstance().publish(new PipelineExecutionEvent(jobExecutionContext.getJobDetail().getKey().getName()));
        }
    }
}
