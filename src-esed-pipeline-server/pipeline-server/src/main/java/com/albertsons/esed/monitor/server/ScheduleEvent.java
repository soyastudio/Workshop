package com.albertsons.esed.monitor.server;

public class ScheduleEvent extends ServiceEvent {
    private final String group;
    private final String name;

    private final long delay;
    private final String calendar;

    public ScheduleEvent(ServiceEvent parent, String group, String name, long delay, String calendar) {
        super(parent);
        this.group = group;
        this.name = name;
        this.delay = delay;
        this.calendar = calendar;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public long getDelay() {
        return delay;
    }

    public String getCalendar() {
        return calendar;
    }

    public static PipelineScheduleEvent pipelineScheduleEvent(ServiceEvent parent, String group, String name, long delay, String calendar) {
        return new PipelineScheduleEvent(parent, group, name, delay, calendar);
    }

    public static class PipelineScheduleEvent extends ScheduleEvent {
        public PipelineScheduleEvent(ServiceEvent parent, String group, String name, long delay, String calendar) {
            super(parent, group, name, delay, calendar);
        }
    }
}
