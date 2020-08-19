#include "AVL.hpp"
#define MAX_LINE_LEN 128
#define MAX_ENTRIES 64

/*================================================================*/

// Constructors and methods of class Node.

/*================================================================*/
AVL::Node::Node() {
    // cout << "Default Constructor" << endl;
}

AVL::Node::Node(const string& e, Node *parent, Node *left, Node *right){
    height = 1;
    setElement(e);
    setParent(parent);
    setLeft(left);
    setRight(right);
}

AVL::Node::~Node(){
    // cout << "Default Destructor" << endl;
}

// Getter method for parent pointer.
AVL::Node* AVL::Node::getParent() const{
    return parent;
}

// Getter method for left pointer.
AVL::Node* AVL::Node::getLeft() const{
    return left;
}

// Getter method for right pointer.
AVL::Node* AVL::Node::getRight() const{
    return right;
}

// Getter method for height.
int AVL::Node::getHeight() const {
    if(!(this == NULL)){
        return height;
    }

    return 0;
}

// Getter method for element.
string AVL::Node::getElement() const {
    return element;
}

// Setter method for left pointer.
void AVL::Node::setLeft(Node* n) {
    left = n;
}

// Setter method for reight pointer.
void AVL::Node::setRight(Node* n) {
    right = n;
}

// Setter method for parent pointer.
void AVL::Node::setParent(Node* n) {
    parent = n;
}

// Setter method for element.
void AVL::Node::setElement(string e) {
    element = e;
}

// Setter method for height.
void AVL::Node::setHeight(int h) {
    height = h;
}

// Method that returns true if a node is left child and false if it is right.
bool AVL::Node::isLeft() const {
    if(this->parent == NULL) {
        return false;
    }
    if(this->parent->left == this)
        return true;
    else
        return false;
}

// Method that returns true if a node is right child and false if it is left.
bool AVL::Node::isRight() const {
    if(this->parent == NULL) {
        return false;
    }
    if(this->parent->right == this)
        return true;
    else
        return false;
}

// Method that returns the height of the right child.
int AVL::Node::rightChildHeight() const {
    if(this->right == NULL){
        return 0;
    }
    return(this->right->getHeight());
}

// Method that returns the height of the left child.
int AVL::Node::leftChildHeight() const {
    if(this->left == NULL){
        return 0;
    }
    return(this->left->getHeight());
}

// Method that updates height of a node based on the heights of left and right children.
int AVL::Node::updateHeight() {

    if(this->getLeft() == NULL){
        if(this->getRight() == NULL){
            return 1;
        }
        else {
            return(this->getRight()->getHeight() + 1);
        }
    }
    else if(this->getRight() == NULL){
        return(this->getLeft()->getHeight() + 1);
    }
    else {
        return (max((this->getLeft()->getHeight()), (this->getRight()->getHeight())) + 1);
    }
}

// Method that returns true if a node is balanced and false if it is not based on balance factor.
bool AVL::Node::isBalanced() {
    int balanceFactor = 0;

    if(this->getLeft() == NULL){
        if(this->getRight() == NULL){
            return true;
        }
        else{
            balanceFactor = rightChildHeight();
        }
    }
    else if(this->getRight() == NULL){
        balanceFactor = leftChildHeight();
    }
    else{
        balanceFactor = leftChildHeight() - rightChildHeight();
    }

    if((balanceFactor >= -1) && (balanceFactor <= 1))
        return true; 
    else
        return false;
}

// Operator =.
AVL::Node& AVL::Node::operator=(Node& it) {
    element = it.element;
    parent = it.parent;
    left = it.left;
    right = it.right;
    height = it.height;

    return *this;
}

// Operator ==.
bool AVL::Node::operator==(Node& it){
    if(height == it.height){
        if(element == it.element){
            return true;
        }
    }
    return false;
}


/*================================================================*/

// Constructors and methods of class Iterator.

/*================================================================*/

// Constructor of Iterator.
AVL::Iterator::Iterator(Node* node) {
    currentNode = node;
    if(currentNode != NULL){
        nodeStack.push(currentNode);
    }
}

