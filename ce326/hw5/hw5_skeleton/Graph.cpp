#include "Graph.hpp"
#include <iostream>
#include <fstream>

using namespace std;

// template<typename T>
// Graph<T>::Graph(bool isDirectedGraph){
//     isDirected = isDirectedGraph;
// }

// template<typename T>
// bool Graph<T>::contains(const T& info){
//     int i;

//     for(i = 0; i < (int)adjList.size(); i++){
//         if(adjList[i][0] == info){
//             // cout << "Found " << adjList[i][0] << endl;
//             return true;
//         }        
//     }
//     // cout << "Not found! :(" << endl;
//     return false;
// }

// template<typename T>
// bool Graph<T>::addVtx(const T& info){
    
//     // Check if info already exists
//     if(contains(info)){
//         cout << info << " already exists!" << endl;
//         return false;
//     }
//     else{
//         vector<T> temp;
//         temp.push_back(info);

//         adjList.push_back(temp);
//     }
//     size++;
//     return true;
// }

// template<typename T>
// int Graph<T>::findDistance(const T& from, const T& to){
//     if(isDirected){
//         for(auto it = edges.begin() ; it != edges.end(); ++it){
//             if((it->from == from) && (it->to == to)){
//                 return it->dist;;
//             }
//         }
//     }
//     else {
//         for(auto it = edges.begin() ; it != edges.end(); ++it){
//             if(((it->from == from) && (it->to == to)) || ((it->from == to) && (it->to == from))){
//                 return it->dist;;
//             }
//         }
//     }

//     return -1;
// }

// template<typename T>
// bool Graph<T>::edgeExists(const T& from, const T& to){
//     if(isDirected){
//         for(auto it = edges.begin() ; it != edges.end(); ++it){
//             if((it->from == from) && (it->to == to)){
//                 return true;
//             }
//         }
//     }
//     else {
//         for(auto it = edges.begin() ; it != edges.end(); ++it){
//             if(((it->from == from) && (it->to == to)) || ((it->from == to) && (it->to == from))){
//                 return true;
//             }
//         }
//     }

//     return false;
// }

// template<typename T>
// bool Graph<T>::addEdg(const T& from, const T& to, int distance){
//     int i;

//     if(!(contains(from)))
//         return false;

//     if(!(contains(to)))
//         return false;  
    
//     // Check if edge already exists is the graph.
//     if(edgeExists(from, to))
//        return false;
    
//     for(i = 0; i < (int)adjList.size(); i++){
//         if(adjList[i][0] == from){
//             adjList[i].push_back(to);
//             Edge<T> temp(from, to, distance);
//             edges.push_back(temp);
//         }        
//     }

//     if(!isDirected){
//         for(i = 0; i < (int)adjList.size(); i++){
//             if(adjList[i][0] == to){
//                 adjList[i].push_back(from);
//             }        
//         }
//     }
    

//     return true;
// }

// template<typename T>
// bool Graph<T>::rmvEdg(const T& from, const T& to){

//     if(!edgeExists(from, to))
//         return false;

//     if(!isDirected){
//         for(auto it = edges.begin() ; it != edges.end(); ++it){
//             if(((it->from == from) && (it->to == to)) || ((it->from == to) && (it->to == from))){
//                 cout << "Going to remove edge(from): " << *it << endl;
//                 edges.erase(it);
//                 it = edges.begin();
//                 it--;
//             }
//         }
//     }
//     else{
//         for(auto it = edges.begin() ; it != edges.end(); ++it){
//             if((it->from == from) && (it->to == to)){
//                 cout << "Going to remove edge(from): " << *it << endl;
//                 edges.erase(it);
//                 it = edges.begin();
//                 it--;
//             }
//         }
//     }

//     return true;
// }

// template<typename T>
// bool Graph<T>::rmvEdgeHelper(const T& rmvVtx){
//     for(auto it = edges.begin() ; it != edges.end(); ++it){
//         if(it->from == rmvVtx){
//             cout << "Going to remove edge(from): " << *it << endl;
//             edges.erase(it);
//             it = edges.begin();
//             it--;
//         }
//         else if(it->to == rmvVtx){
//             cout << "Going to remove edge(to): " << *it << endl;
//             edges.erase(it);
//             it = edges.begin();
//             it--;
//         }
//     }
//     return true;
// }


