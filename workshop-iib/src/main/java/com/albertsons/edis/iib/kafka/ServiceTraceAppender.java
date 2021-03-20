package com.albertsons.edis.iib.kafka;

import com.ibm.broker.trace.Trace;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class ServiceTraceAppender extends AppenderSkeleton {
    private static final String className = "ServiceTraceAppender";

    public ServiceTraceAppender() {
        if (Trace.isOn) {
            Trace.logNamedEntry(this, "<constructor>");
        }

        if (Trace.isOn) {
            Trace.logNamedExit(this, "<constructor>");
        }

    }

    protected void append(LoggingEvent var1) {
        if (Trace.isOn) {
            Trace.logNamedTrace(var1.getLocationInformation().getClassName(), var1.getLocationInformation().getMethodName(), var1.getMessage().toString());
            if (var1.getThrowableStrRep() != null) {
                String[] var2 = var1.getThrowableStrRep();
                int var3 = var2.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    String var5 = var2[var4];
                    Trace.logNamedTrace(var1.getLocationInformation().getClassName(), var1.getLocationInformation().getMethodName(), var5);
                }
            }
        }

    }

    public boolean requiresLayout() {
        return false;
    }

    public void close() {
    }
}

