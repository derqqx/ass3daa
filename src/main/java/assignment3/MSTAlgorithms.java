package assignment3;

import java.util.*;

public class MSTAlgorithms {
    public static class MSTResult {
        public List<Edge> mstEdges = new ArrayList<>();
        public long totalCost = 0;
        public long operationsCount = 0;
        public double executionTimeMs = 0;

        public List<Edge> getMstEdges() { return mstEdges; }
        public long getTotalCost() { return totalCost; }
    }
    // I. прим
    public static MSTResult primMST(Graph graph) {
        MSTResult result = new MSTResult();
        long startTime = System.nanoTime();

        if (graph.getNumVertices() == 0) return result;

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(Edge::getWeight));
        Set<String> inMST = new HashSet<>();

        String startVertex = graph.getVertices().iterator().next();
        inMST.add(startVertex);

        List<Edge> startEdges = graph.getAdjacencies().get(startVertex);
        if (startEdges != null) {
            for (Edge edge : startEdges) {
                if (edge != null) {
                    pq.offer(edge);
                    result.operationsCount++;
                }
            }
        }

        while (!pq.isEmpty() && inMST.size() < graph.getNumVertices()) {
            Edge edge = pq.poll();
            if (edge == null) continue;

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

                List<Edge> newEdges = graph.getAdjacencies().get(newVertex);
                if (newEdges != null) {
                    for (Edge nextEdge : newEdges) {
                        if (nextEdge != null) {
                            String neighbor = nextEdge.getFrom().equals(newVertex) ? nextEdge.getTo() : nextEdge.getFrom();
                            if (!inMST.contains(neighbor)) {
                                pq.offer(nextEdge);
                                result.operationsCount++;
                            }
                        }
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
            if (edge == null) continue;

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
}