package com.albertsons.edis.iib.kafka;

import com.ibm.broker.connector.ByteArrayInputRecord;
import com.ibm.broker.connector.InputRecord;
import com.ibm.broker.connector.PollingInputConnector;
import com.ibm.broker.connector.PollingResult;
import com.ibm.broker.plugin.MbException;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class KafkaPollingResult extends PollingResult {
    private ConsumerRecord record;

    public KafkaPollingResult(PollingInputConnector var1, ConsumerRecord var2) throws MbException {
        super(var1);
        this.record = var2;
    }

    public ConsumerRecord getRecord() {
        return this.record;
    }

    public InputRecord buildInputRecord() throws MbException {
        ByteArrayInputRecord var1 = new ByteArrayInputRecord();
        Object var2 = this.record.value();
        System.out.println("RECORD VALUE:" + this.record.value().toString());
        if (var2 instanceof byte[]) {
            var1.appendByteArray((byte[])((byte[])this.record.value()));
        } else {
            var1.appendByteArray(var2.toString().getBytes());
        }

        return var1;
    }

    public Properties buildProperties() throws MbException {
        Properties var1 = new Properties();
        var1.setProperty("partition", Integer.toString(this.record.partition()));
        var1.setProperty("offset", Long.toString(this.record.offset()));
        var1.setProperty("topicName", this.record.topic());
        var1.setProperty("checksum", Long.toString(this.record.checksum()));
        Object var2 = this.record.key();
        if (var2 != null) {
            var1.setProperty("key", var2.toString());
        }

        return var1;
    }

    public String getTopic() {
        return this.record.topic();
    }

    public int getPartition() {
        return this.record.partition();
    }

    public long getOffset() {
        return this.record.offset();
    }

    public String getKey() {
        Object var1 = this.record.key();
        return var1 == null ? null : var1.toString();
    }

    public void confirm() {
    }

    public void markAsFailed() {
    }
}

