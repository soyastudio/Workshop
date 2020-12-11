package com.albertsons.esed.monitor.service;

import com.albertsons.esed.monitor.server.Pipeline;
import com.google.common.io.CharStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
public class ApiInvocationService {
    static Logger logger = LoggerFactory.getLogger(ApiInvocationService.class);

    @Autowired
    private ExecutorService executorService;

    @Autowired(required = false)
    private SSLSocketFactory sslSocketFactory;

    public JsonElement fullScan(Pipeline pipeline, String filter) throws IOException {
        String type = pipeline.getApi().getType();
        String url = pipeline.getApi().getUrl();

        if (type.startsWith("paginate(") && type.endsWith(")")) {

            JsonArray result = new JsonArray();
            int start = type.indexOf("(");
            int end = type.lastIndexOf(")");
            String[] args = type.substring(start + 1, end).split(",");
            String param = args[0];

            int step = 1;
            int count = 50;

            if (args.length > 1) {
                step = Integer.parseInt(args[1].trim());
            }

            if (args.length > 2) {
                count = Integer.parseInt(args[2].trim());
            }

            List<Future<JsonArray>> futures = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                int page = pipeline.state().next();

                String u = url.replace("{{" + param + "}}", "" + (page * step));

                Future<JsonArray> future = executorService.submit(new Callable<JsonArray>() {
                    @Override
                    public JsonArray call() throws Exception {
                        return invoke(u, filter, null).getAsJsonArray();
                    }
                });
                futures.add(future);
            }

            boolean boo = false;
            while (!boo) {
                try {
                    Thread.sleep(100l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                boo = true;
                for (Future f : futures) {
                    if (!f.isDone()) {
                        boo = false;
                        break;
                    }
                }
            }

            boolean newRound = false;
            for (Future<JsonArray> e : futures) {
                try {
                    JsonArray array = e.get();
                    if (array.size() > 0) {
                        array.forEach(o -> {
                            result.add(o);
                        });
                    } else {
                        newRound = true;
                    }

                } catch (InterruptedException ex) {
                    ex.printStackTrace();

                } catch (ExecutionException ex) {
                    ex.printStackTrace();

                }
            }

            if (newRound) {
                pipeline.state().nextRound();
            }

            return result;

        }

        return null;
    }

    public JsonElement invoke(Pipeline pipeline) throws IOException {
        long startTimestamp = System.currentTimeMillis();

        String type = pipeline.getApi().getType();
        String url = pipeline.getApi().getUrl();
        String filter = pipeline.getApi().getFilter();
        Pipeline.Shifter[] shifters = pipeline.getApi().getShifters();

        if (type == null || type.equalsIgnoreCase("single")) {
            return invoke(url, filter, shifters);

        } else if (type.startsWith("paginate(") && type.endsWith(")")) {

            JsonArray result = new JsonArray();
            int start = type.indexOf("(");
            int end = type.lastIndexOf(")");
            String[] args = type.substring(start + 1, end).split(",");
            String param = args[0];

            int step = 1;
            int count = 50;

            if (args.length > 1) {
                step = Integer.parseInt(args[1].trim());
            }

            if (args.length > 2) {
                count = Integer.parseInt(args[2].trim());
            }

            List<Future<JsonArray>> futures = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                int page = pipeline.state().next();

                String u = url.replace("{{" + param + "}}", "" + (page * step));

                Future<JsonArray> future = executorService.submit(new Callable<JsonArray>() {
                    @Override
                    public JsonArray call() throws Exception {
                        return invoke(u, filter, shifters).getAsJsonArray();
                    }
                });
                futures.add(future);
            }

            boolean boo = false;
            while (!boo) {
                try {
                    Thread.sleep(100l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                boo = true;
                for (Future f : futures) {
                    if (!f.isDone()) {
                        boo = false;
                        break;
                    }
                }
            }

            boolean newRound = false;
            for (Future<JsonArray> e : futures) {
                try {
                    JsonArray array = e.get();
                    if (array.size() > 0) {
                        array.forEach(o -> {
                            result.add(o);
                        });
                    } else {
                        newRound = true;
                    }

                } catch (InterruptedException ex) {
                    ex.printStackTrace();

                } catch (ExecutionException ex) {
                    ex.printStackTrace();

                }
            }

            if (newRound) {
                pipeline.state().nextRound();
            }

            logger.info("result: " + result.size());
            logger.info("Time used: {}", (System.currentTimeMillis() - startTimestamp));

            return result;

        } else if (type.startsWith("aggregate(") && type.endsWith(")")) {

            JsonArray result = new JsonArray();

            String[] args = type.substring(type.indexOf("(") + 1, type.lastIndexOf(")")).split(",");
            String param = args[0];
            int page = 0;
            if (args.length > 1) {
                page = Integer.parseInt(args[1]);
            }

            String u = url.replace("{{" + param + "}}", "" + page);
            JsonArray array = invoke(u, filter, shifters).getAsJsonArray();
            while (array != null && array.size() > 0) {
                array.forEach(e -> {
                    result.add(e);
                });

                page++;
                u = url.replace("{{" + param + "}}", "" + page);
                array = invoke(u, filter, shifters).getAsJsonArray();
            }

            return result;
        }

        return null;
    }

    public JsonElement invoke(String url, String filter, Pipeline.Shifter[] shifters) throws IOException {
        String json = invoke(url);
        if (filter != null) {
            JsonArray jsonArray = PipelineUtils.filterByJsonPath(filter, json);
            if (shifters != null && shifters.length > 0) {
                jsonArray = PipelineUtils.shift(jsonArray, shifters);
            }

            return jsonArray;

        } else {
            return JsonParser.parseString(json);
        }
    }

    public String invoke(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        if(con instanceof HttpsURLConnection && sslSocketFactory != null) {
            HttpsURLConnection sc = (HttpsURLConnection) con;
            sc.setSSLSocketFactory(sslSocketFactory);
        }


        con.setRequestMethod("GET");
        con.setDoOutput(true);

        // Read response:
        int status = con.getResponseCode();
        Reader streamReader = null;
        if (status > 299 && con.getErrorStream() != null) {
            streamReader = new InputStreamReader(con.getErrorStream());
        } else {
            streamReader = new InputStreamReader(con.getInputStream());
        }

        String out = CharStreams.toString(streamReader);
        streamReader.close();

        return out;
    }

    public String invokeHttps(String httpsURL) throws IOException {
        URL url = new URL(httpsURL);
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(true);

        // Read response:
        int status = con.getResponseCode();
        Reader streamReader = null;
        if (status > 299) {
            streamReader = new InputStreamReader(con.getErrorStream());
        } else {
            streamReader = new InputStreamReader(con.getInputStream());
        }

        String out = CharStreams.toString(streamReader);
        streamReader.close();

        return out;
    }

    public String invoke(ApiInvocation curl) throws IOException {
        URL url = new URL(curl.getUrl());

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setDoOutput(true);

        // Read response:
        int status = con.getResponseCode();
        Reader streamReader = null;
        if (status > 299) {
            streamReader = new InputStreamReader(con.getErrorStream());
        } else {
            streamReader = new InputStreamReader(con.getInputStream());
        }

        String out = CharStreams.toString(streamReader);
        streamReader.close();

        return out;
    }
}