// template<typename T>
// bool Graph<T>::rmvVtx(const T& info){
//     int i, j;

//     if(!contains(info)){
//         return false;
//     }
//     else{
//         for(i = 0; i < (int)adjList.size(); i++){
//             if(adjList[i][0] == info){
//                 adjList.erase(adjList.begin() + i);
//             }
//         }
//         for(i = 0; i < (int)adjList.size(); i++){
//             for(j = 0; j < (int)adjList[i].size(); j++){
//                 if(adjList[i][j] == info){
//                     adjList[i].erase(adjList[i].begin() + j);
//                 }
//             }
//         }
//         rmvEdgeHelper(info);
//     }
//     size--;
//     return true;
// }

// // LATHOOOOOS
// // template<typename T>
// // bool Graph<T>::print2DotFile(const char *filename) const{
// //     ofstream dotFile;
// //     int i, j;

// //     dotFile.open(filename);

// //     if(!isDirected){
// //         dotFile << "graph {" << endl;
// //         for(i = 0; i < (int)adjList[i].size(); i++){
// //             for(j = 1; j <= (int)adjList[i].size(); j++){
// //                 dotFile << adjList[i][0] << "--" << adjList[i][j] << endl;
// //             }
// //         }
// //     }
// //     else{
// //         dotFile << "digraph {" << endl;
// //         for(i = 0; i < (int)adjList.size(); i++){
// //             for(j = 1; j <= (int)adjList[i].size(); j++){
// //                 dotFile << adjList[i][0] << "->" << adjList[i][j] << endl;
// //             }
// //         }
// //     }

// //     dotFile << "}";
// //     dotFile.close();

// //     return true;
// // }

// // Method to help find the place of "info" in the adjacency list.
// template<typename T>
// int Graph<T>::findPlace(const T& info) const{
//     for(int i = 0; i < (int)adjList.size(); i++){
//         if(adjList[i][0] == info){
//             return i;
//         }
//     }

//     return -1;
// }

// // Method to help find if a vertex is marked visited or not.
// template<typename T>
// bool Graph<T>::ifVisited(const T& info, bool visited[]) const{

//     if(visited[findPlace(info)])
//         return true;

//     return false;
// }

// // template<typename T>
// // void Graph<T>::dfsHelp(T vertex, bool visited[], list <T> result) const{

// //     visited[findPlace(vertex)] = true;
// //     result.push_back(vertex);
// //     cout << vertex << endl;

// //     for(auto it = adjList[findPlace(vertex)].begin(); it != adjList[findPlace(vertex)].end(); ++it){
// //         if(!ifVisited(*it, visited)){
// //             dfsHelp(*it, visited, result);
// //         }
// //     }
// // }

// template<typename T>
// list<T> Graph<T>::dfs(const T& info) const{
//     bool visited[size];
//     list <T> result;


//     for(int i = 0; i < size; i++){
//         visited[i] = false;
//     }

//     dfsHelp(info, visited, result);

//     return result;
// }

// template<typename T>
// list<T> Graph<T>::bfs(const T& info) const{
//     bool visited[size];
//     int i;
//     queue <T> q;
//     list <T> result;

//     for(i = 0; i < size; i++){
//         visited[i] = false;
//     }

//     q.push(info);
//     result.push_back(info);
//     visited[findPlace(info)] = true;

//     while(!q.empty()){
//         T temp = q.front();
//         q.pop();
//         int pos = findPlace(temp);

//         for(i = 0; i < (int)adjList[pos].size(); i++){
//             if(!ifVisited(adjList[pos][i], visited)){
//                 q.push(adjList[pos][i]);
//                 result.push_back(adjList[pos][i]);
//                 visited[findPlace(adjList[pos][i])] = true;
//             }
//         }
//     }
//     return result;
// }

// template<typename T>
// bool Graph<T>::allVisited(bool visited[]){

//     for(int j = 0; j < size ; j++){
//         if(!visited[j]){
//             return false;
//         }
//     }

//     return true;
// }

// template<typename T>
// T Graph<T>::findUnvisited(bool visited[]){
//     int j; 

//     for(j = 0; j < size ; j++){
//         if(!visited[j]){
//             break;
//         }
//     } 
//     return adjList[j][0];
// }


// // template<typename T>
// // void Graph<T>::mstHelp(T vertex, list<Edge<T>> &result, bool visited[]) {
// //     list<Edge<T>> helper;
// //     T vtxTemp;

