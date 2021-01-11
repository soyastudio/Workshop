package com.albertsons.edis.springboot.service;

import com.albertsons.edis.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class URLPipelineDeployer implements PipelineDeployer {
    private static Logger logger = LoggerFactory.getLogger(URLPipelineDeployer.class);
    private static int minCapacity = 100;

    private File home;
    private PipelineContext context;
    private PipelineScheduler scheduler;

    private int capacity;
    private BlockingQueue<Deployment> queue = null;

    private Map<String, Deployment> deployments = new ConcurrentHashMap<>();
    private Map<String, Pipeline> pipelines = new ConcurrentHashMap<>();
    private Map<String, PipelineProcessor> processors = new ConcurrentHashMap<>();

    public URLPipelineDeployer(File home, int capacity, PipelineContext context) {
        this.home = home;
        this.context = context;
        try {
            this.scheduler = context.getService(PipelineScheduler.class);

        } catch (Exception e) {
            logger.warn("PipelineScheduler is not defined.");
        }

        this.capacity = Math.max(capacity, minCapacity);
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    @PostConstruct
    public void init() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Scanning for deployment...");
                String[] paths = home.list();
                for (String path : paths) {
                    if (deployable(path)) {
                        Deployment deployment = new Deployment(home, path);
                        if (deployments.containsKey(path)) {
                            if (deployment.status.equals(DeploymentStatus.UNDEPLOY)) {
                                if (!queue.contains(deployment) && queue.size() < capacity) {
                                    try {
                                        queue.put(deployment);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            if (DeploymentStatus.DEPLOY.equals(deployment.status)) {
                                if (!queue.contains(deployment) && queue.size() < capacity) {
                                    try {
                                        queue.put(deployment);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, 15000, 15000);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                while (!queue.isEmpty()) {
                    Deployment deployment = queue.poll();
                    new Runnable() {
                        @Override
                        public void run() {
                            process(deployment);
                        }
                    }.run();
                }
            }
        }, 20000, 1000);
    }

    @Override
    public String[] pipelineNames() {
        List<String> list = new ArrayList<>(pipelines.keySet());
        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }

    @Override
    public Pipeline getPipeline(String name) {
        return pipelines.get(name);
    }

    @Override
    public PipelineProcessor getPipelineProcessor(String name) {
        return processors.get(name);
    }

    private synchronized void process(Deployment deployment) {
        if (deployment.status.equals(DeploymentStatus.DEPLOY)) {
            deploy(deployment);

        } else if (deployment.status.equals(DeploymentStatus.UNDEPLOY)) {
            Deployment todo = deployments.get(deployment.path);
            undeploy(todo);
        }

    }

    private synchronized void deploy(Deployment deployment) {
        List<Pipeline> list = deployment.deploy();
        list.forEach(e -> {
            if (pipelines.containsKey(e.getName())) {
                throw new RuntimeException("Pipeline '" + e.getName() + "' already exist");
            }

            pipelines.put(e.getName(), e);

            PipelineProcessor processor = e.getPattern().build();
            if (processor instanceof PipelineComponent.Processor) {
                PipelineComponent.Processor support = (PipelineComponent.Processor) processor;
                try {
                    Method method = PipelineComponent.Processor.class.getDeclaredMethod("init", PipelineContext.class);
                    method.setAccessible(true);
                    method.invoke(support, context);

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            // schedule
            if (scheduler != null && e.getPattern() instanceof SchedulablePipeline) {
                if (e.getMetadata().getCalendar() != null) {
                    scheduler.schedule(processor, e.getName(), e.getMetadata().getCalendar(), e.getMetadata().getDelay());
                }
            }

            processors.put(e.getName(), processor);
        });

        deployments.put(deployment.path, deployment);
    }

    private synchronized void undeploy(Deployment deployment) {
        try {
            deployment.undeploy();
            deployment.pipelines.forEach(e -> {
                if (processors.containsKey(e.getName())) {
                    PipelineProcessor processor = processors.get(e.getName());
                    PipelineComponent.Processor support = (PipelineComponent.Processor) processor;

                    // unschedule
                    if (scheduler != null && e.getPattern() instanceof SchedulablePipeline) {
                        if (e.getMetadata().getCalendar() != null) {
                            scheduler.unschedule(e.getName());
                            try {
                                Thread.sleep(500l);

                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }

                    }

                    try {
                        Method method = PipelineComponent.Processor.class.getDeclaredMethod("destroy", new Class[0]);
                        method.setAccessible(true);
                        method.invoke(support, new Object[0]);

                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    // schedule
                    if (scheduler != null && e.getPattern() instanceof SchedulablePipeline) {
                        if (e.getMetadata().getCalendar() != null) {
                            scheduler.unschedule(e.getName());
                        }

                    }
                }
                pipelines.remove(e.getName());
            });

            deployments.remove(deployment.path);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean deployable(String path) {
        File dir = new File(home, path);
        if (dir.exists() && dir.isDirectory()) {
            File f = new File(dir, "pipeline.json");
            if (f.exists() && f.isFile()) {
                return true;
            }
        }

        return false;
    }

    static enum DeploymentStatus {
        DEPLOY, DEPLOYED, UNDEPLOY, UNDEPLOYED
    }

    static class Deployment {
        private String path;
        private File dir;
        private PipelineClassLoader classLoader;
        private List<Pipeline> pipelines = new ArrayList<>();

        private File configFile;
        private DeploymentStatus status;

        Deployment(File home, String path) {
            this.path = path;
            this.dir = new File(home, path);
            this.configFile = new File(dir, "pipeline.json");

            if (new File(dir, ".undeployed").exists()) {
                this.status = DeploymentStatus.UNDEPLOYED;

            } else if (new File(dir, ".undeploy").exists()) {
                this.status = DeploymentStatus.UNDEPLOY;

            } else {
                this.status = DeploymentStatus.DEPLOY;
            }
        }

        private List<Pipeline> deploy() {
            this.classLoader = new PipelineClassLoader(Thread.currentThread().getContextClassLoader(), dir);
            this.pipelines = new ArrayList<>();

            try {
                InputStream inputStream = new FileInputStream(configFile);
                JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(inputStream));

                if (jsonElement.isJsonArray()) {
                    jsonElement.getAsJsonArray().forEach(e -> {
                        try {
                            pipelines.add(create(e.getAsJsonObject(), classLoader, dir));

                        } catch (ClassNotFoundException classNotFoundException) {
                            throw new RuntimeException(classNotFoundException);
                        }
                    });
                } else if (jsonElement.isJsonObject()) {
                    try {
                        pipelines.add(create(jsonElement.getAsJsonObject(), classLoader, dir));

                    } catch (ClassNotFoundException classNotFoundException) {
                        throw new RuntimeException(classNotFoundException);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mark(DeploymentStatus.DEPLOYED);

            return pipelines;
        }

        private Pipeline create(JsonObject jsonObject, PipelineClassLoader classLoader, File dir) throws ClassNotFoundException {
            Gson gson = new Gson();
            Pipeline.Bod metadata = gson.fromJson(jsonObject.get("bod"), Pipeline.Bod.class);
            String pattern = metadata.getPattern();
            Class<?> clazz = classLoader.loadClass(pattern);
            PipelinePattern ptn = (PipelinePattern) gson.fromJson(jsonObject, clazz);
            if (ptn instanceof PipelineComponent) {
                PipelineComponent component = (PipelineComponent) ptn;

                try {
                    Field locationField = PipelineComponent.class.getDeclaredField("location");
                    locationField.setAccessible(true);
                    locationField.set(component, dir);

                    Field pipelineField = PipelineComponent.class.getDeclaredField("pipeline");
                    pipelineField.setAccessible(true);
                    pipelineField.set(component, metadata.getName());

                    Field timeoutField = PipelineComponent.class.getDeclaredField("timeout");
                    timeoutField.setAccessible(true);
                    timeoutField.set(component, metadata.getTimeout());

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }

            return Pipeline.newInstance(metadata, ptn);
        }

        private void undeploy() throws IOException {
            classLoader.close();
            classLoader = null;
            mark(DeploymentStatus.UNDEPLOYED);
        }

        private void mark(DeploymentStatus status) {
            this.status = status;

            if (!status.equals(DeploymentStatus.DEPLOY) && !status.equals(DeploymentStatus.DEPLOYED)) {
                File descriptor = new File(dir, "." + status.name().toLowerCase());
                if (!descriptor.exists()) {
                    try {
                        descriptor.createNewFile();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            for (DeploymentStatus s : DeploymentStatus.values()) {
                if (!status.equals(s)) {
                    File file = new File(dir, "." + s.name().toLowerCase());
                    if (file.exists()) {
                        try {
                            FileUtils.forceDelete(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Deployment that = (Deployment) o;

            return dir != null ? dir.equals(that.dir) : that.dir == null;
        }

        @Override
        public int hashCode() {
            return dir != null ? dir.hashCode() : 0;
        }
    }

    static class PipelineClassLoader extends ClassLoader {
        private final File root;
        private final URLClassLoader urlClassLoader;

        public PipelineClassLoader(ClassLoader parent, File root) {
            super(parent);
            this.root = root;
            List<URL> urls = new ArrayList<>();
            File[] files = root.listFiles();
            for (File f : files) {
                if (f.isFile() && f.getName().endsWith(".jar")) {
                    try {
                        urls.add(f.toURI().toURL());

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }

            File lib = new File(root, "lib");
            if (lib.exists()) {
                files = lib.listFiles();
                for (File f : files) {
                    if (f.isFile() && f.getName().endsWith(".jar")) {
                        try {
                            urls.add(f.toURI().toURL());

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            this.urlClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), this);
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            return urlClassLoader.loadClass(name);
        }

        @Nullable
        @Override
        public InputStream getResourceAsStream(String name) {
            try {
                return new FileInputStream(new File(root, name));

            } catch (FileNotFoundException e) {
                return null;
            }
        }

        public void close() throws IOException {
            urlClassLoader.close();
        }

        public URL[] getURLs() {
            return urlClassLoader.getURLs();
        }

        @Override
        public URL findResource(String name) {
            return urlClassLoader.findResource(name);
        }

        @Override
        public Enumeration<URL> findResources(String name) throws IOException {
            return urlClassLoader.findResources(name);
        }
    }

}
