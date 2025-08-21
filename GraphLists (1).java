// Simple weighted graph representation 
// Uses an Adjacency Linked Lists, suitable for sparse graphs

import java.io.*;
import java.util.Scanner;

class Heap {

    private int[] a;	   // heap array
    private int[] hPos;	   // hPos[h[k]] == k
    private int[] dist;    // dist[v] = priority of v
    private int N;         // heap size
   
    // The heap constructor gets passed from the Graph:
    //    1. maximum heap size
    //    2. reference to the dist[] array
    //    3. reference to the hPos[] array
    public Heap(int maxSize, int[] _dist, int[] _hPos) {

        N = 0;
        a = new int[maxSize + 1];
        dist = _dist;
        hPos = _hPos;

    }

    public boolean isEmpty() {
        
        return N == 0;

    }
    //move element up heap to its correct position	
    public void siftUp( int k) {

        int v = a[k];
        //while current node not root and value is smaller than parent
        while (k > 1 && dist[v] < dist[a[k/2]]) {

            a[k] = a[k/2];
            hPos[a[k]] = k;
            k = k/2;

        }
    
        a[k] = v;
        hPos[v] = k;

    }
    //move element down heap to its correct position
    public void siftDown( int k) {

        int v, j;
        v = a[k];  
        //continue while current node has children
        while (2*k <= N) {

            j = 2*k;
            //if right child exists &  smaller than left child, use right child
            if (j < N && dist[a[j]] > dist[a[j+1]])
                j++;
            //if curent <= smallest child, stop
            if (dist[v] <= dist[a[j]])
                break;
            
            a[k] = a[j];
            hPos[a[k]] = k;
            k = j;

        }
    
        a[k] = v;
        hPos[v] = k;

    }

    public void insert( int x) {

        a[++N] = x;
        siftUp( N);

    }

    public int remove() {   

        int v = a[1];
        hPos[v] = 0; // v is no longer in heap
        a[N+1] = 0;  // put null node into empty spot
        
        a[1] = a[N--];
        if (N > 0) siftDown(1);

        return v;

    }

}

class Graph {

    class Node {

        public int vert;
        public int wgt;
        public Node next;

    }
    
    // V = number of vertices
    // E = number of edges
    // adj[] is adjacency lists array
    private int V, E;
    private Node[] adj;
    private Node z;
    private int[] mst;
    
    // used for traversing graph
    private int[] visited;
    private int id;
    
    // default constructor
    public Graph(String graphFile)  throws IOException {

        int u, v;
        int e, wgt;
        Node t;

        FileReader fr = new FileReader(graphFile);
		BufferedReader reader = new BufferedReader(fr);
	           
        String splits = " +";  // multiple whitespace as delimiter
		String line = reader.readLine();        
        String[] parts = line.split(splits);
        System.out.println("Parts[] = " + parts[0] + " " + parts[1]);
        
        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);
        
        // create sentinel node
        z = new Node(); 
        z.next = z;
        
        // create adjacency lists, initialised to sentinel node z       
        adj = new Node[V+1];        
        for(v = 1; v <= V; ++v)
            adj[v] = z;               
        
