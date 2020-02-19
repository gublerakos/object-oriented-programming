/**
 * trie
 */

import ce326.hw1.Utilities;
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

    //HELPERS

    void preOrder(trieNode curr, String prefix, StringBuilder helper){

        if(curr == null){
            return;
        }

        for(int i = 0; i < 26; i++){
            if(curr.children[i] != null){
                trieNode node = curr.children[i];
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

    //method to traverse a trie like pre order visiting every node and saving every word in helper.
    void traversTrieHelper(trieNode node, StringBuilder prefix, int level, StringBuilder helper, int flag, int length){

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

    //returns a character related to an array position
    char getChar(int pos){
        char c = (char) (pos + 97);
        return c;
    }

    //METHODS


    //search for "word", if does not exist in the structure adds it into it, else returns false(already exists).
    boolean add(String word){
        trieNode curr = head;
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
    String[] wordsOfprefix(String prefix){
        int i, index;
        trieNode curr = head;

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

    // String toDotDtring(){

    // }

    //MAIN
    public static void main(String[] args) {
        int i;
        trie Trie = new trie();
        boolean retVal;

        String[] word = Utilities.readFile("test3.txt");
       
        for(i = 0; i < 13; i++){
            retVal = Trie.add(word[i]);
        }

        int size = Trie.size();

        System.out.println(Trie);

        // String[] arrayPrefix = Trie.wordsOfprefix("sm");
        
        // for(String element: arrayPrefix){
        //     System.out.println(element);
        // }
        String[] arrayDiffer = Trie.differByOne("body");
        System.out.println("Words that differ by one letter with word body are:");

        for(String element: arrayDiffer){
            System.out.println(element);
        }

        String[] arrayDiffer2 = Trie.differBy("small", 4);
        System.out.println("Words that differ up to 4 letters with word small are:");

        for(String element: arrayDiffer2){
            System.out.println(element);
        }

    }
    
}