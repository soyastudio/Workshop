package com.albertsons.esed.monitor.server;

import com.albertsons.esed.monitor.PipelineServerBoot;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class PipelineServer {

    private static final Logger logger = LoggerFactory.getLogger(PipelineServer.class);
    private static PipelineServer instance;

    private String serverName;
    private File home;
    private File conf;
    private File pipelineHome;

    private EventBus eventBus;

    protected PipelineServer() {
        try {
            init();
            instance = this;

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static PipelineServer getInstance() {
        return instance;
    }

    protected void init() throws URISyntaxException, IOException {
        this.serverName = name();
        this.eventBus = new EventBus(serverName);

        URL url = PipelineServerBoot.class.getProtectionDomain().getCodeSource().getLocation();
        if ("jar".equalsIgnoreCase(url.getProtocol())) {
            String path = url.getPath();
            int index = path.indexOf(".jar");
            path = path.substring(0, index) + ".jar";
            path = new URI(path).getPath();
            home = new File(path);
            if (home.exists()) {
                home = home.getParentFile().getParentFile();
            }

        } else {
            File userHome = new File(System.getProperty("user.home"));
            home = new File(userHome, "Application/" + serverName);
            if (!home.exists()) {
                home.mkdirs();
            }
        }

        System.setProperty("pipeline.server.home", home.getAbsolutePath());
        conf = new File(home, "conf");
        if (!conf.exists()) {
            conf.mkdirs();
        }
        System.setProperty("pipeline.server.conf.dir", conf.getAbsolutePath());

        File configFile = new File(conf, "server-config.properties");
        if (!configFile.exists()) {
            configFile.createNewFile();
        }

        pipelineHome = new File(home, "pipeline");
        if (!pipelineHome.exists()) {
            pipelineHome.mkdirs();
        }
        System.setProperty("pipeline.server.pipeline.dir", pipelineHome.getAbsolutePath());

        logger.info("Pipeline Server Home: {}", home.getAbsolutePath());
        logger.info("Pipeline conf dir: {}", conf.getAbsolutePath());
        logger.info("Pipeline pipeline dir: {}", pipelineHome.getAbsolutePath());

    }

    protected String name() {
        return "pipeline-server";
    }

    public String getServerName() {
        return serverName;
    }

    public File getHome() {
        return home;
    }

    public void publish(ServiceEvent event) {
        eventBus.post(event);
    }

    public <T extends ServiceEvent> void publish(T event, Callback<T> callback, ExceptionHandler exceptionHandler) {
        event.callback(callback, exceptionHandler);
        eventBus.post(event);
    }

    public abstract <T> T getService(Class<T> serviceType);

    protected void register(ServiceEventListener... listeners) {
        for (ServiceEventListener listener : listeners) {
            eventBus.register(listener);
        }
    }

}
