package assignment3;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class MSTTest {

    private Graph createSimpleGraph() {
        List<String> vertices = List.of("A", "B", "C", "D");
        List<Edge> edges = List.of(
                new Edge("A", "B", 1),
                new Edge("A", "C", 4),
                new Edge("B", "C", 2),
                new Edge("C", "D", 3),
                new Edge("B", "D", 5)
        );
        return new Graph(vertices, edges);
    }

    private Graph createDisconnectedGraph() {
        List<String> vertices = List.of("A", "B", "C", "D");
        List<Edge> edges = List.of(
                new Edge("A", "B", 1),
                new Edge("C", "D", 2)
        );
        return new Graph(vertices, edges);
    }

    @Test
    public void testMSTCostIdentical() {
        Graph graph = createSimpleGraph();
        MSTAlgorithms.MSTResult prim = MSTAlgorithms.primMST(graph);
        MSTAlgorithms.MSTResult kruskal = MSTAlgorithms.kruskalMST(graph);

        assertEquals(6, prim.getTotalCost());
        assertEquals(6, kruskal.getTotalCost());
        assertEquals(prim.getTotalCost(), kruskal.getTotalCost());
    }

    @Test
    public void testMSTEdgeCount() {
        Graph graph = createSimpleGraph();
        MSTAlgorithms.MSTResult prim = MSTAlgorithms.primMST(graph);
        MSTAlgorithms.MSTResult kruskal = MSTAlgorithms.kruskalMST(graph);

        assertEquals(3, prim.getMstEdges().size());
        assertEquals(3, kruskal.getMstEdges().size());
    }

    @Test
    public void testDisconnectedGraph() {
        Graph graph = createDisconnectedGraph();
        MSTAlgorithms.MSTResult prim = MSTAlgorithms.primMST(graph);
        MSTAlgorithms.MSTResult kruskal = MSTAlgorithms.kruskalMST(graph);

        assertEquals(-1, prim.getTotalCost());
        assertEquals(-1, kruskal.getTotalCost());
    }

    @Test
    public void testMSTIsAcyclic() {
        Graph graph = createSimpleGraph();
        MSTAlgorithms.MSTResult prim = MSTAlgorithms.primMST(graph);
        MSTAlgorithms.MSTResult kruskal = MSTAlgorithms.kruskalMST(graph);

        assertTrue(isAcyclic(prim.getMstEdges()), "Prim's MST should be acyclic");
        assertTrue(isAcyclic(kruskal.getMstEdges()), "Kruskal's MST should be acyclic");
    }

    @Test
    public void testMSTIsConnected() {
        Graph graph = createSimpleGraph();
        MSTAlgorithms.MSTResult prim = MSTAlgorithms.primMST(graph);
        MSTAlgorithms.MSTResult kruskal = MSTAlgorithms.kruskalMST(graph);

        assertTrue(isConnected(prim.getMstEdges(), graph.getVertices()),
                "Prim's MST should connect all vertices");
        assertTrue(isConnected(kruskal.getMstEdges(), graph.getVertices()),
                "Kruskal's MST should connect all vertices");
    }

    @Test
    public void testDOTGeneration() {
        Graph graph = createSimpleGraph();
        MSTAlgorithms.MSTResult prim = MSTAlgorithms.primMST(graph);

        String dot = graph.toDotFormat(prim.getMstEdges());
        assertNotNull(dot);
        assertTrue(dot.contains("graph G"));
        assertTrue(dot.contains("A -- B"));
        assertTrue(dot.contains("red"));
    }

    private boolean isAcyclic(List<Edge> edges) {
        Set<String> allVertices = new HashSet<>();
        for (Edge edge : edges) {
            allVertices.add(edge.getFrom());
            allVertices.add(edge.getTo());
        }

        DisjointSet ds = new DisjointSet(allVertices);
        for (Edge edge : edges) {
            if (!ds.union(edge.getFrom(), edge.getTo())) {
                return false; // Найден цикл
            }
        }
        return true;
    }

    private boolean isConnected(List<Edge> edges, Set<String> allVertices) {
        if (edges.isEmpty()) return allVertices.size() <= 1;

        DisjointSet ds = new DisjointSet(allVertices);
        for (Edge edge : edges) {
            ds.union(edge.getFrom(), edge.getTo());
        }

        String firstRoot = ds.find(allVertices.iterator().next());
        for (String vertex : allVertices) {
            if (!ds.find(vertex).equals(firstRoot)) {
                return false;
            }
        }
        return true;
    }
}