// //     for(int i = 0; i < (int)edges.size(); i++){
// //         if((edges[i].from == vertex) || (edges[i].to == vertex)){
// //             helper.push_back(edges[i]);
// //         }
// //     }
// //     for(auto it = helper.begin(); it != helper.end(); ++it){
// //         if((ifVisited(it->from, visited)) || (ifVisited(it->to, visited))){
// //             helper.erase(it);
// //             it = helper.begin();
// //             it--;
// //         }
// //     }
// //     visited[findPlace(vertex)] = true;
// //     helper.sort();

// //     if(helper.empty() && !(allVisited(visited))){
// //         while(1){
// //             vtxTemp = findUnvisited(visited);
// //             for(int i = 0; i < (int)edges.size(); i++){
// //                 if((edges[i].from == vtxTemp) || (edges[i].to == vtxTemp)){
// //                     helper.push_back(edges[i]);
// //                 }
// //             }
// //             visited[findPlace(vtxTemp)] = true;
// //             helper.sort();

// //             result.push_back(helper.front());

// //             if(allVisited(visited)){
// //                 break;
// //             }
// //         }
// //     }
// //     else {
// //         if(vertex == (helper.front()).from){    
// //             vtxTemp = (helper.front()).to;
// //         }
// //         else if(vertex == (helper.front()).to){
// //             vtxTemp = (helper.front()).from;
// //         }

// //         result.push_back(helper.front());
// //         if(allVisited(visited)){
// //             return;
// //         }
    
// //         helper.clear();

// //         mstHelp(vtxTemp, result, visited);
// //     }

// // }

// template<typename T>
// std::list<Edge<T>> Graph<T>::mst(){
//     list<Edge<T>> result;
//     bool visited[size];

//     if(isDirected){
//         return result;
//     }

//     for(int i = 0; i < size; i++){
//         visited[i] = false;
//     }

//     mstHelp(adjList[0][0], result, visited);

//     result.sort();

//     return result;
// }

// template<typename T>
// std::list<T> Graph<T>::dijkstra(const T& from, const T& to){
//     vector <int> dist(size, numeric_limits<int>::max());
//     list <T> result;
//     stack <T> helper;
//     T previousVtx[size];
//     T vertex;
//     int edgeDist;
//     priority_queue < pair <int, T>, vector< pair <int, T>>, greater< pair <int, T>>> pq;
    
//     pq.push(make_pair(0, from));
//     dist[findPlace(from)] = 0;

//     while(!pq.empty()){
//         T temp = pq.top().second;
//         pq.pop();

//         for(auto it = adjList[findPlace(temp)].begin() + 1; it != adjList[findPlace(temp)].end(); ++it){
//             edgeDist = findDistance(temp, *it);
//             vertex = *it;
            

//             if(dist[findPlace(vertex)] > (dist[findPlace(temp)] + edgeDist)){
//                 dist[findPlace(vertex)] = dist[findPlace(temp)] + edgeDist;
//                 pq.push(make_pair(dist[findPlace(vertex)], vertex));
//                 previousVtx[findPlace(vertex)] = temp;
//             }
//         }
//     }

//     vertex = to;
//     // cout << vertex;

//     while(vertex != from){
//         helper.push(vertex);
//         vertex = previousVtx[findPlace(vertex)];
//     }
    
//     helper.push(from);

//     while(!helper.empty()){
//         result.push_back(helper.top());
//         helper.pop();
//     }

//     return result;
// }

// template<typename T>
// std::list<T> Graph<T>::bellman_ford(const T& from, const T& to){
//     list <T> result;
//     int i;
//     stack <T> helper;
//     T previousVtx[size];

//     vector <int> dist(size, numeric_limits<int>::max());
//     dist[findPlace(from)] = 0;


//     // Relax all edges (size - 1) times
//     for(i = 0; i < (size - 1); i++){
//         for(int j = 0; j < (int)edges.size(); j++){
//             T fromVtx = edges[j].from;  //src
//             T toVtx = edges[j].to;  //dest
//             int weight = edges[j].dist;

//             if((dist[findPlace(fromVtx)] != numeric_limits<int>::max()) && ((dist[findPlace(fromVtx)] + weight) < dist[findPlace(toVtx)])){
//                 dist[findPlace(toVtx)] = dist[findPlace(fromVtx)] + weight;
//                 previousVtx[findPlace(toVtx)] = fromVtx;
//             }

