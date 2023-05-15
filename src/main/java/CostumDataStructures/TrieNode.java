package CostumDataStructures;
import java.util.HashMap;
import java.util.Map;
public class TrieNode {
    Map<Character, TrieNode> children;
    boolean isWordEnd;

    public TrieNode() {
        children = new HashMap<>();
        isWordEnd = false;
    }
}