// Copy constructor of Iterator.
AVL::Iterator::Iterator(const Iterator& it) {
    currentNode = it.currentNode;
    nodeStack = it.nodeStack;
}

// Operator* used to return the element of a node.
string AVL::Iterator::operator*() {
    if(currentNode != NULL){
        return (nodeStack.top()->getElement());
    }
    else {
        return NULL;
    }
}

// Operator to increase iterator by pushing first right and then left children in the stack to achieve pre order.
AVL::Iterator& AVL::Iterator::operator++() {
    if(!this->nodeStack.empty()){
        currentNode = this->nodeStack.top();
        this->nodeStack.pop();

        if(currentNode->getRight()){
            this->nodeStack.push(currentNode->getRight());
        }
        if(currentNode->getLeft()){
            this->nodeStack.push(currentNode->getLeft());
        }
    }
    else {
        currentNode = NULL;
    }
    return *this;
}

// ++this but return previous this.
AVL::Iterator AVL::Iterator::operator++(int a) {

    Iterator it(this->currentNode);

    
    if(!this->nodeStack.empty()){
        currentNode = this->nodeStack.top();
        this->nodeStack.pop();

        if(currentNode->getRight()){
            this->nodeStack.push(currentNode->getRight());
        }
        if(currentNode->getLeft()){
            this->nodeStack.push(currentNode->getLeft());
        }
    }
    else {
        currentNode = NULL;
    }

    return it;
}

// Operator =
AVL::Iterator& AVL::Iterator::operator=(const Iterator& it) {
    currentNode = it.currentNode;
    if (this->nodeStack != it.nodeStack)
        this->nodeStack = it.nodeStack;

    return *this;
}

// Operator ==
bool AVL::Iterator::operator==(Iterator it) {
    return !(*this != it);
}

// Operator !=
bool AVL::Iterator::operator!=(Iterator it) {
    if(currentNode != it.currentNode){
        if(nodeStack != it.nodeStack){
            return true;
        }
    }
    return false;
}

/*================================================================*/

// Constructors and methods of class AVL.

/*================================================================*/

// Constructor of the AVL.
AVL::AVL() {
    root = NULL;
    size = 0;
}

// Copy constructor of the AVL.
AVL::AVL(const AVL& avl) {
    
    root = new Node(avl.root->getElement(), NULL, NULL, NULL);
    
    Iterator it = avl.begin();
    it++;

    while(it != avl.end()){
        add(it.nodeStack.top()->getElement());
        it++;
    }
}

// Setter method for size.
void AVL::setSize(int s){
    size = s;
}

// Setter method for root element.
void AVL::setRoot(Node* node) {
    root = node;
}

// Getter method for root element.
AVL::Node* AVL::getRoot() const {
    return root;
}

// Getter method for size.
int AVL::getSize() const{
    return size;
}

// Method that runs the tree via pre order (class Iterator) and returns true if "e" exists in the tree.
bool AVL::contains(string e){
    Iterator it = begin();

    while(it.currentNode != end().currentNode) {
        if(it.currentNode->getElement() == e){
            return true;
        }
        it++;
    }
    
    return false;
}

// Method to add "e" element in the tree. Returns false if "e" is already in the tree.
bool AVL::add(string e) {
    
    Node* node;
    Node* par;
    node = getRoot();

    // Empty tree, set root.
    if(node == NULL){
        Node* newNode = new Node(e, NULL, NULL, NULL);
        setRoot(newNode);
    }
    else {
        if(contains(e)){
            return false;
        }
        // If "e" < go to left side, else go to right side of the tree.
        while(node != NULL){
            if(node->getElement() > e){
                par = node;
                node = node->getLeft();
            }
            else if(node->getElement() < e){
                par = node;
                node = node->getRight();
            }
        }

        Node* newNode = new Node(e, par, NULL, NULL);
        if(newNode->getElement() < par->getElement()) {
            par->setLeft(newNode);
        }
        else {
            par->setRight(newNode);
        }

        // After adding node, it's essential to update the height of every node and subsequently check if there is an unbalanced node.
        height(newNode);
        checkBalance(newNode);
    }

    // Increase size.
    setSize(getSize() + 1);
    return true;
}

