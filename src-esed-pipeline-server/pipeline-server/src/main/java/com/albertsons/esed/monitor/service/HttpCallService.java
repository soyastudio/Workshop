package com.albertsons.esed.monitor.service;

import com.albertsons.esed.monitor.server.ServiceEventListener;
import com.google.common.eventbus.Subscribe;
import com.google.gson.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import okhttp3.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

//@Service
public class HttpCallService implements ServiceEventListener<HttpCallEvent> {

    private OkHttpClient httpClient;
    private Gson gson;

    @PostConstruct
    public void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .callTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);
        httpClient = builder.build();

        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Subscribe
    public void onEvent(HttpCallEvent event) {
        if (HttpCallEvent.HttpMethod.GET.equals(event.getHttpMethod())) {
            processGetEvent(event);

        } else if (HttpCallEvent.HttpMethod.POST.equals(event.getHttpMethod())) {
            processPostEvent(event);

        } else if (HttpCallEvent.HttpMethod.PUT.equals(event.getHttpMethod())) {
            processPutEvent(event);

        } else if (HttpCallEvent.HttpMethod.DELETE.equals(event.getHttpMethod())) {
            processDeleteEvent(event);

        }
    }

    private void processGetEvent(HttpCallEvent event) {
        Request request = new Request.Builder()
                .url(event.getUrl())
                .build();

        Call call = httpClient.newCall(request);
        JsonArray array = new JsonArray();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                event.close(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                postProcess(response, event);

            }
        });
    }

    private void processPostEvent(HttpCallEvent event) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(event.getPayload(), JSON);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(event.getUrl())
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @org.jetbrains.annotations.NotNull IOException e) {
                event.close(e);
                System.out.println("-------------------- " + (event.getEndTime() - event.getCreatedTime()));
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @org.jetbrains.annotations.NotNull Response response) throws IOException {
                postProcess(response, event);
            }
        });
    }

    private void processPutEvent(HttpCallEvent event) {

    }

    private void processDeleteEvent(HttpCallEvent event) {

    }

    private void postProcess(Response response, HttpCallEvent event) throws IOException {
        String json = response.body().string();
        if(event.getFilter() != null && event.getShifters().length == 0) {
            event.close(json);

        } else {
            JsonElement result;
            if (event.getFilter() != null) {
                result = filterByJsonPath(event.getFilter(), json);

            } else {
                result = JsonParser.parseString(json);
            }

            event.close(shift(result, event.getShifters()));
        }
    }

    private JsonArray filterByJsonPath(String jsonPath, String json) {
        Configuration JACKSON_JSON_NODE_CONFIGURATION = Configuration.builder().jsonProvider(new GsonJsonProvider())
                .options(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS).build();

        Configuration conf = Configuration.builder().jsonProvider(new GsonJsonProvider())
                .options(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS).build();

        try {
            return JsonPath.using(conf).parse(json).read(jsonPath);

        } catch (Exception e) {
            return new JsonArray();
        }
    }

    private String shift(JsonElement json, HttpCallEvent.Shifter[] shifters) {
        if(shifters.length == 0) {
            return gson.toJson(json);

        } else if(json.isJsonArray()) {
            JsonArray array = new JsonArray();
            json.getAsJsonArray().forEach(e -> {
                array.add(shift(e.getAsJsonObject(), shifters));

            });

            return gson.toJson(array);

        } else {
            return gson.toJson(shift(json.getAsJsonObject(), shifters));

        }
    }

    private JsonArray shift(JsonArray array, HttpCallEvent.Shifter[] shifters) {

        JsonArray result = new JsonArray();
        for (int i = 0; i < array.size(); i++) {
            JsonObject source = array.get(i).getAsJsonObject();
            JsonObject target = new JsonObject();

            for (HttpCallEvent.Shifter shifter : shifters) {
                String to = shifter.getTo();
                String from = shifter.getFrom();
                String exp = shifter.getExpression();

                JsonElement value = source.get(from);
                if (exp != null) {
                    value = Expressions.evaluate(value, exp);
                }

                target.add(to, value);
            }
            result.add(target);
        }

        return result;
    }

    private JsonObject shift(JsonObject src, HttpCallEvent.Shifter[] shifters) {
        JsonObject target = new JsonObject();

        for (HttpCallEvent.Shifter shifter : shifters) {
            String to = shifter.getTo();
            String from = shifter.getFrom();
            String exp = shifter.getExpression();

            JsonElement value = src.get(from);
            if (exp != null) {
                value = Expressions.evaluate(value, exp);
            }

            target.add(to, value);
        }

        return target;
    }
}
