package com.albertsons.edis.springboot.service;

import com.albertsons.edis.ExceptionHandlingService;
import com.albertsons.edis.PipelineDeployer;
import com.albertsons.edis.PipelineProcessService;
import com.albertsons.edis.PipelineProcessor;

import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class PipelineProcessorManager implements PipelineProcessService {

    private PipelineDeployer deployer;
    private ExecutorService executorService;
    private ExceptionHandlingService exceptionHandler;

    private BlockingQueue<PipelineProcessor> queue = null;

    public PipelineProcessorManager(PipelineDeployer deployer, ExecutorService executorService, ExceptionHandlingService exceptionHandler) {
        this.deployer = deployer;
        this.executorService = executorService;
        this.exceptionHandler = exceptionHandler;
        this.queue = new ArrayBlockingQueue<>(1000);
    }

    public void process(String pipeline) {
        if (deployer.getPipelineProcessor(pipeline) == null) {
            throw new NoSuchElementException("Cannot find pipeline: " + pipeline);
        }

        PipelineProcessor processor = deployer.getPipelineProcessor(pipeline);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    processor.process();
                } catch (Exception exception) {
                    exceptionHandler.onException(exception);
                }
            }
        });
    }

    public void enqueue(String pipeline) {
        if (deployer.getPipelineProcessor(pipeline) == null) {
            throw new NoSuchElementException("Cannot find pipeline: " + pipeline);
        }

        PipelineProcessor processor = deployer.getPipelineProcessor(pipeline);
        try {
            queue.put(processor);

        } catch (InterruptedException e) {
            exceptionHandler.onException(e);

        }
    }

    public String[] list() {
        return deployer.pipelineNames();
    }

}