// Method to delete "e" from the tree. Returns false if "e" is not in the tree.
bool AVL::rmv(string e){
    // Empty tree.
    if(root == NULL){
        // cout << "Nothing to delete!" << endl;
    }

    Node* delNode = findNode(e);
    
    // Node "e" does not exist in the tree.
    if(delNode == NULL){
        return false;
    }

    // Both sons null, just deleting it
    if((delNode->getLeft() == NULL) && (delNode->getRight() == NULL)){
        if(delNode == root){
            delete (delNode);
            root = NULL;
        }
        else{
            if(delNode->isLeft()){
                delNode->getParent()->setLeft(NULL);
            }
            else if(delNode->isRight()){
                delNode->getParent()->setRight(NULL);
            }

            // After deleting node, it's essential to update the height of every node and subsequently check if there is an unbalanced node.
            height(delNode);
            checkBalance(delNode);
        }
        
    }
    // At least one son null
    else if((delNode->getLeft() == NULL) || (delNode->getRight() == NULL)){
        // Right son null.
        if(delNode->getRight() == NULL){
            if(delNode == root){
                delNode->getLeft()->setParent(NULL);
                setRoot(delNode->getLeft());
            }
            else{
                delNode->getLeft()->setParent(delNode->getParent());
                if(delNode->isLeft()){
                    delNode->getParent()->setLeft(delNode->getLeft());
                }
                else if(delNode->isRight()){
                    delNode->getParent()->setRight(delNode->getLeft());
                }
            }
            
        }
        // Left sun null.
        else if(delNode->getLeft() == NULL){
            if(delNode == root){
                delNode->getRight()->setParent(NULL);
                setRoot(delNode->getRight());
            }
            else{
                delNode->getRight()->setParent(delNode->getParent());
                if(delNode->isLeft()){
                    delNode->getParent()->setLeft(delNode->getRight());
                }
                else if(delNode->isRight()){
                    delNode->getParent()->setRight(delNode->getRight());
                }
            }
        } 

        // Again checking for height and balance.
        height(delNode);
        checkBalance(delNode);
    }
    // No null sons
    else {
        // Find smallest of right subtree, exchange values, delete.
        Node* node = findLeft(delNode->getRight());
        string helper = delNode->getElement();

        delNode->setElement(node->getElement());
        node->setElement(helper);

        if((node->getLeft() == NULL) && (node->getRight() == NULL)){
            if(node->isRight()){
                node->getParent()->setRight(NULL);
            }
            else if(node->isLeft()){
                node->getParent()->setLeft(NULL);
            }
        }
        else if(node->getRight() != NULL){
            node->getParent()->setRight(node->getRight());
            node->getRight()->setParent(node->getParent());
        }

        // Again height and balance.
        height(node);
        checkBalance(delNode);
    }

    // Decrease size. 
    setSize(getSize() - 1);
    return true;
}

// Method to find the last left node of a subtree. (Used by rmv method, when both sons are NULL)
AVL::Node* AVL::findLeft(Node* node){
    while(node->getLeft() != NULL){
        node = node->getLeft();
    }   

    return node;
}

// Method to find and return the node with "e". (Used by rmv method)
AVL::Node* AVL::findNode(string e){
    Iterator it = begin();

    while(!it.nodeStack.empty()) {
        if((it.nodeStack.top()->getElement()) == e){
            return it.nodeStack.top();
        }
        
        it++;
    }
    
    return NULL;
}

// Method to find and return the highest node of a subtree that will participate in rebalance. (Used by checkBalance method)
AVL::Node* AVL::rebalanceSon(Node* node){
    // Empty.
    if(node == NULL){
        return NULL;
    }

    if((node->leftChildHeight()) > (node->rightChildHeight())){
        return node->getLeft();
    }
    else if((node->leftChildHeight()) < (node->rightChildHeight())){
        return node->getRight();
    }
    //same height
    else if(node->isLeft()){
        return node->getLeft();
    }
    else {
        return node->getRight();
    }
}

