package com.fenglex.janus;

import cn.hutool.core.io.resource.ClassPathResource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.example.GraphOfTheGodsFactory;

import java.io.File;

/**
 * @Author: haifeng
 * @Date: 2019-08-30 10:02
 */

public class LoadData {
    public static void main(String[] args) {
        ClassPathResource classPathResource = new ClassPathResource("janusgraph.properties");
        File file = classPathResource.getFile();
        String path = file.getPath();
        JanusGraph graph = JanusGraphFactory.open(path);
        GraphTraversalSource g = graph.traversal();
        g.V().drop().iterate();
        g.tx().commit();
        graph.tx().commit();
        GraphOfTheGodsFactory.load(graph);
        graph.close();
    }
}
