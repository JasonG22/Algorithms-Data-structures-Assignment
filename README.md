Graph Algorithms Project

This project implements and analyzes four graph algorithms using adjacency lists: Depth-First Search (DFS), Breadth-First Search (BFS), Prim’s Minimum Spanning Tree, and Dijkstra’s Shortest Path Tree. The program reads a graph from a file and executes each algorithm from a specified starting vertex, producing step-by-step console output to illustrate their progress and assist with debugging.

The graph tested was based on a simplified road network in Crumlin, Dublin, containing 13 vertices and 20 edges with varying weights. This structure introduced cycles, multiple paths between nodes, and competing edge costs.

Prim’s algorithm demonstrated how a minimum spanning tree is built incrementally by choosing the lowest-cost edges that extend the tree. Dijkstra’s algorithm showed how shortest paths are established from a source to all other nodes, and how a custom heap improves efficiency in extracting the nearest vertex and updating distances. Traversal techniques with DFS and BFS provided insight into different exploration strategies: DFS for depth-oriented search and BFS for level-based discovery.

Building this project helped me understand the advantages of adjacency lists for representing sparse graphs, especially when compared to adjacency matrices. I also gained practical experience with heaps, priority queues, and array tracking for both Prim’s and Dijkstra’s algorithms. Tracing the state of arrays such as dist[] and parent[] during execution proved useful in understanding algorithm behavior.

Through this work I strengthened my knowledge of graph theory, Java programming, and algorithmic thinking.
