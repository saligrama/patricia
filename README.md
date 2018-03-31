# patricia

Patricia is a bit-alphabet based, space-optimized trie for string storage.

Patricia Trees (also known as Crit-Bit Trees and Compressed Binary Trees) are
extremely space-efficient compared to standard tries due to their compression
of nodes with only one child. Therefore, no unnecessary nodes and edges are
stored, cutting down on both storage and in steps needed to traverse the trie.

This PatriciaTree operates on a bit alphabet and can store any type of ASCII
text. It operates similarly to a Radix Tree, with radix r=2.

To use, simply include the library in your source code and call the functions as below.

```java
import io.github.saligrama.patricia.PatriciaTree;

public class PatriciaTest {
    public static void main(String[] args) {
        PatriciaTree tree = new PatriciaTree();
        System.out.println(tree.add("Foo bar baz quux")); // should print true
        System.out.println(tree.contains("Foo")); // should print false
        System.out.println(tree.contains("Foo bar baz quux")); // should print true
        System.out.println(tree.remove("Foo bar baz quux")); // should print true
    }
}
```
