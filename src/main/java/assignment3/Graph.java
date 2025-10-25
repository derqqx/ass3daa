package assignment3;

import java.util.*;
import java.io.*;

public class Graph {
    private final Set<String> vertices;
    private final List<Edge> edges;
    private final Map<String, List<Edge>> adjacencies;

    public static class GraphData {
        private int id;
        private String size_group;
        private List<String> vertices;
        private List<Edge> edges;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getSize_group() { return size_group; }
        public void setSize_group(String size_group) { this.size_group = size_group; }
        public List<String> getVertices() { return vertices; }
        public void setVertices(List<String> vertices) { this.vertices = vertices; }
        public List<Edge> getEdges() { return edges; }
        public void setEdges(List<Edge> edges) { this.edges = edges; }
    }

    public Graph(List<String> verticesList, List<Edge> edges) {
        this.vertices = new HashSet<>(verticesList);
        this.edges = new ArrayList<>(edges);
        this.adjacencies = new HashMap<>();

        for (String v : vertices) {
            adjacencies.put(v, new ArrayList<>());
        }

        for (Edge edge : edges) {
            adjacencies.get(edge.getFrom()).add(edge);
            adjacencies.get(edge.getTo()).add(edge);
        }
    }

    public Set<String> getVertices() { return vertices; }
    public List<Edge> getEdges() { return edges; }
    public Map<String, List<Edge>> getAdjacencies() { return adjacencies; }
    public int getNumVertices() { return vertices.size(); }
    public int getNumEdges() { return edges.size(); }

    public String toDotFormat(List<Edge> mstEdges) {
        StringBuilder dot = new StringBuilder();
        dot.append("graph G {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [shape=circle];\n\n");

        // добавляем вершины
        for (String vertex : vertices) {
            dot.append("  ").append(vertex).append(";\n");
        }
        dot.append("\n");

        // добавляем ребра
        Set<String> mstEdgeSet = new HashSet<>();
        for (Edge edge : mstEdges) {
            String key = Math.min(edge.getFrom().hashCode(), edge.getTo().hashCode()) + "-" +
                    Math.max(edge.getFrom().hashCode(), edge.getTo().hashCode());
            mstEdgeSet.add(key);
        }

        for (Edge edge : edges) {
            String key = Math.min(edge.getFrom().hashCode(), edge.getTo().hashCode()) + "-" +
                    Math.max(edge.getFrom().hashCode(), edge.getTo().hashCode());

            if (mstEdgeSet.contains(key)) {
                // MST ребра красные
                dot.append("  ").append(edge.getFrom()).append(" -- ").append(edge.getTo())
                        .append(" [label=\"").append(edge.getWeight())
                        .append("\", color=\"red\", penwidth=3.0];\n");
            } else {
                // Обыч ребра чёрные
                dot.append("  ").append(edge.getFrom()).append(" -- ").append(edge.getTo())
                        .append(" [label=\"").append(edge.getWeight()).append("\"];\n");
            }
        }

        dot.append("}\n");
        return dot.toString();
    }

    // Сохранение графа в дот
    public void saveAsDotFile(List<Edge> mstEdges, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(toDotFormat(mstEdges));
            System.out.println("The DOT file is saved: " + filename);
        } catch (IOException e) {
            System.err.println("Error saving the DOT file: " + e.getMessage());
        }
    }
}