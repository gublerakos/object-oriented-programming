/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ce326.hw1;
public class Trie {

    //create head which points to null for now.
    TrieNode head; 
    int counter;
    
    //constructor for class trie  that initialises an object of reference type using constructor of class trieNode.
    public Trie(String[] words){
        boolean retVal;

        head = new TrieNode();
        counter = 0;

        int size = findArrayLength(words);
        
        for(int i = 0; i < size; i++){
            retVal = add(words[i]);
            if(retVal == false){
                //System.out.println("Word " + words[i] +  " already exists in trie!");
            }
        } 
    }

    //HELPERS

    int findArrayLength(String[] array){
        int counter = 0;

        for(int i = 0; i < array.length; i++){
            if(array[i] == null){
                break;
            }
            counter++;
        }
        
        return(counter);
    }

    void preOrder(TrieNode curr, String prefix, StringBuilder helper){

        if(curr == null){
            return;
        }

        for(int i = 0; i < 26; i++){
            if(curr.children[i] != null){
                TrieNode node = curr.children[i];
                if(node.isWord == false){
                    prefix = prefix + " ";
                    prefix = prefix + getChar(i);
                    preOrder(node, prefix, helper);
                    prefix = prefix.substring(0, 0);
                }
                else if(node.isWord == true){
                    prefix = prefix + " ";
                    prefix = prefix + getChar(i);
                    helper.append(prefix.concat("!"));
                    prefix = prefix.substring(0, 0);
                    preOrder(node, prefix, helper);
                    
                }
            }
        }
    }

    String visitNode(TrieNode node, TrieNode parent, int pos, char label,int id, String color){
        int idParent;
        StringBuilder info = new StringBuilder();

        label = getChar(pos);
        id = node.hashCode();
        idParent = parent.hashCode();
        if(node.isWord == true){
            color = "red";
        }
        else if(node.isWord == false){
            color = "black";
        }
        info.append(idParent);
        info.append(" -- ");
        info.append(id);
        info.append("\n");
        info.append(id);
        info.append(" [label=\"");
        info.append(label);
        info.append("\" ,shape=circle, color=");
        info.append(color);
        info.append("]\n");
        
        return(info.toString());
    }

    void traverseTrie(TrieNode curr, String infoNode, int pos, String prefix, StringBuilder helper){
        char label = '\0';
        int id = 0;
        String color = "";

        for(int i = 0; i < 26; i++){
            if(curr.children[i] != null){
                TrieNode node = curr.children[i];
                if(node.isWord == false){
                    infoNode = visitNode(node, curr, i, label, id, color);
                    prefix = prefix + infoNode;
                    traverseTrie(node, infoNode, i, prefix, helper);
                    prefix = prefix.substring(0, 0);
                }
                else if(node.isWord == true){
                    infoNode = visitNode(node, curr, i, label, id, color);
                    prefix = prefix + infoNode;
                    helper.append(prefix);
                    prefix = prefix.substring(0, 0);
                    traverseTrie(node, infoNode, i, prefix, helper);
                }
            }
        }
    }

    //method to traverse a trie like pre order visiting every node and saving every word in helper.
    void traversTrieHelper(TrieNode node, StringBuilder prefix, int level, StringBuilder helper, int flag, int length){

        if(flag == 1){ //wordsOfprefix
            if(node.isWord == true){
                prefix.delete(level, prefix.length());
                helper.append(prefix.toString());
                helper.append("!");
            }

            for(int i = 0; i < 26; i++){
                if(node.children[i] != null){
                    prefix.insert(level, getChar(i));
                    traversTrieHelper(node.children[i], prefix, level + 1, helper, flag, length);
                }
            }
        }
        else if(flag == 0){ //differByOne
            if(node.isWord == true){
                prefix.delete(level, prefix.length());
                if(prefix.length() == length){
                    helper.append(prefix.toString());
                }
            }

            for(int i = 0; i < 26; i++){
                if(node.children[i] != null){
                    prefix.insert(level, getChar(i));
                    traversTrieHelper(node.children[i], prefix, level + 1, helper, flag, length);
                }
            } 
        }
    }

    //returns number of words in the structure, given the head node.
    int numOfWords(TrieNode head){
        int result = 0;

        if(head.isWord == true){
            result++;
        }

        for(int i = 0; i < 26; i++){
            if(head.children[i] != null){
                result = result + numOfWords(head.children[i]);
            }
        }

        return result;
    }

