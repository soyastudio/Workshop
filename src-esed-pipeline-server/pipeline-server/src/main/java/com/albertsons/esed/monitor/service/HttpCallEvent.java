package com.albertsons.esed.monitor.service;

import com.albertsons.esed.monitor.server.ServiceEvent;

import java.util.LinkedHashSet;
import java.util.Set;


public class HttpCallEvent extends ServiceEvent<String> {

    private final String url;
    private HttpMethod httpMethod = HttpMethod.GET;

    private String payload;

    private String filter;
    private Shifter[] shifters;

    protected HttpCallEvent(ServiceEvent<?> parent, String url) {
        super(parent);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPayload() {
        return payload;
    }

    public String getFilter() {
        return filter;
    }

    public Shifter[] getShifters() {
        return shifters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ServiceEvent<?> parent;
        private String url;
        private HttpMethod httpMethod = HttpMethod.GET;
        private String payload;

        private String filter;
        private Set<Shifter> shifters = new LinkedHashSet<>();

        public Builder parent(ServiceEvent<?> parent) {
            this.parent = parent;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder filter(String filter) {
            this.filter = filter;
            return this;
        }

        public Builder shifter(String from, String to, String expression) {
            shifters.add(new Shifter(from, to, expression));
            return this;
        }

        public HttpCallEvent create() {
            if (url == null) {
                throw new IllegalArgumentException("url is not set");
            }

            HttpCallEvent event = new HttpCallEvent(parent, url);
            event.httpMethod = httpMethod;

            event.payload = payload;

            event.filter = filter;
            event.shifters = shifters.toArray(new Shifter[shifters.size()]);

            return event;
        }
    }

    public static enum HttpMethod {
        GET, POST, PUT, DELETE
    }

    public static class Shifter {
        private final String from;
        private final String to;
        private final String expression;

        private Shifter(String from, String to, String expression) {
            this.from = from;
            this.to = to;
            this.expression = expression;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public String getExpression() {
            return expression;
        }
    }
}
