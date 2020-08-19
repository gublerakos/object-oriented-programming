#ifndef __AVL_HPP_
#define __AVL_HPP_

#include <iostream>
#include <cstdlib>
#include <fstream>
#include <stack>
#include <algorithm>
#include <cstring>

using namespace std;

class AVL {
private:
    class Node {
        Node *parent, *left, *right;
        int height;
        string element;

    public:
        Node();
        Node(const string& e, Node *parent, Node *left, Node *right);
        ~Node();
        
        Node*  getParent() const;
        Node*  getLeft() const;
        Node*  getRight() const;
        string getElement() const;
        int    getHeight() const;
    
        void setLeft(Node *);
        void setRight(Node *);
        void setParent(Node *);
        void setElement(string e);
        void setHeight(int h);

        bool isLeft() const;
        bool isRight() const;
        int  rightChildHeight() const;
        int  leftChildHeight() const;
        int  updateHeight();
        bool isBalanced();
        

        bool operator==(Node &);
        Node &operator=(Node &);
    };

private:
  
    int   size;
    Node* root;
    
public:
    
    class Iterator {
      
    public:
        stack<Node *> nodeStack;
        Node* currentNode;
        Iterator(Node* node);
        Iterator(const Iterator &it);
        Iterator& operator++();
        Iterator operator++(int a);
        string operator*(); 
        bool operator!=(Iterator it);
        bool operator==(Iterator it);
        Iterator& operator=(const Iterator& it);
    };
    
    Iterator begin() const;  
    Iterator end() const;
    
    static const int MAX_HEIGHT_DIFF = 1;
    AVL();
    AVL(const AVL& );
    bool contains(string e);
    bool add(string e);
    bool rmv(string e);
    void print2DotFile(char *filename);
    void pre_order(std::ostream& out);

    // new methods.
    Node* getRoot() const;
    void setRoot(Node* node);
    void height(Node*);
    void checkBalance(Node* node);
    int getSize() const;
    void setSize(int s);
    Node* findNode(string e);
    Node* findLeft(Node* node);
    Node* rebalanceSon(Node* node);
    Node* reconstruct(Node* node, Node* son, Node* grandson);
    void deleteTree(Node* node);
    void visitNode(Node* node, string* helper);

    friend std::ostream& operator<<(std::ostream& out, const AVL& tree);  

    AVL& operator  =(const AVL& avl);
    AVL  operator  +(const AVL& avl);
    AVL& operator +=(const AVL& avl);
    AVL& operator +=(const string& e);
    AVL& operator -=(const string& e);
    AVL  operator  +(const string& e);
    AVL  operator  -(const string& e);
};

#endif

