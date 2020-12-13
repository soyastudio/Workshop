package com.albertsons.esed.monitor.patterns.transportation;

import com.albertsons.esed.monitor.server.Callback;
import com.albertsons.esed.monitor.server.PipelineExecutionEvent;
import com.albertsons.esed.monitor.server.PipelineProcessorSupport;
import com.albertsons.esed.monitor.server.PipelineServer;
import com.albertsons.esed.monitor.service.HttpCallEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransportationApiScanProcessor extends PipelineProcessorSupport {
    static Logger logger = LoggerFactory.getLogger(TransportationApiScanProcessor.class);

    @Override
    protected void init() {

    }

    @Override
    public void process(PipelineExecutionEvent event) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ExportCode", "ABSNWCompletedTrips");
        jsonObject.addProperty("ProsperoCustomWebServiceGuid", "007F487D-657F-4B5D-A961-D738B840A689");

        JsonArray params = new JsonArray();
        JsonObject param = new JsonObject();
        param.addProperty("Code", "LastPullDate");
        param.addProperty("Value", "2020-12-08 08:00:32");
        params.add(param);

        jsonObject.add("Parameters", params);

        HttpCallEvent httpCallEvent = HttpCallEvent.builder().url("https://svcin.prospero.com/api/ExportRequest")
                .method(HttpCallEvent.HttpMethod.POST)
                .payload(gson.toJson(jsonObject))
                .create();

        PipelineServer.getInstance().publish(httpCallEvent, new Callback<HttpCallEvent>() {
            @Override
            public void onCompleted(HttpCallEvent event) {

            }
        }, null);

    }
}
