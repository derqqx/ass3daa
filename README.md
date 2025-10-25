# Analytical Report: City Transportation Network Optimization

## 1. Summary of Input Data and Algorithm Results

### Input Data Characteristics
tested the algorithms on 30 graphs of varying sizes and complexities:
- **5 Small graphs**: Up to 30 vertices (e.g., Graph 1: 14 vertices, 34 edges)
- **10 Medium graphs**: Up to 300 vertices (e.g., Graph 9: 289 vertices, 564 edges)
- **10 Large graphs**: Up to 1000 vertices (e.g., Graph 19: 929 vertices, 2887 edges)
- **5 Extra-large graphs**: Up to 3000 vertices (e.g., Graph 29: 2742 vertices, 7417 edges)

### Algorithm Performance Results

**MST Cost Results:**
- Both algorithms produced identical MST costs for all graphs
- Examples: Graph 1 = 346, Graph 16 = 10,436, Graph 29 = 60,714
- This validates the correctness of both implementations

**Execution Time Analysis:**

*Prim's Algorithm Performance:*
- Small graphs: 0.03ms - 4.93ms
- Medium graphs: 0.17ms - 1.66ms
- Large graphs: 0.93ms - 3.66ms
- Extra-large: 1.77ms - 5.33ms

*Kruskal's Algorithm Performance:*
- Small graphs: 0.02ms - 2.99ms
- Medium graphs: 0.22ms - 1.33ms
- Large graphs: 0.62ms - 4.49ms
- Extra-large: 2.07ms - 5.92ms

**Operation Count Comparison:**

*Prim's Operations:*
- Range: 11 to 14,788 operations
- Generally lower than Kruskal for dense graphs
- Scales well with graph density

*Kruskal's Operations:*
- Range: 36 to 107,087 operations
- Higher operation count due to sorting and union-find overhead
- More affected by number of edges

## 2. Comparison: Prim's vs Kruskal's Algorithms

### Theoretical Efficiency

**Prim's Algorithm:**
- Time Complexity: O(E log V) with binary heap
- Best for dense graphs (many edges)
- Uses adjacency list representation
- Always maintains a single tree

**Kruskal's Algorithm:**
- Time Complexity: O(E log E) due to sorting
- Best for sparse graphs (few edges)
- Uses edge list representation
- Builds multiple trees that merge

### Practical Performance Observations

**Execution Time Patterns:**
- For small graphs (<100 vertices): Both algorithms perform similarly
- For medium graphs (100-500 vertices): Prim's often slightly faster
- For large dense graphs: Prim's shows better performance
- For large sparse graphs: Kruskal's can be competitive

**Memory Usage:**
- Prim's requires priority queue and visited set
- Kruskal's requires sorted edges and union-find structure
- Both have similar memory footprints for most cases

**Operation Efficiency:**
- Prim's has fewer total operations in dense graphs
- Kruskal's operation count grows faster with edge count
- Prim's better utilizes graph sparsity through adjacency lists

## 3. Conclusions and Recommendations

**Choose Prim's Algorithm When:**
- Working with dense graphs (many edges compared to vertices)
- Graph is represented as adjacency list
- Memory efficiency is important
- You need consistent performance across graph types

**Choose Kruskal's Algorithm When:**
- Working with sparse graphs (few edges)
- Edges are already sorted or easy to sort
- Implementation simplicity is priority
- Working with edge-based graph representations

**Our Experimental Findings:**
1. **Correctness**: Both algorithms consistently find optimal MST
2. **Performance**: Prim's generally faster for our test cases
3. **Scalability**: Both handle large graphs effectively
4. **Reliability**: Results are reproducible and consistent

The project demonstrates that both algorithms are suitable for city transportation network optimization, with Prim's algorithm showing slight advantages for the dense graphs typical in urban planning scenarios.