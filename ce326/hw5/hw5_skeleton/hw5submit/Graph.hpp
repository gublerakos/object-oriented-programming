#ifndef _GRAPH_HPP_ 
#define _GRAPH_HPP_
#include <list>
#include <ostream>
#include <vector>
#include <stack>
#include <queue>
#include <algorithm>
#include <limits>
#include <iostream>
#include <fstream>
#include <utility>

// #include "NegativeGraphCycle.hpp"

using namespace std;

// Exception to throw when negative cycle is detected.
class NegativeGraphCycle : public exception {
public:
    const char* what() const throw(){
        return "Negative Graph Cycle!";
    }
};

// Struct "Edge" with from, to and dist to represent an edge.
template<typename T>
struct Edge {
    T from;
    T to;
    int dist;
    Edge(T f, T t, int d): from(f), to(t), dist(d){};
    bool operator<(const Edge<T>& e) const{
        return dist < e.dist;
    };
    bool operator>(const Edge<T>& e) const{
        return dist > e.dist;
    }

    template<typename U>
    friend std::ostream& operator<<(std::ostream& out, const Edge<U>& e);
};

template<typename T>
std::ostream& operator<<(std::ostream& out, const Edge<T>& e) {
    out << e.from << " -- " << e.to << " (" << e.dist << ")";
    return out;
}

template <typename T>
class Graph {

public:
    // Constructor of graph to initialize if it is directed or not.
    Graph(bool isDirected = true){
        this->isDirected = isDirected;
    }

    // Method to add a vertex to the graph.
    bool addVtx(const T& info){
        // Check if info already exists
        if(contains(info)){
            // cout << info << " already exists!" << endl;
            return false;
        }
        else{ // Every time a vertex is added, it's being pushed into the adjacency list(vector < vector<T>>).
            vector<T> temp;
            temp.push_back(info);

            adjList.push_back(temp);
        }
        size++;
        return true;
    }

    // Method to remove a vertex from the graph.
    bool rmvVtx(const T& info){
        int i, j;

        if(!contains(info)){
            return false;
        }
        else{
            for(i = 0; i < (int)adjList.size(); i++){
                if(adjList[i][0] == info){
                    adjList.erase(adjList.begin() + i);
                }
            }
            for(i = 0; i < (int)adjList.size(); i++){
                for(j = 0; j < (int)adjList[i].size(); j++){
                    if(adjList[i][j] == info){
                        adjList[i].erase(adjList[i].begin() + j);
                    }
                }
            }
            // When a vertex is removed, every edge to or from that vertex is being removed too.
            rmvEdgeHelper(info);
        }
        size--;
        return true;
    }

    // Method to add an edge to the graph.
    bool addEdg(const T& from, const T& to, int distance){
        int i;

        if(!(contains(from)))
            return false;

        if(!(contains(to)))
            return false;  
        
        // Check if edge already exists is the graph.
        if(edgeExists(from, to))
        return false;
        
        for(i = 0; i < (int)adjList.size(); i++){
            if(adjList[i][0] == from){
                adjList[i].push_back(to);
                Edge<T> temp(from, to, distance);
                edges.push_back(temp);
                
                auto it = adjList[i].begin();
                it++;

                // After adding the edge, the adjacency list gets sorted so that vertices, added earlier in the graph, are in the right place.
                sort(it, adjList[i].end(), [&](const T& first, const T& second) { return (findPlace(first))<(findPlace(second)); });
            }        
        }

        if(!isDirected){
            for(i = 0; i < (int)adjList.size(); i++){
                if(adjList[i][0] == to){
                    adjList[i].push_back(from);

                    auto it = adjList[i].begin();
                    it++;
                    sort(it, adjList[i].end(), [&](const T& first, const T& second) { return (findPlace(first))<(findPlace(second)); });
                }        
            }
        }
        
        return true;
    }

    bool compare(T t1, T t2){
        // Graph<T> g;
        return(findPlace(t1) < findPlace(t2));
    }       

    // Method to remove an edge from the graph.
    bool rmvEdg(const T& from, const T& to){
        if(!edgeExists(from, to))
            return false;

        if(!isDirected){
            for(auto it = edges.begin() ; it != edges.end(); ++it){
                if(((it->from == from) && (it->to == to)) || ((it->from == to) && (it->to == from))){
                    // cout << "Going to remove edge(from): " << *it << endl;
                    edges.erase(it);
                    it = edges.begin();
                    it--;
                }
            }
        }
        else{
            for(auto it = edges.begin() ; it != edges.end(); ++it){
                if((it->from == from) && (it->to == to)){
                    // cout << "Going to remove edge(from): " << *it << endl;
                    edges.erase(it);
                    it = edges.begin();
                    it--;
                }
            }
        }

        return true;
    }

