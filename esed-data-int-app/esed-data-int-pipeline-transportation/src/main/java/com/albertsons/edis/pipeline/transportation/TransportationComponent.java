package com.albertsons.edis.pipeline.transportation;

import com.albertsons.edis.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TransportationComponent extends PipelineComponent<TransportationComponent.Processor> implements SchedulablePipeline {
    static Logger logger = LoggerFactory.getLogger(TransportationComponent.class);

    private String select;
    private String update;
    private String url;
    private int days;

    private String connectionFactoryJndiName;
    private String destinationJndiName;

    public static class Processor extends PipelineComponent.Processor {

        private String select;
        private String update;
        private String url;
        private int days;

        private String connectionFactoryJndiName;
        private String destinationJndiName;

        // services:
        private DataAccessService dataAccessService;
        private OkHttpClient httpClient;
        private JmsMessagePublishService jmsMessagePublishService;
        private Gson gson;

        @Override
        protected void init() {
            dataAccessService = getPipelineContext().getService(DataAccessService.class);

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .callTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS);
            httpClient = builder.build();

            jmsMessagePublishService = getPipelineContext().getService(JmsMessagePublishService.class);

            gson = new GsonBuilder().setPrettyPrinting().create();
        }

        @Override
        public void process() throws PipelineProcessException {
            logger.info("Processing...");
            // select;
            DataAccessService.SelectResult result = dataAccessService.search(dataAccessService.sqlBuilder(select));
            result.getData().forEach(e -> {
                if (e != null && e.isJsonObject()) {
                    scan(e.getAsJsonObject());
                }
            });
        }

        private String createPayload(JsonObject o) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("ExportCode", o.get("dc_route_param1").getAsString());
            jsonObject.addProperty("ProsperoCustomWebServiceGuid", o.get("dc_route_param2").getAsString());


            JsonArray params = new JsonArray();
            JsonObject param = new JsonObject();
            param.addProperty("Code", "LastPullDate");

            try {
                param.addProperty("Value", o.get("lst_upd_ts").getAsString().substring(0, 19));

            } catch (Exception e) {
                getInitialTimeStamp(days);

            } finally {
                params.add(param);
                jsonObject.add("Parameters", params);

            }

            return gson.toJson(jsonObject);
        }

        private String getInitialTimeStamp(int days) {
            String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

            // Get current date
            Date currentDate = new Date();
            System.out.println("date : " + dateFormat.format(currentDate));

            // convert date to localdatetime
            LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            // minus number of days
            localDateTime = localDateTime.minusDays(days);

            // convert LocalDateTime to date
            Date intialLoadDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

            return dateFormat.format(intialLoadDate);
        }

        private void scan(JsonObject o) {
            String payload = this.createPayload(o);
            if (payload == null) {
                return;
            }

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(payload, JSON);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String message = response.body().string();

                    try {
                        jmsMessagePublishService.publish(connectionFactoryJndiName, destinationJndiName, message);
                        dataAccessService.update(dataAccessService.sqlBuilder(update)
                                .set(1, o.getAsJsonObject().get("dc_route_param1").getAsString(), String.class)
                                .set(2, o.getAsJsonObject().get("dc_route_param2").getAsString(), String.class));

                        logger.info("lst_upd_ts column is updated!");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        }
    }

}
