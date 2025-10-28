package assignment3;

import java.util.*;
import java.io.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class MainRunner {

    public static void main(String[] args) {
        String inputFileName = "assign_3_input.json";
        String outputFileName = "output.json";
        String csvFileName = "results_summary.csv";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            Type listType = new TypeToken<List<Graph.GraphData>>(){}.getType();
            List<Graph.GraphData> inputGraphsArray = gson.fromJson(new FileReader(inputFileName), listType);

            JsonArray outputResults = new JsonArray();

            for (Graph.GraphData graphData : inputGraphsArray) {
                Graph graph = new Graph(graphData.getVertices(), graphData.getEdges());

                MSTAlgorithms.MSTResult primResult = MSTAlgorithms.primMST(graph);
                MSTAlgorithms.MSTResult kruskalResult = MSTAlgorithms.kruskalMST(graph);

                // формируем джсон
                JsonObject resultJson = new JsonObject();
                resultJson.addProperty("graph_id", graphData.getId());

                JsonObject inputStats = new JsonObject();
                inputStats.addProperty("vertices", graph.getNumVertices());
                inputStats.addProperty("edges", graph.getNumEdges());
                resultJson.add("input_stats", inputStats);

                JsonArray primEdges = new JsonArray();
                for (Edge edge : primResult.mstEdges) {
                    JsonObject edgeJson = new JsonObject();
                    edgeJson.addProperty("from", edge.getFrom());
                    edgeJson.addProperty("to", edge.getTo());
                    edgeJson.addProperty("weight", edge.getWeight());
                    primEdges.add(edgeJson);
                }

                JsonObject primJson = new JsonObject();
                primJson.add("mst_edges", primEdges);
                primJson.addProperty("total_cost", primResult.totalCost);
                primJson.addProperty("operations_count", primResult.operationsCount);
                primJson.addProperty("execution_time_ms", String.format("%.2f", primResult.executionTimeMs));
                resultJson.add("prim", primJson);

                JsonArray kruskalEdges = new JsonArray();
                for (Edge edge : kruskalResult.mstEdges) {
                    JsonObject edgeJson = new JsonObject();
                    edgeJson.addProperty("from", edge.getFrom());
                    edgeJson.addProperty("to", edge.getTo());
                    edgeJson.addProperty("weight", edge.getWeight());
                    kruskalEdges.add(edgeJson);
                }

                JsonObject kruskalJson = new JsonObject();
                kruskalJson.add("mst_edges", kruskalEdges);
                kruskalJson.addProperty("total_cost", kruskalResult.totalCost);
                kruskalJson.addProperty("operations_count", kruskalResult.operationsCount);
                kruskalJson.addProperty("execution_time_ms", String.format("%.2f", kruskalResult.executionTimeMs));
                resultJson.add("kruskal", kruskalJson);

                outputResults.add(resultJson);
            }

            JsonObject finalOutput = new JsonObject();
            finalOutput.add("results", outputResults);

            try (FileWriter writer = new FileWriter(outputFileName)) {
                writer.write(gson.toJson(finalOutput));
            }

            // Запускаем бенчмарк
            BenchmarkRunner.runBenchmark(inputGraphsArray, csvFileName);

            // Генерируем DOT файлы
            BenchmarkRunner.generateDOTFiles(inputGraphsArray, 5);

            System.out.println("all tasks completed!");
            System.out.println("output.json - full results");
            System.out.println("results_summary.csv - benchmark summary");
            System.out.println("graph_*.dot - visualization files");

        } catch (IOException e) {
            System.err.println("❌ I/O Error: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            System.err.println("❌ JSON Syntax Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
        }
    }
}