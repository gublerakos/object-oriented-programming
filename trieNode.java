/**
 * trieNode
 */
public class trieNode {

    int i;
    
    trieNode[] children = new trieNode[26];
    boolean isWord;

    public trieNode(){

        isWord = false;
        
        for(i = 0; i < 26; i++){
            children[i] = null;
        }
    }
}