    // Method to traverse the graph via dfs and return a list of the traversed nodes.
    std::list<T> dfs(const T& info) const{
        bool visited[size];
        list <T> result;


        for(int i = 0; i < size; i++){
            visited[i] = false; 
        }

        // Recursive function to help the dfs.
        dfsHelp(info, visited, result);

        return result;
    }

    // Method to traverse the graph via bfs and return a list of the traversed nodes.
    std::list<T> bfs(const T& info) const{
        bool visited[size];
        int i;
        queue <T> q;
        list <T> result;

        for(i = 0; i < size; i++){
            visited[i] = false;
        }

        q.push(info);
        result.push_back(info);
        visited[findPlace(info)] = true;

        while(!q.empty()){
            T temp = q.front();
            q.pop();
            int pos = findPlace(temp);

            for(i = 0; i < (int)adjList[pos].size(); i++){
                if(!ifVisited(adjList[pos][i], visited)){
                    q.push(adjList[pos][i]);
                    result.push_back(adjList[pos][i]);
                    visited[findPlace(adjList[pos][i])] = true;
                }
            }
        }
        return result;
    }

    // Method implementing prims algorithm to find the minimum spanning tree.
    // It returns a list of edges which form the mst and it also calculates the minimum cost.
    std::list<Edge<T>> mst(){
        list<Edge<T>> result;
        list<Edge<T>> helper;
        bool visited[size];
        sum = 0;

        if(isDirected){
            return result;
        }

        for(int i = 0; i < size; i++){
            visited[i] = false;
        }

        mstHelp(adjList[0][0], result, visited, helper);

        for(auto it = result.begin(); it != result.end(); it++){
            if(findPlace(it->from) > findPlace(it->to)){
                T temp = it->from;
                it->from = it->to;
                it->to = temp;
            }
        }

        for(auto it = result.cbegin(); it != result.cend(); it++){
            sum = sum + it->dist;
        }

        return result;
    }

    // Helper method to print the dot file.
    bool print2DotFile(const char *filename) const{
        ofstream dotFile;

        dotFile.open(filename);

        if(size == 0){
            return false;
        }

        if(!isDirected){
            dotFile << "graph {\n";
        }
        else{
            dotFile << "digraph {\n";
        }

        for(auto it = edges.begin(); it != edges.end(); it++){
            if(!isDirected){
                dotFile << it->from << " -- " << it->to << "weight = " << it->dist << endl; 
            }
            else{
                dotFile << it->from << " -> " << it->to << "weight = " << it->dist << endl; 
            }
        }

        dotFile << "}\n";
        dotFile.close();
        
        return true;
    }

    // Helper method to return a vertex based on its place in the adjacency list.
    T findVtx(int pos){
        int i;
        for(i = 0; i < size; i++){
            if(i == pos){
                return adjList[i][0];
            }
        }
        return adjList[i][0];
    }
    
    // Helper method to return the vertex with the minimum distance.
    T minDist(int dist[], bool sptSet[]){
        int min = numeric_limits<int>::max();
        T index;

        for(int i = 0; i < size; i++){
            if(sptSet[i] == false && dist[i] <= min){
                min = dist[i];
                index = findVtx(i);
            }
        }

        return index;
    }
    