// Method that checks if a node is balanced. If it is not reconstruct method is called to define the rotation.
void AVL::checkBalance(Node* node) {
    // Empty.
    if(node == NULL) {
        return;
    }

    Node* son;
    Node* grandson;

    while(node != NULL){
        if(!node->isBalanced()){
            son = rebalanceSon(node);
            grandson = rebalanceSon(son);

            node = reconstruct(node, son, grandson);
            height(node->getLeft());
            height(node->getRight());
            height(node);
        }
        else{
            node = node->getParent();   
        }
    }
}

// Method to define wanted rotation (left, right, double left, double right) to reconstruct the tree and return  the highest node that participated in the rotation.
AVL::Node* AVL::reconstruct(Node* node, Node* son, Node* grandson) {
    // Right simple rotation. (both son and grandson are left children)
    if((son->isLeft()) && (grandson->isLeft())){
        // cout << "Simple right rotation!" << endl;
        if(!(node == root)){
            if(node->isLeft()){
                node->getParent()->setLeft(son);
            }
            else {
                node->getParent()->setRight(son);
            }
            son->setParent(node->getParent());
        }
        node->setLeft(son->getRight());
        if(!(son->getRight() == NULL)){
            son->getRight()->setParent(node);
        }
        son->setRight(node);
        node->setParent(son);
        if(node == root){
            setRoot(son);
            son->setParent(NULL);
        }
        return son;
    }
    // Left simple rotation. (both son and grandson are right children)
    else if((son->isRight()) && (grandson->isRight())){
        // cout << "Simple left rotation!" << endl;
        if(!(node == root)){
            if(node->isRight()){
                node->getParent()->setRight(son);
            }
            else {
                node->getParent()->setLeft(son);
            }
            son->setParent(node->getParent());
        }
        node->setRight(son->getLeft());
        if(!(son->getLeft() == NULL)){
            son->getLeft()->setParent(node);
        }
        son->setLeft(node);
        node->setParent(son);
        if(node == root){
            setRoot(son);
            son->setParent(NULL);
        }
        return son;
    }
    // Double left rotation.
    else if(grandson->isLeft()){
        // cout << "Double left rotation!" << endl;
        node->setRight(grandson->getLeft());
        if(grandson->getLeft() != NULL){
            grandson->getLeft()->setParent(node);
        }
        son->setLeft(grandson->getRight());
        if(grandson->getRight() != NULL){
            grandson->getRight()->setParent(son);
        }

        if(!(node == root)){
            if(node->isRight()){
                node->getParent()->setRight(grandson);
            }
            else {
                node->getParent()->setLeft(grandson);
            }
            grandson->setParent(node->getParent());
        }

        node->setParent(grandson);
        son->setParent(grandson);
        grandson->setLeft(node);
        grandson->setRight(son);
        if(node == root){
            setRoot(grandson);
            grandson->setParent(NULL);
        }
        return grandson;
    }
    // Double right rotation
    else {
        // cout << "Double right rotation!" << endl;
        node->setLeft(grandson->getRight());
        if(grandson->getRight() != NULL){
            grandson->getRight()->setParent(node);
        }
        son->setRight(grandson->getLeft());
        if(grandson->getLeft() != NULL){
            grandson->getLeft()->setParent(son);
        }

        if(!(node == root)){
            if(node->isLeft()){
                node->getParent()->setLeft(grandson);
            }
            else {
                node->getParent()->setRight(grandson);
            }
            grandson->setParent(node->getParent());
        }

        node->setParent(grandson);
        son->setParent(grandson);
        grandson->setLeft(son);
        grandson->setRight(node);
        if(node == root){
            setRoot(grandson);
            grandson->setParent(NULL);
        }
        return grandson;
    }    
}

// Method to update the height of every node starting from the bottom.
void AVL::height(Node* node) {
    while(node != NULL){
        node->setHeight(node->updateHeight());
        node = node->getParent();
    }
}