//         }
//     }

//     //  If we get another shorter path there is a negative cycle. 
//     for(int j = 0; j < (int)edges.size(); j++){
//         T fromVtx = edges[j].from;  //src
//         T toVtx = edges[j].to;  //dest
//         int weight = edges[j].dist;

//         if((dist[findPlace(fromVtx)] != numeric_limits<int>::max()) && ((dist[findPlace(fromVtx)] + weight) < dist[findPlace(toVtx)])){
//             cout << "Negative Cycle!" << endl;
//             // NegativeGraphCycle ex;
//             // throw ex;
//             return result;
//         }

//     }

//     T vertex = to;

//     while(vertex != from){
//         helper.push(vertex);
//         vertex = previousVtx[findPlace(vertex)];
//     }
    
//     helper.push(from);

//     while(!helper.empty()){
//         result.push_back(helper.top());
//         helper.pop();
//     }

//     return result;
// }

// template<typename T>
// void Graph<T>::printEdgeList(){
//     for(int i = 0; i < (int)edges.size(); i++){
//         cout << edges[i] << endl;
//     }

//     cout << "===================================" << endl;

// }

// template<typename T>
// void Graph<T>::printAdjacencyList(){
    
//     for(int i = 0; i < (int)adjList.size(); i++){
//         for(int j = 0; j < (int)adjList[i].size(); j++){
//             cout << adjList[i][j] << " ->" ;
//         }
//         cout << endl;
//     }

//     cout << "===================================" << endl;
// }


int main() {

    Graph<string> maria(true);

    maria.addVtx("alpha");
    maria.addVtx("beta");
    maria.addVtx("gamma");
    maria.addVtx("delta");
    maria.addVtx("epsilon");
    maria.addVtx("zeta");    
    maria.addVtx("theta");
    maria.addVtx("iota");
    maria.addVtx("kappa");
    maria.addVtx("lamda");
    maria.addVtx("ypsilon");
    maria.addVtx("sigma");
    maria.addVtx("omega");

    maria.addEdg("alpha", "beta", 6);
    maria.addEdg("alpha", "lamda", 3);

    maria.addEdg("beta", "gamma", 3);
    maria.addEdg("beta", "delta", 4);
    // maria.addEdg("beta", "lamda", 3);
    maria.addEdg("gamma", "epsilon", 6);

    maria.addEdg("delta", "zeta", 5);
    maria.addEdg("delta", "kappa", 2);

    maria.addEdg("epsilon", "zeta", 2);
    maria.addEdg("epsilon", "theta", 8);
    // maria.addEdg("epsilon", "theta", 10);

    maria.addEdg("zeta", "theta", 10);
    // maria.addEdg("zeta", "theta", 8);
    // maria.addEdg("zeta", "kappa", 2);

    maria.addEdg("theta", "iota", 5);
    // maria.addEdg("theta", "iota", -5);
    maria.addEdg("theta", "sigma", 1);
    maria.addEdg("theta", "omega", 10);

    maria.addEdg("iota", "kappa", 1);
    maria.addEdg("iota", "ypsilon", 2);

    // maria.addEdg("iota", "kappa", -4);
    // maria.addEdg("iota", "ypsilon", -4);

    maria.addEdg("kappa", "iota", 5);
    maria.addEdg("kappa", "zeta", 2);

    maria.addEdg("lamda", "beta", 2);
    maria.addEdg("lamda", "kappa", 10);

    maria.addEdg("ypsilon", "omega", 3);
    // maria.addEdg("ypsilon", "omega", -4);

    maria.addEdg("sigma", "ypsilon", 3);

    maria.addEdg("omega", "epsilon", 2);
    maria.addEdg("omega", "sigma", 2);


    // maria.printAdjacencyList();
    // maria.printEdgeList();

    // list<Edge<string>> result = maria.mst();

    // for(Edge<string> n : result){
    //     cout << n << endl;
    // }
    // cout << endl;
    string from("delta");
    string to("alpha");
    
    list <string> result = maria.dijkstra(from, to);
    // maria.dijkstra("beta", "alpha");
    // list <string> result = maria.bellman_ford(maria.adjList[0][0], maria.adjList[5][0]);
    for(string n : result){
        cout << n << endl;
    }

    return (0);
}