       // read the edges
        System.out.println("Reading edges from text file");
        for(e = 1; e <= E; ++e) {

            line = reader.readLine();
            if (line == null || line.trim().isEmpty()) {

                System.out.println("end of file or blank line while reading edges." + e);
                break; 
            
            }

            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]); 
            wgt = Integer.parseInt(parts[2]);
            
            System.out.println("Edge " + toChar(u) + "--(" + wgt + ")--" + toChar(v));   

            // insert at the beginning of adj[u]
            t = new Node();
            t.vert = v;
            t.wgt = wgt;
            t.next = adj[u];
            adj[u] = t;

            // insert at the beginning of adj[v] since it's undirected
            t = new Node();
            t.vert = u;
            t.wgt = wgt;
            t.next = adj[v];
            adj[v] = t;  
            
        }
       
    }
   
    // convert vertex into char for pretty printing
    private char toChar(int u) {  

        return (char)(u + 64);

    }
    
    // method to display the graph representation
    public void display() {

        int v;
        Node n;
        
        for(v=1; v<=V; ++v){

            System.out.print("\nadj[" + toChar(v) + "] ->" );
            for(n = adj[v]; n != z; n = n.next) 
            System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->");    
        
        }

        System.out.println("");

    }

    // Depth First Traversal 
    public void DF(int s) {

        visited = new int[V + 1];  // 1-based indexing
        System.out.print("\nDepth First Traversal starting from " + toChar(s) + ":\n");
        dfVisit( s );
        System.out.println("");

    }

    private void dfVisit(int v) {

        visited[v] = 1;
        System.out.print(toChar(v) + " ");
        
        for (Node t = adj[v]; t != z; t = t.next) {

            if (visited[t.vert] == 0) {

                dfVisit(t.vert);
            
            }
        
        }
    
    }

    // Breadth First Traversal - Using Queue
    public void breadthFirst(int s) {

        visited = new int[V + 1];  // 1-based indexing
        int[] queue = new int[V + 1]; 
        int front = 0, rear = 0;

        System.out.print("\nBreadth First Traversal starting from " + toChar(s) + ":\n");

        visited[s] = 1;
        queue[rear++] = s;

        while (front != rear) {

            int v = queue[front++];
            System.out.print(toChar(v) + " ");

            for (Node t = adj[v]; t != z; t = t.next) {

                if (visited[t.vert] == 0) {

                    visited[t.vert] = 1;
                    queue[rear++] = t.vert;

               }
            
            }
        
        }

        System.out.println("");
   
    }

    // prim's algorithm
	public void MST_Prim(int s) {

        int v, u;
        int wgt, wgt_sum = 0;
        int[]  dist, parent, hPos;
        Node t;

        //init arrays
        dist = new int[V + 1];
        parent = new int[V + 1];
        hPos = new int[V + 1];
        mst = new int[V + 1];  // this will store the MST parent tree
        
        //initial values for vertices
        for (v = 1; v <= V; ++v) {

            dist[v] = Integer.MAX_VALUE;  // make distances unreachable
            parent[v] = 0;                 // no parent yet
            hPos[v] = 0;                   // not in heap yte
        
        }
        
        dist[s] = 0;  // starting vertex distance is 0
        Heap h = new Heap(V, dist, hPos);
        h.insert(s); //insert start vertx into heap
        
        //until heap is empty
        while (!h.isEmpty()) {

            v = h.remove();
            mst[v] = parent[v];  // parent of v in mst
            wgt_sum += dist[v];  // add v distance
    
            System.out.println("\nSelected vertex: " + toChar(v));
            System.out.print("Heap: ");
            for (int i = 1; i <= V; i++) {

                if (hPos[i] > 0)
                    System.out.print(toChar(i) + "(" + dist[i] + ") ");
            
            }

            System.out.println();

            //current distnces to all vertices
            System.out.print("dist[]: ");
            for (int i = 1; i <= V; i++) {

                System.out.print(toChar(i) + "=" + (dist[i] == Integer.MAX_VALUE ? "∞" : dist[i]) + " ");
            
            }

            System.out.println();

            System.out.print("parent[]: ");
            for (int i = 1; i <= V; i++) {

                System.out.print(toChar(i) + "=" + toChar(parent[i]) + " ");
            
            }
            
            System.out.println();

            //traverse adjacency list of current vertex
            for (t = adj[v]; t != z; t = t.next) {

                u = t.vert;
                wgt = t.wgt;

                //update distance to u if new distance is shorter
                if (wgt < dist[u]) { 

                    dist[u] = wgt;
                    parent[u] = v;

                    //if u not in heap insert
                    if (hPos[u] == 0)
                        h.insert(u);
                    else
                        h.siftUp(hPos[u]); // otherwise update position 

                }
            
            }
        
        }
    
        System.out.print("\n\nWeight of MST = " + wgt_sum + "\n");
                  		
	}
    
    public void showMST(){

        System.out.print("\n\nMinimum Spanning tree parent array is:\n");
        for(int v = 1; v <= V; ++v)
            System.out.println(toChar(v) + " -> " + toChar(mst[v]));
        
        System.out.println("");
    
    }

    // Disjkstra's algorith
    public void SPT_Dijkstra(int s) {

        int v, u;
        int wgt;
        int[] dist, parent, hPos;
        Node t;
    
        dist = new int[V + 1];
        parent = new int[V + 1];
        hPos = new int[V + 1];
    
        //set initial values for all vertices
        for (v = 1; v <= V; ++v) {

            dist[v] = Integer.MAX_VALUE; //make distances unreachbale
            parent[v] = 0;
            hPos[v] = 0;

        }
    
        dist[s] = 0;  // starting vertex distance is 0
        Heap h = new Heap(V, dist, hPos);
        h.insert(s); //starting vetrix in heap
    
        //until heap is empty
        while (!h.isEmpty()) {

            v = h.remove();

            System.out.println("\nSelected vertex: " + toChar(v));
            System.out.print("Heap: ");

            for (int i = 1; i <= V; i++) {

                if (hPos[i] > 0)
                    System.out.print(toChar(i) + "(" + dist[i] + ") ");
            
            }
            System.out.println();

            //print current distances from source to all vertices
            System.out.print("dist[]: ");
            for (int i = 1; i <= V; i++) {

                System.out.print(toChar(i) + "=" + (dist[i] == Integer.MAX_VALUE ? "∞" : dist[i]) + " ");
           
            }
            
            System.out.println();

            System.out.print("parent[]: ");
            for (int i = 1; i <= V; i++) {

                System.out.print(toChar(i) + "=" + (parent[i] == 0 ? "-" : toChar(parent[i])) + " ");
           
            }
            
            System.out.println();
            
            //traverse adjacency list of current vertex 
            for (t = adj[v]; t != z; t = t.next) {

                u = t.vert;
                wgt = t.wgt;
                //update distance to u if new distance is shorter
                if (dist[v] + wgt < dist[u]) {

                    dist[u] = dist[v] + wgt;
                    parent[u] = v;

                    if (hPos[u] == 0)
                        h.insert(u);
                    else
                        h.siftUp(hPos[u]);
                
                }

            }
        
        }
        //Print the final spt parent array
        System.out.println("\n\nShortest Path Tree (SPT) parent array:");
        for (v = 1; v <= V; ++v) {

            System.out.println(toChar(v) + " -> " + toChar(parent[v]));
        
        }
    
    }

}

public class GraphLists {
    //main
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the name of the graph file: ");
        String fname = scanner.nextLine();

        System.out.print("Enter the starting vertex number (1 = A, 2 = B, ..., 12 = L, 13 = M): ");
        int s = scanner.nextInt();

        Graph g = new Graph(fname);
       
        g.display();

        g.DF(s);
        g.breadthFirst(s);
        g.MST_Prim(s);  
        g.showMST();

        // Measuring memory and time 
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();  // run garbage collector
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.currentTimeMillis();

        g.SPT_Dijkstra(s);

        long endTime = System.currentTimeMillis();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("\nRunning Time: " + (endTime - startTime) + " ms");
        System.out.println("Memory Used: " + ((memoryAfter - memoryBefore) / (1024 * 1024)) + " MB");

        scanner.close();    
                   
    }

}
