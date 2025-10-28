package assignment3;

import java.io.*;
import java.util.*;

public class BenchmarkRunner {

    public static void runBenchmark(List<Graph.GraphData> graphs, String csvFileName) {
        StringBuilder csvOutput = new StringBuilder("Graph ID,Size Group,Vertices,Edges,Prim Cost,Kruskal Cost,Prim Time (ms),Kruskal Time (ms),Prim Ops,Kruskal Ops\n");

        for (Graph.GraphData graphData : graphs) {
            Graph graph = new Graph(graphData.getVertices(), graphData.getEdges());

            MSTAlgorithms.MSTResult primResult = MSTAlgorithms.primMST(graph);
            MSTAlgorithms.MSTResult kruskalResult = MSTAlgorithms.kruskalMST(graph);

            String sizeGroup = graphData.getSize_group() != null ? graphData.getSize_group() : "unknown";
            csvOutput.append(String.format("%d,%s,%d,%d,%d,%d,%.2f,%.2f,%d,%d\n",
                    graphData.getId(), sizeGroup, graph.getNumVertices(), graph.getNumEdges(),
                    primResult.totalCost, kruskalResult.totalCost,
                    primResult.executionTimeMs, kruskalResult.executionTimeMs,
                    primResult.operationsCount, kruskalResult.operationsCount
            ));
        }

        try (FileWriter writer = new FileWriter(csvFileName)) {
            writer.write(csvOutput.toString());
            System.out.println("✅ Benchmark results saved to: " + csvFileName);
        } catch (IOException e) {
            System.err.println("❌ Error saving CSV: " + e.getMessage());
        }
    }

    public static void generateDOTFiles(List<Graph.GraphData> graphs, int limit) {
        for (Graph.GraphData graphData : graphs) {
            if (graphData.getId() > limit) break;

            Graph graph = new Graph(graphData.getVertices(), graphData.getEdges());
            MSTAlgorithms.MSTResult primResult = MSTAlgorithms.primMST(graph);
            MSTAlgorithms.MSTResult kruskalResult = MSTAlgorithms.kruskalMST(graph);

            String baseName = "graph_" + graphData.getId();
            graph.saveAsDotFile(primResult.mstEdges, baseName + "_prim.dot");
            graph.saveAsDotFile(kruskalResult.mstEdges, baseName + "_kruskal.dot");

            System.out.println("Graph " + graphData.getId() + ": DOT files are created");
        }
    }
}