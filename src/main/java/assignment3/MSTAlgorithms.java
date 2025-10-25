package assignment3;

import java.util.*;
import java.io.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class MSTAlgorithms {

    public static class MSTResult {
        public List<Edge> mstEdges = new ArrayList<>();
        public long totalCost = 0;
        public long operationsCount = 0;
        public double executionTimeMs = 0;

        public List<Edge> getMstEdges() { return mstEdges; }
        public long getTotalCost() { return totalCost; }
        public long getOperationsCount() { return operationsCount; }
        public double getExecutionTimeMs() { return executionTimeMs; }
    }

    // I. прим
    public static MSTResult primMST(Graph graph) {
        MSTResult result = new MSTResult();
        long startTime = System.nanoTime();

        if (graph.getNumVertices() == 0) return result;

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(Edge::getWeight));
        Set<String> inMST = new HashSet<>();
        Map<String, Edge> minEdge = new HashMap<>();

        String startVertex = graph.getVertices().iterator().next();
        inMST.add(startVertex);

        for (Edge edge : graph.getAdjacencies().get(startVertex)) {
            pq.offer(edge);
            result.operationsCount++;
        }

        while (!pq.isEmpty() && inMST.size() < graph.getNumVertices()) {
            Edge edge = pq.poll();
            result.operationsCount++;

            String u = edge.getFrom();
            String v = edge.getTo();

            if (inMST.contains(u) && inMST.contains(v)) {
                continue;
            }

            String newVertex = inMST.contains(u) ? v : u;
            if (!inMST.contains(newVertex)) {
                inMST.add(newVertex);
                result.mstEdges.add(edge);
                result.totalCost += edge.getWeight();

                for (Edge nextEdge : graph.getAdjacencies().get(newVertex)) {
                    String neighbor = nextEdge.getFrom().equals(newVertex) ? nextEdge.getTo() : nextEdge.getFrom();
                    if (!inMST.contains(neighbor)) {
                        pq.offer(nextEdge);
                        result.operationsCount++;
                    }
                }
            }
        }

        long endTime = System.nanoTime();
        result.executionTimeMs = (endTime - startTime) / 1_000_000.0;

        if (result.mstEdges.size() != graph.getNumVertices() - 1) {
            result.totalCost = -1;
            result.mstEdges.clear();
        }

        return result;
    }

    // II. крускал
    public static MSTResult kruskalMST(Graph graph) {
        MSTResult result = new MSTResult();
        long startTime = System.nanoTime();

        if (graph.getNumVertices() <= 1) return result;

        List<Edge> sortedEdges = new ArrayList<>(graph.getEdges());
        Collections.sort(sortedEdges);
        result.operationsCount += (long) (sortedEdges.size() * Math.log(sortedEdges.size()));

        DisjointSet ds = new DisjointSet(graph.getVertices());

        for (Edge edge : sortedEdges) {
            if (ds.union(edge.getFrom(), edge.getTo())) {
                result.mstEdges.add(edge);
                result.totalCost += edge.getWeight();
            }
            result.operationsCount += ds.operationsCount;
            ds.operationsCount = 0;

            if (result.mstEdges.size() == graph.getNumVertices() - 1) {
                break;
            }
        }

        long endTime = System.nanoTime();
        result.executionTimeMs = (endTime - startTime) / 1_000_000.0;

        if (result.mstEdges.size() != graph.getNumVertices() - 1) {
            result.totalCost = -1;
            result.mstEdges.clear();
        }

        return result;
    }

    // III. Mэйн - Запуск
    public static void main(String[] args) {
        String inputFileName = "assign_3_input.json";
        String outputFileName = "output.json";
        String csvFileName = "results_summary.csv";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            Type listType = new TypeToken<List<Graph.GraphData>>(){}.getType();
            List<Graph.GraphData> inputGraphsArray = gson.fromJson(new FileReader(inputFileName), listType);

            JsonArray outputResults = new JsonArray();
            StringBuilder csvOutput = new StringBuilder("Graph ID,Size Group,Vertices,Edges,Prim Cost,Kruskal Cost,Prim Time (ms),Kruskal Time (ms),Prim Ops,Kruskal Ops\n");

            for (Graph.GraphData graphData : inputGraphsArray) {
                Graph graph = new Graph(graphData.getVertices(), graphData.getEdges());

                MSTResult primResult = primMST(graph);
                MSTResult kruskalResult = kruskalMST(graph);

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

                if (graphData.getId() <= 5) {
                    String baseName = "graph_" + graphData.getId();

                    // дот файл для прим
                    graph.saveAsDotFile(primResult.mstEdges, baseName + "_prim.dot");

                    // дот файл для крускал
                    graph.saveAsDotFile(kruskalResult.mstEdges, baseName + "_kruskal.dot");

                    System.out.println("📊 Граф " + graphData.getId() + ": DOT файлы созданы");
                }

                // Запись в цсв
                String sizeGroup = graphData.getSize_group() != null ? graphData.getSize_group() : "unknown";
                csvOutput.append(String.format("%d,%s,%d,%d,%d,%d,%.2f,%.2f,%d,%d\n",
                        graphData.getId(), sizeGroup, graph.getNumVertices(), graph.getNumEdges(),
                        primResult.totalCost, kruskalResult.totalCost,
                        primResult.executionTimeMs, kruskalResult.executionTimeMs,
                        primResult.operationsCount, kruskalResult.operationsCount
                ));
            }

            JsonObject finalOutput = new JsonObject();
            finalOutput.add("results", outputResults);

            try (FileWriter writer = new FileWriter(outputFileName)) {
                writer.write(gson.toJson(finalOutput));
            }

            try (FileWriter writer = new FileWriter(csvFileName)) {
                writer.write(csvOutput.toString());
            }

            System.out.println("MST computation complete. Results saved to " + outputFileName + " and " + csvFileName);
            System.out.println("DOT files created for first 5 graphs (can be converted to PNG using Graphviz)");

        } catch (IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            System.err.println("JSON Syntax Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}