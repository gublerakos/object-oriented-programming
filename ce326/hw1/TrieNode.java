/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ce326.hw1;

public class TrieNode {

    int i;
    
    TrieNode[] children = new TrieNode[26];
    boolean isWord;

    public TrieNode(){

        isWord = false;
        
        for(i = 0; i < 26; i++){
            children[i] = null;
        }
    }
}
