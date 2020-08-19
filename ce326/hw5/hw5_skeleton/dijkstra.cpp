std::list<T> dijkstra(const T& from, const T& to){
    vector <int> dist(size, numeric_limits<int>::max());
    list <T> result;
    stack <T> helper;
    T previousVtx[size];
    T vertex;
    int edgeDist;
    priority_queue <pair<int, T>, vector<pair<int, T>>, greater<pair<int,T>>> pq;


    pq.push(make_pair(0, from));
    dist[findPlace(from)] = 0;
    while(!pq.empty()){
        T temp = pq.top().second;
        pq.pop();

        for(auto it = adjList[findPlace(temp)].begin() + 1; it != adjList[findPlace(temp)].end(); ++it){
            edgeDist = findDistance(temp, *it);
            vertex = *it;
            

            if(dist[findPlace(vertex)] > (dist[findPlace(temp)] + edgeDist)){
                dist[findPlace(vertex)] = dist[findPlace(temp)] + edgeDist;
                pq.push(make_pair(dist[findPlace(vertex)], vertex));
                previousVtx[findPlace(vertex)] = temp;
            }
        }
    }

    vertex = to;
    // cout << vertex;

    while(vertex != from){
        helper.push(vertex);
        vertex = previousVtx[findPlace(vertex)];
    }
    
    helper.push(from);

    while(!helper.empty()){
        result.push_back(helper.top());
        helper.pop();
    }

    return result;
}
