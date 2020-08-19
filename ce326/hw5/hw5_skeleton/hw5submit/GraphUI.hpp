
#ifndef _GRAPH_UI_
#define _GRAPH_UI_
#include <iostream>
#include <bits/stdc++.h>

using namespace std;

template <typename T>
int graphUI() {
  
  string option, line;
  bool digraph = false;
  // int length; 
  
  std::cin >> option;
  if(!option.compare("digraph"))
    digraph = true;
  Graph<T> g(digraph);
  
  while(true) {
    
    std::stringstream stream;
    std::cin >> option;
    
    if(!option.compare("av")) {
      getline(std::cin, line);
      stream << line;
      T vtx(stream);
      if(g.addVtx(vtx))
        std::cout << "av " << vtx << " OK\n";
      else
        std::cout << "av " << vtx << " NOK\n";
    }
    else if(!option.compare("rv")) {
      getline(std::cin, line);
      stream << line;
      T vtx(stream);
      if(g.rmvVtx(vtx))
        std::cout << "rv " << vtx << " OK\n";
      else
        std::cout << "rv " << vtx << " NOK\n";
    }
    else if(!option.compare("ae")) {
      getline(std::cin, line);
      stream << line;
    
      T vtxFrom(stream);
      T vtxTo(stream);

      int dist;
      stream >> dist;
      if(g.addEdg(vtxFrom, vtxTo, dist)){
        std::cout << "ae " << vtxFrom << " " << vtxTo << " OK\n";
      }
      else{
        std::cout << "ae " << vtxFrom << " " << vtxTo << " NOK\n";
      }
    }
    else if(!option.compare("re")) {
      getline(std::cin, line);
      stream << line;
      T vtxFrom(stream);
      T vtxTo(stream);
      if(g.rmvEdg(vtxFrom, vtxTo)){
        std::cout << "re" << vtxFrom << vtxTo << "OK\n";
      }
      else{
        std::cout << "re" << vtxFrom << vtxTo << "NOK\n";
      }
    }
    else if(!option.compare("dot")) {
      getline(std::cin, line);
      stream << line;
      string filename_str = stream.str();
      const char* filename = filename_str.c_str();

      if(g.print2DotFile(filename)){
        cout << "dot" << filename << "OK\n";
      }
      else{
        cout << "dot" << filename << "NOK\n";
      }
    }
    else if(!option.compare("bfs")) {
      getline(std::cin, line);
      stream << line;
      T node(stream);

      // g.printAdjacencyList();

      std::cout << "\n----- BFS Traversal -----\n";

      std::list<T> result = g.bfs(node);
      auto it = result.cbegin();
      cout << *it;

      it++;
      for (*it; it != result.cend(); ++it){
		    std::cout << " -> " << *it;
	    }
      
      std::cout << "\n-------------------------\n";
    }
    else if(!option.compare("dfs")) {
      getline(std::cin, line);
      stream << line;
      T node(stream);

      std::cout << "\n----- DFS Traversal -----\n";

      std::list<T> result = g.dfs(node);

      auto it = result.cbegin();
      cout << *it;

      it++;
      for (*it; it != result.cend(); ++it){
		    std::cout << " -> " << *it;
	    }
      
      
      std::cout << "\n-------------------------\n";
    }
    else if(!option.compare("dijkstra")) {
      getline(std::cin, line);
      stream << line;
      T from(stream);
      T to(stream);

      std::cout << "Dijkstra (" << from << " - " << to <<"): ";
      list<T> result = g.dijkstra(from, to);

      auto it = result.cbegin();
      cout << *it;

      it++;
      for (*it; it != result.cend(); ++it){
		    std::cout << ", " << *it;
	    }
      std::cout << endl;
    }
    else if(!option.compare("bellman-ford")) {
      getline(std::cin, line);
      stream << line;
      T from(stream);
      T to(stream);
      
      std::cout << "Bellman-Ford (" << from << " - " << to <<"): ";
      list<T> result = g.bellman_ford(from, to);
      
      auto it = result.cbegin();
      cout << *it;

      it++;
      for (*it; it != result.cend(); ++it){
		    std::cout << ", " << *it;
	    }
      std::cout << endl;
    }
    else if(!option.compare("mst")) {

      std::cout << "\n--- Min Spanning Tree ---\n";

      list<Edge<T>> result = g.mst();
      for(Edge<T> n : result){
        std::cout << n << endl;
      }
      std::cout << "MST Cost: " << g.sum << endl;
    }
    else if(!option.compare("q")) {
      cerr << "bye bye...\n";
      return 0;
    }
    else if(!option.compare("#")) {
      string line;
      getline(std::cin,line);
      cerr << "Skipping line: " << line << endl;
    }
    else {
      std::cout << "INPUT ERROR\n";
      return -1;
    }
  }
  return -1;  
}

#endif