// Method that connects classes AVL and Iterator and returns an Iterator at the beggining of the tree.
AVL::Iterator AVL::begin() const {
    Iterator it(root);

    return (it);
}

// Method that connects classes AVL and Iterator and returns an Iterator at the end of the tree.
AVL::Iterator AVL::end() const {
    
    Iterator it(NULL);

    return (it);
}

// Method to pre-order the tree.
void AVL::pre_order(std::ostream& out){
    Iterator it = begin();

    while(it.currentNode != end().currentNode) {
        out << *it << " "; 
        it++;
    }
}

// Method to visit every node (pre-order) and save info for dot file.
void AVL::visitNode(Node* node, string* helper){

    *helper = *helper + node->getElement() + " [shape=circle, color=black]" + "\n";
    if(node->getLeft() != NULL){
        *helper = *helper + node->getElement() + " -> " + node->getLeft()->getElement() + "\n";
        visitNode(node->getLeft(), helper);
    }
    
    if(node->getRight() != NULL){
        *helper = *helper + node->getElement() + " -> " + node->getRight()->getElement() + "\n";
        visitNode(node->getRight(), helper);
    }
}

// Method to print a dot file.
void AVL::print2DotFile(char *filename){
    ofstream dotFile;
    string helper;
    
    dotFile.open(filename);
    dotFile << "digraph {" << endl;
    visitNode(root, &helper);
    dotFile << helper;
    dotFile << "}";
    dotFile.close();
}

// Method to delete every node in a tree begging with node.
void AVL::deleteTree(AVL::Node* node) {  
    if(node != NULL){
        /* first delete both subtrees */
        if(node->getLeft() != NULL){
            deleteTree(node->getLeft());  
        }
        
        if(node->getRight() != NULL){
            deleteTree(node->getRight());  
        }
        
        /* then delete the node */
        delete node; 
    }
} 

// Operator <<.
std::ostream& operator<<(std::ostream& out, const AVL& tree) {
    AVL::Iterator it = tree.begin();

    while(!it.nodeStack.empty()) {
        if(!(it.nodeStack.top() == NULL)){
            out << *it << " ";
            it++;
        }
        else{
            break;
        }
    }

    return out;
}

// Operator=.
AVL& AVL::operator=(const AVL& avl){

    if(root != NULL){
        deleteTree(this->root);
    }
    if(avl.root != NULL){
        root = new Node(avl.root->getElement(), NULL, NULL, NULL);
    }
    else {
        root = NULL;
        return *this;
    }
    
    Iterator it = avl.begin();
    it++;

    while(it != avl.end()){
        add(it.nodeStack.top()->getElement());
        it++;
    }

    return *this;
}

// Operator+.(avl)
AVL AVL::operator+(const AVL& avl){

    AVL avlNew;

    for(AVL::Iterator it = this->begin(); it != this->end(); ++it) {
        avlNew.add(*it);
    }

    for(AVL::Iterator it = avl.begin(); it != avl.end(); ++it) {
        avlNew.add(*it);
    }

    return avlNew;
}

// Operator+.(string)
AVL AVL::operator+(const string& e){

    AVL avlNew;
   
    for(AVL::Iterator it = this->begin(); it != this->end(); ++it) {
        avlNew.add(*it);
    }

    avlNew.add(e);
    return avlNew;
}

// Operator-.(string)
AVL AVL::operator-(const string& e){
    
    AVL avlNew;
    
    for(AVL::Iterator it = this->begin(); it != this->end(); ++it) {  
        avlNew.add(*it);
    }
    avlNew.rmv(e);
    
    return avlNew;
}

// Operator+=.(avl)
AVL& AVL::operator+=(const AVL& avl){
    for(AVL::Iterator it = avl.begin(); it != avl.end(); ++it) {
        this->add(*it);
    }

    return *this;
}

// Operator+=.(string)
AVL& AVL::operator +=(const string& e){
    this->add(e);

    return *this;
}

// Operator-=.(string)
AVL& AVL::operator -=(const string& e){
    this->rmv(e);

    return *this;
}