package com.albertsons.esed.monitor.server;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

//@Service
public class PipelineDeploymentService implements ServiceEventListener<PipelineDeploymentEvent> {
    static Logger logger = LoggerFactory.getLogger(PipelineDeploymentService.class);

    private File pipelineHome;

    private Map<File, PipelineDeployment> deployments = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        pipelineHome = new File(System.getProperty("pipeline.server.pipeline.dir"));
        if (!pipelineHome.exists()) {
            pipelineHome.mkdirs();
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                File[] dirs = pipelineHome.listFiles();
                for(File dir: dirs) {
                    if(deployable(dir)) {
                        File undeploy = new File(dir, ".undeploy");
                        File redeploy = new File(dir, ".redeploy");
                        File pause = new File(dir, ".pause");

                        if(undeploy.exists()) {
                            PipelineServer.getInstance().publish(PipelineDeploymentEvent.createUndeployEvent(dir));

                        } else if(redeploy.exists()) {
                            PipelineServer.getInstance().publish(PipelineDeploymentEvent.createUndeployEvent(dir));

                        } else {
                            PipelineServer.getInstance().publish(PipelineDeploymentEvent.createVerifyEvent(dir));
                        }
                    }
                }

            }
        }, 10000, 10000);
    }

    @Subscribe
    public void onEvent(PipelineDeploymentEvent event) {
        File dir = event.getFile();
        if(event instanceof PipelineDeploymentEvent.VerifyEvent) {
            if(!deployments.containsKey(dir)) {

                PipelineDeployment deployment = new PipelineDeployment(event.getFile());
                deployments.put(dir, deployment);

                File file = new File(dir, "pipeline.json");
                PipelineServer.getInstance().publish(PipelineEvent.createPipelineCreationEvent(file));

            } else {
                PipelineDeployment deployment = deployments.get(dir);

            }
        }
    }

    private boolean deployable(File dir) {
        if(dir.isDirectory()) {
            File ppl = new File(dir, "pipeline.json");
            return ppl.exists();
        }
        return false;
    }
}