    //returns a character related to an array position
    char getChar(int pos){
        char c = (char) (pos + 97);
        return c;
    }

    //METHODS

    //search for "word", if does not exist in the structure adds it into it, else returns false(already exists).
    boolean add(String word){
        TrieNode curr = head;
        char c;
        int pos;

        //check if "word" already exists.
        if(contains(word)){
            return false;
        }
        else{
            for(int i = 0; i < word.length(); i++){
                c = word.charAt(i);
                pos = c - 'a';

                if(curr.children[pos] == null){
                    curr.children[pos] = new TrieNode();
                }

                curr = curr.children[pos];
            }
            curr.isWord = true;
            counter++;
        }
        return true;
    }

    //returns true if "word" is in the structure, else false.
    boolean contains(String word){
        TrieNode curr = head;
        int pos;
        char c;

        for(int i = 0; i < word.length(); i++){
            c = word.charAt(i);
            pos = c - 'a';
            if(curr.children[pos] == null){
                return(false);
            }
            else{
                curr = curr.children[pos];
            }
        }
        return((curr != null) && (curr.isWord == true));
    }

    //returns number of words in the trie.
    int size() {
        return(counter);
    }

    //returns words with the same length with "word" that differ by one letter.
    String[] differByOne(String word){

        String[] returnArray = differBy(word, 1);

        return(returnArray);
    }

    //returns words with the same length with "word" that differ by "max" letters.
    String[] differBy(String word, int max){
        int size, i, j;
        int diff;

        StringBuilder helper = new StringBuilder();
        StringBuilder prefix = new StringBuilder();

        traversTrieHelper(head, prefix, 0, helper, 0, word.length());

        String Words = helper.toString();

        size = (helper.length()/word.length());

        String[] testArray = new String[size];
        for(i = 0; i < size; i++){
            testArray[i] = Words.substring(0, word.length());
            Words = Words.substring(word.length());
        }

        StringBuilder returnStr = new StringBuilder();
        for(i = 0; i < size; i++){
            diff = 0;
            String tester = testArray[i];
            for(j = 0; j < word.length(); j++){
                if(tester.charAt(j) != word.charAt(j)){
                    diff++;
                }
            }
            if((diff <= max) && (diff != 0)){
                returnStr.append(testArray[i]);
            }
        }
        size = (returnStr.length()/word.length());
        Words = returnStr.toString();

        String[] returnArray = new String[size];
        for(i = 0; i < size; i++){
            returnArray[i] = Words.substring(0, word.length());
            Words = Words.substring(word.length());
        }

        return(returnArray);
    }

    //returns words with the same "prefix".
    String[] wordsOfPrefix(String prefix){
        int i, index;
        TrieNode curr = head;

        //check if that prefix exists in the trie.
        for(i = 0; i < prefix.length(); i++){
            char c = prefix.charAt(i);
            int pos = c - 'a';
            if(curr.children[pos] == null){
                System.out.println("No such word!");
            }
            else{
                curr = curr.children[pos];
            }
        }
        //right here i am sure there are words with that prefix.
        int words = numOfWords(curr);

        StringBuilder sb = new StringBuilder(prefix);
        StringBuilder helper = new StringBuilder("");
        String[] returnArray = new String[words];
        
        //flag = 1 if this method is called by wordsOfprefix and zero if it is called by differByOne.
        traversTrieHelper(curr, sb, prefix.length(), helper, 1, 0);

        String Words = new String();
        Words = helper.toString();

        for(i = 0; i < words; i++){
            index = Words.indexOf("!");
            returnArray[i] = Words.substring(0, index);
            Words = Words.substring(index + 1);
        }

        return(returnArray);
    }

    public String toString() {
        StringBuilder helper = new StringBuilder();
        String str = new String();

        preOrder(head, "", helper);
        str = helper.toString();

        return(str);
    }

    String toDotString(){
        StringBuilder dotFile = new StringBuilder("graph Trie {\n");
        String infoNode = new String();
        String prefix = new String();
        StringBuilder helper = new StringBuilder();

        dotFile.append(head.hashCode());
        dotFile.append(" [label=\"ROOT\" ,shape=circle, color=black]\n");
        traverseTrie(head, infoNode, 0, prefix, helper);

        dotFile.append(helper);
        dotFile.append("}");

        String toDot = dotFile.toString();

        return(toDot);
    }
}