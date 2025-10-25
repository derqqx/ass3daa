package assignment3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GraphGenerator {

    static class Edge {
        String from;
        String to;
        int weight;

        Edge(String from, String to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    static class Graph {
        int id;
        String size_group;
        List<String> vertices;
        List<Edge> edges;

        Graph(int id, String size_group, List<String> vertices, List<Edge> edges) {
            this.id = id;
            this.size_group = size_group;
            this.vertices = vertices;
            this.edges = edges;
        }
    }

    public static void main(String[] args) {
        List<Graph> graphs = new ArrayList<>();

        // Группы графов с size_group
        generateGraphs(graphs, 1, 5, 5, 29, "small");       // 5 Small
        generateGraphs(graphs, 6, 15, 30, 299, "medium");   // 10 Medium
        generateGraphs(graphs, 16, 25, 300, 999, "large");  // 10 Large
        generateGraphs(graphs, 26, 30, 1000, 2999, "extra_large"); // 5 Extra Large

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        try (FileWriter writer = new FileWriter("assign_3_input.json")) {
            gson.toJson(graphs, writer); // ПРЯМОЙ МАССИВ, без GraphSet
            System.out.println("The 'assign_3_input' file.json' has been created successfully!");
            System.out.println("Statistics:");
            System.out.println("   - Small graphs: 5 (<30 vertexes)");
            System.out.println("   - Medium graphs: 10 (<300 vertexes)");
            System.out.println("   - Large graphs: 10 (<1000 vertexes)");
            System.out.println("   - Extra graphs: 5 (<3000 vertexes)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateGraphs(List<Graph> graphs, int startId, int endId, int minNodes, int maxNodes, String sizeGroup) {
        Random rand = new Random();

        for (int id = startId; id <= endId; id++) {
            int numNodes = rand.nextInt(maxNodes - minNodes + 1) + minNodes;
            List<String> vertices = new ArrayList<>();
            for (int i = 1; i <= numNodes; i++) {
                vertices.add("N" + i);
            }

            List<Edge> edges = new ArrayList<>();

            for (int i = 2; i <= numNodes; i++) {
                String from = "N" + i;
                String to = "N" + (rand.nextInt(i - 1) + 1);
                int weight = rand.nextInt(100) + 1;
                edges.add(new Edge(from, to, weight));
            }

            // доп рандом еджес
            int extraEdges = rand.nextInt(numNodes * 2) + numNodes / 2;
            for (int i = 0; i < extraEdges; i++) {
                String from = "N" + (rand.nextInt(numNodes) + 1);
                String to = "N" + (rand.nextInt(numNodes) + 1);
                if (!from.equals(to) && !edgeExists(edges, from, to)) {
                    edges.add(new Edge(from, to, rand.nextInt(100) + 1));
                }
            }

            graphs.add(new Graph(id, sizeGroup, vertices, edges));
        }
    }

    private static boolean edgeExists(List<Edge> edges, String a, String b) {
        for (Edge e : edges) {
            if ((e.from.equals(a) && e.to.equals(b)) || (e.from.equals(b) && e.to.equals(a))) {
                return true;
            }
        }
        return false;
    }
}