    // Method implementing dijkstra's algorithm to find the shortest path from a node to another.
    std::list<T> dijkstra(const T& from, const T& to){
        int dist[size]; // array with distances, initialized to INF.
        bool sptSet[size]; // array to store vertices with min distance found.
        list <T> result; // list with final distances.
        T previousVtx[size]; // array to store previous vertices in order to find the wanted path.
        int edgeDist;
        T vertex, vtxTemp;
        stack <T> helper;

        for(int j = 0; j < size; j++){
            dist[j] =  numeric_limits<int>::max();
            sptSet[j] = false;
        }
        
        dist[findPlace(from)] = 0; // first vertex dist = 0.

        for(int i = 0; i < size; i++){
            T temp = minDist(dist, sptSet);
            sptSet[findPlace(temp)] = true;


            for(auto it = adjList[findPlace(temp)].begin() + 1; it != adjList[findPlace(temp)].end(); ++it){
                edgeDist = findDistance(temp, *it);
                vertex = *it;
                // cout << "temp = " << temp << " -> " << vertex << endl;

                if((dist[findPlace(vertex)] > (dist[findPlace(temp)] + edgeDist)) && (!sptSet[findPlace(vertex)])){
                    dist[findPlace(vertex)] = dist[findPlace(temp)] + edgeDist;
                    previousVtx[findPlace(vertex)] = temp;
                }
            }
        }

        vertex = to;

        while(vertex != from){
            vtxTemp = previousVtx[findPlace(vertex)];
            if(edgeExists(vtxTemp, vertex)){
                helper.push(vertex);
                vertex = previousVtx[findPlace(vertex)];
            }
            else{
                result.clear();
                return result;
            }
        }
        
        helper.push(from);

        while(!helper.empty()){
            result.push_back(helper.top());
            helper.pop();
        }

        return result;
    }

    // Edge<T> findEdge(const T& from, const T& to) {
    //     int j;
    //     for(j = 0; j < (int)edges.size(); j++){
    //         if((edges[j].from == from) && (edges[j].to == to)){
    //             return edges[j];
    //         }
    //     }
    //     return edges[j];
    // }

    // Method implementing bellman ford's algorithm to find the shortest path from a node to another and detect possible negative cycle.
    std::list<T> bellman_ford(const T& from, const T& to){
        list <T> result;
        int i;
        stack <T> helper;
        T previousVtx[size];

        vector <int> dist(size, numeric_limits<int>::max());
        dist[findPlace(from)] = 0;


        // Relax all edges (size - 1) times
        for(i = 0; i < (size - 1); i++){
            for(int j = 0; j < (int)edges.size(); j++){
                T fromVtx = edges[j].from;  //src
                T toVtx = edges[j].to;  //dest
                int weight = edges[j].dist;

                if((dist[findPlace(fromVtx)] != numeric_limits<int>::max()) && ((dist[findPlace(fromVtx)] + weight) < dist[findPlace(toVtx)])){
                    dist[findPlace(toVtx)] = dist[findPlace(fromVtx)] + weight;
                    previousVtx[findPlace(toVtx)] = fromVtx;
                }

            }
        }

        //  If we get another shorter path there is a negative cycle. 
        for(int j = 0; j < (int)edges.size(); j++){
            T fromVtx = edges[j].from;  //src
            T toVtx = edges[j].to;  //dest
            int weight = edges[j].dist;

            if((dist[findPlace(fromVtx)] != numeric_limits<int>::max()) && ((dist[findPlace(fromVtx)] + weight) < dist[findPlace(toVtx)])){
                cout << "Negative Graph Cycle!";
                // NegativeGraphCycle ex;
                // throw ex;
                return result;
            }

        }

        T vertex = to;

        T vtxTemp;
        while(vertex != from){
            vtxTemp = previousVtx[findPlace(vertex)];
            if(edgeExists(vtxTemp, vertex)){
                helper.push(vertex);
                vertex = previousVtx[findPlace(vertex)];
            }
            else{
                result.clear();
                return result;
            }
        }
        
        helper.push(from);

        while(!helper.empty()){
            result.push_back(helper.top());
            helper.pop();
        }

        return result;
    }

    // New
    vector<vector<T>> adjList;  //Adjacency list
    int size = 0;
    bool isDirected;

    // Method to find if a vertex already exists in the graph.
    bool contains(const T& info){
        int i;

        for(i = 0; i < (int)adjList.size(); i++){
            if(adjList[i][0] == info){
                // cout << "Found " << adjList[i][0] << endl;
                return true;
            }        
        }
        // cout << "Not found! :(" << endl;
        return false;
    }

    vector<Edge<T>> edges;
    // Helper method
    void printEdgeList(){

        // cout << "===================================" << endl;

        for(int i = 0; i < (int)edges.size(); i++){
            // cout << edges[i] << endl;
        }

        // cout << "===================================" << endl;
    }
    // Helper method
    void printAdjacencyList(){

        // cout << "===================================" << endl;

        for(int i = 0; i < (int)adjList.size(); i++){
            for(int j = 0; j < (int)adjList[i].size(); j++){
                // cout << adjList[i][j] << " ->" ;
            }
            // cout << endl;
        }

        // cout << "===================================" << endl;
    }

