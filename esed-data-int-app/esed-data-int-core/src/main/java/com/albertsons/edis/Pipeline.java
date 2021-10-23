package com.albertsons.edis;

public class Pipeline {
    private Bod bod;
    private PipelinePattern<?> pattern;

    private Pipeline(Bod bod, PipelinePattern<?> pattern) {
        this.bod = bod;
        this.pattern = pattern;
    }

    public String getName() {
        return bod.name;
    }

    public Bod getMetadata() {
        return bod;
    }

    public PipelinePattern<?> getPattern() {
        return pattern;
    }

    public static Pipeline newInstance(Bod bod, PipelinePattern<?> pattern) {
        return new Pipeline(bod, pattern);
    }

    public static class Bod {
        private String name;
        private String pattern;

        private long timeout = 600000l;
        private String calendar;
        private long delay;

        public String getName() {
            return name;
        }

        public String getPattern() {
            return pattern;
        }

        public long getTimeout() {
            return timeout;
        }

        public String getCalendar() {
            return calendar;
        }

        public long getDelay() {
            return delay;
        }
    }

}
