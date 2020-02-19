/**
 * trie
 */

import ce326.hw1.Utilities;
//import java.util.Scanner; 
import java.lang.StringBuilder;

public class trie {

    //create head which points to null for now.
    static trieNode head; 
    int counter;
    
    //constructor for class trie  that initialises an object of reference type using constructor of class trieNode.
    public trie(){
        head = new trieNode();
        counter = 0;
    }

    //METHODS


    //search for "word", if does not exist in the structure adds it into it, else returns false(already exists).
    boolean add(String word){
        trieNode curr = head;
        char c;
        int pos;
        // boolean retVal = true;

        //check if "word" already exists.
        if(contains(word)){
            return false;
        }
        else{
            for(int i = 0; i < word.length(); i++){
                c = word.charAt(i);
                pos = c - 'a';

                if(curr.children[pos] == null){
                    curr.children[pos] = new trieNode();
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
        trieNode curr = head;
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


    //returns number of words in the structure, given the head node.
    int numOfWords(trieNode head){
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
    //returns number of words in the trie.
    int size() {
        return(counter);
    }

    char getChar(int pos){
        char c = (char) (pos + 97);
        return c;
    }

/*
    //returns words with the same length with "word" that differ by one letter.
    String[] differByOne(String word){

    }


    //returns words with the same length with "word" that differ by "max" letters.
    String[] differBy(String word, int max){

    }

*/
    void preOrder(trieNode curr, StringBuilder prefix, StringBuilder helper, StringBuilder arrayWords){

        if(curr == null){
            return;
        }

        for(int i = 0; i < 26; i++){
            if(curr.children[i] != null){
                trieNode node = curr.children[i];
                if(node.isWord == false){
                    helper.append(" ");
                    helper.append(getChar(i));
                    prefix.append(getChar(i));
                    preOrder(node, prefix, helper, arrayWords);
                }
                else if(node.isWord == true){
                    helper.append(" ");
                    helper.append(getChar(i));
                    helper.append("!");
                    prefix.append(getChar(i));
                    arrayWords.append(helper);
                    preOrder(node, prefix, helper, arrayWords);
                    
                }
                prefix.deleteCharAt(prefix.length() - 1);
            }
        }
    }
    
    // arrayWords.append(prefix);
    public String toString() {
        StringBuilder returner = new StringBuilder();
        String str = new String();

        traversTrieHelper(head, "", 0, returner);
        str = returner.toString();

        return (str);
    }

    void traversTrie(){
        StringBuilder words = new StringBuilder();
        
        traversTrieHelper(head, "", 0, words);
    }

    void traversTrieHelper(trieNode node, String prefix, int level, StringBuilder returner){

        if(node.isWord == true){
            //prefix.delete(level, prefix.length());
            returner.append(prefix.concat("!"));
            return;
            //System.out.println(prefix.toString());
        }

        for(int i = 0; i < 26; i++){
            if(node.children[i] != null){
                prefix = prefix + getChar(i);
                //prefix.insert(level, getChar(i));
                traversTrieHelper(node.children[i], prefix, level + 1, returner);
                prefix = prefix.substring(0, prefix.length()-1);
            }
        }
    }





    //returns words with the same "prefix".
    String[] wordsOfprefix(String prefix){
        
        trieNode curr = head;

        //check if that prefix exists in the trie.
        for(int i = 0; i < prefix.length(); i++){
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
        //trieNode node = curr;
        int words = numOfWords(curr);
        System.out.println(words);

        //StringBuilder sb = new StringBuilder(prefix);
        //System.out.println(sb.toString());
        //traversTrieHelper(node, "", prefix.length(), sb);

        String[] returnArray = new String[words];

        return(returnArray);
    }


    //MAIN
    public static void main(String[] args) {

        trie Trie = new trie();
        boolean retVal;

        String[] word = Utilities.readFile("test.txt");
       
        for(int i = 0; i < 9; i++){
            retVal = Trie.add(word[i]);
        }

        retVal = Trie.contains("small");
        //System.out.println(retVal);

        int size = Trie.size();
        System.out.println(size);

        //String[] wordsPrefix = Trie.wordsOfprefix("small");

        StringBuilder prefix = new StringBuilder("");
        StringBuilder helper = new StringBuilder("");

        StringBuilder arrayWords = new StringBuilder();
        
        Trie.preOrder(head, prefix, helper, arrayWords);
        System.out.println(Trie);
        //System.out.println(arrayWords.toString());

        // for(String element: arrayWords){
        //     System.out.println(element);
        // }
        
        //Trie.traversTrie();

        // retVal = Util.dot2png("out");
        // System.out.println(retVal);
    }
    
}