    // Helper method to find if an edge already exists in the graph.
    bool edgeExists(const T& from, const T& to){
        if(isDirected){
            for(auto it = edges.begin() ; it != edges.end(); ++it){
                if((it->from == from) && (it->to == to)){
                    return true;
                }
            }
        }
        else {
            for(auto it = edges.begin() ; it != edges.end(); ++it){
                if(((it->from == from) && (it->to == to)) || ((it->from == to) && (it->to == from))){
                    return true;
                }
            }
        }

        return false;
    }

    // Helper method to remove all edges from a vertex that is being removed.
    bool rmvEdgeHelper(const T& rmvVtx){
        for(auto it = edges.begin() ; it != edges.end(); ++it){
            if(it->from == rmvVtx){
                // cout << "Going to remove edge(from): " << *it << endl;
                edges.erase(it);
                it = edges.begin();
                it--;
            }
            else if(it->to == rmvVtx){
                // cout << "Going to remove edge(to): " << *it << endl;
                edges.erase(it);
                it = edges.begin();
                it--;
            }
        }
        return true;
    }

    // Helper method to return the place of a node in the adjacency list.
    int findPlace(const T& info) const{
        for(int i = 0; i < (int)adjList.size(); i++){
            if(adjList[i][0] == info){
                return i;
            }
        }

        return -1;
    }

    // Helper method that returns true if a vertex is visited.
    bool ifVisited(const T& info, bool visited[]) const{
        if(visited[findPlace(info)])
            return true;

        return false;
    }

    // static bool sortByDist(const Edge<T> &e1, const Edge<T> &e2) { return e1.dist < e2.dist; }

    // Recursive function to help find the minimum spanning tree.
    void mstHelp(T vertex, list<Edge<T>> &result, bool visited[], list<Edge<T>> helper){
        
        T vtxTemp;

        visited[findPlace(vertex)] = true;

        if(allVisited(visited)){
            result.sort();
            return;
        }

        for(int i = 0; i < (int)edges.size(); i++){
            if((edges[i].from == vertex) || (edges[i].to == vertex)){
                helper.push_back(edges[i]);
            }
        }

        for(auto it = helper.begin(); it != helper.end(); ++it){
            if((ifVisited(it->to, visited)) && (ifVisited(it->from, visited))){
                it = helper.erase(it);
                it--;
            }
        }
        
        helper.sort();
        
        if(vertex == (helper.front()).from){    
            vtxTemp = (helper.front()).to;
        }
        else if(vertex == (helper.front()).to){
            vtxTemp = (helper.front()).from;
        }
        else{
            if(ifVisited((helper.front()).to, visited)){
                vtxTemp = (helper.front()).from;
            }
            else{
                vtxTemp = (helper.front()).to;
            }
        }

        result.push_back(helper.front());
        
        auto it = helper.begin();
        it = helper.erase(it);

        mstHelp(vtxTemp, result, visited, helper);
    }

    // Helper method that returns true if every vertex is visited.
    bool allVisited(bool visited[]){
            for(int j = 0; j < size ; j++){
            if(!visited[j]){
                return false;
            }
        }

        return true;
    }

    // Recursive function to help dfs the graph.
    void dfsHelp(T vertex, bool visited[], list <T> &result) const{
        visited[findPlace(vertex)] = true;
        result.push_back(vertex);

        for(auto it = adjList[findPlace(vertex)].begin(); it != adjList[findPlace(vertex)].end(); ++it){
            if(!ifVisited(*it, visited)){
                dfsHelp(*it, visited, result);
            }
        }
    }

    // T findUnvisited(bool visited[]){
    //     int j; 

    //     for(j = 0; j < size ; j++){
    //         if(!visited[j]){
    //             break;
    //         }
    //     } 
    //     return adjList[j][0];
    // }

    // Helper method to return the distance between "from" and "to".
    int findDistance(const T& from, const T& to){
        if(isDirected){
            for(auto it = edges.begin() ; it != edges.end(); ++it){
                if((it->from == from) && (it->to == to)){
                    return it->dist;;
                }
            }
        }
        else {
            for(auto it = edges.begin() ; it != edges.end(); ++it){
                if(((it->from == from) && (it->to == to)) || ((it->from == to) && (it->to == from))){
                    return it->dist;;
                }
            }
        }

        return -1;
    }

    int sum;
};

#endif
