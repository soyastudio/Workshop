package com.albertsons.edis.kafka.console.kafka;

import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class ClusterModel {
    private final String clusterId;
    private final Node controller;
    private final Collection<Node> nodes;

    private ClusterModel(String clusterId, Node controller, Collection<Node> nodes) {
        this.clusterId = clusterId;
        this.controller = controller;
        this.nodes = nodes;
    }

    public static ClusterModel fromDescribeClusterResult(DescribeClusterResult cluster) {
        KafkaFuture<String> id = cluster.clusterId();
        KafkaFuture<Node> controller = cluster.controller();
        KafkaFuture<Collection<Node>> nodes = cluster.nodes();
        while (!(id.isDone() && controller.isDone() && nodes.isDone())) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            return new ClusterModel(id.get(), controller.get(), nodes.get());

        } catch (InterruptedException | ExecutionException e) {
            throw new KafkaAdminException(e);
        }
    }
}
